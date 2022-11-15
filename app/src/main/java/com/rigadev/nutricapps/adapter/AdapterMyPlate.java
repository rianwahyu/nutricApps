package com.rigadev.nutricapps.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rigadev.nutricapps.databinding.AdapterPlateBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.MyPlateModel;

import java.util.List;

public class AdapterMyPlate extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<MyPlateModel> loc ;

    private ItemClickListener itemClikListener;

    public AdapterMyPlate(Context context, List<MyPlateModel> loc) {
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
        return new ViewHolderRow(AdapterPlateBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                viewGroup, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (viewHolder instanceof ViewHolderRow) {
            final ViewHolderRow viewHolders = (ViewHolderRow) viewHolder;

            viewHolders.binding.textFoodName.setText(loc.get(position).getName());
            viewHolders.binding.textKalori.setText(loc.get(position).getCalories() +" kkal");
            viewHolders.binding.textJumlahPorsi.setText("Jumlah Porsi : "+loc.get(position).getPortion());
            viewHolders.binding.textTanggal.setText("Tanggal : "+loc.get(position).getDatePlate());

        }
    }


    @Override
    public int getItemCount() {
        return loc == null ? 0 : loc.size();
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        AdapterPlateBinding binding;

        public ViewHolderRow(AdapterPlateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
