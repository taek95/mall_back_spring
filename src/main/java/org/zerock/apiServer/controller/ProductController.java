package org.zerock.apiServer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.apiServer.dto.ProductDTO;
import org.zerock.apiServer.util.CustomFileUtil;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CustomFileUtil fileUtil;

    // 파일 데이터는 JSON으로 못받음
    // 원본 사이즈의 파일은 용량이 클 수도 있어서 용량을 작게하기 위해 썸네일을 만들어줌 => 라이브러리 이용
    @PostMapping("/")
    public Map<String,String> register(ProductDTO productDTO) {
        log.info("register: " + productDTO);
        List<MultipartFile> files = productDTO.getFiles();
        List<String> uploadedFileNames = fileUtil.saveFiles(files);
        productDTO.setUploadedFileNames(uploadedFileNames);
        log.info(uploadedFileNames);
        return Map.of("Result", "Success");
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable("fileName") String fileName) {
        return fileUtil.getFile(fileName);
    }



}
