package com.epikat.chaipaani.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.epikat.chaipaani.Dashboard;
import com.epikat.chaipaani.R;
import com.epikat.chaipaani.pojo.MenuItem;
import com.epikat.chaipaani.pojo.OrderItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ordersAdapter extends RecyclerView.Adapter<ordersAdapter.MenuView> {

    List<OrderItem> items_list;
    Context context;

    ArrayList<String> ordertemp;
    ArrayList<String> ordertemp2;
    TextView selectedprodDislpay;

    public ordersAdapter(Context context, ArrayList<OrderItem> data) {
        this.context = context;
        this.items_list = data;
    }

    @NonNull
    @Override
    public ordersAdapter.MenuView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.individual_orderitem , viewGroup , false);
        ordersAdapter.MenuView vh = new ordersAdapter.MenuView(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuView menuView, int i) {
        final OrderItem item = items_list.get(i);
        Log.w("updating", item.toString());
        final TextView orderno = menuView.orderno;
        orderno.setText("Order No: " + item.getId() + "");
        TextView date = menuView.orderDate;
        date.setText("Date: " + item.getDate());
        TextView amount = menuView.amount;
        amount.setText("Total: " + item.getAmount() + "");
        TextView status = menuView.status;

        String s = "\u25CF " + item.getStatus();
        SpannableString ss1=  new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(1.5f), 0,1, 0); // set size
        if(item.getStatus().equals("FINISHED"))
            ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#008577")), 0, s.length(), 0);// set color
        else if(item.getStatus().equals("NOT ACCEPTED"))
            ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#D81B60")), 0, s.length(), 0);// set color
        status.setText(ss1);

        RelativeLayout clickableLayout = (RelativeLayout) menuView.clickableLayout;
        clickableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView products = menuView.products;
                TextView quantities = menuView.quantities;

                if(products.getVisibility() == View.VISIBLE){
                    products.setVisibility(View.GONE);
                    quantities.setVisibility(View.GONE);
                }else{
                    products.setVisibility(View.VISIBLE);
                    quantities.setVisibility(View.VISIBLE);
                    new getOrders().execute(orderno, products, quantities);
                }
            }
        });
    }


    @Override
    public int getItemCount() {



        return items_list.size();
    }

    class MenuView extends RecyclerView.ViewHolder{

        TextView orderno, orderDate, amount, status, products, quantities;
        RelativeLayout clickableLayout;
        public MenuView(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        public void initView(final View itemView){
            orderno = (TextView) itemView.findViewById(R.id.individual_orderitem_orderno);
            orderDate = (TextView) itemView.findViewById(R.id.individual_orderitem_date);
            amount = (TextView) itemView.findViewById(R.id.individual_orderitem_amount);
            status = (TextView) itemView.findViewById(R.id.individual_orderitem_status);
            products = (TextView) itemView.findViewById(R.id.individual_orderitem_products);
            quantities = (TextView) itemView.findViewById(R.id.individual_orderitem_quantity);
            clickableLayout = (RelativeLayout) itemView.findViewById(R.id.individual_orderitem_relLayout);
        }
    }


    public class getOrders extends AsyncTask<TextView, Void, String> {
        JSONObject obj;

        @Override
        protected String doInBackground(final TextView... textviews) {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                String URL = "https://chaipani.herokuapp.com/api/orders/items/" + textviews[0].getText().toString();
                JSONObject jsonBody = new JSONObject();
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("response here", response);
                        Log.i("VOLLEY here", response);
                        updateOrdersList(response, textviews[1], textviews[2]);
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

    public void updateOrdersList(String response, TextView prodview, TextView quanview) {

        try {
            JSONObject object = new JSONObject(response);
            JSONArray data = object.getJSONArray("data");
            prodview.setText("");
            quanview.setText("");
            ordertemp = new ArrayList<>();
            ordertemp2 = new ArrayList<>();
            selectedprodDislpay = prodview;
            for (int i = 0; i < data.length(); i++) {
//                String actText = prodview.getText().toString();
//                prodview.setText(data.getJSONObject(i).get("item_id").toString() + " " + actText);
//                Log.w("sending text", prodview.getText().toString());
               // prodview.setText(prodview.getText() + " " + data.getJSONObject(i).get("item_id").toString() );
                //new getProductNames().execute(prodview).get();
                //quanview.setText(quanview.getText() + "\n" +   data.getJSONObject(i).get("quantity").toString());
                ordertemp.add(data.getJSONObject(i).get("item_id").toString() + " " + data.getJSONObject(i).get("quantity").toString());
                new getProductNames().execute(ordertemp.get(i));
            }

        } catch (Exception e) {
            Log.w("excep", e.toString());
        }
    }

    public class getProductNames extends AsyncTask<String, Void, String> {
        JSONObject obj;

        @Override
        protected String doInBackground(final String... strings) {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                //final String id = textViews[0].getText().toString().substring(0,1);
                //Log.w("checking for:", textViews[0].getText().toString() +"------" +textViews[0].getText().toString().substring(1,2) );
                String URL = "https://chaipani.herokuapp.com/api/items/" + strings[0].split(" ")[0];
                JSONObject jsonBody = new JSONObject();
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("response here", response);
                        Log.i("VOLLEY here", response);
                        updateOrdersListWithNames(response, strings[0]);
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

    public void updateOrdersListWithNames(String response, String string) {
        Log.w("update item function123", response);
        try {
            JSONObject object = new JSONObject(response);
            JSONObject data = object.getJSONObject("data");
            //String actdata = prodview.getText().toString();
           // actdata = actdata.substring(1);
               // prodview.setText(actdata + "\n" +   data.get("name").toString());
            //actdata = actdata.replace(" " + id + " "," " + data.get("name").toString() + " ");
            //prodview.setText(actdata);
           // Log.w("sdjb", string + " " + data.get("name").toString() );
            ordertemp2.add(string.split(" ")[1] + " " + data.get("name").toString());
            Log.w("final list", ordertemp2.toString());
            selectedprodDislpay.setText("");
            for (String s: ordertemp2)
                selectedprodDislpay.setText(selectedprodDislpay.getText() + "\n" + s);
        } catch (JSONException e) {
            Log.w("excep", e.toString());
        }
    }
}
