package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void init() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addToCart() {
        User user = getUser();
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        Item itemFake = getItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of((itemFake)));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(10);
        modifyCartRequest.setUsername("testUser");
        ResponseEntity<Cart> responseCart = cartController.addToCart(modifyCartRequest);

        assertNotNull(responseCart);
        assertEquals(200, responseCart.getStatusCodeValue());
        Cart cart = responseCart.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(100), cart.getTotal());
    }

    @Test
    public void addCartWithUserNotExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(10);
        modifyCartRequest.setUsername("testUser");

        ResponseEntity<Cart> responseCart = cartController.addToCart(modifyCartRequest);
        assertEquals(404, responseCart.getStatusCodeValue());
        Cart cart = responseCart.getBody();
        assertNull(cart);
    }

    @Test
    public void addCartWithItemNotExists() {
        User userFake = getUser();
        when(userRepository.findByUsername("testUser")).thenReturn(userFake);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(5);
        modifyCartRequest.setUsername("testUser");

        ResponseEntity<Cart> responseCart = cartController.addToCart(modifyCartRequest);
        assertEquals(404, responseCart.getStatusCodeValue());
        Cart cart = responseCart.getBody();
        assertNull(cart);
    }

    @Test
    public void removeCart() throws Exception {
        User userFake = getUser();
        when(userRepository.findByUsername("testUser")).thenReturn(userFake);

        Item itemFake = getItem();
        when(itemRepository.findById(1L)).thenReturn(Optional.of((itemFake)));

        ModifyCartRequest request1 = new ModifyCartRequest();
        request1.setItemId(1L);
        request1.setQuantity(3);
        request1.setUsername("testUser");
        ResponseEntity<Cart> responseCart1 = cartController.addToCart(request1);
        assertNotNull(responseCart1);
        assertEquals(200, responseCart1.getStatusCodeValue());

        ModifyCartRequest request2 = new ModifyCartRequest();
        request2.setItemId(1L);
        request2.setQuantity(2);
        request2.setUsername("testUser");
        ResponseEntity<Cart> responseCart2 = cartController.removeFromcart(request2);

        assertNotNull(responseCart2);
        assertEquals(200, responseCart2.getStatusCodeValue());
        Cart cart = responseCart2.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(10), cart.getTotal());
    }

    @Test
    public void removeCartWithUserNotExists() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(5);
        modifyCartRequest.setUsername("testUser");

        ResponseEntity<Cart> responseCart = cartController.removeFromcart(modifyCartRequest);
        assertEquals(404, responseCart.getStatusCodeValue());
        Cart cart = responseCart.getBody();
        assertNull(cart);
    }

    @Test
    public void removeCartWithItemNotExists() throws Exception {
        User userFake = getUser();
        when(userRepository.findByUsername("testUser")).thenReturn(userFake);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(5);
        modifyCartRequest.setUsername("testUser");

        ResponseEntity<Cart> responseCart = cartController.removeFromcart(modifyCartRequest);
        assertEquals(404, responseCart.getStatusCodeValue());
        Cart cart = responseCart.getBody();
        assertNull(cart);
    }

    private User getUser() {
        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("testUser");
        user.setPassword("Hashed");
        user.setCart(cart);
        return user;
    }

    private Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        BigDecimal price = BigDecimal.valueOf(10);
        item.setPrice(price);
        item.setDescription("Description item");
        return item;
    }
}
