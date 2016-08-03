package com.example.user.simpleui;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by user on 2016/8/1.
 */

@ParseClassName("DrinkOrder")
public class DrinkOrder extends ParseObject implements Parcelable {

    static final String DRINK_COL = "drink";
    static final String LNUMBER_COL = "lNumber";
    static final String MNUMBER_COL = "mNumber";
    static final String ICE_COL = "ice";
    static final String SUGAR_COL = "sugar";
    static final String NOTE_COL = "note";

    public DrinkOrder(Drink drink)
    {
        super();
        this.setDrink(drink);
    }

    public DrinkOrder(){ super();}

    public int total()
    {
//        return 0;
        Drink drink = getDrink();
        if(drink != null)
            return getDrink().getlPrice() * getlNumber() + getDrink().getmPrice() * getmNumber();
        else
            return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if(getObjectId() == null)
        {
            dest.writeInt(0);
            dest.writeParcelable(this.getDrink(), flags);
            dest.writeInt(this.getmNumber());
            dest.writeInt(this.getlNumber());
            dest.writeString(this.getIce());
            dest.writeString(this.getSugar());
            dest.writeString(this.getNote());
        }
        else
        {
            dest.writeInt(1);
            dest.writeString(getObjectId());
        }

    }

    protected DrinkOrder(Parcel in) {
        super();
        this.setDrink((Drink)in.readParcelable(Drink.class.getClassLoader()));
        this.setmNumber(in.readInt());
        this.setlNumber(in.readInt());
        this.setIce(in.readString());
        this.setSugar(in.readString());
        this.setNote(in.readString());
    }

    public static final Parcelable.Creator<DrinkOrder> CREATOR = new Parcelable.Creator<DrinkOrder>() {
        @Override
        public DrinkOrder createFromParcel(Parcel source) {
            int isDraft = source.readInt();
            if (isDraft == 0)
            {
                return new DrinkOrder(source);
            }
            else {
                return getDrinkOrderFromCache(source.readString());
            }
        }

        @Override
        public DrinkOrder[] newArray(int size) {
            return new DrinkOrder[size];
        }
    };

    public Drink getDrink() {
        return (Drink)getParseObject(DRINK_COL);
    }

    public void setDrink(Drink drink) {
        put(DRINK_COL, drink);
    }

    public int getmNumber() {
        return getInt(MNUMBER_COL);
    }

    public void setmNumber(int mNumber) {
        put(MNUMBER_COL, mNumber);
    }

    public int getlNumber() {
        return getInt(LNUMBER_COL);
    }

    public void setlNumber(int lNumber) {
        put(LNUMBER_COL, lNumber);
    }

    public String getIce() {
        return getString(ICE_COL);
    }

    public void setIce(String ice) {
       put(ICE_COL, ice);
    }

    public String getSugar() {
        return getString(SUGAR_COL);
    }

    public void setSugar(String sugar) {
        put(SUGAR_COL, sugar);
    }

    public String getNote() {
        return getString(NOTE_COL);
    }

    public void setNote(String note) {
        put(NOTE_COL, note);
    }

    public static ParseQuery<DrinkOrder> getQuery() { return  ParseQuery.getQuery(DrinkOrder.class);}

    public static DrinkOrder getDrinkOrderFromCache(String objectId)
    {
        try {
            DrinkOrder drinkOrder = getQuery().fromLocalDatastore().get(objectId);
            return drinkOrder;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DrinkOrder.createWithoutData(DrinkOrder.class, objectId);
    }

}
