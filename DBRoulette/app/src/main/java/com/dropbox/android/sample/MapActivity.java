package com.dropbox.android.sample;
/**
 * Created by shruti on 5/16/2015.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends Activity {

    GoogleMap Gmap;
    String Latitude, Longitude;
    ArrayList<String> data;
    private static final String LOG_TAG = DisplayListActivity.class.getSimpleName();

    private final String PHOTO_DIR = "/Photos/";
    private static final String APP_KEY = "161dy3jfut7urb9";
    private static final String APP_SECRET = "jmhmj0dchi360a7";

    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;

    DropboxAPI<AndroidAuthSession> mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        data = getIntent().getStringArrayListExtra("imageList");
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        SharedPreferences prefs = this.getSharedPreferences(
                "com.dropbox.android.sample", Context.MODE_PRIVATE);
        Latitude = prefs.getString("latitude", null);
        Longitude = prefs.getString("longitude",null);

        Log.d("LAT", Latitude);
        Log.d("LONG", Longitude);

        Double lat = Double.valueOf(Latitude);
        Double lon = Double.valueOf(Longitude);

        Gmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        Gmap.setMyLocationEnabled(true);

        // Add a marker to the map activity to display current location based on the latitude and longitude.
        Gmap.addMarker(new MarkerOptions().position(
                new LatLng(lat, lon)).title("Tap Here"));
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(lat, lon));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        Gmap.moveCamera(center);
        Gmap.animateCamera(zoom);

        Gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {

                LayoutInflater inflater = LayoutInflater.from(MapActivity.this);
                final View v1 = inflater.inflate(R.layout.marker_layout, null);
                 new DownloadThumbnailTask(MapActivity.this, mApi,
                         PHOTO_DIR, v1,data).execute();
                final AlertDialog builder = new AlertDialog.Builder(MapActivity.this)
                        .setView(v1)
                        .create();

                builder.setOnShowListener(new DialogInterface.OnShowListener() {


                    public void onShow(DialogInterface arg0) {

                    }
                });


                builder.show();

                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

}
