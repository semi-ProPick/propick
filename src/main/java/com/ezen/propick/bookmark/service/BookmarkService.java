package com.ezen.propick.bookmark.service;

import com.ezen.propick.bookmark.dto.BookmarkDTO;
import com.ezen.propick.bookmark.entity.Bookmark;
import com.ezen.propick.bookmark.repository.BookmarkRepository;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ProductRepository productRepository;

    @Transactional
    public BookmarkDTO addBookmark(BookmarkDTO bookmarkDTO) {
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUserNoAndProduct_ProductId(
                bookmarkDTO.getUserNo(), bookmarkDTO.getProductId());

        if (existingBookmark.isPresent()) {
            throw new IllegalArgumentException("이미 북마크에 추가된 상품입니다.");
        }

        Product product = productRepository.findById(bookmarkDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        Bookmark bookmark = Bookmark.builder()
                .bookmarkStatus("ACTIVE")
                .userNo(bookmarkDTO.getUserNo()) // String
                .product(product)
                .build();

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        return BookmarkDTO.builder()
                .bookmarkId(savedBookmark.getBookmarkId())
                .bookmarkStatus(savedBookmark.getBookmarkStatus())
                .userNo(savedBookmark.getUserNo())
                .productId(savedBookmark.getProduct().getProductId())
                .build();
    }

    @Transactional
    public void removeBookmark(String userNo, Integer productId) { // Integer -> String
        if (userNo == null || productId == null) {
            throw new IllegalArgumentException("사용자 ID와 상품 ID는 필수입니다.");
        }

        Bookmark bookmark = bookmarkRepository.findByUserNoAndProduct_ProductId(userNo, productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 북마크를 찾을 수 없습니다: userNo=" + userNo + ", productId=" + productId));

        bookmarkRepository.delete(bookmark);
    }

    public List<ProductListDTO> getBookmarkedProducts(String userNo) { // Integer -> String
        List<Bookmark> bookmarks = bookmarkRepository.findByUserNo(userNo);
        return bookmarks.stream()
                .filter(bookmark -> "ACTIVE".equals(bookmark.getBookmarkStatus()))
                .map(bookmark -> {
                    Product product = bookmark.getProduct();
                    if (product != null) {
                        return new ProductListDTO(
                                product.getProductId(),
                                product.getProductName(),
                                product.getBrand() != null ? product.getBrand().getBrandName() : "Unknown Brand",
                                product.getProductType(),
                                product.getProductPrice(),
                                null,
                                product.getProductImages().stream()
                                        .map(image -> image.getImageUrl())
                                        .collect(Collectors.toList())
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<ProductListDTO> getTop3BookmarkedProducts() {
        List<Object[]> topProducts = bookmarkRepository.findTop3ByBookmarkCount();

        List<Integer> productIds = topProducts.stream()
                .map(obj -> (Integer) obj[0])
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);

        return products.stream().map(product -> {
                    ProductListDTO dto = new ProductListDTO(
                            product.getProductId(),
                            product.getProductName(),
                            product.getBrand() != null ? product.getBrand().getBrandName() : "Unknown Brand",
                            product.getProductType(),
                            product.getProductPrice(),
                            null,
                            product.getProductImages().stream()
                                    .map(image -> image.getImageUrl())
                                    .collect(Collectors.toList())
                    );
                    for (Object[] obj : topProducts) {
                        if (((Integer) obj[0]).equals(product.getProductId())) {
                            dto.setBookmarkCount(((Long) obj[1]).intValue());
                            break;
                        }
                    }
                    return dto;
                }).sorted((p1, p2) -> Integer.compare(p2.getBookmarkCount(), p1.getBookmarkCount()))
                .collect(Collectors.toList());
    }
}