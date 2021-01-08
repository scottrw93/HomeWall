package com.scottw.homewall;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.*;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.scottw.homewall.core.problem.Problem;
import com.scottw.homewall.core.problem.ProblemRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class HomeWall implements HttpFunction {
  private static final TypeReference<List<List<Map<String, Integer>>>> HOLDS = new TypeReference<>() {};
  private static final TypeReference<List<Map<String, Integer>>> HOLD = new TypeReference<>() {};

  static {
    System.setProperty("GOOGLE_CLOUD_PROJECT", "homewall-301021");
  }

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final Datastore datastore = DatastoreOptions
    .newBuilder()
    .build()
    .getService();

  @Override
  public void service(HttpRequest request, HttpResponse response)
    throws Exception {
    response.appendHeader("Access-Control-Allow-Origin", "*");
    response.appendHeader("Content-Type", "application/json");

    String method = request.getMethod();
    switch (method) {
      case "OPTIONS":
        handleOptions(request, response);
        break;
      case "GET":
        handleGet(request, response);
        break;
      case "PUT":
        handlePut(request, response);
        break;
      case "POST":
        handlePost(request, response);
        break;
      default:
        response.setStatusCode(405);
        break;
    }
  }

  private void handlePost(HttpRequest request, HttpResponse response)
    throws IOException {
    switch (request.getPath()) {
      case "/problems":
        response
          .getWriter()
          .write(
            objectMapper.writeValueAsString(
              createProblem(
                objectMapper.readValue(
                  request.getInputStream(),
                  ProblemRequest.class
                )
              )
            )
          );
        break;
      default:
        response.setStatusCode(404);
        break;
    }
  }

  private Problem createProblem(ProblemRequest problemRequest)
    throws JsonProcessingException {
    Problem problem = Problem
      .builder()
      .from(problemRequest)
      .setCreatedAt(Instant.now().toEpochMilli())
      .setUuid(UUID.randomUUID())
      .build();

    Key taskKey = datastore
      .newKeyFactory()
      .setKind("Problem")
      .newKey(problem.getUuid().toString());

    datastore.put(
      Entity
        .newBuilder(taskKey)
        .set("uuid", problem.getUuid().toString())
        .set("name", problem.getName())
        .set(
          "holds",
          Blob.copyFrom(objectMapper.writeValueAsBytes(problem.getHolds()))
        )
        .set("author", problem.getAuthor())
        .set("grade", problem.getGrade())
        .set("createdAt", problem.getCreatedAt())
        .build()
    );

    return problem;
  }

  private void handlePut(HttpRequest request, HttpResponse response)
    throws IOException {
    switch (request.getPath()) {
      case "/holds":
        response
          .getWriter()
          .write(
            objectMapper.writeValueAsString(
              upsertHolds(
                objectMapper.readValue(request.getInputStream(), HOLDS)
              )
            )
          );
        break;
      default:
        response.setStatusCode(404);
        break;
    }
  }

  private List<List<Map<String, Integer>>> upsertHolds(
    List<List<Map<String, Integer>>> holds
  )
    throws JsonProcessingException {
    Key taskKey = datastore.newKeyFactory().setKind("Holds").newKey("default");

    Entity.Builder builder = Entity.newBuilder(taskKey);

    for (int i = 0; i < holds.size(); i++) {
      builder.set(
        "hold" + i,
        Blob.copyFrom(objectMapper.writeValueAsBytes(holds.get(i)))
      );
    }

    datastore.put(builder.build());

    return holds;
  }

  private void handleGet(HttpRequest request, HttpResponse response)
    throws IOException {
    switch (request.getPath()) {
      case "/problems":
        response
          .getWriter()
          .write(objectMapper.writeValueAsString(getProblems()));
        break;
      case "/holds":
        response.getWriter().write(objectMapper.writeValueAsString(getHolds()));
        break;
      default:
        response.setStatusCode(404);
    }
  }

  private List<List<Map<String, Integer>>> getHolds() throws IOException {
    Entity entity = datastore.get(
      datastore.newKeyFactory().setKind("Holds").newKey("default")
    );

    if (entity == null) {
      return Collections.emptyList();
    }

    return entity
      .getNames()
      .stream()
      .map(
        name -> {
          try {
            List<Map<String, Integer>> hold = objectMapper.readValue(
              entity.getBlob(name).asInputStream(),
              HOLD
            );
            return hold;
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      )
      .collect(Collectors.toList());
  }

  private List<Problem> getProblems() throws IOException {
    QueryResults<Entity> results = datastore.run(
      Query
        .newEntityQueryBuilder()
        .setLimit(100)
        .setOffset(0)
        .setKind("Problem")
        .build()
    );

    ArrayList<Problem> problems = new ArrayList<>();
    while (results.hasNext()) {
      Entity entity = results.next();
      problems.add(
        Problem
          .builder()
          .setUuid(UUID.fromString(entity.getString("uuid")))
          .setName(entity.getString("name"))
          .setAuthor(entity.getString("author"))
          .setGrade(entity.getString("grade"))
          .setCreatedAt(entity.getLong("createdAt"))
          .setHolds(
            objectMapper.readValue(
              entity.getBlob("holds").asInputStream(),
              HOLDS
            )
          )
          .build()
      );
    }
    return problems;
  }

  private void handleOptions(HttpRequest request, HttpResponse response) {
    response.appendHeader(
      "Access-Control-Allow-Methods",
      "POST, PUT, GET, OPTIONS, DELETE"
    );
    response.appendHeader("Access-Control-Allow-Headers", "Content-Type");
    response.appendHeader("Access-Control-Max-Age", "3600");
    response.setStatusCode(HttpURLConnection.HTTP_NO_CONTENT);
  }
}
