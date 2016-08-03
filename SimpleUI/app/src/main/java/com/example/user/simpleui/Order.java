package com.example.user.simpleui;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by user on 2016/7/27.
 */
@ParseClassName("Order")
public class Order extends ParseObject {

    static final String NOTE_COL = "note";
    static final String STOREINFO_COL = "storeInfo";
    static final String DRINKORDERS_COL = "drinkOrders";

    public int total()
    {
        int total = 0;
        for (DrinkOrder drinkOrder :getDrinkOrders())
        {
            total += drinkOrder.total();
        }
        return total;
    }

    public String getNote() {
        return getString(NOTE_COL);
    }

    public void setNote(String note) {
        put(NOTE_COL, note);
    }

    public String getStoreInfo() {
        return getString(STOREINFO_COL);
    }

    public void setStoreInfo(String storeInfo) {
        put(STOREINFO_COL, storeInfo);
    }

    public List<DrinkOrder> getDrinkOrders() {
        return getList(DRINKORDERS_COL);
    }

    public void setDrinkOrders(List<DrinkOrder> drinkOrders) {
        put(DRINKORDERS_COL, drinkOrders);
    }

    public static ParseQuery<Order> getQuery()
    {
        ParseQuery<Order> query = ParseQuery.getQuery(Order.class);
        query.include(DRINKORDERS_COL);
        query.include(DRINKORDERS_COL + "." + DrinkOrder.DRINK_COL);
        return query;
    }
}
