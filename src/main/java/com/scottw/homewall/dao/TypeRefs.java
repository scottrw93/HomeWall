package com.scottw.homewall.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.scottw.homewall.core.wall.Hold;
import java.util.List;
import java.util.Map;

public class TypeRefs {
  public static final TypeReference<List<Hold>> HOLDS = new TypeReference<>() {};

  private TypeRefs() {}
}
