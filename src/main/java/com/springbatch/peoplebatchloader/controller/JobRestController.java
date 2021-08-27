package com.springbatch.peoplebatchloader.controller;

import java.util.HashMap;
import java.util.Map;

import com.springbatch.peoplebatchloader.constants.Constants;
import com.springbatch.peoplebatchloader.response.JobResponse;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobRestController {

  private final JobLauncher jobLauncher;
  private final Job job;

  public JobRestController(JobLauncher jobLauncher, Job job) {
    this.jobLauncher = jobLauncher;
    this.job = job;
  }

  @GetMapping("/file/{fileName:.+}")
  public ResponseEntity<JobResponse> runJobFileLoader(@PathVariable String fileName) {
    Map<String, JobParameter> parameterMap = new HashMap<>();
    parameterMap.put(Constants.JOB_PARAM_FILE_NAME, new JobParameter(fileName));
    try {
      jobLauncher.run(job, new JobParameters(parameterMap));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new JobResponse("error", "Failure: " + e.getMessage()));
    }
    return ResponseEntity.ok(new JobResponse("success", "File loaded successfully"));
  }
}
