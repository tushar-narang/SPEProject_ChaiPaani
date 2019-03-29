package com.epikat.chaipaani;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Login extends AppCompatActivity {

    Button login;
    EditText emailid, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailid = (EditText) findViewById(R.id.Login_emailid);
        password = (EditText) findViewById(R.id.Login_password);
        login = (Button) findViewById(R.id.Login_loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new checkLogin().execute(emailid.getText().toString(), password.getText().toString());
            }
        });
    }


    public class checkLogin extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String URL = "http://chaipani.herokuapp.com/api/login";
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

                                Intent i = new Intent(Login.this, Dashboard.class);
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
