package com.ezen.propick.survey.entity;

import com.ezen.propick.product.entity.Product;
import com.ezen.propick.survey.enumpackage.ProteinType;
import com.ezen.propick.survey.enumpackage.RecommendationTiming;
import com.ezen.propick.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Entity
@Table(name="recommendation")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id", updatable = false, nullable = false)
    private Integer recommendationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private SurveyResponse responseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User userNo;

    //float -> BigDeciaml 변경 정확한 소수 연산이 필요할때는 BigDecimal을 사용
    @Column(name="recommendation_intake_amount", precision=10, scale=2, nullable = false)
    private BigDecimal recommendationIntakeAmount;

    @Column(name="recommendation_timing", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecommendationTiming recommendationTiming;


    @Column(name="recommendation_protein_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProteinType recommendationProteinType;

    @Column(name="recommendation_warnings")
    private String recommendationWarning;


    public void setRecommendationIntakeAmount(BigDecimal amount) {
        this.recommendationIntakeAmount = amount.setScale(2, RoundingMode.HALF_UP);
    }

}
