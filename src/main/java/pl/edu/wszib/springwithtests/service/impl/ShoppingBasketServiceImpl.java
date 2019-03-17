package pl.edu.wszib.springwithtests.service.impl;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.wszib.springwithtests.NotFoundException;
import pl.edu.wszib.springwithtests.dao.ProductDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketItemDao;
import pl.edu.wszib.springwithtests.dto.ProductDTO;
import pl.edu.wszib.springwithtests.dto.ShoppingBasketDTO;
import pl.edu.wszib.springwithtests.dto.ShoppingBasketItemDTO;
import pl.edu.wszib.springwithtests.model.Product;
import pl.edu.wszib.springwithtests.model.ShoppingBasket;
import pl.edu.wszib.springwithtests.model.ShoppingBasketItem;
import pl.edu.wszib.springwithtests.service.ShoppingBasketService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShoppingBasketServiceImpl implements ShoppingBasketService {


    @Autowired
    private ShoppingBasketDao dao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private Mapper mapper;

    @Autowired
    ShoppingBasketItemDao shoppingBasketItemDao;

    @Override
    public ShoppingBasketDTO add(ShoppingBasketDTO shoppingBasketItemDTO) {
        return mapper.map(
                dao.save(
                        mapper.map(shoppingBasketItemDTO, ShoppingBasket.class)
                ), ShoppingBasketDTO.class);
    }

    @Override
    public ShoppingBasketDTO update(ShoppingBasketDTO shoppingBasketItemDTO) {
        return mapper.map(
                dao.save(
                        mapper.map(shoppingBasketItemDTO, ShoppingBasket.class)
                ), ShoppingBasketDTO.class);
    }

    @Override
    public void remove(Integer integer) {
        dao.deleteById(integer);
    }

    @Override
    public ShoppingBasketDTO find(Integer integer) {
        return dao.findById(integer)
                .map(p -> mapper.map(p, ShoppingBasketDTO.class))
                .orElse(null);
    }

    @Override
    public List<ShoppingBasketDTO> findAll() {
        return  dao.findAll().stream()
                .map(p -> mapper.map(p, ShoppingBasketDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ShoppingBasketDTO addProduct(ProductDTO productDTO, Integer shoppingBasketId) {
        ShoppingBasket basket = dao.findById(shoppingBasketId).orElse(null);
        if (basket == null) throw new NotFoundException();
        Product product = mapper.map(productDTO, Product.class);
        if(!productDao.existsById(product.getId())) {
            throw new NotFoundException();
        }
        ShoppingBasketItem item = shoppingBasketItemDao.findByProductIdAndShoppingBasketId(product.getId(), basket.getId());
        if(item != null) {
            item.setAmount(item.getAmount()+1);
        } else {
            item = new ShoppingBasketItem();
            item.setAmount(1);
            item.setProduct(product);
            item.setShoppingBasket(basket);
        }
        shoppingBasketItemDao.save(item);
        basket.setNetValue(countNet(basket));
        basket.setGrossValue(countGross(basket));

        ShoppingBasketDTO basketDTO = mapper.map(basket, ShoppingBasketDTO.class);
        basketDTO.setItems(getItem(basket));
        return basketDTO;
    }
    private double countNet(ShoppingBasket basket) {
        List<ShoppingBasketItem> items = shoppingBasketItemDao.findAllByShoppingBasketId(basket.getId());
        return items.stream().mapToDouble(item ->item.getAmount()*item.getProduct().getCost())
                        .sum();
    }
    private double countGross(ShoppingBasket basket) {
        List<ShoppingBasketItem> items = shoppingBasketItemDao.findAllByShoppingBasketId(basket.getId());
        return items.stream().mapToDouble(item ->item.getAmount()*item.getProduct().getCost()*item.getProduct().getVat().rate)
                .sum();
    }
    private List<ShoppingBasketItemDTO> getItem(ShoppingBasket basket) {
        return shoppingBasketItemDao.findAllByShoppingBasketId(basket.getId())
                                    .stream().map(item -> mapper.map(item, ShoppingBasketItemDTO.class))
                                    .collect(Collectors.toList());
    }
}
