package com.ezen.propick.survey.dto.survey;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerDTO {
    private Integer questionId;
    private List<Integer> selectedOptionIds;
}

