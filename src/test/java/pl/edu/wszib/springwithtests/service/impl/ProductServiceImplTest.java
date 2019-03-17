package pl.edu.wszib.springwithtests.service.impl;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import pl.edu.wszib.springwithtests.dao.ProductDao;
import pl.edu.wszib.springwithtests.dto.ProductDTO;
import pl.edu.wszib.springwithtests.model.Product;
import pl.edu.wszib.springwithtests.model.Vat;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(JUnit4.class)
public class ProductServiceImplTest {

    @InjectMocks
    ProductServiceImpl productService;
    @Mock
    ProductDao mockDao;
    @Spy
    Mapper mapper = new DozerBeanMapper();

    @Before
    public void setUp() {
        initMocks(this);
    }
    @Test
    public void testAdd() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Produkt");
        productDTO.setCost(10d);
        productDTO.setVat(Vat.VALUE_23);

        Product product = Mockito.mock(Product.class);
        Mockito.when(mapper.map(productDTO, Product.class)).thenReturn(product);
        Mockito.when(mockDao.save(product)).thenReturn(product);

        productService.add(productDTO);

        Mockito.verify(mockDao, Mockito.times(1)).save(product);
    }
    @Test
    public void testDel() {
        productService.remove(5);
        Mockito.verify(mockDao, Mockito.times(1)).deleteById(5);
    }
}
