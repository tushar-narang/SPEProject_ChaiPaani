package com.epikat.chaipaani;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.epikat.chaipaani.Adapters.cartAdapter;
import com.epikat.chaipaani.pojo.CartItem;
import com.epikat.chaipaani.pojo.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class cart extends AppCompatActivity {

    RecyclerView recyclerView;
    static RecyclerView.Adapter mAdapter;
    public static ArrayList<MenuItem> menuitems;
    public static TextView placeOrderButton;
    public static int orderAmt;
    public static int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        menuitems = new ArrayList<>();
        mAdapter = new cartAdapter(getApplicationContext(), menuitems);

        recyclerView = (RecyclerView) findViewById(R.id.cart_recycleList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        placeOrderButton = (TextView) findViewById(R.id.placeOrderButton);
        orderAmt = 0;
        loadcartItems();

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("clicked by", Dashboard.loggedinUser + " ");

                AlertDialog.Builder builder = new AlertDialog.Builder(cart.this);
                builder.setMessage("Confirm Order?");
               // builder.setTitle("");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        placeorder();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });
    }

    public void placeorder(){
        if (Dashboard.loggedinUser != 0) {
            try {
                String prods = "\"product_orders\" : [";
                List<CartItem> items = menuActivity.db.getAllItems();
                int i = items.size();
                for (CartItem item : items) {
                    if (i > 1)
                        prods += "{ \"product_id\" : " + item.getId() + ", \"quantity\" : " + item.getQuantity() + " },";
                    else
                        prods += "{ \"product_id\" : " + item.getId() + ", \"quantity\" : " + item.getQuantity() + " }";
                    i--;
                }
                prods += "],";
                JSONObject obj = new JSONObject("" +
                        "{\n" +
                        "\t\"user_id\" : " + Dashboard.loggedinUser + "," +
                        prods +
                        "\"amount\" : " + orderAmt + "}");

                Log.w("orderjson", obj.toString());
                new placeOrder().execute(obj.toString());
            } catch (Exception e) {
                Log.w("exec", e.toString());
            }

        }
    }
    public void loadcartItems() {

        List<CartItem> items = menuActivity.db.getAllItems();
        //menuitems = new ArrayList<>();
        // mAdapter.notifyDataSetChanged();
        counter = items.size();
        for (CartItem item : items) {
//            MenuItem orderitem = new MenuItem();
//            orderitem.setId(item.getId());
//            orderitem.setName(item.getName());
//            menuitems.add(orderitem);
//            mAdapter.notifyItemInserted(menuitems.size());

            new getItemsInCategory().execute(item.getId() + "");
        }
    }

    public class getItemsInCategory extends AsyncTask<String, Void, String> {
        JSONObject obj;

        @Override
        protected String doInBackground(final String... strings) {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String URL = "https://chaipani.herokuapp.com/api/items/" + strings[0];
                JSONObject jsonBody = new JSONObject();
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("response here", response);
                        Log.i("VOLLEY here", response);
                        updateCartItems(response);
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

    public void updateCartItems(String response) {
        Log.w("update item function123", response);
        try {
            JSONObject object = new JSONObject(response);
            // JSONArray data = object.getJSONArray("data");
            JSONObject data = object.getJSONObject("data");
            MenuItem orderitem = new MenuItem();
            //orderitem.setName(data.getJSONObject(0).getString("name"));
            orderitem.setId(data.getInt("id"));
            orderitem.setName(data.getString("name"));
            orderitem.setPrice(data.getInt("price"));
            orderitem.setImageurl(data.getString("item_pic"));
            Log.w("item:", orderitem.toString());
            menuitems.add(orderitem);
            mAdapter.notifyItemInserted(menuitems.size());


        } catch (JSONException e) {
            Log.w("excep", e.toString());
        }
    }

    public class placeOrder extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(final String... strings) {

                final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String URL = "https://chaipani.herokuapp.com/api/orders";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("LOG_VOLLEY", response);
                        if(response.equals("200")){
                            List<CartItem> items = menuActivity.db.getAllItems();
                            for(CartItem item :items){
                                menuActivity.db.deleteItem(item.getId());
                                menuitems.clear();
                                mAdapter.notifyDataSetChanged();
                                Intent i = new Intent(cart.this, Orders.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("LOG_VOLLEY", error.toString());
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return strings[0] == null ? null : strings[0].getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", strings[0], "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            responseString = String.valueOf(response.statusCode);
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                requestQueue.add(stringRequest);

            return null;
        }
    }
}
