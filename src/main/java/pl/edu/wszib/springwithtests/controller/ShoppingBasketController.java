package pl.edu.wszib.springwithtests.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.wszib.springwithtests.dto.ProductDTO;
import pl.edu.wszib.springwithtests.dto.ShoppingBasketDTO;
import pl.edu.wszib.springwithtests.dto.ShoppingBasketDTO;
import pl.edu.wszib.springwithtests.model.ShoppingBasket;
import pl.edu.wszib.springwithtests.service.ProductService;
import pl.edu.wszib.springwithtests.service.ShoppingBasketService;

import java.util.List;

@RestController
@RequestMapping("/shoppingBasket")
public class ShoppingBasketController {

    @Autowired
    private ShoppingBasketService service;

    @GetMapping
    public List<ShoppingBasketDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ShoppingBasketDTO findOne(@PathVariable int id) {
        return service.find(id);
    }

    @PostMapping
    public ShoppingBasketDTO add(@RequestBody ShoppingBasketDTO dto) {
        return service.add(dto);
    }

    @PutMapping
    public ShoppingBasketDTO update(@RequestBody ShoppingBasketDTO dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable int id) {
        service.remove(id);
    }

    @PostMapping("/add")
    public ShoppingBasketDTO addProduct(@RequestBody ProductDTO productDTO, @RequestParam int shoppingBasketId) {
        return service.addProduct(productDTO, shoppingBasketId);
    }

}
