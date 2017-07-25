package com.sanved.simplenotepad;

import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.content.Context;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by Sanved on 04-07-2016.
 */
public class RVAdapt extends RecyclerView.Adapter<RVAdapt.DataHolder> {

    ArrayList<MahitiBhandar> list;
    static ArrayList<MahitiBhandar> list2;
    //private static Tracker mTracker;

    SharedPreferences pref;
    SharedPreferences.Editor ed;


    Context context;

    RVAdapt(ArrayList<MahitiBhandar> list, Context context) {
        this.list = list;
        list2 = list;
        this.context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        ed = pref.edit();
        /*AnalyticsApplication application = (AnalyticsApplication) context;
        mTracker = application.getDefaultTracker();*/
    }

    public static class DataHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPreview, tvSize;
        CardView cv;
        /*
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
        SharedPreferences.Editor ed = pref.edit();
*/
        DataHolder(final View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvPreview = (TextView) v.findViewById(R.id.tvPreview);
            tvSize = (TextView) v.findViewById(R.id.tvSize);
            cv = (CardView) v.findViewById(R.id.cvListItem);

        }

    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        DataHolder dh = new DataHolder(v);
        return dh;
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {
        holder.tvName.setText(list.get(position).getName());
        holder.tvPreview.setText(list.get(position).getPreview());
        if(pref.getBoolean("sizeShow",true)){
            holder.tvSize.setText(list.get(position).getSize());
        }else{
            holder.tvSize.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

