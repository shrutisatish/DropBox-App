package com.dropbox.android.sample;
/**
 * Created by shruti on 5/16/2015.
 */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

public class DisplayListActivity extends Activity {

    ShareActionProvider mShareActionProvider;
    private static final String LOG_TAG = DisplayListActivity.class.getSimpleName();

    private final String PHOTO_DIR = "/Photos/";
    private static final String APP_KEY = "161dy3jfut7urb9";
    private static final String APP_SECRET = "jmhmj0dchi360a7";

    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;

    DropboxAPI<AndroidAuthSession> mApi;
    ListView list;
    ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        data = getIntent().getStringArrayListExtra("result");
        list = (ListView) findViewById(R.id.listView1);

        // using shared preferences get the current location from the GeoTask Async task since the city name(current location)
        // has to be appended to the photo title.

        SharedPreferences prefs = this.getSharedPreferences(
                "com.dropbox.android.sample", Context.MODE_PRIVATE);
        String location = prefs.getString("currentLocation", null);
        ArrayList<String> dataLocationlist=new ArrayList<String>();
       // int i = 0;

        for (String string : data) {
            dataLocationlist.add( string + "_" + location);
           // i++;
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, dataLocationlist);
        list.setAdapter(adapter);

        // clicking on any one og the listview items opens another intent that displays the photo that was taken

        list.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent1 = new Intent(DisplayListActivity.this, ImageActivity.class);
                intent1.putExtra("ImageName", data.get(position).split("_")[0]);
                intent1.putStringArrayListExtra("imageList",data);
                startActivity(intent1);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);

        MenuItem menuItem = menu.findItem(R.id.action_map);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        // this is to specify the menu items. This activity has one menu item that displays the map along with the photos
        // that were taken at a particular location

        if (id == R.id.action_share) {
            return true;
        }
        if (id == R.id.action_map) {
            Intent intent = new Intent(DisplayListActivity.this, MapActivity.class);
            startActivity(intent);
        }

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
            Editor edit = prefs.edit();
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
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
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
