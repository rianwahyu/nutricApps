package com.rigadev.nutricapps.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rigadev.nutricapps.databinding.AdapterHistoryConsultationBinding;
import com.rigadev.nutricapps.databinding.AdapterHistoryFoodOrderBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.HistoryDoctorModel;
import com.rigadev.nutricapps.model.HistoryFoodModel;
import com.rigadev.nutricapps.util.MyConfig;

import java.util.List;

public class AdapterHistoryConsultation extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<HistoryDoctorModel> loc ;

    private ItemClickListener itemClikListener;

    public AdapterHistoryConsultation(Context context, List<HistoryDoctorModel> loc) {
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
        return new ViewHolderRow(AdapterHistoryConsultationBinding.inflate(LayoutInflater.from(viewGroup.getContext()),
                viewGroup, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        if (viewHolder instanceof ViewHolderRow) {
            final ViewHolderRow viewHolders = (ViewHolderRow) viewHolder;

            viewHolders.binding.textConsultationID.setText(loc.get(position).getIdConsultation());
            viewHolders.binding.textDoctorName.setText("Nama Dokter : " + loc.get(position).getDoctorName());
            viewHolders.binding.textTanggalOrder.setText("Tanggal Konsultasi : "+loc.get(position).getDateConsultation());
            viewHolders.binding.textStatusPayment.setText("Status Pembayaran : " +loc.get(position).getStatusConsultation());
            viewHolders.binding.textKetStatus.setText("Keterangan : " +loc.get(position).getKetStatus());
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
        AdapterHistoryConsultationBinding binding;

        public ViewHolderRow(AdapterHistoryConsultationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
