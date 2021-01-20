package com.scottw.homewall.dao;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;

public class DatastoreFactory {

  static {
    System.setProperty("GOOGLE_CLOUD_PROJECT", "homewall-301021");
  }

  private static final Datastore datastore = DatastoreOptions
    .newBuilder()
    .build()
    .getService();

  private DatastoreFactory() {}

  protected static Datastore fetch() {
    return datastore;
  }
}
