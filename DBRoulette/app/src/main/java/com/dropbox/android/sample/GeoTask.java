package com.dropbox.android.sample;
/**
 * Created by shruti on 5/16/2015.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class GeoTask extends AsyncTask<Void, Void, String> {
    Context mContext;
    Double latitude,longitude;
    ProgressDialog progressDialog;
    String location, state;

    public GeoTask(Context mContext, Double latitude, Double longitude) {
        this.mContext = mContext;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    @Override
    protected String doInBackground(Void... params) {
        List<Address> addressList = null;

        Geocoder geoCoder = new Geocoder(mContext);
        try {
            addressList = geoCoder.getFromLocation(latitude,
                    longitude, 1);


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return addressList.get(0).getLocality().toString();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Getting Location...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();
        SharedPreferences prefs = mContext.getSharedPreferences(
                "com.dropbox.android.sample", Context.MODE_PRIVATE);
        prefs.edit().putString("currentLocation",result).apply();
        if (result == null) {
            Toast.makeText(mContext,
                    "Could not get location information!",
                    Toast.LENGTH_SHORT).show();
        } else {
            //Location.setText(result.get(0).getLocality().toString()); // Charlotte
            //location = result.get(0).getLocality().toString();
           // state = result.get(0).getAdminArea();  //NC
        }
    }
}


