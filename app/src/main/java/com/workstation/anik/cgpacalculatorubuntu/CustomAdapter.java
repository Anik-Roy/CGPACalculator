package com.workstation.anik.cgpacalculatorubuntu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;


public class CustomAdapter extends ArrayAdapter< resultInfo>{
    private int flag;

    public CustomAdapter(Context context, ArrayList<resultInfo>ob, int flag) {
        super(context, R.layout.custom_list_view, ob);
        this.flag = flag;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater aniksInflater = LayoutInflater.from(getContext());

        View aniksView;
        aniksView = aniksInflater.inflate(R.layout.custom_list_view, parent, false);

        resultInfo ob = new resultInfo();
        ob = getItem(position);

        TextView serial = aniksView.findViewById(R.id.serial);
        TextView credit = aniksView.findViewById(R.id.credit);
        TextView gpa = aniksView.findViewById(R.id.gpa);
        TextView totalgpa = aniksView.findViewById(R.id.totalgpa);


        if (ob != null) {
            if(flag == 2)
                serial.setTextColor(ContextCompat.getColor(getContext(), R.color.listColor));
            else if(flag == 1) {
                serial.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            }
            serial.setText(String.format(Locale.getDefault(), "%d",  ob.getId()));
        }

        if (ob != null) {
            if(flag == 2)
                credit.setTextColor(ContextCompat.getColor(getContext(), R.color.listColor));

            else if(flag == 1)
                credit.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

            credit.setText(String.format(Locale.getDefault(), "%.2f",  ob.getCredit()));
        }

        if (ob != null) {
            gpa.setText(String.format(Locale.getDefault(), "%.2f",  ob.getGpa()));
            if(flag == 2)
                gpa.setTextColor(ContextCompat.getColor(getContext(), R.color.listColor));
            else if(flag == 1)
                gpa.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

        }

        if (ob != null) {
            totalgpa.setText(String.format(Locale.getDefault(), "%.2f", ob.getTotalGpa()));
            if(flag == 2)
                totalgpa.setTextColor(ContextCompat.getColor(getContext(), R.color.listColor));
            else if(flag == 1)
                totalgpa.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

        }

        return aniksView;
    }
}