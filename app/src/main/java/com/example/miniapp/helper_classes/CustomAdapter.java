package com.example.miniapp.helper_classes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniapp.R;
import com.example.miniapp.models.Task;
import com.example.miniapp.models.UserDBManager;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> implements Observer {
    private UserDBManager userDBManager;
    public ArrayList<Task> tasksList;

    public CustomAdapter(UserDBManager userDBManager){
        this.userDBManager = userDBManager;
        userDBManager.addObserver(this);
    }

    public void openDB(){
        userDBManager.openDB();
    }

    public void closeDB(){
        userDBManager.closeDB();
    }

    public void updateList() {
        if (tasksList == null){
            tasksList = new ArrayList<>();
        }

        userDBManager.updateListForChanges(tasksList);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_row_layout, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Task task = tasksList.get(position);

        holder.bind(task);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean expanded = task.isExpanded();
                task.setExpanded(!expanded);
                notifyItemChanged(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void update(Observable observable, Object o) {
        notifyDataSetChanged();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTaskRow;
        public TextView textViewSubDateStart;
        private TextView textViewSubDateCreated;
        private View subItem;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTaskRow = itemView.findViewById(R.id.text_view_task_row);
            textViewSubDateStart = itemView.findViewById(R.id.text_view_sub_date_start);
            textViewSubDateCreated = itemView.findViewById(R.id.text_view_sub_date_created);
            subItem = itemView.findViewById(R.id.layout_sub_items);
        }

        public void bind(Task task) {

            boolean expanded = task.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            textViewTaskRow.setText(task.getTask());
            textViewSubDateStart.setText("Start by: " + task.getDateStart());
            textViewSubDateCreated.setText("Date created: " + task.getDateCreated());
            // TODO: add mark done?
        }
    }

}
