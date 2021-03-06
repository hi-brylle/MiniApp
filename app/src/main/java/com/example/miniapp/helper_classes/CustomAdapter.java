package com.example.miniapp.helper_classes;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniapp.R;
import com.example.miniapp.models.Repository;
import com.example.miniapp.models.Task;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> implements ISubscriber<Task> {
    public ArrayList<Task> taskList;
    protected static IOnImageClickedListener imageClickedListener;

    public CustomAdapter(IOnImageClickedListener imageClickedListener){
        CustomAdapter.imageClickedListener = imageClickedListener;
        Repository.INSTANCE.addSub(this);
        taskList = new ArrayList<>();
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_row_layout, parent, false);

        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.bind(task);

        holder.itemView.setOnClickListener(view -> {
            boolean expanded = task.isExpanded();
            task.setExpanded(!expanded);
            notifyItemChanged(position);
        });

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void update(Task t) {
        taskList.add(t);
        notifyDataSetChanged();
    }

    public void updateHomeScreen(){
        taskList.clear();
        Repository.INSTANCE.onRequestNotify();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTaskRow;
        private TextView textViewSubDateStart;
        private TextView textViewSubDateCreated;
        private View subItem;
        private TextView textViewSubAddress;
        private ImageView imageViewSub;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTaskRow = itemView.findViewById(R.id.text_view_task_row);
            textViewSubDateStart = itemView.findViewById(R.id.text_view_sub_date_start);
            textViewSubDateCreated = itemView.findViewById(R.id.text_view_sub_date_created);
            subItem = itemView.findViewById(R.id.layout_sub_items);
            textViewSubAddress = itemView.findViewById(R.id.text_view_sub_address);
            imageViewSub = itemView.findViewById(R.id.image_view_sub);
        }

        public void bind(Task task) {
            boolean expanded = task.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            textViewTaskRow.setText(task.getTask());
            textViewSubDateStart.setText(String.format("Start by: %s", task.getDateStart()));
            textViewSubDateCreated.setText(String.format("Date created: %s", task.getDateCreated()));
            textViewSubAddress.setText(task.getAddress());
            imageViewSub.setImageURI(Uri.parse(task.getImageURI()));

        }
    }

    public interface IOnImageClickedListener {
        void onImageClicked(String stringUri);
    }

}
