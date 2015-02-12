package me.roovent.morning;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.squareup.picasso.Picasso;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * This service means to download images in background which will be shown when alarm ringing.
 * Usually it will be called a few minutes before alarm ringing.
 * The Picasso library will download a image and cache it. Its url is shared by SharedPreferences.
 *
 * Created by Qing Yang on 1/31/15.
 */
public class AlarmImageService extends IntentService {
    public static final String PREFERENCE_IMAGE_URL = "preference_image_url";

    public AlarmImageService() {
        super(AlarmImageService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            /* Retrieve the image url */
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(RingingImage.BASE_URL).build();
            RingingImage.ImageGetter image = restAdapter.create(RingingImage.ImageGetter.class);
            String imageUrl = image.getImageUrl().url;

            /* Cache the image */
            Picasso.with(this).load(imageUrl).fetch();

            /* Save the url which will be shared by AlarmRingingActivity */
            getSharedPreferences(PREFERENCE_IMAGE_URL, Context.MODE_MULTI_PROCESS)
                    .edit()
                    .putString(PREFERENCE_IMAGE_URL, imageUrl)
                    .commit();

            Log.i(getClass().getName(), "Image downloading: " + imageUrl);
        } catch (RetrofitError e) {
            Log.e(getClass().getName(),
                    String.format("Type: %s; Message: %s", e.getKind(), e.getMessage()));
        }
    }
}
