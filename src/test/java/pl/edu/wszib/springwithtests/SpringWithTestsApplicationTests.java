package pl.edu.wszib.springwithtests;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.wszib.springwithtests.dao.ProductDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketItemDao;
import pl.edu.wszib.springwithtests.dto.ProductDTO;
import pl.edu.wszib.springwithtests.dto.ShoppingBasketDTO;
import pl.edu.wszib.springwithtests.model.Product;
import pl.edu.wszib.springwithtests.model.ShoppingBasket;
import pl.edu.wszib.springwithtests.model.ShoppingBasketItem;
import pl.edu.wszib.springwithtests.model.Vat;
import pl.edu.wszib.springwithtests.service.ShoppingBasketService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringWithTestsApplicationTests {

	@Autowired
	ShoppingBasketService service;

	@Autowired
	ShoppingBasketDao shoppingBasketDao;

	@Autowired
	ProductDao productDao;

	@Autowired
	ShoppingBasketItemDao shoppingBasketItemDao;

	@Autowired
	Mapper mapper = new DozerBeanMapper();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testShoppingBasketIdNotExist() {
		int testBasketId = 325;
		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(1);
		productDTO.setVat(Vat.VALUE_23);
		productDTO.setCost(15d);
		productDTO.setName("Produkt");
		expectedException.expect(NotFoundException.class);
		service.addProduct(productDTO, testBasketId);
	}
	@Test
	public void testShoppingBasketIdExistProductNotExist() {
		int testBasketId = 325;

		ShoppingBasket shoppingBasket = new ShoppingBasket();
		shoppingBasket = shoppingBasketDao.save(shoppingBasket);

		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(1);
		productDTO.setVat(Vat.VALUE_23);
		productDTO.setCost(15d);
		productDTO.setName("Produkt");

		expectedException.expect(NotFoundException.class);
		service.addProduct(productDTO, shoppingBasket.getId());

	}
	@Test
	public void testShoppingBasketIdExistProductExistShoppingBasketItemExist() {
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

		ShoppingBasketDTO result = service.addProduct(mapper.map(product, ProductDTO.class),shoppingBasket.getId());

		final Product copyProduct = product;
		final ShoppingBasketItem copyShoppingBasketItem = shoppingBasketItem;
		Assert.assertEquals(shoppingBasket.getId(), result.getId());
		Assert.assertEquals(1, result.getItems().size());
		Assert.assertTrue(result.getItems().stream().anyMatch(i->i.getProduct().getId().equals(copyProduct.getId())));
		Assert.assertTrue(result.getItems().stream().filter(i->i.getProduct().getId().equals(copyProduct.getId())).findFirst()
				.map(i->i.getAmount()==copyShoppingBasketItem.getAmount()+1).orElse(false));
	}

}
