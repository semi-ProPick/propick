package com.ezen.propick.survey.engine;

import com.ezen.propick.survey.dto.result.SurveyRecommendationResultDTO;
import com.ezen.propick.survey.dto.result.SurveyResultInputDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProteinRecommendationEngine {

    public SurveyRecommendationResultDTO generate(SurveyResultInputDTO input) {
        double bmi = calculateBMI(input.getHeightCm(), input.getWeightKg());
        String bmiStatus = classifyBMI(bmi);

        List<String> allConcerns = new ArrayList<>(input.getHealthConcerns().keySet());
        if (input.getPurpose() != null && !input.getPurpose().isBlank()) {
            allConcerns.add(input.getPurpose());
        }
        List<String> level1 = filterByPriority(allConcerns, input.getPurpose(), 1);
        List<String> level2 = filterByPriority(allConcerns, input.getPurpose(), 2);
        List<String> level3 = filterByPriority(allConcerns, input.getPurpose(), 3);


        double[] intake = calculateProteinIntake(input.getPurpose(), input.getWorkoutFreq(), input.getWeightKg(), level1, level2, input.getAge());

        List<String> recommendedTypes = getRecommendedProtein(level1, level2, level3, input.getAge());
        List<String> avoidTypes = getAvoidProtein(level1, level2, input.getAge());
        recommendedTypes.removeIf(avoidTypes::contains);
        String timing = getIntakeTiming(input, level1, level2, level3, input.getAge());
        Map<String, Integer> timingRatio = calculateTimingRatio(input, level1, level2, level3, input.getAge());

        List<String> warnings = getWarnings(level1, level2, level3, input.getAge());
        Map<String, Integer> proteinRecommendationStats = getRecommendedProteinMap(level1, level2, level3, input.getAge());

        return new SurveyRecommendationResultDTO(
                input.getName(),
                input.getGender(),
                input.getAge(),
                bmi, bmiStatus,
                intake[0], intake[1],
                recommendedTypes, avoidTypes,
                timing, warnings,
                input.getHealthConcerns(),
                proteinRecommendationStats,
                timingRatio
        );
    }

    private double calculateBMI(double heightCm, double weightKg) {
        double heightM = heightCm / 100.0;
        return Math.round((weightKg / (heightM * heightM)) * 10.0) / 10.0;
    }

    private String classifyBMI(double bmi) {
        if (bmi < 18.5) return "저체중";
        if (bmi < 23.0) return "정상";
        if (bmi < 25.0) return "과체중";
        return "비만";
    }

    private double[] calculateProteinIntake(String purpose, String freq, double weightKg, List<String> level1, List<String> level2, int age) {
        double min = 1.2, max = 1.6;

        if (level1.contains("KIDNEY")) {
            min = 0.6; max = 0.8;
        } else if (level1.contains("LIVER")) {
            min = 1.0; max = 1.2;
        } else if (level1.contains("CARDIO")) {
            min = 0.8; max = 1.0;
        } else if (level2.contains("TIRED") || level2.contains("SWELLING")) {
            min = max = 1.0;
        } else if (level2.contains("ARTHRITIS")) {
            min = 1.2; max = 1.6;
        } else {
            switch (purpose) {
                case "MUSCLE" -> { min = 1.6; max = 2.0; }
                case "PROTEIN_SUPPORT" -> { min = 1.0; max = 1.5; }
                case "DIET" -> { min = 1.2; max = 1.6; }
                case "MEAL" -> { min = 1.2; max = 2.0; }
            }

            switch (freq) {
                case "LOW" -> { max -= 0.2; }
                case "MID" -> { min += 0.1; max += 0.1; }
                case "HIGH" -> { min += 0.2; max += 0.2; }
            }
        }

        if (!level1.contains("KIDNEY")) {
            if (age <= 12) {
                // 유아/어린이: 전체 섭취량 제한
                min = Math.min(min * 0.5, 1.0);
                max = Math.min(max * 0.5, 1.2);
            } else if (age <= 18) {
                // 청소년 성장기: max 증가 가능
                max = Math.min(max * 1.2, 2.4);
            } else if (age >= 65) {
                // 고령자: max 제한, 소화 부담 고려
                min = Math.max(min - 0.2, 0.8);
                max = Math.min(max - 0.2, 1.2);
            }
        }

        return new double[] {
                Math.round(weightKg * min),
                Math.round(weightKg * max)
        };
    }

    private List<String> filterByPriority(List<String> concerns, String purpose, int level) {
        List<String> level1 = List.of("KIDNEY", "LIVER", "CARDIO");
        List<String> level2 = List.of("CONSTIPATION", "TIRED", "SLEEP", "DIARRHEA", "LACTOSE", "ACNE", "ALLERGY", "DIGESTION", "ARTHRITIS");
        List<String> level3 = List.of("MUSCLE", "DIET", "MEAL", "PROTEIN_SUPPORT");

        List<String> fullList = new ArrayList<>(concerns);
        if (purpose != null && !purpose.isBlank()) {
            fullList.add(purpose);
        }

        return fullList.stream()
                .filter(c -> switch (level) {
                    case 1 -> level1.contains(c);
                    case 2 -> level2.contains(c);
                    case 3 -> level3.contains(c);
                    default -> false;
                }).collect(Collectors.toList());
    }

    private List<String> getRecommendedProtein(List<String> l1, List<String> l2, List<String> l3, int age) {
        Set<String> result = new LinkedHashSet<>();

        if (l1.contains("KIDNEY")) result.add("WPI");
        if (l1.contains("LIVER")) result.addAll(List.of("WPI", "ISP"));
        if (l2.contains("LACTOSE")) result.addAll(List.of("WPI", "WPH"));
        if (l2.contains("ACNE")) result.addAll(List.of("WPI", "WPH"));
        if (l2.contains("DIARRHEA")) result.addAll(List.of("WPI", "ISP"));
        if (l2.contains("CONSTIPATION")) result.addAll(List.of("WPH", "ISP"));
        if (l2.contains("SLEEP")) result.addAll(List.of("WPH", "WPI"));
        if (l2.contains("TIRED")) result.add("WPH");
        if (l3.contains("MUSCLE")) result.addAll(List.of("WPI", "WPH"));
        if (l3.contains("MEAL")) result.addAll(List.of("ISP", "CASEIN"));

        // 연령에 따른 추천 단백질 추가
        if (age >= 65) result.add("WPH");
        if (age <= 12) result.add("ISP");

        return new ArrayList<>(result);
    }

    private List<String> getAvoidProtein(List<String> l1, List<String> l2, int age) {
        Set<String> result = new LinkedHashSet<>();
        if (l1.contains("LIVER")) result.add("WPH");
        if (l2.contains("LACTOSE")) result.add("WPC");
        if (l2.contains("ACNE")) result.addAll(List.of("WPC", "CASEIN"));
        if (l2.contains("SLEEP")) result.add("취침 전 고단백");
        if (l2.contains("ALLERGY")) result.add("ISP");
        if (age >= 65) result.add("CASEIN");
        if (age <= 12) result.addAll(List.of("WPH", "CASEIN"));
        return new ArrayList<>(result);
    }

    private String getIntakeTiming(SurveyResultInputDTO input, List<String> l1, List<String> l2, List<String> l3, int age) {
        if (l1.contains("LIVER")) return "점심 시간 섭취";
        if (l2.contains("SLEEP")) return "취침 3시간 전 이전";
        if (l2.contains("CONSTIPATION")) return "기상 후 공복 섭취";
        if (l2.contains("ARTHRITIS")) return "운동 직후 (비타민D 포함)";
        if ("MUSCLE".equals(input.getPurpose())) {
            return switch (input.getWorkoutFreq()) {
                case "HIGH" -> "운동 후 즉시 및 취침 전";
                case "MID" -> "운동 직후 30분 이내";
                default -> "운동 후 1시간 이내";
            };
        }
        if ("DIET".equals(input.getPurpose())) return "식사 대용 또는 아침";
        return "아침 또는 운동 직후";
    }

    private Map<String, Integer> calculateTimingRatio(SurveyResultInputDTO input, List<String> l1, List<String> l2, List<String> l3, int age) {
        Map<String, Integer> ratio = new LinkedHashMap<>();

        if ("MUSCLE".equals(input.getPurpose())) {
            ratio.put("운동 후", 40);
            ratio.put("취침 전", 20);
            ratio.put("아침", 15);
            ratio.put("점심", 15);
            ratio.put("저녁", 10);
        } else if ("DIET".equals(input.getPurpose())) {
            ratio.put("아침", 30);
            ratio.put("점심", 25);
            ratio.put("저녁", 15);
            ratio.put("운동 후", 20);
            ratio.put("취침 전", 10);
        } else if ("MEAL".equals(input.getPurpose())) {
            ratio.put("아침", 35);
            ratio.put("점심", 35);
            ratio.put("저녁", 20);
            ratio.put("운동 후", 5);
            ratio.put("취침 전", 5);
        } else {
            ratio.put("아침", 20);
            ratio.put("점심", 20);
            ratio.put("저녁", 20);
            ratio.put("운동 후", 20);
            ratio.put("취침 전", 20);
        }

        if (l1.contains("LIVER")) {
            ratio.put("저녁", 10);
            ratio.put("점심", ratio.getOrDefault("점심", 20) + 10);
        }

        if (l2.contains("SLEEP")) {
            ratio.put("취침 전", 0);
            ratio.put("저녁", ratio.getOrDefault("저녁", 20) + 10);
        }

        if (l2.contains("CONSTIPATION")) {
            ratio.put("아침", ratio.getOrDefault("아침", 20) + 10);
        }

        if (l2.contains("ARTHRITIS")) {
            ratio.put("운동 후", ratio.getOrDefault("운동 후", 20) + 10);
        }

        if (age >= 65) {
            ratio.put("취침 전", 0);
            ratio.put("아침", ratio.getOrDefault("아침", 20) + 10);
            ratio.put("점심", ratio.getOrDefault("점심", 20) + 10);
        }

        int total = ratio.values().stream().mapToInt(Integer::intValue).sum();
        if (total != 100) {
            double scale = 100.0 / total;
            ratio.replaceAll((k, v) -> Math.max(0, (int) Math.round(v * scale)));
        }

        return ratio;
    }

    private List<String> getWarnings(List<String> l1, List<String> l2, List<String> l3, int age) {
        List<String> msg = new ArrayList<>();
        if (l1.contains("KIDNEY")) msg.add("단백질 섭취량을 0.6~0.8g/kg 이하로 제한하세요.");
        if (l1.contains("LIVER")) msg.add("과도한 단백질 섭취는 간에 부담이 될 수 있습니다.");
        if (l2.contains("LACTOSE")) msg.add("WPC 등 유당이 포함된 제품은 피하세요.");
        if (l2.contains("ACNE")) msg.add("WPC, CASEIN은 여드름을 유발할 수 있습니다.");
        if (l2.contains("SLEEP")) msg.add("취침 3시간 전 이후 단백질 섭취는 피해주세요.");
        if (l2.contains("DIARRHEA")) msg.add("WPC 등 유당 함유 단백질을 피해주세요.");
        if (l2.contains("DIGESTION")) msg.add("소화 흡수 속도가 빠른 WPH 형태를 추천합니다.");
        if(l2.contains("ALLERGY")) msg.add("ISP가 포함된 제품을 피하세요");
        if (age >= 65) msg.add("고령자의 경우 소화 흡수가 쉬운 WPH 형태의 단백질을 권장합니다.");
        if (age <= 12) msg.add("어린이의 경우 성인의 절반 정도의 단백질 섭취가 적절합니다. ISP 등 소화 부담이 적은 제품을 추천합니다.");
        return msg;
    }

    private Map<String, Integer> getRecommendedProteinMap(List<String> l1, List<String> l2, List<String> l3, int age) {
        Map<String, Integer> result = new HashMap<>();
        if (l1.contains("KIDNEY")) increment(result, "WPI");
        if (l1.contains("LIVER")) {
            increment(result, "WPI");
            increment(result, "ISP");
        }
        if (l2.contains("LACTOSE")) {
            increment(result, "WPI");
            increment(result, "WPH");
        }
        if (l2.contains("ACNE")) {
            increment(result, "WPI");
            increment(result, "WPH");
        }
        if (l2.contains("DIARRHEA")) {
            increment(result, "WPI");
            increment(result, "ISP");
        }
        if (l2.contains("ALLERGY")) {
            increment(result, "WPC");
            increment(result, "WPI");
        }
        if (l2.contains("CONSTIPATION")) {
            increment(result, "WPH");
            increment(result, "ISP");
        }
        if (l2.contains("SLEEP")) {
            increment(result, "WPH");
            increment(result, "WPI");
        }
        if (l2.contains("TIRED")) {
            increment(result, "WPH");
        }
        if (l3.contains("MUSCLE")) {
            increment(result, "WPI");
            increment(result, "WPH");
        }
        if (l3.contains("MEAL")) {
            increment(result, "ISP");
            increment(result, "CASEIN");
        }
        if (age >= 65) increment(result, "WPH");
        if (age <= 12) increment(result, "ISP");

        return result;
    }

    private void increment(Map<String, Integer> map, String protein) {
        map.put(protein, map.getOrDefault(protein, 0) + 1);
    }

}
