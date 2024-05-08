package org.zerock.apiServer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.apiServer.domain.Product;
import org.zerock.apiServer.domain.ProductImage;
import org.zerock.apiServer.dto.PageRequestDTO;
import org.zerock.apiServer.dto.PageResponseDTO;
import org.zerock.apiServer.dto.ProductDTO;
import org.zerock.apiServer.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.zerock.apiServer.domain.QProduct.product;
import static org.zerock.apiServer.domain.QProductImage.productImage;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Override
    public PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO) {

        Pageable pageable = PageRequest.of(pageRequestDTO.getPage()-1,
                pageRequestDTO.getSize(),
                Sort.by("pno").descending());
        Page<Object[]> res = productRepository.selectList(pageable);

        // Object[] => 0번째는 product, 1번째는 productImage
        List<ProductDTO> dtoList = res.get().map(arr -> {
            ProductDTO productDTO = null;
            Product product = (Product) arr[0];
            ProductImage productImage = (ProductImage) arr[1];
            productDTO = ProductDTO.builder()
                    .pno(product.getPno())
                    .pname(product.getPname())
                    .pdesc(product.getPdesc())
                    .price(product.getPrice())
                    .build();
            String imageStr = productImage.getFileName();
            productDTO.setUploadFileNames(List.of(imageStr));

            return productDTO;
        }).collect(Collectors.toList());

        long totalCount = res.getTotalElements();

        return PageResponseDTO.<ProductDTO>withAll()
                .dtoList(dtoList)
                .totalCount(totalCount)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    private Product dtoToEntity(ProductDTO productDTO) {
        Product product = Product.builder()
                .pno(productDTO.getPno())
                .pname(productDTO.getPname())
                .pdesc(productDTO.getPdesc())
                .price(productDTO.getPrice())
                .build();

        // colletion 처리
        List<String> uploadFileNames = productDTO.getUploadFileNames();
        if(uploadFileNames == null || uploadFileNames.size() == 0) {
            return product;
        }

        uploadFileNames.forEach(fileName -> {
            product.addImageString(fileName);
        });

        return product;
    }

    @Override
    public Long register(ProductDTO productDTO) {
        Product product = dtoToEntity(productDTO);
        log.info("----------------------");
        log.info(product);
        log.info(product.getImageList());
        Long pno = productRepository.save(product).getPno();
        return pno;
    }

    @Override
    public ProductDTO get(Long pno) {

        Optional<Product> res = productRepository.findById(pno);
        Product product = res.orElseThrow();
        return entityToDTO(product);

    }

    private ProductDTO entityToDTO(Product product) {
        ProductDTO productDTO = ProductDTO.builder()
                .pno(product.getPno())
                .pname(product.getPname())
                .pdesc(product.getPdesc())
                .price(product.getPrice())
                .delFlag(product.isDelFlag())
                .build();

        List<ProductImage> imageList = product.getImageList();
        if(imageList == null || imageList.isEmpty()) {
            return productDTO;
        }
        List<String> fileNameList = imageList.stream().map(productImage ->
                productImage.getFileName()).toList();
        productDTO.setUploadFileNames(fileNameList);

        return productDTO;
    }

    @Override
    public void modify(ProductDTO productDTO) {

        // 조회
        Optional<Product> res = productRepository.findById(productDTO.getPno());
        Product product = res.orElseThrow();
        // 변경내용 반영
        product.changePrice(productDTO.getPrice());
        product.changeName(productDTO.getPname());
        product.changeDesc(productDTO.getPdesc());
        product.changeDel(productDTO.isDelFlag());

        // 이미지 처리
        List<String> uploadFileNames = productDTO.getUploadFileNames();
        product.clearList();
        if(uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(uploadName -> {
                product.addImageString(uploadName);
            });
        }
        // 저장
        productRepository.save(product);
    }

    @Override
    public void remove(Long pno) {
        productRepository.deleteById(pno);
    }
}
