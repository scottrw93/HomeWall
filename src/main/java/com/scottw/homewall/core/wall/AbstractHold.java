package com.scottw.homewall.core.wall;

import com.scottw.homewall.core.OurStyle;
import java.util.List;
import org.immutables.value.Value;

@OurStyle
@Value.Immutable
public abstract class AbstractHold {

  @Value.Default
  public boolean isTagged() {
    return false;
  }

  public abstract List<Point> getPoints();
}
