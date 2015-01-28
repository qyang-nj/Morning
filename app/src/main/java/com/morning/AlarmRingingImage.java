package com.morning;

import retrofit.http.GET;

/**
 * Created by Qing Yang on 1/28/15.
 */

public class AlarmRingingImage {
    public static final String BASE_URL = "http://floating-earth-4908.herokuapp.com";

    public interface ImageGetter {
        @GET("/clock_images/random_image_url")
        AlarmRingingImage getImageUrl();
    }

    public String url;
}