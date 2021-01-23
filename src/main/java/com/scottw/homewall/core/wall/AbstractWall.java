package com.scottw.homewall.core.wall;

import com.scottw.homewall.core.OurStyle;
import java.util.UUID;
import org.immutables.value.Value;

@OurStyle
@Value.Immutable
public abstract class AbstractWall extends AbstractWallRequest {

  public abstract UUID getUuid();
}
