package com.ezen.propick.bookmark.entity;

import com.ezen.propick.bookmark.Enum.BookmarkStatus;
import com.ezen.propick.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookmark")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Integer bookmark_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "bookmark_status")
    private BookmarkStatus bookmark_status;

    @Column(name = "user_no")
    private Integer userNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",  nullable = false)
    private Product product;
   // referencedColumnName = "product_id"
}