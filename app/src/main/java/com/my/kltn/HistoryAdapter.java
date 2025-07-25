package com.my.kltn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.content.Intent;  // Thêm dòng này
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<HistoryItem> items;

    public HistoryAdapter(Context context, List<HistoryItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        holder.resultText.setText(item.getResult());
        holder.dateText.setText(item.getDate());
        holder.imageView.setImageResource(item.getImageRes());

        holder.itemView.setOnClickListener(v -> {
            // Xử lý khi nhấn vào một item
            Intent intent = new Intent(context, HistoryDetailActivity.class);
            intent.putExtra("result", item.getResult());
            intent.putExtra("date", item.getDate());
            intent.putExtra("imageRes", item.getImageRes());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView resultText, dateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.historyImage);
            resultText = itemView.findViewById(R.id.resultText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }
}