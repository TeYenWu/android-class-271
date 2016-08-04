package com.example.user.simpleui;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import android.os.Handler;
import java.util.logging.LogRecord;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Order order = getIntent().getParcelableExtra("order");

        final TextView noteTextView = (TextView)findViewById(R.id.noteTextView);
        TextView orderResultTextView = (TextView)findViewById(R.id.orderResultsTextView);
        TextView storeInfoTextView = (TextView)findViewById(R.id.storeInfoTextView);


        noteTextView.setText(order.getNote());
        storeInfoTextView.setText(order.getStoreInfo());

        String orderResultsText = "";
        for (DrinkOrder drinkOrder: order.getDrinkOrders())
        {
            String mNumber = String.valueOf(drinkOrder.getmNumber());
            String lNumber = String.valueOf(drinkOrder.getlNumber());
            String drinkName = drinkOrder.getDrink().getName();
            orderResultsText += drinkName + "  M: " + mNumber + "  L:  " + lNumber + "\n";
        }

        orderResultTextView.setText(orderResultsText);


        final TextView testTextView = (TextView)findViewById(R.id.testTextView);

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                testTextView.setText("Hello Handler");
                Log.e("Handler Thread ID", Long.toString(Thread.currentThread().getId()));
                return false;
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                testTextView.setText("Hello Handler POST DELAY");
            }
        },10000);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
//                    testTextView.setText("Hello Thread");
                    Log.e("Thread ID", Long.toString(Thread.currentThread().getId()));
                    handler.sendMessage(new Message());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        Log.e("Main Thread ID", Long.toString(Thread.currentThread().getId()));
    }
}
