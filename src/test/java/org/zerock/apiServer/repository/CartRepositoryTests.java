package org.zerock.apiServer.repository;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.apiServer.domain.Cart;
import org.zerock.apiServer.domain.CartItem;
import org.zerock.apiServer.domain.Member;
import org.zerock.apiServer.domain.Product;
import org.zerock.apiServer.dto.CartItemListDTO;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
public class CartRepositoryTests {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Transactional
    @Commit
    @Test
    public void testInsertByProduct() {
        String email = "user1@aaa.com";
        Long pno = 6L;
        int qty = 40;

        CartItem cartItem = cartItemRepository.getItemOfPno(email, pno);
        // 이미 이 상품이 사용자의 장바구니에 있을 때
        if(cartItem != null) {
            cartItem.changeQty(qty);
            cartItemRepository.save(cartItem);
            return ;
        }
        Optional<Cart> res = cartRepository.getCartOfMember(email);
        Cart cart = null;
        // 사용자의 장바구니 자체가 없을 때
        if(res.isEmpty()) {
            Member member = Member.builder().email(email).build();
            Cart tempCart = Cart.builder().owner(member).build();
            cart = cartRepository.save(tempCart);
        } else { // 장바구니는 있으나 해당 상품이 없을 때
            cart = res.get();
        }

        Product product = Product.builder().pno(pno).build();
        cartItem = CartItem.builder().cart(cart).product(product).qty(qty).build();

        cartItemRepository.save(cartItem);
    }

    @Test
    public void testListOfMember() {
        String email = "user1@aaa.com";
        List<CartItemListDTO> cartItemListDTOList = cartItemRepository.getItemsOfCartDTOByEmail(email);
        for ( CartItemListDTO dto : cartItemListDTOList) {
            log.info(dto);
        }

    }

    @Transactional
    @Commit
    @Test
    public void testUpdateByCino() {
        Long cino = 1L;
        int qty = 66;

        Optional<CartItem> res = cartItemRepository.findById(cino);
        CartItem cartItem = res.orElseThrow();
        cartItem.changeQty(qty);
        cartItemRepository.save(cartItem);
    }

    @Test
    public void testDeleteThenList() {
        Long cino = 1L;
        Long cno = cartItemRepository.getCartFromItem(cino);
        cartItemRepository.deleteById(cino);
        List<CartItemListDTO> cartItemList = cartItemRepository.getItemsOfCartDTOByCart(cno);
        for(CartItemListDTO dto : cartItemList) {
            log.info(dto);
        }
    }
}
