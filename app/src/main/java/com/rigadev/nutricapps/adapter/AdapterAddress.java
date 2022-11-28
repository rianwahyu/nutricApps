package com.rigadev.nutricapps.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rigadev.nutricapps.databinding.AdapterAddressBinding;
import com.rigadev.nutricapps.databinding.AdapterHistoryFoodOrderBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.listener.ItemUbahClickListener;
import com.rigadev.nutricapps.model.AddressModel;
import com.rigadev.nutricapps.model.HistoryFoodModel;
import com.rigadev.nutricapps.util.MyConfig;

import java.util.List;

public class AdapterAddress extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<AddressModel> loc ;

    private ItemClickListener itemClikListener;
    private ItemUbahClickListener itemUbahClickListener;

    public AdapterAddress(Context context, List<AddressModel> loc) {
        this.context = context;
        this.loc = loc;
    }

    public void setClickListener(ItemClickListener itemClikListener){
        this.itemClikListener = itemClikListener;
    }

    public void setUbahClickListener(ItemUbahClickListener itemUbahClickListener){
        this.itemUbahClickListener = itemUbahClickListener;
    }

    int row_index=-1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderRow(AdapterAddressBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                viewGroup, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (viewHolder instanceof ViewHolderRow) {
            final ViewHolderRow viewHolders = (ViewHolderRow) viewHolder;

            viewHolders.binding.textAddress.setText(loc.get(position).getAddress());
            viewHolders.binding.btnPilihAlamat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClikListener !=null) itemClikListener.onClick(v, position);
                }
            });

            viewHolders.binding.btnUbahAlmat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemUbahClickListener !=null) itemUbahClickListener.onUbahClick(v, position);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return loc == null ? 0 : loc.size();
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        AdapterAddressBinding binding;

        public ViewHolderRow(AdapterAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
