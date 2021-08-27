package com.springbatch.peoplebatchloader.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import javax.persistence.EntityManagerFactory;

import com.springbatch.peoplebatchloader.constants.Constants;
import com.springbatch.peoplebatchloader.entity.PeopleEntity;
import com.springbatch.peoplebatchloader.properties.ApplicationProperties;
import com.springbatch.peoplebatchloader.record.PeopleRecord;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

@Configuration
public class BatchJobConfiguration {

  @Autowired
  private JobBuilderFactory jobBuilderFactory;

  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Autowired
  private ApplicationProperties applicationProperties;

  @Autowired
  @Qualifier(value = "batchEntityManagerFactory")
  private EntityManagerFactory batchEntityManagerFactory;

  @Bean
  JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
    JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
    postProcessor.setJobRegistry(jobRegistry);
    return postProcessor;
  }

  @Bean
  public Job job(Step step) throws Exception {
    return this.jobBuilderFactory.get(Constants.JOB_NAME).validator(validator()).start(step).build();
  }

  @Bean
  public JobParametersValidator validator() {
    return new JobParametersValidator() {
      @Override
      public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String fileName = parameters.getString(Constants.JOB_PARAM_FILE_NAME);
        if (StringUtils.isBlank(fileName)) {
          throw new JobParametersInvalidException("The " + Constants.JOB_PARAM_FILE_NAME + " parameter is required.");
        }
        try {
          Path file = Paths.get(applicationProperties.getBatch().getInputPath() + File.separator + fileName);
          if (Files.notExists(file) || !Files.isReadable(file)) {
            throw new Exception("File did not exist or was not readable");
          }
        } catch (Exception e) {
          e.printStackTrace();
          throw new JobParametersInvalidException(
              "The input path + " + Constants.JOB_PARAM_FILE_NAME + " parameter needs to be a valid file location.");
        }
      }
    };
  }

  @Bean
  public Step step(ItemReader<PeopleRecord> itemReader, Function<PeopleRecord, PeopleEntity> processor,
      JpaItemWriter<PeopleEntity> writer) throws Exception {
    return this.stepBuilderFactory.get(Constants.STEP_NAME).<PeopleRecord, PeopleEntity>chunk(2).reader(itemReader)
        .processor(processor).writer(writer).build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<PeopleRecord> reader(
      @Value("#{jobParameters['" + Constants.JOB_PARAM_FILE_NAME + "']}") String fileName) {
    return new FlatFileItemReaderBuilder<PeopleRecord>().name(Constants.ITEM_READER_NAME)
        .resource(
            new PathResource(Paths.get(applicationProperties.getBatch().getInputPath() + File.separator + fileName)))
        .linesToSkip(1).lineMapper(lineMapper()).build();
  }

  @Bean
  public LineMapper<PeopleRecord> lineMapper() {
    DefaultLineMapper<PeopleRecord> mapper = new DefaultLineMapper<>();
    mapper.setFieldSetMapper((fieldSet) -> PeopleRecord.builder().sourceId(fieldSet.readString(0))
        .firstName(fieldSet.readString(1)).lastName(fieldSet.readString(2)).birthDate(fieldSet.readString(3)).build());
    mapper.setLineTokenizer(new DelimitedLineTokenizer());
    return mapper;
  }

  @Bean
  @StepScope
  public Function<PeopleRecord, PeopleEntity> processor() {
    return (peopleRecord) -> {
      return PeopleEntity.builder().sourceId(peopleRecord.getSourceId()).firstName(peopleRecord.getFirstName())
          .lastName(peopleRecord.getLastName())
          .birthDate(LocalDate.parse(peopleRecord.getBirthDate(), DateTimeFormatter.ofPattern("dd/M/yyyy"))).build();
    };
  }

  @Bean
  @StepScope
  public JpaItemWriter<PeopleEntity> writer() {
    JpaItemWriter<PeopleEntity> writer = new JpaItemWriter<>();
    writer.setEntityManagerFactory(batchEntityManagerFactory);
    return writer;
  }
}
