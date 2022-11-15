package com.rigadev.nutricapps.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rigadev.nutricapps.databinding.AdapterMyactivityBinding;
import com.rigadev.nutricapps.databinding.AdapterPlateBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.MyActivityModel;
import com.rigadev.nutricapps.model.MyPlateModel;

import java.util.List;

public class AdapterMyActivity extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<MyActivityModel> loc ;

    private ItemClickListener itemClikListener;

    public AdapterMyActivity(Context context, List<MyActivityModel> loc) {
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
        return new ViewHolderRow(AdapterMyactivityBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                viewGroup, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (viewHolder instanceof ViewHolderRow) {
            final ViewHolderRow viewHolders = (ViewHolderRow) viewHolder;

            viewHolders.binding.textFoodName.setText(loc.get(position).getName());
            viewHolders.binding.textKalori.setText(loc.get(position).getCalories() +" kkal");
            viewHolders.binding.textJumlahPorsi.setText("Tanggal : "+ loc.get(position).getDateActivity());

        }
    }


    @Override
    public int getItemCount() {
        return loc == null ? 0 : loc.size();
    }

    public class ViewHolderRow extends RecyclerView.ViewHolder {
        AdapterMyactivityBinding binding;

        public ViewHolderRow(AdapterMyactivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
