package com.dropbox.android.sample;
/**
 * Created by shruti on 5/16/2015.
 */
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;


public class ListFiles extends AsyncTask<Void, Void, ArrayList<String>> {
	private DropboxAPI dropboxApi;
	private String path;
	private Handler handler;
	private Context context;

	public ListFiles(DropboxAPI dropboxApi, String path,Context context) {
		this.dropboxApi = dropboxApi;
		this.path = path;
		this.context = context.getApplicationContext();
	}

    // this async task goes through the directory where the photos are stored and add it to an arraylist called files.
	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		ArrayList<String> files = new ArrayList<String>();
		try {
			Entry directory = dropboxApi.metadata(path, 1000, null, true, null);
			for (Entry entry : directory.contents) {
				files.add(entry.fileName());
			}
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		return files;
	}

    // once the background activity completes, the on post execute method starts a new intent
    // that displays all the images that have been taken by the camera.
    //it also passes the array list to the next intent.
	@Override
	protected void onPostExecute(ArrayList<String> result) {

		Intent intent = new Intent(context,DisplayListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       // getApplicationContext().startActivity(intent);
		intent.putStringArrayListExtra("result",result);
		context.startActivity(intent);

	}

}
