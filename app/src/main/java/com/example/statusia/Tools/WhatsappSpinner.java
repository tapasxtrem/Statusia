package com.example.statusia.Tools;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.example.statusia.Activitys.MainActivity.WhatsAppPackage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.statusia.Adapters.WhatsappSpinnerItemAdapter;
import com.example.statusia.R;
import com.example.statusia.classes.WhatsappSpinnerItem;

import java.util.ArrayList;

public class WhatsappSpinner{
    Spinner spinner;
    Context context;
    ArrayList<WhatsappSpinnerItem> wSpinnerList;
    WhatsappSpinnerItemAdapter WSAdapter;
    SharedPreferences sharedPreferences;

    public WhatsappSpinner(Context context,Spinner spinner){
        super();
        this.context = context;
        this.spinner = spinner;
        initWSpinnerList();
        WSAdapter = new WhatsappSpinnerItemAdapter(context, wSpinnerList);
        sharedPreferences = context.getSharedPreferences("SPINNER_POS",MODE_PRIVATE);
    }

    public void setSpinner(){
        spinner.setAdapter(WSAdapter);
        Log.e(TAG, "setSpinner: "+getSpinnerPosition());
        spinner.setSelection(getSpinnerPosition());
    }

    public void setAdapter(String name){

    }

    public void resetPreviousSpinnerPos(){
        spinner.setSelection(getSpinnerPosition());
    }

    public int getSpinnerPosition(){
        return sharedPreferences.getInt("POS",0);
    }

    public void setSpinnerPosition(int pos){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("POS",pos);
        editor.apply();
    }

    private void initWSpinnerList(){
        wSpinnerList = new ArrayList<>();
        wSpinnerList.add(new WhatsappSpinnerItem("WhatsApp", R.drawable.whatsapp_icon));
        wSpinnerList.add(new WhatsappSpinnerItem("WhatsApp Business",R.drawable.whatsapp_business_icon));
    }
}
