package com.my.kltn;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<AnalyzedImage> analyzedImages; // Updated type
    private Context context;

    public ImageAdapter(ArrayList<AnalyzedImage> analyzedImages, Context context) {
        this.analyzedImages = analyzedImages;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
            textView = itemView.findViewById(R.id.textViewAnalysis);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_analysis, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnalyzedImage item = analyzedImages.get(position);
        Glide.with(context)
                .load(Uri.parse(item.getImageUri()))
                .into(holder.imageView);
        holder.textView.setText("Phân tích"); // Hoặc item.getTimestamp()

        // ✅ Thêm sự kiện click để mở ResultActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ResultActivity.class);
            intent.putExtra("imageUri", item.getImageUri());
            intent.putExtra("message", item.getMessage());
            intent.putExtra("timestamp", item.getTimestamp());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return analyzedImages.size();
    }
}