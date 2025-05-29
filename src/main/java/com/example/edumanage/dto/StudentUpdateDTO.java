package com.example.edumanage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentUpdateDTO {
    private String name;
    private Integer gender;
    private String phone;
    private String idCard;
    private String address;
    private String highestDegree;
    private LocalDate graduationDate;
    private String classId;
}