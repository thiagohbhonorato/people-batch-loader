package com.springbatch.peoplebatchloader.record;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PeopleRecord implements Serializable {

  private String sourceId;
  private String firstName;
  private String lastName;
  private String birthDate;
}
