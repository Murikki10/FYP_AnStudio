package com.example.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.ImageViewHolder> {

    private List<String> imageUrls;
    private Context context;

    public PostImagesAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // 使用 Glide 加載圖片
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image) // 佔位圖
                .into(holder.imageView);

        // 點擊圖片事件：彈出放大圖片的彈窗
        holder.imageView.setOnClickListener(v -> {
            // 確保 context 是 AppCompatActivity，因為我們需要使用 FragmentManager
            if (context instanceof AppCompatActivity) {
                // 使用 ImagePopupDialog 顯示放大圖片
                ImagePopupDialog.newInstance(imageUrl)
                        .show(((AppCompatActivity) context).getSupportFragmentManager(), "ImagePopupDialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}