package com.ezen.propick.survey.dto.result;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SurveyResultInputDTO {
    private double heightCm;
    private double weightKg;
    private String purpose; // ex. "근육 증가"
    private String workoutFreq; // ex. "주2~3회"
    private List<String> healthConcerns; // ex. ["간 질환", "수면장애"]
    private int age;
}
