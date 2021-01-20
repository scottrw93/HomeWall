package com.scottw.homewall.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

public class TypeRefs {
  public static final TypeReference<List<List<Map<String, Integer>>>> HOLDS = new TypeReference<>() {};
  public static final TypeReference<List<Map<String, Integer>>> HOLD = new TypeReference<>() {};

  private TypeRefs() {}
}
