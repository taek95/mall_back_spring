package org.zerock.apiServer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zerock.apiServer.dto.CartItemDTO;
import org.zerock.apiServer.dto.CartItemListDTO;
import org.zerock.apiServer.service.CartService;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PreAuthorize("#itemDTO.email == authentication.name")
    @PostMapping("/change")
    public List<CartItemListDTO> changeCart(@RequestBody CartItemDTO itemDTO) {
        log.info(itemDTO);
        if(itemDTO.getQty() <= 0) {
            return cartService.remove(itemDTO.getCino());
        }
        return cartService.addOrModify(itemDTO);
    }

    // principal = security에 로그인한 사용자정보
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/items")
    public List<CartItemListDTO> getCartItems(Principal principal) {
        String email = principal.getName();
        log.info("email: " + email);
        return cartService.getCartItems(email);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{cino}")
    public List<CartItemListDTO> removeFromCart(@PathVariable("cino") Long cino) {
        return cartService.remove(cino);
    }
}

