package com.example.user.simpleui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by user on 2016/7/28.
 */
public class DrinkAdapter extends BaseAdapter {

    List<Drink> drinks;
    LayoutInflater inflater;

    public DrinkAdapter(Context context, List<Drink> drinks)
    {
        this.drinks = drinks;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return drinks.size();
    }

    @Override
    public Object getItem(int position) {
        return drinks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.listview_drink_item, null);
            holder = new Holder();
            holder.drinkNameTextView = (TextView)convertView.findViewById(R.id.drinkNameTextView);
            holder.lPriceTextView = (TextView)convertView.findViewById(R.id.lPriceTextView);
            holder.mPriceTextView = (TextView)convertView.findViewById(R.id.mPriceTextView);
            holder.imageView = (ImageView)convertView.findViewById(R.id.imageView);

            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder)convertView.getTag();
        }

        Drink drink = drinks.get(position);

        holder.drinkNameTextView.setText(drink.name);
        holder.lPriceTextView.setText(String.valueOf(drink.lPrice));
        holder.mPriceTextView.setText(String.valueOf(drink.mPrice));
        holder.imageView.setImageResource(drink.imageId);

        return convertView;
    }

    class Holder{
        TextView drinkNameTextView;
        TextView lPriceTextView;
        TextView mPriceTextView;
        ImageView imageView;
    }
}
