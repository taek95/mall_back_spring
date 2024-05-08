package org.zerock.apiServer.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

// 상품 업로드 + 상품 조회 DTO
// DB에 파일을 저장하면 안된다. 성능, 속도 안나옴, DB엔 파일 이름들만
// 스프링에서는 multipartFile을 제공한다.

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    // 상품 번호
    private Long pno;
    private String pname;
    private int price;
    private String pdesc;
    private boolean delFlag;

    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>();

    // 파일 이름들 목록
    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();

}
