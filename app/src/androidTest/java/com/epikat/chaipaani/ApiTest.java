package com.epikat.chaipaani;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ApiTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        //assertEquals("com.epikat.chaipaani", appContext.getPackageName());
        //boolean resp = isConnectedToServer("https://chaipani.herokuapp.com/api/categoriesereee",10000);
        int resp = tryAPI();
        //assertEquals(200, resp);
    }
    public boolean isConnectedToServer(String url, int timeout) {
        try {
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();

            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }

    public int tryAPI(){
        try {

            String url = "https://chaipani.herokuapp.com/api/categories";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(false);
            int responseCode = con.getResponseCode();
            Log.w("resp", responseCode+"");
            return responseCode;
        }catch (Exception e){

        }
        return 0;
    }
}