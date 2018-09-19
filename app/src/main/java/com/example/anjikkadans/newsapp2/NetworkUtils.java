package com.example.anjikkadans.newsapp2;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {

    // tag for log messages
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public NetworkUtils() {
    }

    //Returns jsonResultString from the url String produced
    public List<News> getNews(String requestUrl) {
        //creates URL object
        URL url = createURL(requestUrl);

        //perform http request to the URL and get json response
        List<News> newsList = null;
        try {
            newsList = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsList;
    }

    //Takes URL parameter and makes request to the server, returns a String
    private static List<News> makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //if connection is successful
            //then read input stream and parse response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "makeHttpRequest: Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "makeHttpRequest: Problem retrieving news stories.", e);
        } finally {
            if (urlConnection == null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return getNewsList(jsonResponse);
    }

    //Returns a URL from a String object
    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "createURL: Problem building the url.", e);
        }
        return url;

    }

    //Converts the input stream from server into a String with the JSON response
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder outStream = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                outStream.append(line);
                line = reader.readLine();
            }
        }
        return outStream.toString();
    }

    //traverse through the jsonString returns list of news object
    private static List<News> getNewsList(String jsonString) {

        List<News> newsList = new ArrayList<>();
        if (jsonString == null || jsonString.length() == 0) {
            return null;
        }
        try {

            JSONObject baseJSONObject = new JSONObject(jsonString);
            JSONObject jsonObject = baseJSONObject.getJSONObject("response");
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject newsJSONObject = jsonArray.getJSONObject(i);

                String newsType = newsJSONObject.getString("type");
                String newsTitle = newsJSONObject.getString("webTitle");
                String newsURL = newsJSONObject.getString("webUrl");
                String newsTopic = newsJSONObject.getString("sectionName");

                JSONArray authorArray = newsJSONObject.getJSONArray("tags");
                JSONObject authorObject = authorArray.getJSONObject(0);
                String authorName = authorObject.getString("webTitle");

                News newNews = new News(newsTitle, newsType, newsURL, newsTopic, authorName);
                newsList.add(newNews);
                Log.v(TAG, "new item added to news list " + newNews.toString());

            }
        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "error parsing json data");
        }
        return newsList;
    }


}
