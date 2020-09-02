package com.example.miniapp.helper_classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniapp.R;
import com.example.miniapp.models.Task;
import com.example.miniapp.views.CustomViewHolder;

import java.util.Collections;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    List<Task> tasksList = Collections.emptyList();

    public CustomAdapter(List<Task> tasks){
        tasksList = tasks;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_row_layout, parent, false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.textViewTaskRow.setText(tasksList.get(position).getTask());
        holder.textViewDateRow.setText(tasksList.get(position).getDateStart().toString());
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void insert(int position, Task task){
        tasksList.add(position, task);
        notifyItemInserted(position);
    }

    public void remove(Task task){
        int position = tasksList.indexOf(task);
        tasksList.remove(position);
        notifyItemRemoved(position);
    }
}
