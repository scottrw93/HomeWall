package com.scottw.homewall.resources;

import java.util.Collections;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("test")
public class HomeWallApp extends Application {

  @Override
  public Set<Object> getSingletons() {
    return Collections.singleton(new TestResource());
  }

  @Override
  public Set<Class<?>> getClasses() {
    return Collections.singleton(TestResource.class);
  }
}
