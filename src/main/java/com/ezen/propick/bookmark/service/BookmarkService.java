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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Optional<Bookmark> existingBookmark = bookmarkRepository.findByUser_UserNoAndProduct_ProductId(user.getUserNo(), bookmarkDTO.getProductId());

        Bookmark bookmark;
        if (existingBookmark.isPresent()) {
            bookmark = existingBookmark.get();
            if (bookmark.getBookmarkStatus().equals("ACTIVE")) {
                throw new IllegalArgumentException("이미 북마크에 추가된 상품입니다.");
            }
            bookmark.setBookmarkStatus("ACTIVE");
        } else {
            Product product = productRepository.findById(bookmarkDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

            bookmark = Bookmark.builder()
                    .bookmarkStatus("ACTIVE")
                    .user(user)
                    .product(product)
                    .build();
        }

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        System.out.println("저장된 북마크: userId=" + savedBookmark.getUser().getUserId() + ", userNo=" + savedBookmark.getUser().getUserNo() + ", productId=" + savedBookmark.getProduct().getProductId() + ", status=" + savedBookmark.getBookmarkStatus());

        return BookmarkDTO.builder()
                .bookmarkId(savedBookmark.getBookmarkId())
                .bookmarkStatus(savedBookmark.getBookmarkStatus())
                .userId(savedBookmark.getUser().getUserId())
                .productId(savedBookmark.getProduct().getProductId())
                .build();
    }

    @Transactional
    public void removeBookmark(String userId, Integer productId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Bookmark bookmark = bookmarkRepository.findByUser_UserNoAndProduct_ProductId(user.getUserNo(), productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 북마크를 찾을 수 없습니다: userId=" + userId + ", productId=" + productId));

        bookmarkRepository.delete(bookmark);
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

        List<Integer> productIds = topProducts.stream()
                .map(obj -> (Integer) obj[0])
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);

        return products.stream()
                .map(product -> {
                    ProductListDTO dto = mainProductService.convertToProductListDTO(product);
                    for (Object[] obj : topProducts) {
                        if (((Integer) obj[0]).equals(product.getProductId())) {
                            dto.setBookmarkCount(((Long) obj[1]).intValue());
                            break;
                        }
                    }
                    return dto;
                })
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