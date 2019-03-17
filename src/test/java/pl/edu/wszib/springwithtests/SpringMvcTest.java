package pl.edu.wszib.springwithtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.edu.wszib.springwithtests.dao.ProductDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketItemDao;
import pl.edu.wszib.springwithtests.dto.ProductDTO;
import pl.edu.wszib.springwithtests.dto.ShoppingBasketDTO;
import pl.edu.wszib.springwithtests.model.Product;
import pl.edu.wszib.springwithtests.model.ShoppingBasket;
import pl.edu.wszib.springwithtests.model.ShoppingBasketItem;
import pl.edu.wszib.springwithtests.model.Vat;

import java.util.Collections;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringMvcTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ShoppingBasketDao shoppingBasketDao;
    @Autowired
    ProductDao productDao;
    @Autowired
    ShoppingBasketItemDao shoppingBasketItemDao;
    @Autowired
    Mapper mapper = new DozerBeanMapper();

    @Test
    public void testShoppingBasketNotExist() throws Exception {
        int testShoppingBasketId = 1457;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(58);
        productDTO.setName("TEST!!!!!!!!!!!");
        productDTO.setCost(25d);
        productDTO.setVat(Vat.VALUE_23);

        mockMvc.perform(MockMvcRequestBuilders.post("/shoppingBasket/add?")
                .contentType("application/json").content(objectMapper.writer().writeValueAsBytes(productDTO))
                .param("shoppingBasketId", String.valueOf(testShoppingBasketId))).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    public void testShoppingBasketExistProductNotExist() throws Exception {

        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket = shoppingBasketDao.save(shoppingBasket);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1);
        productDTO.setVat(Vat.VALUE_23);
        productDTO.setCost(15d);
        productDTO.setName("Produkt");
        mockMvc.perform(MockMvcRequestBuilders.post("/shoppingBasket/add?")
                .contentType("application/json").content(new Gson().toJson(productDTO))
                .param("shoppingBasketId", String.valueOf(shoppingBasket.getId()))).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    public void testShoppingBasketExistProductExistShoppingBasketItemExist() throws Exception {
        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket = shoppingBasketDao.save(shoppingBasket);

        Product product = new Product();
        product.setCost(14d);
        product.setName("Produkt!");
        product.setVat(Vat.VALUE_23);
        product = productDao.save(product);

        ShoppingBasketItem shoppingBasketItem = new ShoppingBasketItem();
        shoppingBasketItem.setProduct(product);
        shoppingBasketItem.setShoppingBasket(shoppingBasket);
        shoppingBasketItem.setAmount(1);
        shoppingBasketItem = shoppingBasketItemDao.save(shoppingBasketItem);

        MvcResult result =
        mockMvc.perform(MockMvcRequestBuilders.post("/shoppingBasket/add?")
                .contentType("application/json").content(new Gson().toJson(product))
                .param("shoppingBasketId", String.valueOf(shoppingBasket.getId()))).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andReturn();

        ShoppingBasketDTO shoppingBasketDTO = new Gson()
                .fromJson(result.getResponse().getContentAsString(), ShoppingBasketDTO.class);


    }
}
