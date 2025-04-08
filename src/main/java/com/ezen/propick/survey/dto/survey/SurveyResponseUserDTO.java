package com.ezen.propick.survey.dto.survey;

import com.ezen.propick.survey.entity.SurveyResponse;
import com.ezen.propick.survey.entity.SurveyResponseOption;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseUserDTO {
    private Integer responseId;
    private String surveyTitle;
    private String responseDate;
    private String purpose;
    private List<String> mainConcerns;

    // 📌 정적 팩토리 메서드
    public static SurveyResponseUserDTO fromEntity(SurveyResponse entity) {
        String purpose = null;
        List<String> concerns = new ArrayList<>();

        for (SurveyResponseOption option : entity.getSurveyResponseOptions()) {
            if (option.getOption() != null) {
                int questionId = option.getQuestion().getQuestionId();
                String text = option.getOption().getOptionText();

                if (questionId == 6) {
                    purpose = text;
                } else if (questionId == 8) {
                    concerns.add(text);
                }
            }
        }

        return SurveyResponseUserDTO.builder()
                .responseId(entity.getResponseId())
                .surveyTitle(entity.getSurveyId().getSurveyTitle())
                .responseDate(entity.getResponseDate().toLocalDate().toString())
                .purpose(purpose)
                .mainConcerns(concerns)
                .build();
    }
}