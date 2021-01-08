package com.scottw.homewall;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.Credentials;
import com.google.cloud.datastore.*;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.scottw.homewall.core.problem.Problem;
import com.scottw.homewall.core.problem.ProblemRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HomeWall implements HttpFunction {

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

    switch (request.getMethod()) {
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
    throws IOException {}

  private void handleGet(HttpRequest request, HttpResponse response)
    throws IOException {
    switch (request.getPath()) {
      case "/problems":
        response
          .getWriter()
          .write(objectMapper.writeValueAsString(getProblems()));
        break;
      default:
        response.setStatusCode(404);
    }
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
              new TypeReference<List<List<Map<String, Integer>>>>() {}
            )
          )
          .build()
      );
    }
    return problems;
  }

  private void handleOptions(HttpRequest request, HttpResponse response) {
    response.appendHeader("Access-Control-Allow-Methods", "GET");
    response.appendHeader("Access-Control-Allow-Headers", "Content-Type");
    response.appendHeader("Access-Control-Max-Age", "3600");
    response.setStatusCode(HttpURLConnection.HTTP_NO_CONTENT);
  }
}
