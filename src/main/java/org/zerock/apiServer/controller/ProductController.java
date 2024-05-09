package org.zerock.apiServer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.apiServer.dto.PageRequestDTO;
import org.zerock.apiServer.dto.PageResponseDTO;
import org.zerock.apiServer.dto.ProductDTO;
import org.zerock.apiServer.service.ProductService;
import org.zerock.apiServer.util.CustomFileUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CustomFileUtil fileUtil;
    private final ProductService productService;

    // 파일 데이터는 JSON으로 못받음
    // 원본 사이즈의 파일은 용량이 클 수도 있어서 용량을 작게하기 위해 썸네일을 만들어줌 => 라이브러리 이용
    @PostMapping("/")
    public Map<String,Long> register(ProductDTO productDTO) {
        log.info("register: " + productDTO);
        List<MultipartFile> files = productDTO.getFiles();
        List<String> uploadedFileNames = fileUtil.saveFiles(files);
        productDTO.setUploadFileNames(uploadedFileNames);
        log.info(uploadedFileNames);
        Long pno = productService.register(productDTO);

        // 너무 빨리처리되면 모달창이 보일 시간도 없다.
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Map.of("Result", pno);
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable("fileName") String fileName) {
        return fileUtil.getFile(fileName);
    }

    // 어떤 권한이 있는 사람한테 허락을 해줄거야?
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {
        return productService.getList(pageRequestDTO);

    }

    @GetMapping("/{pno}")
    public ProductDTO read(@PathVariable("pno") Long pno) {
        return productService.get(pno);
    }

    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable Long pno, ProductDTO productDTO) {
        productDTO.setPno(pno);

        // 현재 DB에 저장된 상품 정보
        ProductDTO oldProductDTO = productService.get(pno);

        // 새로운 file upload
        List<MultipartFile> files = productDTO.getFiles();
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);

        List<String> uploadedFileNames = productDTO.getUploadFileNames();
        // 새로 업로드 된 파일이 있다면
        if(currentUploadFileNames != null && !currentUploadFileNames.isEmpty()) {
            // 원래 파일 이름에 추가해주기
            uploadedFileNames.addAll(currentUploadFileNames);
        }
        productService.modify(productDTO);

        // a,b,c 에 d 가 추가 될때 c가 없어져야 함
        List<String> oldFileNames = oldProductDTO.getUploadFileNames();
        if(oldFileNames != null && oldFileNames.size() > 0) {
            List<String> removeFiles =
                oldFileNames.stream().filter(fileName -> uploadedFileNames.indexOf(fileName) == -1).collect(Collectors.toList());
            fileUtil.deleteFiles(removeFiles);
        }
        return Map.of("RESULT", "SUCCESS");
    }

    // 원래는 삭제가 없다
    @DeleteMapping("/{pno}")
    public Map<String, String> remove(@PathVariable Long pno) {
        // 삭제해야 하는 파일들 이름
        List<String> oldFileNames = productService.get(pno).getUploadFileNames();
        productService.remove(pno);
        fileUtil.deleteFiles(oldFileNames);
        return Map.of("RESULT", "SUCCESS");
    }

}
