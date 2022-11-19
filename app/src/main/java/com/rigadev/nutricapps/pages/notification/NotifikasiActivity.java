package com.rigadev.nutricapps.pages.notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.adapter.AdapterCart;
import com.rigadev.nutricapps.adapter.AdapterNotifikasi;
import com.rigadev.nutricapps.database.SQLiteHelpers;
import com.rigadev.nutricapps.databinding.ActivityNotifikasiBinding;
import com.rigadev.nutricapps.databinding.DialogDetailNotifikasiBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.CartModel;
import com.rigadev.nutricapps.model.NotifikasiModel;
import com.rigadev.nutricapps.util.MyConfig;

import java.util.ArrayList;
import java.util.List;

public class NotifikasiActivity extends AppCompatActivity implements ItemClickListener {

    Context context = this;
    ActivityNotifikasiBinding binding;

    LinearLayoutManager linearLayoutManager;
    AdapterNotifikasi adapterNotifikasi;
    List<NotifikasiModel> listNotifikasi = new ArrayList<>();
    SQLiteHelpers sqLiteHelpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotifikasiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sqLiteHelpers = new SQLiteHelpers(context);
        binding.rcNotifikasi.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcNotifikasi.setLayoutManager(linearLayoutManager);
        adapterNotifikasi = new AdapterNotifikasi(context, listNotifikasi);
        binding.rcNotifikasi.setAdapter(adapterNotifikasi);
        adapterNotifikasi.setClickListener(this);
        //initItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initItem();
    }

    @SuppressLint("Range")
    void initItem() {
        listNotifikasi.clear();

        Cursor c = sqLiteHelpers.getNotification();
        Log.d("jumlah", String.valueOf(c.getCount()));

        if (c != null) {
            binding.rcNotifikasi.setVisibility(View.VISIBLE);
            binding.linearNotifikasiEmpty.setVisibility(View.GONE);
            while (c.moveToNext()) {
                String idNotifikasi = c.getString(c.getColumnIndex(SQLiteHelpers.NOTIFICATION_ID));
                String namaNotifikasi = c.getString(c.getColumnIndex(SQLiteHelpers.NOTIFICATION_NAME));
                String contentNotifikasi = c.getString(c.getColumnIndex(SQLiteHelpers.NOTIFICATION_CONTENT));
                String dateNotifikasi = c.getString(c.getColumnIndex(SQLiteHelpers.NOTIFICATION_DATE));
                String readNotifikasi = c.getString(c.getColumnIndex(SQLiteHelpers.NOTIFICATION_READ));


                NotifikasiModel cart = new NotifikasiModel();
                cart.setIdNotifikasi(idNotifikasi);
                cart.setNamaNotifikasi(namaNotifikasi);
                cart.setContentNotifikasi(contentNotifikasi);
                cart.setDateNotifikasi(dateNotifikasi);
                cart.setReadNotifikasi(dateNotifikasi);
                listNotifikasi.add(cart);
            }
            adapterNotifikasi.notifyDataSetChanged();
        } else {
            binding.rcNotifikasi.setVisibility(View.GONE);
            binding.linearNotifikasiEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    DialogDetailNotifikasiBinding dialogNotifikasiBinding;
    @Override
    public void onClick(View view, int position) {
        NotifikasiModel nm = listNotifikasi.get(position);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        dialogNotifikasiBinding = DialogDetailNotifikasiBinding.inflate(
                LayoutInflater.from(context));
        alert.setView(dialogNotifikasiBinding.getRoot());
        alert.create();
        alert.setTitle("Detail Notifikasi");

        dialogNotifikasiBinding.textContentNotifikasi.setText(nm.getContentNotifikasi());
        dialogNotifikasiBinding.textTannggal.setText( MyConfig.konvertDateIndoTime(nm.getDateNotifikasi()) );
        dialogNotifikasiBinding.textJudulNotifikasi.setText(nm.getNamaNotifikasi());

        final AlertDialog dialog = alert.create();

        dialogNotifikasiBinding.btnHapusNotifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                sqLiteHelpers.deleteNotification(nm.getIdNotifikasi());
                onResume();
            }
        });
        dialog.show();
    }
}