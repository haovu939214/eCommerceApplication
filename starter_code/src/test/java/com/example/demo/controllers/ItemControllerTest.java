package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void init() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItems() {
        List<Item> items = new ArrayList<>();
        Item itemFake = getItem();
        items.add(itemFake);
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> responseItem = itemController.getItems();
        assertNotNull(responseItem);
        assertEquals(200, responseItem.getStatusCodeValue());
        List<Item> returnItem = responseItem.getBody();
        assertNotNull(returnItem);
    }

    @Test
    public void getItemById() {
        Item itemFake = getItem();
        when(itemRepository.findById(0L)).thenReturn(Optional.of((itemFake)));
        final ResponseEntity<Item> responseItem = itemController.getItemById(0L);
        Item item = responseItem.getBody();
        assertNotNull(item);
        assertEquals(itemFake.getId(), item.getId());
    }

    @Test
    public void getItemByIdNotExists() {
        final ResponseEntity<Item> responseItem = itemController.getItemById(1L);
        assertNotNull(responseItem);
        assertEquals(404, responseItem.getStatusCodeValue());
    }

    @Test
    public void getItemsByName() {
        List<Item> items = new ArrayList<>();
        Item itemFake = getItem();
        items.add(itemFake);
        when(itemRepository.findByName(itemFake.getName())).thenReturn(items);
        final ResponseEntity<List<Item>> responseItem = itemController.getItemsByName(itemFake.getName());
        List<Item> itemResponse = responseItem.getBody();
        assertNotNull(itemResponse);
        assertEquals(itemResponse.get(0).getName(), itemFake.getName());
    }

    @Test
    public void getItemsByNameNotExists() {
        final ResponseEntity<List<Item>> responseItem = itemController.getItemsByName("testNotExist");
        assertNotNull(responseItem);
        assertEquals(404, responseItem.getStatusCodeValue());
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
