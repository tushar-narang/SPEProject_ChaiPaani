package com.epikat.chaipaani.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epikat.chaipaani.R;
import com.epikat.chaipaani.cart;
import com.epikat.chaipaani.menuActivity;
import com.epikat.chaipaani.pojo.CartItem;
import com.epikat.chaipaani.pojo.MenuItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class cartAdapter extends RecyclerView.Adapter<cartAdapter.MenuView> {

    ArrayList<MenuItem> items_list;
    int render_item;
    Context context;
    CircleImageView iv;

    public cartAdapter(Context context, ArrayList<MenuItem> data) {
        this.context = context;
        this.items_list = data;
    }

    @NonNull
    @Override
    public cartAdapter.MenuView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.individual_menuitem , viewGroup , false);
        MenuView vh = new MenuView(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final cartAdapter.MenuView myViewHolder, int i) {
        final MenuItem item = items_list.get(i);
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
            Log.w("checking for quantity", cartItem.getId() + " " + item.getId());
            if(cartItem.getId() == item.getId()){
                Log.w("found quantity", cartItem.getQuantity() + " ");
                addToBag.setVisibility(View.GONE);
                countLayout.setVisibility(View.VISIBLE);
                quancount.setText(cartItem.getQuantity() +"");

                if(cart.counter >=0) {
                    Log.w("orderamt inc by:", cartItem.getId() + ": " + cartItem.getQuantity() + "*" + item.getPrice() + " prev val:" + cart.orderAmt + " prev counter:" + cart.counter);
                    cart.orderAmt += (cartItem.getQuantity() * item.getPrice());
                    cart.counter--;
                    Log.w("orderamt inc by:", cartItem.getId() + ": " + cartItem.getQuantity() + "*" + item.getPrice() + " new val:" + cart.orderAmt + " new counter:" + cart.counter);

                    if (cart.counter == 0) {
                        String s= "PLACE ORDER\n(Total Amount:" + cart.orderAmt +")";
                        SpannableString ss1=  new SpannableString(s);
                        ss1.setSpan(new RelativeSizeSpan(1.2f), 0,11, 0); // set size
                        ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, 11, 0);// set color
                       cart.placeOrderButton.setText(ss1);
                        cart.counter--;
                    }
                }
                break;
            }
        }


        addToBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countLayout.setVisibility(View.VISIBLE);
                addToBag.setVisibility(View.GONE);
                menuActivity.db.addItem(new CartItem(item.getId(), 1));
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
                menuActivity.db.updateItem(new CartItem(item.getId(), Integer.parseInt(quancount.getText().toString()) ));
                Log.w("adding amt", item.getPrice() + " oldval:" + cart.orderAmt);
                cart.orderAmt += item.getPrice();
                Log.w("after adding", cart.orderAmt + "");
                String s= "PLACE ORDER\n(Total Amount:" + cart.orderAmt +")";
                SpannableString ss1=  new SpannableString(s);
                ss1.setSpan(new RelativeSizeSpan(1.2f), 0,11, 0); // set size
                ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, 11, 0);// set color
                cart.placeOrderButton.setText(ss1);

            }
        });
        Button quandec = myViewHolder.quanDec;
        quandec.setText("<");
        quandec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("Subtract amt", item.getPrice() + " oldval:" + cart.orderAmt);
                cart.orderAmt -= item.getPrice();
                Log.w("after adding", cart.orderAmt + "");
                String s= "PLACE ORDER\n(Total Amount:" + cart.orderAmt +")";
                SpannableString ss1=  new SpannableString(s);
                ss1.setSpan(new RelativeSizeSpan(1.2f), 0,11, 0); // set size
                ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, 11, 0);// set color
                cart.placeOrderButton.setText(ss1);
                if (Integer.parseInt(quancount.getText().toString()) > 1){
                    quancount.setText((Integer.parseInt(quancount.getText().toString())-1) +"");
                    menuActivity.db.updateItem(new CartItem(item.getId(), Integer.parseInt(quancount.getText().toString())));

                }
                else{
//                    countLayout.setVisibility(View.GONE);
//                    addToBag.setVisibility(View.VISIBLE);

                    cart.menuitems.remove(item);
                    cartAdapter.this.notifyDataSetChanged();
                    menuActivity.db.deleteItem( item.getId() );
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