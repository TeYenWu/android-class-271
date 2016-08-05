package com.example.user.simpleui;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.LocationListener;

import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import android.os.Handler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

public class OrderDetailActivity extends AppCompatActivity implements GeoCodingTask.GeoCodingResponse, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, RoutingListener {

    final static int ACCESS_FINE_LOCATION_REQUEST_CODE = 0;

    GoogleMap map;
    LatLng storeLocation;

    GoogleApiClient googleApiClient;

    LocationRequest locationRequest;

    Polyline polyline;

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
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("NTU").snippet("Hello Marker");
        map.moveCamera(cameraUpdate);
        map.addMarker(markerOptions);

        storeLocation = latLng;

        createGoogleApiClient();
    }

    private void createGoogleApiClient()
    {
        if(googleApiClient == null)
        {
            googleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
            googleApiClient.connect();

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
            }
            return;
        }

        if(locationRequest == null)
        {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        LatLng start;
        if(location != null) {
            start = new LatLng(location.getLatitude(), location.getLongitude());

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 17));

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .alternativeRoutes(false)
                    .waypoints(start, storeLocation)
                    .withListener(this)
                    .build();

            routing.execute();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ACCESS_FINE_LOCATION_REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                onConnected(null);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(polyline == null)
        {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(location.getLatitude(), location.getLongitude()), storeLocation)
                    .withListener(this)
                    .build();

            routing.execute();
            return;
        }

        List<LatLng> points = polyline.getPoints();

        int index = -1;

        for(int i=0; i < points.size();i ++)
        {
            if(i < points.size() -1)
            {
                LatLng point1 = points.get(i);
                LatLng point2 =  points.get(i+1);
                double offset = 0.0001;

                Double maxLat = Math.max(point1.latitude, point2.latitude) + offset;
                Double maxLng = Math.max(point1.longitude, point2.longitude) + offset;
                Double minLat = Math.min(point1.latitude, point2.latitude) - offset;
                Double minLng = Math.min(point1.longitude, point2.longitude) - offset;
                if(location.getLatitude() >= minLat && location.getLatitude() <= maxLat && location.getLongitude() >= minLng && location.getLongitude() <= maxLng)
                {
                    index = i;
                    break;
                }
            }
        }

        if(index != -1)
        {
            for (int i = index -1 ; i>=0 ; i--)
            {
                points.remove(0);
            }
            points.set(0, new LatLng(location.getLatitude(), location.getLongitude()));
            polyline.setPoints(points);
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
        Route route = arrayList.get(i);

        List<LatLng> points = route.getPoints();

        PolylineOptions polylineOptions = new PolylineOptions();

        polylineOptions.addAll(points);

        polylineOptions.color(Color.GREEN);
        polylineOptions.width(10);

        polyline = map.addPolyline(polylineOptions);
    }

    @Override
    public void onRoutingCancelled() {

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
