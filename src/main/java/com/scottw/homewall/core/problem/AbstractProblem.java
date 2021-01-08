package com.scottw.homewall.core.problem;

import com.scottw.homewall.core.MyStyle;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.immutables.value.Value;

@MyStyle
@Value.Immutable
public abstract class AbstractProblem extends AbstractProblemRequest {

  public abstract long getCreatedAt();

  public abstract UUID getUuid();
}
