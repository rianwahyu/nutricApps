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
import com.rigadev.nutricapps.databinding.AdapterDoctorBinding;
import com.rigadev.nutricapps.databinding.AdapterFoodBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.DoctorModel;
import com.rigadev.nutricapps.model.FoodModel;
import com.rigadev.nutricapps.util.MyConfig;

import java.util.List;


public class AdapterDoctor extends RecyclerView.Adapter<AdapterDoctor.ViewHolder> {

    public Context context;
    private final List<DoctorModel> loc ;

    private ItemClickListener clickListener;

    public AdapterDoctor(Context context, List<DoctorModel> loc) {

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

        return new ViewHolder(AdapterDoctorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    // Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        viewHolder.binding.textNamaDokter.setText(loc.get(position).getFullname());

        Glide.with(context)
                .load(loc.get(position).getDoctorPhoto())
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

        AdapterDoctorBinding binding;
        public ViewHolder(AdapterDoctorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}

