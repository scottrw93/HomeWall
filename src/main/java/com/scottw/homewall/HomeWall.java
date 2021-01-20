package com.scottw.homewall;

import static com.scottw.homewall.dao.TypeRefs.HOLDS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.*;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.scottw.homewall.core.problem.Problem;
import com.scottw.homewall.core.problem.ProblemRequest;
import com.scottw.homewall.dao.HoldsDao;
import com.scottw.homewall.dao.ProblemsDao;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class HomeWall implements HttpFunction {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ProblemsDao problemsDao;
  private final HoldsDao holdsDao;

  public HomeWall() {
    this.problemsDao = new ProblemsDao();
    this.holdsDao = new HoldsDao();
  }

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
      case "DELETE":
        handleDelete(request, response);
        break;
      default:
        response.setStatusCode(405);
        break;
    }
  }

  private void handleDelete(HttpRequest request, HttpResponse response) {
    String path = request.getPath();
    if (path.startsWith("/problems/")) {
      if (path.split("/").length == 3) {
        String uuid = path.split("/")[2].trim();
        problemsDao.deleteProblem(UUID.fromString(uuid));
        response.setStatusCode(204);
        return;
      }
    }
    response.setStatusCode(404);
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

  private Problem createProblem(ProblemRequest problemRequest) {
    Problem problem = Problem
      .builder()
      .from(problemRequest)
      .setCreatedAt(Instant.now().toEpochMilli())
      .setUuid(UUID.randomUUID())
      .build();

    problemsDao.createProblem(
      Problem
        .builder()
        .from(problemRequest)
        .setCreatedAt(Instant.now().toEpochMilli())
        .setUuid(UUID.randomUUID())
        .build()
    );

    return problem;
  }

  private List<List<Map<String, Integer>>> upsertHolds(
    List<List<Map<String, Integer>>> holds
  ) {
    holdsDao.upsertHolds(holds);

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
    return holdsDao.getHolds();
  }

  private List<Problem> getProblems() throws IOException {
    return problemsDao.getProblems();
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
