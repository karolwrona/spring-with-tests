package pl.edu.wszib.springwithtests.service.impl;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.*;
import pl.edu.wszib.springwithtests.NotFoundException;
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

@RunWith(JUnit4.class)
public class ShoppingBasketServiceImplTest {

    @InjectMocks
    ShoppingBasketServiceImpl basketService;

    @Mock
    ProductDao productDao;
    @Mock
    ShoppingBasketDao shoppingBasketDao;
    @Mock
    ShoppingBasketItemDao shoppingBasketItemDao;
    @Spy
    Mapper mapper = new DozerBeanMapper();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShoppingBasketIdNotExist() {
        int testShoppingBasketId = 6742;
        ProductDTO productDTO = Mockito.mock(ProductDTO.class);
        expectedException.expect(NotFoundException.class);
        basketService.addProduct(productDTO, testShoppingBasketId);
    }

    @Test
    public void testShoppingBasketExistProductNotExist() {
        int testShoppingBasketID = 68;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(58);
        productDTO.setName("TEST!!!!!!!!!!!");
        productDTO.setCost(25d);
        productDTO.setVat(Vat.VALUE_23);

        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket.setId(testShoppingBasketID);
        Mockito.when(shoppingBasketDao.findById(testShoppingBasketID)).thenReturn(Optional.of(shoppingBasket));

        expectedException.expect(NotFoundException.class);
        basketService.addProduct(productDTO, testShoppingBasketID);
    }

    @Test
    public void testShoppingBasketExistProductExistShoppingBasketItemNotExist() {
        int testShoppingBasketID = 457;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(58);
        productDTO.setName("TEST!!!!!!!!!!!");
        productDTO.setCost(25d);
        productDTO.setVat(Vat.VALUE_23);

        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket.setId(testShoppingBasketID);
        Mockito.when(shoppingBasketDao.findById(testShoppingBasketID)).thenReturn(Optional.of(shoppingBasket));
        Mockito.when((productDao.existsById(productDTO.getId()))).thenReturn(true);

        ShoppingBasketItem item = new ShoppingBasketItem();
        item.setId(545);
        item.setShoppingBasket(shoppingBasket);
        item.setAmount(15);
        item.setProduct(mapper.map(productDTO, Product.class));


        Mockito.when(shoppingBasketItemDao.findByProductIdAndShoppingBasketId(productDTO.getId(), testShoppingBasketID)).thenReturn(item);

        Mockito.when(shoppingBasketItemDao.findAllByShoppingBasketId(testShoppingBasketID)).thenReturn(Collections.singletonList(item));

        ShoppingBasketDTO result = basketService.addProduct(productDTO, testShoppingBasketID);

        Mockito.verify(shoppingBasketItemDao).save(item);

        Assert.assertEquals(testShoppingBasketID, result.getId().intValue());
        Assert.assertEquals(1, result.getItems().size());
        Assert.assertTrue(result.getItems().stream().anyMatch(i->i.getProduct().getId().equals(productDTO.getId())));
        Assert.assertTrue(result.getItems().stream().filter(i->i.getProduct().getId().equals(productDTO.getId())).findFirst()
                .map(i->i.getAmount()==16).orElse(false));
    }
    @Test
    public void testShoppingBasketExistProductExistShoppingBasketItemExist() {
        int testShoppingBasketID = 457;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(58);
        productDTO.setName("TEST!!!!!!!!!!!");
        productDTO.setCost(25d);
        productDTO.setVat(Vat.VALUE_23);

        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket.setId(testShoppingBasketID);
        Mockito.when(shoppingBasketDao.findById(testShoppingBasketID)).thenReturn(Optional.of(shoppingBasket));
        Mockito.when((productDao.existsById(productDTO.getId()))).thenReturn(true);

        ShoppingBasketItem item = new ShoppingBasketItem();
        item.setId(545);
        item.setShoppingBasket(shoppingBasket);
        item.setAmount(1);
        item.setProduct(mapper.map(productDTO, Product.class));


        Mockito.when(shoppingBasketItemDao.findAllByShoppingBasketId(testShoppingBasketID)).thenReturn(Collections.singletonList(item));

        ShoppingBasketDTO result = basketService.addProduct(productDTO, testShoppingBasketID);

        Assert.assertEquals(testShoppingBasketID, result.getId().intValue());
        Assert.assertEquals(1, result.getItems().size());
        Assert.assertTrue(result.getItems().stream().anyMatch(i->i.getProduct().getId().equals(productDTO.getId())));
        Assert.assertTrue(result.getItems().stream().filter(i->i.getProduct().getId().equals(productDTO.getId())).findFirst()
                .map(i->i.getAmount()==1).orElse(false));
    }

}
