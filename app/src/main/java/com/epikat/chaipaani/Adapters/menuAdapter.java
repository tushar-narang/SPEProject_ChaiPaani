package com.epikat.chaipaani.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.epikat.chaipaani.R;

import com.epikat.chaipaani.pojo.OrderMenuItem;

import java.util.ArrayList;


public class menuAdapter extends RecyclerView.Adapter<menuAdapter.MenuView> {

    ArrayList<OrderMenuItem> items_list;
    int render_item;
    Context context;

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
    public void onBindViewHolder(@NonNull menuAdapter.MenuView myViewHolder, int i) {
        final OrderMenuItem item = items_list.get(i);
        TextView name = myViewHolder.name;
        name.setText(item.getName());
        TextView price = myViewHolder.price;
        price.setText("Price: " + item.getPrice());
    }

    @Override
    public int getItemCount() {
        return items_list.size();
    }

    class MenuView extends RecyclerView.ViewHolder{

        TextView name, price;

        public MenuView(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }

        public void initView(final View itemView){
            name = itemView.findViewById(R.id.individual_menuitem_name);
            price = itemView.findViewById(R.id.individual_menuitem_price);
        }
    }
}