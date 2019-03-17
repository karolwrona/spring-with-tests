package pl.edu.wszib.springwithtests.dto;

import javax.persistence.Transient;
import java.util.List;

public class ShoppingBasketDTO {

    private Integer id;
    private List<ShoppingBasketItemDTO> items;
    private double grossValue;
    private double netValue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ShoppingBasketItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ShoppingBasketItemDTO> items) {
        this.items = items;
    }

    public double getGrossValue() {
        return grossValue;
    }

    public void setGrossValue(double grossValue) {
        this.grossValue = grossValue;
    }

    public double getNetValue() {
        return netValue;
    }

    public void setNetValue(double netValue) {
        this.netValue = netValue;
    }
}
