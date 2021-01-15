package com.scottw.homewall.resources;

import javax.ws.rs.Path;

@Path("test")
public class TestResource {

  public String test() {
    return "test";
  }
}
