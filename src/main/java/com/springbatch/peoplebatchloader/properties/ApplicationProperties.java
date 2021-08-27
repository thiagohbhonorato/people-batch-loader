package com.springbatch.peoplebatchloader.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

  private final Batch batch = new Batch();

  public Batch getBatch() {
    return this.batch;
  }

  public static class Batch {
    private String inputPath = "C:\\spring-batch-loader-file-data";

    public String getInputPath() {
      return this.inputPath;
    }

    public void setInputPath(String inputPath) {
      this.inputPath = inputPath;
    }
  }
}
