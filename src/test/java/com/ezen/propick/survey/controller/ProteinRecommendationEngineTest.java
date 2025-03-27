package com.ezen.propick.survey.engine;

import com.ezen.propick.survey.dto.result.SurveyRecommendationResultDTO;
import com.ezen.propick.survey.dto.result.SurveyResultInputDTO;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProteinRecommendationEngineTest {

    private final ProteinRecommendationEngine engine = new ProteinRecommendationEngine();

    static Stream<TestCase> provideTestCases() {
        return Stream.of(
                new TestCase("다이어트", "주1회", 160, 50, List.of("유당불내증"), 19.5, "정상", 60, 80,
                        List.of("WPI"), List.of("WPC"), List.of("WPC 등 유당이 포함된 제품은 피하세요.")),

                new TestCase("근육 증가", "주2~3회", 175, 70, List.of("여드름"), 22.9, "정상", 126, 140,
                        List.of("WPI", "WPH"), List.of("WPC", "카제인"), List.of("WPC, 카제인은 여드름을 유발할 수 있습니다.")),

                new TestCase("단백질 보충", "주1회", 180, 90, List.of("간 질환"), 27.8, "비만", 90, 108,
                        List.of("WPI", "ISP"), List.of("WPH"), List.of("과도한 단백질 섭취는 간에 부담이 될 수 있습니다."))
        );
    }

    @ParameterizedTest(name = "Test case #{index} - {0}")
    @MethodSource("provideTestCases")
    void testEngineRecommendations(TestCase tc) {
        SurveyResultInputDTO input = SurveyResultInputDTO.builder()
                .heightCm(tc.height)
                .weightKg(tc.weight)
                .purpose(tc.purpose)
                .workoutFreq(tc.freq)
                .healthConcerns(tc.concerns)
                .build();

        SurveyRecommendationResultDTO result = engine.generate(input);

        assertEquals(tc.expectedBmi, result.getBmi(), 0.1);
        assertEquals(tc.expectedBmiStatus, result.getBmiStatus());
        assertEquals(tc.minIntake, result.getMinIntakeGram());
        assertEquals(tc.maxIntake, result.getMaxIntakeGram());
        tc.expectedRecommendedTypes.forEach(type -> assertTrue(result.getRecommendedTypes().contains(type)));
        tc.expectedAvoidTypes.forEach(type -> assertTrue(result.getAvoidTypes().contains(type)));
        tc.expectedWarnings.forEach(msg -> assertTrue(result.getWarningMessages().contains(msg)));
    }

    private record TestCase(
            String purpose,
            String freq,
            int height,
            int weight,
            List<String> concerns,
            double expectedBmi,
            String expectedBmiStatus,
            int minIntake,
            int maxIntake,
            List<String> expectedRecommendedTypes,
            List<String> expectedAvoidTypes,
            List<String> expectedWarnings
    ) {}
}