package com.morning.network;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NetworkHelper {
    public static void get(String url, ResponseHandler rh) {
        HttpParam hp = new HttpParam(url, rh);
        new HttpAsyncTask().execute(hp);
    }

    public static interface ResponseHandler {
        public void response(byte[] btyes);
    }

    private static class HttpParam {
        String url;
        ResponseHandler resHandler;
        byte[] bytes;

        HttpParam(String url, ResponseHandler rh) {
            this.url = url;
            this.resHandler = rh;
        }
    }

    private static class HttpAsyncTask extends
            AsyncTask<HttpParam, Void, HttpParam> {
        @Override
        protected HttpParam doInBackground(HttpParam... hps) {
            HttpParam hp = hps[0];
            assert hp != null;

            InputStream inputStream = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(
                        hp.url));
                inputStream = httpResponse.getEntity().getContent();
            } catch (IOException e) {
                Log.e("alarm", e.getMessage());
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];

            try {
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            hp.bytes = buffer.toByteArray();
            return hp;
        }

        @Override
        protected void onPostExecute(HttpParam hp) {
            if (hp.resHandler != null) {
                hp.resHandler.response(hp.bytes);
            }
        }
    }
}
