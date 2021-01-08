package com.scottw.homewall.core.problem;

import com.scottw.homewall.core.OurStyle;

import java.util.UUID;
import org.immutables.value.Value;

@OurStyle
@Value.Immutable
public abstract class AbstractProblem extends AbstractProblemRequest {

  public abstract long getCreatedAt();

  public abstract UUID getUuid();
}
