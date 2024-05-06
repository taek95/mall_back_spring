package org.zerock.apiServer.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "tbl_product")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageList")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;
    private String pname;
    private int price;
    private String pdesc;
    private boolean delFlag;
    @ElementCollection
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    public void changePrice(int price) {
        this.price = price;
    }
    public void changeDesc(String desc) {
        this.pdesc = desc;
    }
    public void changeName(String name) {
        this.pname = name;
    }
    // 삭제는 실제로 파일을 삭제하는게 아님
    public void changeDel(boolean delFlag) {
        this.delFlag = delFlag;
    }
    public void addImage(ProductImage image) {
        image.setOrd(imageList.size());
        imageList.add(image);
    }
    public void addImageString(String fileName) {
        ProductImage productImage = ProductImage.builder()
                .fileName(fileName)
                .build();
        addImage(productImage);
    }

    public void clearList() {
        this.imageList.clear();
    }
}
