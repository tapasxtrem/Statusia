package com.example.statusia.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.statusia.R;
import com.example.statusia.classes.WhatsappSpinnerItem;

import java.util.ArrayList;

public class WhatsappSpinnerItemAdapter extends ArrayAdapter<WhatsappSpinnerItem> {

    public WhatsappSpinnerItemAdapter(@NonNull Context context, ArrayList<WhatsappSpinnerItem> list) {
        super(context,0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initGetView(position,parent,R.layout.whatsapp_spinner_view);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initDropDownView(position,parent,R.layout.whatsapp_spinner_item_adapter);
    }

    public View initDropDownView(int position, ViewGroup parent, int layout){
        View view = LayoutInflater.from(getContext()).
                inflate(layout, parent, false);

        TextView textViewName = view.findViewById(R.id.whatsappSpinnerItem);
        ImageView whatsappSpinnerIcon = view.findViewById(R.id.whatsappSpinnerIcon);

        WhatsappSpinnerItem currentItem = getItem(position);

        if (currentItem != null) {
            textViewName.setText(currentItem.getItemName());
            whatsappSpinnerIcon.setImageResource(getIcon(currentItem.getItemName()));
        }
        return view;
    }

    public View initGetView(int position, ViewGroup parent, int layout){
        View view = LayoutInflater.from(getContext()).
                inflate(layout, parent, false);

        ImageView whatsappSpinnerIcon = view.findViewById(R.id.whatsappSpinnerIcon);
//        ImageView spinnerSelecter = view.findViewById(R.id.spinnerSelecter);

        WhatsappSpinnerItem currentItem = getItem(position);

        if (currentItem != null) {
            whatsappSpinnerIcon.setImageResource(getIcon(currentItem.getItemName()));
        }
        return view;
    }

    public int getIcon(String name){
        switch (name) {
            case "WhatsApp":
                return R.drawable.whatsapp_icon;
            case "WhatsApp Business":
                return R.drawable.whatsapp_business_icon;
            case "WhatsApp Parallel Space":
                return R.drawable.whatsapp_duel;
            default:
                return R.drawable.whatsapp_business_duel;
        }
    }
}
