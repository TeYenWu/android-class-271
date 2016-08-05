package com.example.user.simpleui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;

/**
 * Created by user on 2016/8/5.
 */
public class GeoCodingTask extends AsyncTask<String, Void, double[]> {

    WeakReference<GeoCodingResponse> geoCodingResponseWeakReference;

    @Override
    protected double[] doInBackground(String... params) {
//            Log.e("Background Thread ID", Long.toString(Thread.currentThread().getId()));
        double[] latlng = Utils.getLatLngFromAddress(params[0]);
        if(latlng != null)
        {
            return latlng;
        }
        return null;
    }

    @Override
    protected void onPostExecute(double[] latlng) {
        super.onPostExecute(latlng);
        if(latlng!=null)
        {
            LatLng result = new LatLng(latlng[0], latlng[1]);
            if (geoCodingResponseWeakReference.get() != null)
            {
                GeoCodingResponse geoCodingResponse = geoCodingResponseWeakReference.get();
                geoCodingResponse.callbackWithGeoCodingResult(result);
            }
        }
//            Log.e("PostExecute Thread ID", Long.toString(Thread.currentThread().getId()));

    }

    public GeoCodingTask(GeoCodingResponse geoCodingResponse)
    {
        geoCodingResponseWeakReference = new WeakReference<GeoCodingResponse>(geoCodingResponse);
    }

    interface GeoCodingResponse{
        void callbackWithGeoCodingResult(LatLng latLng);
    }
}
