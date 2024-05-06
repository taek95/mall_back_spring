package org.zerock.apiServer.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.apiServer.domain.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // EntityGraph를 이용하면 두번의 쿼리를 한번의 쿼리로 처리하게 해줌
    // DB는 무조건 많이 접근 안해야 성능이 향상
    @EntityGraph(attributePaths = "imageList")
    @Query("select p from Product p where p.pno = :pno")
    Optional<Product> selectOne(@Param("pno") Long pno);

    @Modifying
    @Query("update Product p set p.delFlag = :delFlag where p.pno = :pno")
    void updateToDelete(@Param("pno") Long pno, @Param("delFlag") boolean delFlag);
}
