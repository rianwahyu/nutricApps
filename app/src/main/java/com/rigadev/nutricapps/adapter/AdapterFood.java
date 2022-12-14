package com.rigadev.nutricapps.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.databinding.AdapterFoodBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.FoodModel;
import com.rigadev.nutricapps.util.MyConfig;


import java.util.List;


public class AdapterFood extends RecyclerView.Adapter<AdapterFood.ViewHolder> {

    public Context context;
    private final List<FoodModel> loc ;

    private ItemClickListener clickListener;

    public AdapterFood(Context context, List<FoodModel> loc) {

        this.context = context;
        this.loc = loc;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a layout

        return new ViewHolder(AdapterFoodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    // Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        viewHolder.binding.textNamaMenu.setText(loc.get(position).getFoodName());
        viewHolder.binding.textHarga.setText("Rp. " + MyConfig.formatNumberComma(loc.get(position).getPrice()));

        Glide.with(context)
                .load(loc.get(position).getFoodPhoto())
                .apply(new RequestOptions().placeholder(R.drawable.food_placeholder).error(R.drawable.food_placeholder))
                .into(viewHolder.binding.imgFood);

        viewHolder.binding.cardFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) clickListener.onClick(v, position);
            }
        });
    }

    //Returns the total number of items in the data set hold by the adapter.
    @Override
    public int getItemCount() {
        return loc.size();
    }

    // initializes some private fields to be used by RecyclerView.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        AdapterFoodBinding binding;
        public ViewHolder(AdapterFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

