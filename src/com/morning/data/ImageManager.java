package com.morning.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.morning.Constants;
import com.morning.network.NetworkHelper;
import com.morning.network.NetworkHelper.ResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class ImageManager {

    private static Bitmap image = null;

    public static Bitmap getRandomImage() {
        if (image == null) {
            /* Empty Bitmap */
            ImageManager.image = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
        }
        return ImageManager.image;
    }

    public static void downloadImage(final Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (!(networkInfo != null && networkInfo.isConnected())) {
			/* Network is NOT connected. */
            loadDefaultImage(context);
            return;
        }

		/* Network is connected. */
        NetworkHelper.get(Constants.URL_GET_IMAGE, new ResponseHandler() {
            @Override
            public void response(byte[] bytes) {
                String res = new String(bytes);
                try {

                    final String url = new JSONObject(res).getString("url");

                    NetworkHelper.get(url, new ResponseHandler() {
                        @Override
                        public void response(byte[] bytes) {
                            Bitmap img = BitmapFactory.decodeByteArray(bytes,
                                    0, bytes.length);
                            // new ImageDbHandler(context).addImage(url, img);
                            ImageManager.image = img;
                        }
                    });

                } catch (JSONException e) {
                    Log.e(Constants.TAG, e.getMessage());
                    loadDefaultImage(context);
                }
            }
        });
    }

    private static void loadDefaultImage(Context context) {
        InputStream is;
        try {
            is = context.getAssets().open("images/roy_goodman.png");
            ImageManager.image = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(Constants.TAG, e.getMessage());
			/* Empty Bitmap */
            ImageManager.image = Bitmap.createBitmap(0, 0, Config.ARGB_8888);
        }
    }
}
