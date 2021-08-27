package com.springbatch.peoplebatchloader.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@EnableBatchProcessing
public class BatchConfiguration implements BatchConfigurer {

  private JobRepository jobRepository;
  private JobExplorer jobExplorer;
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(value = "batchTransactionManager")
  private PlatformTransactionManager batchTransactionManager;

  @Autowired
  @Qualifier(value = "batchDataSource")
  private DataSource batchDataSource;

  @Override
  public JobRepository getJobRepository() throws Exception {
    return this.jobRepository;
  }

  @Override
  public JobLauncher getJobLauncher() throws Exception {
    return this.jobLauncher;
  }

  @Override
  public JobExplorer getJobExplorer() throws Exception {
    return this.jobExplorer;
  }

  @Override
  public PlatformTransactionManager getTransactionManager() throws Exception {
    return this.batchTransactionManager;
  }

  protected JobRepository createJobRepository() throws Exception {
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setDataSource(this.batchDataSource);
    factory.setTransactionManager(getTransactionManager());
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  protected JobLauncher createJobLauncher() throws Exception {
    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
    jobLauncher.setJobRepository(this.jobRepository);
    // jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
    jobLauncher.afterPropertiesSet();
    return jobLauncher;
  }

  @PostConstruct
  public void afterPropertiesSet() throws Exception {
    this.jobRepository = createJobRepository();
    JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
    jobExplorerFactoryBean.setDataSource(this.batchDataSource);
    jobExplorerFactoryBean.afterPropertiesSet();
    this.jobExplorer = jobExplorerFactoryBean.getObject();
    this.jobLauncher = createJobLauncher();
  }
}
