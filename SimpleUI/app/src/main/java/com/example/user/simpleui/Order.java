package com.example.user.simpleui;

import android.app.ActionBar;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/7/27.
 */
@ParseClassName("Order")
public class Order extends ParseObject implements Parcelable{

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


    public static void getOrdersFromLocal(final FindCallback<Order> findCallback)
    {
        getQuery().fromLocalDatastore().findInBackground(findCallback);
    }

    public static void getOrdersFromRemote(final FindCallback<Order> callback)
    {
        getQuery().findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> objects, ParseException e) {
                if(e == null)
                {
                    Order.pinAllInBackground("Order", objects);
                    callback.done(objects, e);
                }
                else
                {
                    getOrdersFromLocal(callback);
                }
            }
        });
    }


    public static ParseQuery<Order> getQuery()
    {
        ParseQuery<Order> parseQuery = ParseQuery.getQuery(Order.class);
        parseQuery.include("drinkOrders");
        parseQuery.include("drinkOrders.drink");
        return parseQuery;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if(this.getObjectId() == null)
        {
            dest.writeInt(0);
            dest.writeString(this.getNote());
            dest.writeString(this.getStoreInfo());
            dest.writeParcelableArray((DrinkOrder[])this.getDrinkOrders().toArray(), flags);
        }
        else
        {
            dest.writeInt(1);
            dest.writeString(this.getObjectId());
        }

    }

    public static Order getOrderFromCache(String objectId)
    {
        try {
            return getQuery().fromLocalDatastore().get(objectId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Order()
    {
        super();
    }

    protected Order(Parcel in) {
        super();
//        this.set
        this.setNote(in.readString());
        this.setStoreInfo(in.readString());
        this.setDrinkOrders(in.readArrayList(Order.class.getClassLoader()));
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            int isDraft = source.readInt();
            if(isDraft == 0)
            {
                return new Order(source);
            }
            else
            {
                String objectId = source.readString();
                return  Order.getOrderFromCache(objectId);
            }


        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
