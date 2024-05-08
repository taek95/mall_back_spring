package org.zerock.apiServer.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.apiServer.domain.Product;
import org.zerock.apiServer.dto.PageRequestDTO;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testInsert() {
        for(int i=0;i<10;i++) {
            Product product = Product.builder()
                    .pname("Test")
                    .pdesc("Test desc")
                    .price(1000)
                    .build();
            product.addImageString(UUID.randomUUID() + "_" + "IMAGE1.jpa");
            product.addImageString(UUID.randomUUID() + "_" + "IMAGE2.jpa");
            productRepository.save(product);
        }
    }

    @Transactional
    @Test
    public void testRead() {
        Long pno = 1L;
        Optional<Product> res = productRepository.findById(pno);
        Product product = res.orElseThrow();
        log.info(product);
        log.info(product.getImageList());
    }

    @Test
    public void testRead2() {
        Long pno = 1L;
        Optional<Product> res = productRepository.selectOne(pno);
        Product product = res.orElseThrow();
        log.info(product);
        log.info(product.getImageList());
    }

    @Transactional
    @Commit
    @Test
    public void testDelete() {
        Long pno = 2L;
        productRepository.updateToDelete(2L,true);
    }

    @Test
    public void testUpdate() {
        Product product = productRepository.selectOne(1L).get();
        product.changePrice(3000);
        product.clearList();

        product.addImageString(UUID.randomUUID() + "_" + "PIMAGE1.jpa");
        product.addImageString(UUID.randomUUID() + "_" + "PIMAGE2.jpa");
        product.addImageString(UUID.randomUUID() + "_" + "PIMAGE3.jpa");
        productRepository.save(product);
    }

    @Test
    public void testList() {
        Pageable pageable = PageRequest.of(0,10, Sort.by("pno").descending());
        Page<Object[]> res = productRepository.selectList(pageable);
        res.getContent().forEach(arr -> log.info(Arrays.toString(arr)));
    }

    @Test
    public void testSearch() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
        productRepository.searchList(pageRequestDTO);
    }
}
