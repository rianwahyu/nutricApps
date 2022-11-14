package com.rigadev.nutricapps.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.database.SQLiteHelpers;
import com.rigadev.nutricapps.databinding.AdapterCartFoodBinding;
import com.rigadev.nutricapps.databinding.AdapterHistoryFoodOrderBinding;
import com.rigadev.nutricapps.listener.DecreaseClickListener;
import com.rigadev.nutricapps.listener.IncreaseClickListener;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.CartModel;
import com.rigadev.nutricapps.model.HistoryFoodModel;
import com.rigadev.nutricapps.util.MyConfig;

import java.util.List;

public class AdapterHistoryFood extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<HistoryFoodModel> loc ;

    private ItemClickListener itemClikListener;

    public AdapterHistoryFood(Context context, List<HistoryFoodModel> loc) {
        this.context = context;
        this.loc = loc;
    }

    public void setClickListener(ItemClickListener itemClikListener){
        this.itemClikListener = itemClikListener;
    }

    int row_index=-1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderRow(AdapterHistoryFoodOrderBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                viewGroup, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (viewHolder instanceof ViewHolderRow) {
            final ViewHolderRow viewHolders = (ViewHolderRow) viewHolder;

            viewHolders.binding.textFoodOrderId.setText(loc.get(position).getFoodOrderID());
            viewHolders.binding.textTotalPayment.setText("Total Pembelian :  Rp. " + MyConfig.formatNumberComma(loc.get(position).getTotalPayment()));
            viewHolders.binding.textTanggalOrder.setText(loc.get(position).getDateOrder());
            viewHolders.binding.textStatusPayment.setText("Status Pembayaran : " +loc.get(position).getStatusPayment());

            viewHolders.binding.cardHistoryFood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClikListener !=null) itemClikListener.onClick(v, position);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return loc == null ? 0 : loc.size();
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        AdapterHistoryFoodOrderBinding binding;

        public ViewHolderRow(AdapterHistoryFoodOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
