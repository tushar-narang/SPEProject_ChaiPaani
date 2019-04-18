 package com.epikat.chaipaani;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import com.epikat.chaipaani.Adapters.menuAdapter;
import com.epikat.chaipaani.DBHandler.DatabaseHandler;
import com.epikat.chaipaani.pojo.CartItem;
import com.epikat.chaipaani.pojo.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

 public class menuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

     private RecyclerView recyclerView;
     private RecyclerView.Adapter mAdapter;
     private RecyclerView.LayoutManager layoutManager;
     ArrayList<MenuItem> items;
     public static TextView cartTotalItems;
     public static DatabaseHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHandler(this);
        new loadCategories().execute();
        items = new ArrayList<>();
        mAdapter = new menuAdapter(getApplicationContext() , items);

        recyclerView = (RecyclerView) findViewById(R.id.menu_MenuList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cartTotalItems = (TextView) findViewById(R.id.menu_carttotalitems);

//        final SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(this);
//
//        SharedPreferences.OnSharedPreferenceChangeListener prefListener;
//        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
//            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//
//                Log.w("change----", prefs.getInt("totalitems", 100) +"");
//                cartTotalItems.setText(pref.getInt("totalitems",100)+"");
//
//            }
//        };
//        pref.registerOnSharedPreferenceChangeListener(prefListener);

        cartTotalItems.setText(db.getItemsCount() + "");

        CircleImageView opencart = (CircleImageView) findViewById(R.id.opencart);
        opencart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(menuActivity.this, cart.class);
                startActivity(i);
            }
        });
    }

     @Override
     public void onRestart()
     {
         super.onRestart();
         finish();
         startActivity(getIntent());
     }

    public void showCart(View view){
        List<CartItem> items = db.getAllItems();
        for(CartItem i: items){
            Log.w("item--", i.getId() + " " + i.getQuantity());
        }
    }
     @Override
     protected void onPause() {
         super.onPause();
     }

     @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(android.view.MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

            Log.w("id" , item.toString());
        String title = item.getTitle().toString().split(":")[0];

        Log.w("selected:::::", id + "--" + item.getTitle());
        new getItemsInCategory().execute(title);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



     public class loadCategories extends AsyncTask<String, Void, String> {
         JSONObject obj;
         @Override
         protected String doInBackground(final String... strings) {
             try {
                 RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                 String URL = "https://chaipani.herokuapp.com/api/categories";
                 JSONObject jsonBody = new JSONObject();
                 final String requestBody = jsonBody.toString();

                 StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         Log.w("response here", response);
                         Log.i("VOLLEY here", response);
                         updateSideMenu(response);
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

     public void updateSideMenu(String response){
        Log.w("update function", response);
        try {
            JSONObject object = new JSONObject(response);
            JSONArray data = object.getJSONArray("data");

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            final Menu menu = navigationView.getMenu();
            for (int i = 0; i < data.length(); i++) {

                menu.add(data.getJSONObject(i).getString("id") + ":" +data.getJSONObject(i).getString("name"));
//                MenuItem item = new MenuItem();
//                item.setName(data.getJSONObject(i).getString("name"));
//                items.add(item);
//                mAdapter.notifyItemInserted(i);
            }


        }catch (JSONException e){

        }
     }

     public class getItemsInCategory extends AsyncTask<String, Void, String> {
         JSONObject obj;
         @Override
         protected String doInBackground(final String... strings) {
             try {
                 RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                 String URL = "https://chaipani.herokuapp.com/api/categories/" + strings[0];
                 JSONObject jsonBody = new JSONObject();
                 final String requestBody = jsonBody.toString();

                 StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {
                         Log.w("response here", response);
                         Log.i("VOLLEY here", response);
                         updateMenuItems(response);
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

     public void updateMenuItems(String response){
         Log.w("update item function", response);
         try {
             JSONObject object = new JSONObject(response);
             JSONArray data = object.getJSONArray("data");
             items.clear();
             mAdapter.notifyDataSetChanged();
             for (int i = 0; i < data.length(); i++) {
                 MenuItem item = new MenuItem();
                 item.setId(Integer.parseInt(data.getJSONObject(i).getString("id")));
                 item.setName(data.getJSONObject(i).getString("name"));
                 item.setPrice(Integer.parseInt(data.getJSONObject(i).getString("price")));
                 item.setImageurl(data.getJSONObject(i).getString("item_pic"));
                 items.add(item);

                 mAdapter.notifyItemInserted(items.size());
             }


         }catch (JSONException e){

         }
     }

}
