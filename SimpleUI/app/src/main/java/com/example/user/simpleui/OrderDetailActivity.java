package com.example.user.simpleui;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import android.os.Handler;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.logging.LogRecord;

public class OrderDetailActivity extends AppCompatActivity implements GeoCodingTask.GeoCodingResponse{

    GoogleMap map;

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

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                (new GeoCodingTask(OrderDetailActivity.this)).execute("台北市羅斯福路四段一號");
            }
        });

        ImageView staticMapImageView = (ImageView)findViewById(R.id.staticMapImageView);


        Log.e("Main Thread ID", Long.toString(Thread.currentThread().getId()));
    }

    @Override
    public void callbackWithGeoCodingResult(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        map.moveCamera(cameraUpdate);

    }
//
//    public static class GeoCodingTask extends AsyncTask<String, Void, Bitmap>{
//
//        WeakReference<ImageView> imageViewWeakReference;
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
////            Log.e("Background Thread ID", Long.toString(Thread.currentThread().getId()));
//            double[] latlng = Utils.getLatLngFromAddress(params[0]);
//            if(latlng != null)
//            {
//                return Utils.getStaticMapFromLatLng(latlng);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            super.onPostExecute(bitmap);
//            if(bitmap!=null)
//            {
//                if(imageViewWeakReference.get() != null)
//                {
//                    ImageView imageView = imageViewWeakReference.get();
//                    imageView.setImageBitmap(bitmap);
//
//                }
//            }
////            Log.e("PostExecute Thread ID", Long.toString(Thread.currentThread().getId()));
//
//        }
//
//        public GeoCodingTask(ImageView imageView)
//        {
//            imageViewWeakReference = new WeakReference<ImageView>(imageView);
//        }
//    }
}
