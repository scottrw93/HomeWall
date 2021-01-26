package com.scottw.homewall.core.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scottw.homewall.core.OurStyle;
import com.scottw.homewall.core.wall.Hold;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.immutables.value.Value;

@OurStyle
@Value.Immutable
@JsonDeserialize(as = ProblemRequest.class)
public abstract class AbstractProblemRequest {

  public abstract String getName();

  public abstract String getGrade();

  public abstract String getAuthor();

  public abstract UUID getWallUuid();

  public abstract List<Hold> getHolds();
}
