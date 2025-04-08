package com.ezen.propick.notification.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopUpDTO {
    private Integer pId;
    private String title;
    private String pStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String pType;
    private Integer brandId;
}