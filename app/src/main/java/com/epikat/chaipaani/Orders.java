package com.epikat.chaipaani;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

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
import com.epikat.chaipaani.Adapters.cartAdapter;
import com.epikat.chaipaani.Adapters.ordersAdapter;
import com.epikat.chaipaani.pojo.MenuItem;
import com.epikat.chaipaani.pojo.OrderItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Orders extends AppCompatActivity {

    RecyclerView recyclerView;
    static RecyclerView.Adapter mAdapter;
    public static ArrayList<OrderItem> orderitems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        orderitems = new ArrayList<>();
        mAdapter = new ordersAdapter(getApplicationContext(), orderitems);

        recyclerView = (RecyclerView) findViewById(R.id.orders_recycleList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        new getOrders().execute();


    }


    public class getOrders extends AsyncTask<String, Void, String> {
        JSONObject obj;

        @Override
        protected String doInBackground(final String... strings) {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String URL = "https://chaipani.herokuapp.com/api/orders/user/" + Dashboard.loggedinUser;
                JSONObject jsonBody = new JSONObject();
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateOrdersList(response);
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
                        String responseString = "";
                        if (response != null) {
                            if (response.statusCode == 200) {
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

    public void updateOrdersList(String response) {
        try {
            JSONObject object = new JSONObject(response);
            JSONArray data = object.getJSONArray("data");
            //JSONObject data = object.getJSONObject("data");
            orderitems.clear();
            mAdapter.notifyDataSetChanged();
            for (int i = 0; i < data.length(); i++) {
                OrderItem orderitem = new OrderItem();
                orderitem.setId(data.getJSONObject(i).getInt("id"));
                orderitem.setDate(data.getJSONObject(i).getString("created_at"));
                orderitem.setAmount(data.getJSONObject(i).getInt("amount"));
                orderitem.setStatus(data.getJSONObject(i).getString("order_status"));
                Log.w("item:", orderitem.toString());
                orderitems.add(orderitem);
                mAdapter.notifyItemInserted(orderitems.size());
            }


        } catch (JSONException e) {
            Log.w("excep", e.toString());
        }
    }
}
