package com.scottw.homewall.core.problem;

import com.scottw.homewall.core.MyStyle;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;

@MyStyle
@Value.Immutable
public abstract class AbstractProblemRequest {

  public abstract String getName();

  public abstract String getGrade();

  public abstract String getAuthor();

  public abstract List<List<Map<String, Integer>>> getHolds();
}
