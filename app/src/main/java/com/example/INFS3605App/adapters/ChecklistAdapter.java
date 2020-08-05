package com.example.INFS3605App.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.INFS3605App.R;

import java.util.ArrayList;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.MyViewHolder> {
    public ArrayList<String> checklistItems;
    public Context mContext;

    public ChecklistAdapter(ArrayList<String> checklistItems,Context mContext){
        this.checklistItems = checklistItems;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ChecklistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_checklist,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChecklistAdapter.MyViewHolder holder, int position) {
        holder.checkBox.setText(checklistItems.get(position));
    }

    @Override
    public int getItemCount() {
        return checklistItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkBox);
        }

    }
}
