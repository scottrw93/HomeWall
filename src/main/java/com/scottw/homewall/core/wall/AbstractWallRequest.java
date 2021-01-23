package com.scottw.homewall.core.wall;

import com.scottw.homewall.core.OurStyle;
import java.net.URI;
import java.util.List;
import org.immutables.value.Value;

@OurStyle
@Value.Immutable
public abstract class AbstractWallRequest {

  public abstract String getName();

  public abstract List<Hold> getHolds();

  public abstract URI getImage();
}
