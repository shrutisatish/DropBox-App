package com.dropbox.android.sample;
/**
 * Created by shruti on 5/16/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;


public class ImageActivity extends Activity {
    private static final String APP_KEY = "161dy3jfut7urb9";
    private static final String APP_SECRET = "jmhmj0dchi360a7";
    ShareActionProvider mShareActionProvider;
    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;
    DropboxAPI<AndroidAuthSession> mApi;
    private final String PHOTO_DIR = "/Photos/";
    private static final String LOG_TAG = ImageActivity.class.getSimpleName();
    ImageView mImage;
    String imageName;

    // this activity downloads the photo once the listview is clicked.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);

        imageName = new String(getIntent().getExtras().getString("ImageName"));
        mImage = (ImageView) findViewById(R.id.imageClick);

        // it starts a new async task to display the photo taken
        DownloadPicture download = new DownloadPicture(ImageActivity.this, mApi, PHOTO_DIR, mImage, imageName);
        download.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) menuItem.getActionProvider();

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.



        return true;
    }

    // this method is used to share the image

    //File root = Environment.getExternalStorageDirectory();
    private Intent createShareIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        File file = new File("/storage/emulated/0/" + imageName);
        Boolean bool = file.exists();
        Uri uri = Uri.fromFile(file);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        // shareIntent.putExtra(Intent.EXTRA_TEXT,"text to share");
        return shareIntent;
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // the action bar item click had many options.
        //The photo taken can either be shared directly.
        // or special effects can be applied to the photos before they are shares.
        // some of the special effects that can be applied include rounded corner,flip the image or reflect the image.
        // more effects can be added on a similar line.

        Intent intent = new Intent(ImageActivity.this, EffectActivity.class);
        intent.putExtra("ImageName",imageName);

        if (id == R.id.action_share) {
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }else if(id==R.id.action_round){
            intent.putExtra("Effect",1);
            startActivity(intent);

        }else if(id==R.id.action_flip){
            intent.putExtra("Effect",2);
            startActivity(intent);

        }else if(id==R.id.action_reflect){
            intent.putExtra("Effect",3);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
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
