package org.zerock.apiServer.domain;

// element collection

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

    private String fileName;
    // 순번이고, ord 0번인 친구를 대표이미지로 설정하는 기능 가능
    private int ord;

    public void setOrd(int ord) {
        this.ord = ord;
    }
}
