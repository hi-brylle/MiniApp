package com.example.miniapp.helper_classes;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniapp.R;
import com.example.miniapp.models.Task;
import com.example.miniapp.viewmodels.HomeScreenViewModel;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private HomeScreenViewModel homeScreenViewModel;
    private ArrayList<Task> tasksList;

    public CustomAdapter(HomeScreenViewModel homeScreenViewModel){
        this.homeScreenViewModel = homeScreenViewModel;
    }

    public void initList() {
        if (tasksList == null){
            tasksList = new ArrayList<>();
        }

        new CustomAsync().execute();
//        tasksList = homeScreenViewModel.readAll();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_row_layout, parent, false);

        return new CustomViewHolder(itemView);
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

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTaskRow;
        public TextView textViewDateRow;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTaskRow = itemView.findViewById(R.id.text_view_task_row);
            textViewDateRow = itemView.findViewById(R.id.text_view_date_row);
        }
    }

    private class CustomAsync extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            tasksList = homeScreenViewModel.readAll();
            return null;
        }

    }
}
