package me.roovent.morning;

import retrofit.http.GET;

/**
 * Created by Qing Yang on 1/28/15.
 */

public class AlarmRingingImage {
    /* "http://floating-earth-4908.herokuapp.com/clock_images/random_image_url" */
    public static final String BASE_URL = "http://floating-earth-4908.herokuapp.com";

    public interface ImageGetter {
        @GET("/clock_images/random_image_url")
        AlarmRingingImage getImageUrl();
    }

    public String url;
}
