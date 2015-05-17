package com.dropbox.android.sample;

/**
 * Created by shruti on 5/16/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import java.io.File;
import java.io.IOException;


public class EffectActivity extends Activity {


    ShareActionProvider mShareActionProvider;


    private static final boolean USE_OAUTH1 = false;

    private static final String LOG_TAG = EffectActivity.class.getSimpleName();
    ImageView mImage;
    String imageName;
    Bitmap effectBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect);
        imageName = new String(getIntent().getExtras().getString("ImageName"));
        mImage = (ImageView) findViewById(R.id.imageEffect);

        // get photo from the path it is saved to

        File file = new File("/storage/emulated/0/" + imageName);
        Boolean bool = file.exists();
        Uri uri = Uri.fromFile(file);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int effect = getIntent().getExtras().getInt("Effect");
        // based on the option choosen by the user, switch between the effects

        switch (effect) {
            case 1:
                effectBitmap = ImageEffectUtility.roundCorner(bitmap, 75);
                break;
            case 2:
                effectBitmap = ImageEffectUtility.flip(bitmap, 1);
                break;
            case 3:
                // statements // they are executed if variable ==  any of the above c's
                effectBitmap = ImageEffectUtility.applyReflection(bitmap);
                break;
            default:
                // statements // they are executed if none of the above case is satisfied
                break;
        }


        mImage.setImageBitmap(effectBitmap);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_effect, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share_effect);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) menuItem.getActionProvider();

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        return true;
    }

    // this intent is used to share the photo using whatsapp,facebook etc.
    private Intent createShareIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        Bitmap bitmap = effectBitmap;
        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"title", null);
        Uri bmpUri = Uri.parse(pathofBmp);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
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

        if (id == R.id.action_share_effect) {
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
