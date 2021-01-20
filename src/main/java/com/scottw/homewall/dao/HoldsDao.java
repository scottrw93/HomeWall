package com.scottw.homewall.dao;

import static com.scottw.homewall.dao.TypeRefs.HOLD;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HoldsDao {
  private final Datastore datastore;
  private final ObjectMapper objectMapper;

  public HoldsDao() {
    this.datastore = DatastoreFactory.fetch();
    this.objectMapper = new ObjectMapper();
  }

  public void upsertHolds(List<List<Map<String, Integer>>> holds) {
    Key taskKey = datastore.newKeyFactory().setKind("Holds").newKey("default");

    Entity.Builder builder = Entity.newBuilder(taskKey);

    for (int i = 0; i < holds.size(); i++) {
      try {
        builder.set(
          "hold" + i,
          Blob.copyFrom(objectMapper.writeValueAsBytes(holds.get(i)))
        );
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }

    datastore.put(builder.build());
  }

  public List<List<Map<String, Integer>>> getHolds() {
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
}
