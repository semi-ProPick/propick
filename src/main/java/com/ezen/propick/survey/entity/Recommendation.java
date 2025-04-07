package com.ezen.propick.survey.entity;

import com.ezen.propick.product.entity.Product;
import com.ezen.propick.survey.enumpackage.ProteinType;
import com.ezen.propick.survey.enumpackage.RecommendationTiming;
import com.ezen.propick.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Entity
@Table(name = "recommendation")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id", updatable = false, nullable = false)
    private Integer recommendationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private SurveyResponse responseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product productId;

}
