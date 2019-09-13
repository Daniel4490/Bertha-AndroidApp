package com.au.berthaau.HttpHelpers;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

// <parameters, progress, result>
// You can pass things into the task with parameters, monitor the progress of the task with progress,
// and get a result back from the task using result.

public class HttpPostAsyncTask extends AsyncTask<String, Void, Void> {

    // This is the JSON body of the post
    String postData;

    // This is a constructor that allows you to pass in the JSON body
    public HttpPostAsyncTask(String postData) {
        if (postData != null) {
            this.postData = postData;
        }
    }

    // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
    @Override
    protected Void doInBackground(String... params) {

        try {
            // This is getting the url from the string we passed in
            URL url = new URL(params[0]);

            // Create the urlConnection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestProperty("Content-Type", "application/json");

            urlConnection.setRequestMethod("POST");


            // OPTIONAL - Sets an authorization header
            // urlConnection.setRequestProperty("Authorization", "someAuthString");

            // Send the post body
            if (this.postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(postData);
                writer.flush();
            }

            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {
                // OPTIONAL - Handle successful request
                //InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

            } else {
                // Status code is not 200 - request failed
                // Do something to handle the error
            }

        } catch (Exception e) {
            Log.d("TAG", e.getLocalizedMessage());
        }
        return null;
    }
}
