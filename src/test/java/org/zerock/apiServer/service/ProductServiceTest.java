package org.zerock.apiServer.service;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.zerock.apiServer.dto.PageRequestDTO;
import org.zerock.apiServer.dto.PageResponseDTO;
import org.zerock.apiServer.dto.ProductDTO;

import java.util.UUID;

@SpringBootTest
@Log4j2
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Test
    public void testList() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
        PageResponseDTO<ProductDTO> responseDTO = productService.getList(pageRequestDTO);
        log.info(responseDTO.getDtoList());
    }

    @Test
    public void testRegister() {
        ProductDTO productDTO = ProductDTO.builder()
                .pname("새로운 상품")
                .pdesc("신규 추가 상품입니다.")
                .price(1000)
                .build();

        productDTO.setUploadFileNames(
                java.util.List.of(
                        UUID.randomUUID()+"_"+"Test1.jpg",
                        UUID.randomUUID()+"_"+"Test2.jpg"));
        productService.register(productDTO);
    }
}
