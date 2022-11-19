package com.rigadev.nutricapps.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rigadev.nutricapps.databinding.AdapterMyactivityBinding;
import com.rigadev.nutricapps.databinding.AdapterNotifikasiBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.MyActivityModel;
import com.rigadev.nutricapps.model.NotifikasiModel;
import com.rigadev.nutricapps.util.MyConfig;

import java.util.List;

public class AdapterNotifikasi extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<NotifikasiModel> loc ;

    private ItemClickListener itemClikListener;

    public AdapterNotifikasi(Context context, List<NotifikasiModel> loc) {
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
        return new ViewHolderRow(AdapterNotifikasiBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                viewGroup, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (viewHolder instanceof ViewHolderRow) {
            final ViewHolderRow viewHolders = (ViewHolderRow) viewHolder;

            viewHolders.binding.textJudulNotifikasi.setText(loc.get(position).getNamaNotifikasi());
            viewHolders.binding.textContentNotifikasi.setText(loc.get(position).getContentNotifikasi());
            viewHolders.binding.textTannggal.setText(MyConfig.konvertDateIndoTime(loc.get(position).getDateNotifikasi()) );

            viewHolders.binding.cardNotifikasi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClikListener != null) itemClikListener.onClick(view, position);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return loc == null ? 0 : loc.size();
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        AdapterNotifikasiBinding binding;

        public ViewHolderRow(AdapterNotifikasiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
