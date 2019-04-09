package com.epikat.chaipaani;

import android.util.Log;

import org.junit.Test;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testAPI() {
        int resp = tryAPI();
        assertEquals(200, resp);
    }

    public int tryAPI(){
        try {

            String url = "https://chaipani.herokuapp.com/api/categories";
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(false);
            int responseCode = con.getResponseCode();
            return responseCode;
        }catch (Exception e){
        }
        return 0;
    }

}