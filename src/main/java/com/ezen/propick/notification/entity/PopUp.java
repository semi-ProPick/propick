package com.ezen.propick.notification.entity;

import com.ezen.propick.notification.Enum.PopUpStatus;
import com.ezen.propick.notification.Enum.PopUpType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pop_up")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PopUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "p_id")
    private Integer pId;

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "p_status")
    private PopUpStatus pStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "p_type")
    private PopUpType pType;

    @Column(name = "brand_id")
    private Integer brandId;
}