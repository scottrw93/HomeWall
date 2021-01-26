package com.scottw.homewall;

public class HomeWall {
  //
  //  static {
  //    System.setProperty("GOOGLE_CLOUD_PROJECT", "homewall-301021");
  //  }
  //
  //  private final ObjectMapper objectMapper = new ObjectMapper();
  //  private final ProblemsDao problemsDao;
  //  private final WallsDao wallsDao;
  //
  //  public HomeWall() {
  //    this.problemsDao = new ProblemsDao();
  //    this.wallsDao = new WallsDao();
  //  }
  //
  //  @Override
  //  public void service(HttpRequest request, HttpResponse response)
  //    throws Exception {
  //
  //    response.appendHeader("Access-Control-Allow-Origin", "*");
  //    response.appendHeader("Content-Type", "application/json");
  //
  //    String method = request.getMethod();
  //    switch (method) {
  //      case "OPTIONS":
  //        handleOptions(request, response);
  //        break;
  //      case "GET":
  //        handleGet(request, response);
  //        break;
  //      case "PUT":
  //        break;
  //      case "POST":
  //        handlePost(request, response);
  //        break;
  //      case "DELETE":
  //        handleDelete(request, response);
  //        break;
  //      default:
  //        response.setStatusCode(405);
  //        break;
  //    }
  //  }
  //
  //  private void handleDelete(HttpRequest request, HttpResponse response) {
  //    String path = request.getPath();
  //    if (path.startsWith("/problems/")) {
  //      if (path.split("/").length == 3) {
  //        String uuid = path.split("/")[2].trim();
  //        problemsDao.deleteProblem(UUID.fromString(uuid));
  //        response.setStatusCode(204);
  //        return;
  //      }
  //    }
  //    response.setStatusCode(404);
  //  }
  //
  //  private void handlePost(HttpRequest request, HttpResponse response)
  //    throws IOException {
  //    switch (request.getPath()) {
  //      case "/images":
  //        try (InputStream is = request.getInputStream()) {
  //          response
  //            .getWriter()
  //            .write(
  //              objectMapper.writeValueAsString(
  //                Map.of(
  //                  "src",
  //                  UploadWallImage.uploadImage(
  //                    is,
  //                    request.getContentType().orElseThrow()
  //                  )
  //                )
  //              )
  //            );
  //        }
  //        break;
  //      case "/problems":
  //        response
  //          .getWriter()
  //          .write(
  //            objectMapper.writeValueAsString(
  //              createProblem(
  //                objectMapper.readValue(
  //                  request.getInputStream(),
  //                  ProblemRequest.class
  //                )
  //              )
  //            )
  //          );
  //        break;
  //      case "/walls":
  //        response
  //          .getWriter()
  //          .write(
  //            objectMapper.writeValueAsString(
  //              createWall(
  //                objectMapper.readValue(
  //                  request.getInputStream(),
  //                  WallRequest.class
  //                )
  //              )
  //            )
  //          );
  //        break;
  //      default:
  //        response.setStatusCode(404);
  //        break;
  //    }
  //  }
  //
  //  private Wall createWall(WallRequest wallRequest) {
  //    return wallsDao.createWall(wallRequest);
  //  }
  //
  //  private Problem createProblem(ProblemRequest problemRequest) {
  //    return problemsDao.createProblem(problemRequest);
  //  }
  //
  //  private void handleGet(HttpRequest request, HttpResponse response)
  //    throws IOException {
  //    switch (request.getPath()) {
  //      case "/problems":
  //        response
  //          .getWriter()
  //          .write(objectMapper.writeValueAsString(getProblems()));
  //        break;
  //      case "/walls":
  //        response.getWriter().write(objectMapper.writeValueAsString(getWalls()));
  //        break;
  //      default:
  //        response.setStatusCode(404);
  //    }
  //  }
  //
  //  private List<Wall> getWalls() {
  //    return wallsDao.getWalls();
  //  }
  //
  //  private List<Problem> getProblems() throws IOException {
  //    return problemsDao.getProblems();
  //  }
  //
  //  private void handleOptions(HttpRequest request, HttpResponse response) {
  //    response.appendHeader(
  //      "Access-Control-Allow-Methods",
  //      "POST, PUT, GET, OPTIONS, DELETE"
  //    );
  //    response.appendHeader("Access-Control-Allow-Headers", "Content-Type");
  //    response.appendHeader("Access-Control-Max-Age", "3600");
  //    response.setStatusCode(HttpURLConnection.HTTP_NO_CONTENT);
  //  }
}
