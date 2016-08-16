package com.example.user.simpleui;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Order order = getIntent().getParcelableExtra("order");


//        Log.d("Debug", order.getDrinkOrders().get(0).getDrink().getImageParseFile().getUrl());
//        Picasso.with(inflater.getContext()).load(order.getDrinkOrders().getImageURL()).into(holder.imageView);

        final TextView textView = (TextView) findViewById(R.id.textView10);
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                textView.setText("HelloWorld");
                Log.e("HandlerThreadID", Long.toString(Thread.currentThread().getId()));
                Log.e("HandlerThreadName", Thread.currentThread().getName());
                return false;
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("HelloWorld");
            }
        }, 1000);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                    handler.sendMessage(new Message());
                    Log.e("ThreadID", Long.toString(Thread.currentThread().getId()));
                    Log.e("ThreadName", Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
//        thread.start();
        Log.e("ActivityThreadID", Long.toString(Thread.currentThread().getId()));
        Log.e("ActivityThreadName",Thread.currentThread().getName());


//        HandlerThread
    }
}
