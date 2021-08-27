package com.springbatch.peoplebatchloader.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.springbatch.peoplebatchloader.PeopleBatchLoaderApplication;
import com.springbatch.peoplebatchloader.constants.Constants;
import com.springbatch.peoplebatchloader.entity.PeopleEntity;
import com.springbatch.peoplebatchloader.record.PeopleRecord;
import com.springbatch.peoplebatchloader.repository.PeopleRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PeopleBatchLoaderApplication.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@Transactional
public class BatchJobConfigurationTest {

  @Autowired
  private Job job;

  @Autowired
  private FlatFileItemReader<PeopleRecord> reader;

  private JobParameters jobParameters;

  @Autowired
  private Function<PeopleRecord, PeopleEntity> processor;

  @Autowired
  private JpaItemWriter<PeopleEntity> writer;

  @Autowired
  private PeopleRepository peopleRepository;

  @Before
  public void setUp() {
    Map<String, JobParameter> params = new HashMap<>();
    params.put(Constants.JOB_PARAM_FILE_NAME, new JobParameter("people-unit-testing.csv"));
    jobParameters = new JobParameters(params);
  }

  @Test
  public void test() {
    assertNotNull(job);
    assertEquals(Constants.JOB_NAME, job.getName());
  }

  @Test
  public void testReader() throws Exception {
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
    int count = 0;
    try {
      count = StepScopeTestUtils.doInStepScope(stepExecution, () -> {
        int numPeople = 0;
        PeopleRecord people;
        try {
          reader.open(stepExecution.getExecutionContext());
          while ((people = reader.read()) != null) {
            assertNotNull(people);
            assertEquals("c4ca4238a0b923820dcc509a6f75849b", people.getSourceId());
            assertEquals("Alice", people.getFirstName());
            assertEquals("Manuela", people.getLastName());
            assertEquals("01/01/1990", people.getBirthDate());
            numPeople++;
          }
        } finally {
          try {
            reader.close();
          } catch (Exception e) {
            fail(e.toString());
          }
        }
        return numPeople;
      });
    } catch (Exception e) {
      fail(e.toString());
    }
    assertEquals(1, count);
  }

  @Test
  public void testProcessor() throws Exception {
    PeopleRecord peopleRecord = new PeopleRecord("c81e728d9d4c2f636f067f89cc14862c", "Sophia", "Valentina",
        "02/01/1990");
    PeopleEntity entity = processor.apply(peopleRecord);
    assertNotNull(entity);
    assertEquals("c81e728d9d4c2f636f067f89cc14862c", entity.getSourceId());
    assertEquals("Sophia", entity.getFirstName());
    assertEquals("Valentina", entity.getLastName());
    assertEquals(2, entity.getBirthDate().getDayOfMonth());
    assertEquals(1, entity.getBirthDate().getMonthValue());
    assertEquals(1990, entity.getBirthDate().getYear());
  }

  @Test
  public void testWriter() throws Exception {
    PeopleEntity entity = PeopleEntity.builder().sourceId("c81e728d9d4c2f636f067f89cc14862c").firstName("Sophia")
        .lastName("Valentina").birthDate(LocalDate.of(1990, 1, 2)).build();
    StepExecution execution = MetaDataInstanceFactory.createStepExecution();
    StepScopeTestUtils.doInStepScope(execution, () -> {
      writer.write(Arrays.asList(entity));
      return null;
    });
    assertTrue(peopleRepository.findAll().size() > 0);
  }
}
