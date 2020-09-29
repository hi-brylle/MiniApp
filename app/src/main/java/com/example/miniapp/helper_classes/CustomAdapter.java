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
import com.example.miniapp.models.IUserDBManager;
import com.example.miniapp.models.Task;
import com.example.miniapp.models.UserDBManager;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> implements ISubscriber<Task> {
    private IUserDBManager dbManager;
    public ArrayList<Task> taskList;
    protected static onImageClickedListener imageClickedListener;
    private onItemLongClickedListener itemLongClickedListener;

    // for item removal, stored during onLongClick, but only used when deletion is confirmed
    private int cachedPosition;

    public CustomAdapter(UserDBManager userDBManager,
                         onImageClickedListener imageClickedListener,
                         onItemLongClickedListener itemLongClickedListener){
        this.dbManager = userDBManager;
        this.dbManager.addSub(this);
        CustomAdapter.imageClickedListener = imageClickedListener;
        this.itemLongClickedListener = itemLongClickedListener;
        if (taskList == null){
            taskList = new ArrayList<>();
        }
    }

    public void openDB(){
        dbManager.openDB();
    }

    public void closeDB(){
        dbManager.closeDB();
    }

    public void updateList() {
        taskList.clear();
        dbManager.listenForChanges();
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

        holder.itemView.setOnLongClickListener(view -> {
            Task longClicked = taskList.get(position);
            cachedPosition = position;
            itemLongClickedListener.onItemLongClicked(longClicked);
            return true;
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
        if (t != null){
            taskList.add(t);
        }
        // due to Java's type erasure, I cannot implement this class to implement both
        // ISub<T> and ISub<Int>
        // this is a hack, however, right now, addition and deletion of items are the only
        // two actions that can be done with the list, so I guess this is okay. I hope I'm not wrong.
        else {
            updateList();
        }

        taskList.trimToSize();
        notifyDataSetChanged();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTaskRow;
        public TextView textViewSubDateStart;
        private TextView textViewSubDateCreated;
        private View subItem;
        private ImageView imageViewSub;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTaskRow = itemView.findViewById(R.id.text_view_task_row);
            textViewSubDateStart = itemView.findViewById(R.id.text_view_sub_date_start);
            textViewSubDateCreated = itemView.findViewById(R.id.text_view_sub_date_created);
            subItem = itemView.findViewById(R.id.layout_sub_items);
            imageViewSub = itemView.findViewById(R.id.image_view_sub);
        }

        public void bind(Task task) {
            boolean expanded = task.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            textViewTaskRow.setText(task.getTask());
            textViewSubDateStart.setText(String.format("Start by: %s", task.getDateStart()));
            textViewSubDateCreated.setText(String.format("Date created: %s", task.getDateCreated()));

            if (!task.getImageURI().equals("")){
                imageViewSub.setImageURI(Uri.parse(task.getImageURI()));
                imageViewSub.setOnClickListener(view -> imageClickedListener.onImageClicked(task.getImageURI()));
            }
            // TODO: add mark done?
        }
    }

    public interface onImageClickedListener {
        void onImageClicked(String stringUri);
    }

    public interface onItemLongClickedListener {
        void onItemLongClicked(Task longClicked);
    }

}
