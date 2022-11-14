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
import com.rigadev.nutricapps.databinding.AdapterFoodV2Binding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.FoodModel;
import com.rigadev.nutricapps.util.MyConfig;

import java.util.List;


public class AdapterFoodV2 extends RecyclerView.Adapter<AdapterFoodV2.ViewHolder> {

    public Context context;
    private final List<FoodModel> loc ;

    private ItemClickListener clickListener;

    public AdapterFoodV2(Context context, List<FoodModel> loc) {

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

        return new ViewHolder(AdapterFoodV2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    // Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        viewHolder.binding.textNamaMakanan.setText(loc.get(position).getFoodName());
        viewHolder.binding.textKeterangan.setText(loc.get(position).getIngredient());
        viewHolder.binding.textNutrisi.setText("Nutrisi : "+ loc.get(position).getNutrition());
        viewHolder.binding.textKalori.setText("Kalori : "+loc.get(position).getCalories());

        Glide.with(context)
                .load(loc.get(position).getFoodPhoto())
                .apply(new RequestOptions().placeholder(R.drawable.food_placeholder).error(R.drawable.food_placeholder))
                .into(viewHolder.binding.imgFood);

        viewHolder.binding.btnBeli.setOnClickListener(new View.OnClickListener() {
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

        AdapterFoodV2Binding binding;
        public ViewHolder(AdapterFoodV2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

