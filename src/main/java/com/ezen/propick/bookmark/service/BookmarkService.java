package com.ezen.propick.bookmark.service;

import com.ezen.propick.bookmark.dto.BookmarkDTO;
import com.ezen.propick.bookmark.entity.Bookmark;
import com.ezen.propick.bookmark.repository.BookmarkRepository;
import com.ezen.propick.product.dto.ProductListDTO;
import com.ezen.propick.product.entity.Product;
import com.ezen.propick.product.repository.ProductRepository;
import com.ezen.propick.product.service.MainProductService;
import com.ezen.propick.user.entity.User;
import com.ezen.propick.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MainProductService mainProductService;

    @Transactional
    public BookmarkDTO addBookmark(BookmarkDTO bookmarkDTO) {
        User user = userRepository.findByUserId(bookmarkDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        System.out.println("User found: userId=" + bookmarkDTO.getUserId() + ", userNo=" + user.getUserNo());

        // userNo와 productId로 중복 확인
        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUser_UserNoAndProduct_ProductId(user.getUserNo(), bookmarkDTO.getProductId());
        if (existingBookmark.isPresent()) {
            Bookmark bookmark = existingBookmark.get();
            System.out.println("Existing bookmark found: bookmarkId=" + bookmark.getBookmarkId() + ", status=" + bookmark.getBookmarkStatus());
            if ("ACTIVE".equals(bookmark.getBookmarkStatus())) {
                throw new IllegalStateException("이미 추가된 북마크입니다.");
            } else {
                bookmark.setBookmarkStatus("ACTIVE");
                Bookmark updatedBookmark = bookmarkRepository.save(bookmark);
                return BookmarkDTO.builder()
                        .bookmarkId(updatedBookmark.getBookmarkId())
                        .bookmarkStatus(updatedBookmark.getBookmarkStatus())
                        .userId(updatedBookmark.getUser().getUserId())
                        .productId(updatedBookmark.getProduct().getProductId())
                        .build();
            }
        } else {
            System.out.println("No existing bookmark found for userNo=" + user.getUserNo() + ", productId=" + bookmarkDTO.getProductId());
        }

        Product product = productRepository.findById(bookmarkDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        Bookmark bookmark = Bookmark.builder()
                .bookmarkStatus("ACTIVE")
                .user(user)
                .product(product)
                .build();

        try {
            Bookmark savedBookmark = bookmarkRepository.save(bookmark);
            System.out.println("저장된 북마크: userId=" + savedBookmark.getUser().getUserId() + ", userNo=" + savedBookmark.getUser().getUserNo() + ", productId=" + savedBookmark.getProduct().getProductId() + ", status=" + savedBookmark.getBookmarkStatus());
            return BookmarkDTO.builder()
                    .bookmarkId(savedBookmark.getBookmarkId())
                    .bookmarkStatus(savedBookmark.getBookmarkStatus())
                    .userId(savedBookmark.getUser().getUserId())
                    .productId(savedBookmark.getProduct().getProductId())
                    .build();
        } catch (DataIntegrityViolationException e) {
            System.out.println("중복 데이터 삽입 시도 감지: userId=" + bookmarkDTO.getUserId() + ", productId=" + bookmarkDTO.getProductId());
            throw new IllegalStateException("이미 추가된 북마크입니다.");
        }
    }

    @Transactional
    public void removeBookmark(String userId, Integer productId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        System.out.println("User found: userId=" + userId + ", userNo=" + user.getUserNo());

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUser_UserNoAndProduct_ProductId(user.getUserNo(), productId);
        if (!existingBookmark.isPresent()) {
            throw new IllegalArgumentException("해당 북마크를 찾을 수 없습니다: userId=" + userId + ", productId=" + productId);
        }

        // userNo와 productId로 삭제
        bookmarkRepository.deleteByUser_UserNoAndProduct_ProductId(user.getUserNo(), productId);
        System.out.println("Bookmarks deleted for userNo=" + user.getUserNo() + ", productId=" + productId);
    }

    @Transactional
    public void removeBookmarkById(Integer bookmarkId, String userId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new IllegalArgumentException("북마크를 찾을 수 없습니다: bookmarkId=" + bookmarkId));

        if (!bookmark.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 북마크만 삭제할 수 있습니다.");
        }

        bookmarkRepository.delete(bookmark);
    }

    public List<ProductListDTO> getBookmarkedProducts(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        List<Bookmark> bookmarks = bookmarkRepository.findByUser_UserNoAndBookmarkStatus(user.getUserNo(), "ACTIVE");
        System.out.println("조회된 북마크 수: " + (bookmarks != null ? bookmarks.size() : "NULL"));
        System.out.println("조회된 북마크 리스트: " + bookmarks);

        return bookmarks.stream()
                .map(bookmark -> mainProductService.convertToProductListDTO(bookmark.getProduct()))
                .collect(Collectors.toList());
    }

    public List<ProductListDTO> getTop3BookmarkedProducts() {
        List<Object[]> topProducts = bookmarkRepository.findTopBookmarkedProducts(PageRequest.of(0, 3));
        System.out.println("Top Products from Repository: " + (topProducts != null ? topProducts : "NULL"));

        if (topProducts == null || topProducts.isEmpty()) {
            System.out.println("No top bookmarked products found.");
            return Collections.emptyList();
        }

        List<Integer> productIds = topProducts.stream()
                .map(obj -> (Integer) obj[0])
                .collect(Collectors.toList());
        System.out.println("Product IDs: " + productIds);

        List<Product> products = productRepository.findAllById(productIds);
        System.out.println("Found Products: " + (products != null ? products.size() + " items" : "NULL"));
        if (products != null && !products.isEmpty()) {
            products.forEach(p -> System.out.println("Product: " + p.getProductId() + " - " + p.getProductName()));
        } else {
            System.out.println("No products found for IDs: " + productIds);
        }

        return products.stream()
                .map(product -> {
                    ProductListDTO dto = mainProductService.convertToProductListDTO(product);
                    if (dto == null) {
                        System.out.println("DTO conversion failed for product: " + product.getProductId());
                    } else {
                        for (Object[] obj : topProducts) {
                            if (((Integer) obj[0]).equals(product.getProductId())) {
                                dto.setBookmarkCount(((Long) obj[1]).intValue());
                                break;
                            }
                        }
                    }
                    return dto;
                })
                .filter(dto -> dto != null)
                .sorted((p1, p2) -> Integer.compare(p2.getBookmarkCount(), p1.getBookmarkCount()))
                .collect(Collectors.toList());
    }

    public List<ProductListDTO> getProductsWithBookmarkStatus(String userId, List<ProductListDTO> products) {
        User user = userId != null ? userRepository.findByUserId(userId).orElse(null) : null;
        List<Bookmark> bookmarks = user != null ? bookmarkRepository.findByUser_UserNoAndBookmarkStatus(user.getUserNo(), "ACTIVE") : List.of();

        return products.stream().map(product -> {
            boolean isBookmarked = bookmarks.stream()
                    .anyMatch(bookmark -> bookmark.getProduct().getProductId().equals(product.getProductId()));
            product.setBookmarked(isBookmarked);
            return product;
        }).collect(Collectors.toList());
    }


}