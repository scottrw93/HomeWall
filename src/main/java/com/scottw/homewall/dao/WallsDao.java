package com.scottw.homewall.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.*;
import com.scottw.homewall.core.wall.Hold;
import com.scottw.homewall.core.wall.Point;
import com.scottw.homewall.core.wall.Wall;
import com.scottw.homewall.core.wall.WallRequest;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class WallsDao {
  private final Datastore datastore;
  private final ObjectMapper objectMapper;

  public WallsDao() {
    this.datastore = DatastoreFactory.fetch();
    this.objectMapper = new ObjectMapper();
  }

  public Wall createWall(WallRequest wallRequest) {
    UUID uuid = UUID.randomUUID();
    Key taskKey = datastore
      .newKeyFactory()
      .setKind("Wall")
      .newKey(uuid.toString());

    Entity.Builder builder = Entity
      .newBuilder(taskKey)
      .set("image", wallRequest.getImage().toString())
      .set("name", wallRequest.getName())
      .set("createdAt", Instant.now().toEpochMilli());

    for (int i = 0; i < wallRequest.getHolds().size(); i++) {
      try {
        builder.set(
          "hold" + i,
          Blob.copyFrom(
            objectMapper.writeValueAsBytes(wallRequest.getHolds().get(i))
          )
        );
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }

    datastore.put(builder.build());

    return Wall.builder().from(wallRequest).setUuid(uuid).build();
  }

  public List<Wall> getWalls() {
    QueryResults<Entity> results = datastore.run(
      Query
        .newEntityQueryBuilder()
        .setLimit(100)
        .setOffset(0)
        .setKind("Wall")
        .build()
    );

    ArrayList<Wall> walls = new ArrayList<>();
    while (results.hasNext()) {
      Entity entity = results.next();

      walls.add(
        Wall
          .builder()
          .setUuid(UUID.fromString(entity.getKey().getName()))
          .setImage(URI.create(entity.getString("image")))
          .setHolds(extractHolds(entity))
          .setName(entity.getString("name"))
          .build()
      );
    }
    return List.copyOf(walls);
  }

  public Optional<Wall> getWall(UUID uuid) {
    Key taskKey = datastore
      .newKeyFactory()
      .setKind("Wall")
      .newKey(uuid.toString());

    Optional<Entity> maybeEntity = Optional.ofNullable(datastore.get(taskKey));

    return maybeEntity.map(
      entity ->
        Wall
          .builder()
          .setUuid(UUID.fromString(entity.getKey().getName()))
          .setImage(URI.create(entity.getString("image")))
          .setHolds(extractHolds(entity))
          .setName(entity.getString("name"))
          .build()
    );
  }

  private Iterable<Hold> extractHolds(Entity entity) {
    return entity
      .getNames()
      .stream()
      .filter(name -> name.startsWith("hold"))
      .map(
        name -> {
          try {
            Hold hold = objectMapper.readValue(
              entity.getBlob(name).asInputStream(),
              Hold.class
            );
            return hold;
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      )
      .collect(Collectors.toList());
  }
}
