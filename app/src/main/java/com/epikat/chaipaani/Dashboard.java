package com.epikat.chaipaani;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {

    JSONObject userdata;
    CircleImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        String token = getIntent().getStringExtra("token");
        new getUserDetails().execute(token);

        iv = (CircleImageView) findViewById(R.id.Dashboard_userImage);
    }

    public void Dashboard_viewMenu(View view){
        Intent i = new Intent(this, menuActivity.class);
        startActivity(i);
    }




    public class getUserDetails extends AsyncTask<String, Void, String> {
        JSONObject obj;
        @Override
        protected String doInBackground(final String... strings) {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String URL = "https://chaipani.herokuapp.com/api/user";
                JSONObject jsonBody = new JSONObject();
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("response here", response);
                        Log.i("VOLLEY here", response);
                        updateUserName(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w("error", error.toString());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        // Basic Authentication
                        //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                        headers.put("Authorization", "Bearer " + strings[0]);
                        return headers;
                    }
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {

                        String responseString = "";
                        if (response != null) {
                                if(response.statusCode == 200) {
                                    responseString = new String(response.data);
                                    Log.w("details", responseString);
                                }
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                requestQueue.add(stringRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public void updateUserName(String response){
        Log.w("update ", response);
        try {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.Dashboard_loadingBar);
            TextView username = (TextView) findViewById(R.id.Dashboard_nameOfUser);
            progressBar.setVisibility(View.GONE);
            JSONObject obj = new JSONObject(response);
            username.setText(obj.getString("name"));
            username.setVisibility(View.VISIBLE);

            RequestQueue mRequestQueue = Volley.newRequestQueue(this.getApplicationContext());
            ImageRequest imageRequest = new ImageRequest(obj.getString("profile_pic"), new BitmapListener(), 0, 0, null, null, new MyErrorListener());
            mRequestQueue.add(imageRequest);
        }catch (JSONException e){

        }


    }

    private class BitmapListener implements Response.Listener<Bitmap> {
        @Override
        public void onResponse(Bitmap response) {
            iv.setImageBitmap(response);

        }
    }

    private class MyErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            iv.setImageResource(R.drawable.addbutton);
        }
    }
}
