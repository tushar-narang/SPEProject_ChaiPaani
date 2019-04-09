package com.epikat.chaipaani;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    Button login;
    EditText emailid, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView iv = (ImageView) findViewById(R.id.main_appname);
                ObjectAnimator animation = ObjectAnimator.ofFloat(iv, "translationY", -500f);
                animation.setDuration(2000);
                animation.start();

            }
        }, 2000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LinearLayout loginlayout = findViewById(R.id.main_loginlayout);
                loginlayout.setVisibility(View.VISIBLE);
                ProgressBar loading = findViewById(R.id.main_loadingbar);
                loading.setVisibility(View.GONE);

            }
        }, 4000);


        emailid = (EditText) findViewById(R.id.Login_emailid);
        password = (EditText) findViewById(R.id.Login_password);
        login = (Button) findViewById(R.id.Login_loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar loading = findViewById(R.id.Login_loading);
                loading.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
                new checkLogin().execute(emailid.getText().toString(), password.getText().toString());
            }
        });
//        RequestParams params = new RequestParams();
//        params.put("email", "Bhavesh.Gulecha@iiitb.org");
//        params.put("password", "12345678");
//        client.post("chaipani.herokuapp.com/api/login", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                Log.w("success", responseBody.toString());
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.w("failure", responseBody.toString());
//            }
//        });

    }




    public class checkLogin extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String URL = "https://chaipani.herokuapp.com/api/login";
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("email", strings[0]);
                jsonBody.put("password", strings[1]);
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("response", response);
                        // Log.i("VOLLEY", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w("error", error.toString());

                        ProgressBar loading = findViewById(R.id.Login_loading);
                        loading.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);

                        TextView errortext = (TextView) findViewById(R.id.Login_error);
                        errortext.setText(error.toString());
                    }
                }) {
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
                        String token = "";
                        if (response != null) {
                            try{
                                String responseString = new String(response.data);
                                JSONObject obj = new JSONObject(responseString);
                                token = obj.getJSONObject("data").getString("token");
                                Log.w("token", token);



                                Intent i = new Intent(MainActivity.this, Dashboard.class);
                                i.putExtra("token", token);
                                startActivity(i);
                                finish();

                            }catch (JSONException e){

                            }

                        }
                        return Response.success(token, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}


