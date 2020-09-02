package com.example.miniapp.views;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniapp.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewTaskRow;
    public TextView textViewDateRow;

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewTaskRow = itemView.findViewById(R.id.text_view_task_row);
        textViewDateRow = itemView.findViewById(R.id.text_view_date_row);
    }
}
