package com.example.user.simpleui;

import java.util.List;

/**
 * Created by user on 2016/7/27.
 */
public class Order {
    String note;
    String storeInfo;
    List<DrinkOrder> drinkOrders;

    public int total()
    {
        int total = 0;
        for (DrinkOrder drinkOrder :drinkOrders)
        {
            total += drinkOrder.total();
        }
        return total;
    }
}
