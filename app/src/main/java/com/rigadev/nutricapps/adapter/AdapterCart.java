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
import com.rigadev.nutricapps.listener.DecreaseClickListener;
import com.rigadev.nutricapps.listener.IncreaseClickListener;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.CartModel;
import com.rigadev.nutricapps.util.MyConfig;


import java.util.List;

public class AdapterCart extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<CartModel> loc ;

    private DecreaseClickListener decreaseClickListener;
    private IncreaseClickListener increaseClickListener;
    private ItemClickListener itemClikListener;

    int stokCart = 0;
    int totalHarga = 0;
    SQLiteHelpers sqLiteHelpers;

    public AdapterCart(Context context, List<CartModel> loc) {
        this.context = context;
        this.loc = loc;
    }

    public void setDecreaseClickListener(DecreaseClickListener decreaseClickListener) {
        this.decreaseClickListener = decreaseClickListener;
    }

    public void setIncreaseClickListener(IncreaseClickListener increaseClickListener) {
        this.increaseClickListener = increaseClickListener;
    }

    public void setClickListener(ItemClickListener itemClikListener){
        this.itemClikListener = itemClikListener;
    }

    int row_index=-1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderRow(AdapterCartFoodBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                viewGroup, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (viewHolder instanceof ViewHolderRow) {
            final ViewHolderRow viewHolders = (ViewHolderRow) viewHolder;

            sqLiteHelpers = new SQLiteHelpers(context);

            String idCart = loc.get(position).getIdCart();
            int hargaValue = Integer.parseInt(loc.get(position).getHarga());

            viewHolders.binding.textNamaMenu.setText(loc.get(position).getNamaMenu());
            viewHolders.binding.textHarga.setText("Rp. " + MyConfig.formatNumberComma(loc.get(position).getHarga()));
            viewHolders.binding.textTotalHarga.setText("Rp. " + MyConfig.formatNumberComma(loc.get(position).getTotalHarga()));
            viewHolders.binding.textJumlah.setText(loc.get(position).getJumlah());

            Glide.with(context)
                    .load( loc.get(position).getFotoMenu())
                    .apply(new RequestOptions().placeholder(R.drawable.food_placeholder)
                            .error(R.drawable.food_placeholder))
                    .into(viewHolders.binding.imgMenu);

            stokCart = Integer.parseInt(loc.get(position).getJumlah());

            viewHolders.binding.textDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((Integer.parseInt(viewHolders.binding.textJumlah.getText().toString()) - 1 )< 0){
                        viewHolders.binding.textJumlah.setText(String.valueOf(0));
                        stokCart = 0;
                    }else{
                        viewHolders.binding.textJumlah.setText(String.valueOf(Integer.parseInt(viewHolders.binding.textJumlah.getText().toString()) - 1));
                        stokCart = stokCart -1;
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (decreaseClickListener !=null) decreaseClickListener.onClickDecrease(v,position,
                                    viewHolders.binding.textJumlah.getText().toString());

                            totalHarga = Integer.parseInt(viewHolders.binding.textJumlah.getText().toString()) * hargaValue;

                            viewHolders.binding.textTotalHarga.setText("Rp. "
                                    + MyConfig.priceToString(Double.parseDouble(String.valueOf(totalHarga))));
                        }
                    },1000);

                }
            });

            viewHolders.binding.textIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolders.binding.textJumlah.setText(String.valueOf(Integer.parseInt(viewHolders.binding.textJumlah.getText().toString()) + 1));
                    stokCart = stokCart + 1;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (increaseClickListener !=null) increaseClickListener.onClickIncrease(v,position,
                                    viewHolders.binding.textJumlah.getText().toString());

                            totalHarga = Integer.parseInt(viewHolders.binding.textJumlah.getText().toString()) * hargaValue;

                            viewHolders.binding.textTotalHarga.setText("Rp. "
                                    + MyConfig.priceToString(Double.parseDouble(String.valueOf(totalHarga))));
                        }
                    },1000);
                }
            });

            viewHolders.binding.cardImgMenu.setOnClickListener(new View.OnClickListener() {
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
        AdapterCartFoodBinding binding;

        public ViewHolderRow(AdapterCartFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
