package com.ezen.propick.product.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ImageUtils {
    // 이미지 URL을 디코딩하는 메서드
    public static String decodeImageUrl(String imageUrl) {
        try {
            return URLDecoder.decode(imageUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return imageUrl;  // 디코딩 실패 시 원본 URL 반환
        }
    }
}
