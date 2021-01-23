package com.scottw.homewall.core.wall;

import com.scottw.homewall.core.OurStyle;
import org.immutables.value.Value;

@OurStyle
@Value.Immutable
public abstract class AbstractPoint {

  public abstract int getX();

  public abstract int getY();
}
