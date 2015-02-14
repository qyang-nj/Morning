package me.roovent.morning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by Qing Yang on 2/13/15.
 */
public class RingingImageProvider {
    private static final String TAG = RingingImageProvider.class.getName();

    private Context mContext;

    public RingingImageProvider(Context context) {
        mContext = context;
    }

    public void init() {
        /* download three images */
        downloadImage();
        downloadImage();
        downloadImage();
    }

    public Bitmap getImage() {
        Bitmap bitmap = null;
        String[] files = mContext.fileList();
        Log.v(TAG, String.format("Currently has %d images downloaded.", files.length));

        if (files.length > 0) {
            String filename = files[new Random().nextInt(files.length)];
            try {
                FileInputStream fis = mContext.openFileInput(filename);
                bitmap = BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }

            /* delete it, because it's been used. */
            mContext.deleteFile(filename);

            /* download a new one */
            downloadImage();
        } else {
            init();
        }
        return bitmap;
    }

    private void downloadImage() {
        new AsyncTask<Void, Void, Bitmap>() {
            private String mFilename = "invalid";

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bitmap = null;
                try {
                     /* Retrieve the image url */
                    RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(RingingImage.BASE_URL).build();
                    RingingImage.ImageGetter image = restAdapter.create(RingingImage.ImageGetter.class);
                    String imageUrl = image.getImageUrl().url;

                    /* Download the image */
                    Log.v(TAG, "Image downloading: " + imageUrl);
                    bitmap = Picasso.with(mContext).load(imageUrl).get();
                    mFilename = FilenameUtils.getBaseName(imageUrl);

                } catch (RetrofitError e) {
                    Log.e(TAG, String.format("Type: %s; Message: %s", e.getKind(), e.getMessage()));
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    String filename = mFilename + ".jpg";
                    try {
                        FileOutputStream fos = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                        Log.v(TAG, "Image saved: " + filename);
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }.execute();
    }
}
