package com.epikat.chaipaani.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.epikat.chaipaani.Dashboard;
import com.epikat.chaipaani.R;

import com.epikat.chaipaani.menuActivity;
import com.epikat.chaipaani.pojo.CartItem;
import com.epikat.chaipaani.pojo.OrderMenuItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class menuAdapter extends RecyclerView.Adapter<menuAdapter.MenuView> {

    ArrayList<OrderMenuItem> items_list;
    int render_item;
    Context context;
    CircleImageView iv;

    public menuAdapter(Context context, ArrayList<OrderMenuItem> data) {
        this.context = context;
        this.items_list = data;
    }

    @NonNull
    @Override
    public menuAdapter.MenuView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.individual_menuitem , viewGroup , false);
        MenuView vh = new MenuView(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final menuAdapter.MenuView myViewHolder, int i) {
        final OrderMenuItem item = items_list.get(i);
        TextView name = myViewHolder.name;
        name.setText(item.getName());
        TextView price = myViewHolder.price;
        price.setText("Price: " + item.getPrice());
        iv = myViewHolder.imageview;
        Picasso.with(context).load(item.getImageurl()).into(iv);

        final LinearLayout countLayout = myViewHolder.countlayout;
        final Button addToBag = myViewHolder.addToBag;
        countLayout.setVisibility(View.GONE);
        addToBag.setVisibility(View.VISIBLE);

        final TextView quancount = myViewHolder.quancountText;
        quancount.setText("1" + "");

        List<CartItem> items = menuActivity.db.getAllItems();
        for(CartItem cartItem : items){
            if(cartItem.getName().equals(item.getName())){
                addToBag.setVisibility(View.GONE);
                countLayout.setVisibility(View.VISIBLE);
                quancount.setText(cartItem.getQuantity() +"");
                break;
            }
        }


        addToBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countLayout.setVisibility(View.VISIBLE);
                addToBag.setVisibility(View.GONE);
                menuActivity.db.addItem(new CartItem(item.getName(), 1));
                menuActivity.cartTotalItems.setText(menuActivity.db.getItemsCount() + "");
//                SharedPreferences pref = context.getSharedPreferences("cart", Context.MODE_PRIVATE);
//                int totalitems = pref.getInt("totalitems", 0);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putInt("totalitems", totalitems+1);
//                editor.commit();
//                Log.w("here::" , pref.getInt("totalitems", 100) + "");
//                Toast.makeText(context, "Added To Cart", Toast.LENGTH_SHORT).show();
//                menuActivity.cartTotalItems.setText((Integer.parseInt(menuActivity.cartTotalItems.getText() +"") +1 )+"");
            }
        });


        Button quaninc = myViewHolder.quanInc;
        quaninc.setText(">");
        quaninc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quancount.setText((Integer.parseInt(quancount.getText().toString())+1) +"");
                menuActivity.db.updateItem(new CartItem(item.getName(), Integer.parseInt(quancount.getText().toString()) ));
            }
        });
        Button quandec = myViewHolder.quanDec;
        quandec.setText("<");
        quandec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(quancount.getText().toString()) > 1){
                    quancount.setText((Integer.parseInt(quancount.getText().toString())-1) +"");
                    menuActivity.db.updateItem(new CartItem(item.getName(), Integer.parseInt(quancount.getText().toString())));

                }
                else{
                    countLayout.setVisibility(View.GONE);
                    addToBag.setVisibility(View.VISIBLE);
                    menuActivity.db.deleteItem( item.getName() );
                    menuActivity.cartTotalItems.setText(menuActivity.db.getItemsCount() + "");
//                    SharedPreferences pref = context.getSharedPreferences("cart", Context.MODE_PRIVATE);
//                    int totalitems = pref.getInt("totalitems", 0);
//                    SharedPreferences.Editor editor = pref.edit();
//                    editor.putInt("totalitems", totalitems-1);
//                    editor.commit();
//                    Log.w("here::" , pref.getInt("totalitems", 100) + "");
//                    Toast.makeText(context, "Removed From Cart", Toast.LENGTH_SHORT).show();
//
//                    menuActivity.cartTotalItems.setText((Integer.parseInt(menuActivity.cartTotalItems.getText() +"") -1 )+"");
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return items_list.size();
    }

    class MenuView extends RecyclerView.ViewHolder{

        TextView name, price, quancountText;
        CircleImageView imageview;
        Button addToBag, quanInc, quanDec;
        LinearLayout countlayout;

        public MenuView(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        public void initView(final View itemView){
            name = itemView.findViewById(R.id.individual_menuitem_name);
            price = itemView.findViewById(R.id.individual_menuitem_price);
            imageview = itemView.findViewById(R.id.individual_menuitem_image);
            addToBag = itemView.findViewById(R.id.individual_menuitem_addToBag);
            countlayout = itemView.findViewById(R.id.individual_menuitem_countlayout);
            quancountText = itemView.findViewById(R.id.individual_menuitem_countlayout_counttext);
            quanInc = itemView.findViewById(R.id.individual_menuitem_countlayout_incbutton);
            quanDec = itemView.findViewById(R.id.individual_menuitem_countlayout_decbutton);
        }
    }
}