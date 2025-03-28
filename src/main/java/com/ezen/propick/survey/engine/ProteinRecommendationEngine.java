package com.ezen.propick.survey.engine;

import com.ezen.propick.survey.dto.result.SurveyRecommendationResultDTO;
import com.ezen.propick.survey.dto.result.SurveyResultInputDTO;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProteinRecommendationEngine {

    public SurveyRecommendationResultDTO generate(SurveyResultInputDTO input) {
        //1. BMI 계산
        double bmi = calculateBMI(input.getHeightCm(), input.getWeightKg());
        String bmiStatus = classifyBMI(bmi);

        // 2. 건강 요소 우선순위 분리
        List<String> level1 = filterByPriority(input.getHealthConcerns(), 1); // 질병 위험 (신장, 간, 심혈관)
        List<String> level2 = filterByPriority(input.getHealthConcerns(), 2); // 일반 건강 증상 (피로, 소화, 피부)
        List<String> level3 = filterByPriority(input.getHealthConcerns(), 3); // 운동 빈도 / 목적 (근성장, 다이어트 등)

        // 3. 단백질 섭취량 계산
        double[] intake = calculateProteinIntake(input.getPurpose(), input.getWorkoutFreq(), input.getWeightKg(), level1, level3, input.getAge());

        // 4. 추천 및 회피 단백질
        List<String> recommendedTypes = getRecommendedProtein(level1, level2, level3, input.getAge());
        List<String> avoidTypes = getAvoidProtein(level1, level2, level3, input.getAge());

        // 5. 섭취 타이밍
        String timing = getIntakeTiming(input, level1, level2, level3, input.getAge());

        // 6. 경고 메시지
        List<String> warnings = getWarnings(level1, level2, level3, input.getAge());

        // 최종 결과 DTO 생성
        return new SurveyRecommendationResultDTO(
                input.getName(),
                input.getGender(),
                input.getAge(),
                bmi, bmiStatus,
                intake[0], intake[1],
                recommendedTypes, avoidTypes,
                timing, warnings
        );
    }

    //BMI 계산: 몸무게(kg) / (키(m) × 키(m))
    private double calculateBMI(double heightCm, double weightKg) {
        double heightM = heightCm / 100.0;
        //소수점 첫째자리까지 반올림
        return Math.round((weightKg / (heightM * heightM)) * 10.0) / 10.0;
    }

    private String classifyBMI(double bmi) {
        if (bmi < 18.5) return "저체중";
        if (bmi < 23.0) return "정상";
        if (bmi < 25.0) return "과체중";
        return "비만";
    }

    private double[] calculateProteinIntake(String purpose, String freq, double weightKg, List<String> level1, List<String> level3, int age) {
        double min = 1.2, max = 1.6;

        if (level1.contains("신장 질환")) {
            min = 0.6; max = 0.8;
        } else if (level1.contains("간 질환")) {
            min = 1.0; max = 1.2;
        }else if  (level1.contains("심혈관 질환")) {
            min = 0.8; max = 1.0;
        } else if (level3.contains("부종") || level3.contains("피로")) {
            min = max = 1.0;
        } else if (level3.contains("관절염")) {
            min = 1.2; max = 1.6;
        } else {
            switch (purpose) {
                case "근육 증가" -> {
                    switch (freq) {
                        case "주1회" -> { min = 1.6; max = 1.8; }
                        case "주2~3회" -> { min = 1.8; max = 2.0; }
                        case "주4회 이상" -> { min = 2.0; max = 2.2; }
                    }
                }
                case "단백질 보충" -> {
                    min = 1.0; max = 1.5;
                }
                case "식사 대용" -> {
                    min = 1.2; max = 2.0;
                }
            }
        }

        // 연령 추가 로직
        if (age >= 65) { // 노년층
            min = Math.max(min - 0.2, 0.8); // 노년층 단백질 최소량 조정 (소화 고려)
            max = Math.max(max - 0.2, 1.2);
        } else if (age <= 12) { // 어린이
            min = Math.min(min * 0.6, 1.2);
            max = Math.min(max * 0.6, 1.4);
        }
        /*
         * 사용자 체중(kg)에 단백질 g/kg 최소치 곱
         * 예: 60kg × 1.6g/kg = 96g
         * 계산된 값을 반올림해서 소수점 없이 정수로 반환
         * 최소값과 최대값을 배열로 반환
         * */
        return new double[] {
                Math.round(weightKg * min),
                Math.round(weightKg * max)
        };
    }

    private List<String> filterByPriority(List<String> concerns, int level) {
        List<String> level1 = List.of("신장 질환", "간 질환", "심혈관 질환");
        List<String> level2 = List.of("유당불내증", "여드름", "수면장애", "변비", "관절염", "피로", "부종");
        List<String> level3 = List.of("근육 증가", "다이어트");

        return concerns.stream()
                .filter(c -> switch (level) {
                    case 1 -> level1.contains(c);
                    case 2 -> level2.contains(c);
                    case 3 -> level3.contains(c);
                    default -> false;
                }).collect(Collectors.toList());
    }

    private List<String> getRecommendedProtein(List<String> l1, List<String> l2, List<String> l3, int age) {
        Set<String> result = new LinkedHashSet<>();
        if (l1.contains("신장 질환")) result.add("WPI");
        if (l1.contains("간 질환")) result.addAll(List.of("WPI", "ISP"));
        if (l3.contains("근육 증가")) result.addAll(List.of("WPI", "WPH"));
        if (l2.contains("유당불내증")) result.addAll(List.of("WPI", "WPH"));
        if (l2.contains("여드름")) result.addAll(List.of("WPI", "WPH"));
        if (l2.contains("설사")) result.add("WPI");
        if (l2.contains("변비")) result.add("WPH");

        // 연령 추가 로직
        if (age >= 65) result.add("WPH"); // 노년층 소화 쉬운 형태 추가 추천
        if (age <= 12) result.add("ISP"); // 어린이 대두 단백질 추천 (성장 도움)

        return new ArrayList<>(result);
    }

    private List<String> getAvoidProtein(List<String> l1, List<String> l2, List<String> l3, int age) {
        Set<String> result = new LinkedHashSet<>();
        if (l1.contains("간 질환")) result.add("WPH");
        if (l2.contains("유당불내증")) result.add("WPC");
        if (l2.contains("여드름")) result.addAll(List.of("WPC", "카제인"));
        if (l2.contains("수면장애")) result.add("취침 전 고단백");
        if (l2.contains("대두 알레르기")) result.add("ISP");

        // 연령 추가 로직
        if (age >= 65) result.add("카제인"); // 노년층은 소화 느린 단백질 회피
        if (age <= 12) result.addAll(List.of("WPH", "카제인")); // 어린이는 소화 어려운 단백질 회피

        return new ArrayList<>(result);
    }

    private String getIntakeTiming(SurveyResultInputDTO input, List<String> l1, List<String> l2, List<String> l3, int age) {
        if (l1.contains("간 질환")) return "점심 시간 섭취";
        if (l2.contains("수면장애")) return "취침 3시간 전 이전";
        if (l2.contains("변비")) return "기상 후 공복 섭취";
        if (l2.contains("관절염")) return "운동 직후 (비타민D 포함)";
        if ("근육 증가".equals(input.getPurpose())) {
            if ("주4회 이상".equals(input.getWorkoutFreq())) return "운동 후 즉시 및 취침 전";
            if ("주2~3회".equals(input.getWorkoutFreq())) return "운동 직후 30분 이내";
            return "운동 후 1시간 이내";
        }
        if (input.getPurpose().equals("다이어트")) return "식사 대용 또는 아침";
        return "아침 또는 운동 직후";
    }

    private List<String> getWarnings(List<String> l1, List<String> l2, List<String> l3, int age) {
        List<String> msg = new ArrayList<>();
        if (l1.contains("신장 질환")) msg.add("단백질 섭취량을 0.6~0.8g/kg 이하로 제한하세요.");
        if (l1.contains("간 질환")) msg.add("과도한 단백질 섭취는 간에 부담이 될 수 있습니다.");
        if (l2.contains("유당불내증")) msg.add("WPC 등 유당이 포함된 제품은 피하세요.");
        if (l2.contains("여드름")) msg.add("WPC, 카제인은 여드름을 유발할 수 있습니다.");
        if (l2.contains("수면장애")) msg.add("취침 3시간 전 이후 단백질 섭취는 피해주세요.");
        if (l2.contains("설사")) msg.add("WPC 등 유당 함유 단백질을 피해주세요.");
        if (l2.contains("소화불량")) msg.add("소화 흡수 속도가 빠른 WPH 형태를 추천합니다.");

        // 연령별 추가 메시지
        if (age >= 65) msg.add("고령자의 경우 소화 흡수가 쉬운 WPH 형태의 단백질을 권장합니다.");
        if (age <= 12) msg.add("어린이의 경우 성인의 절반 정도의 단백질 섭취가 적절합니다. ISP 등 소화 부담이 적은 제품을 추천합니다.");

        return msg;

    }
}
