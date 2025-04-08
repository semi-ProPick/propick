package com.ezen.propick.bookmark.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkDTO {

    private Integer bookmarkId;
    private String bookmarkStatus;
    private String userId;
    private Integer productId;
}