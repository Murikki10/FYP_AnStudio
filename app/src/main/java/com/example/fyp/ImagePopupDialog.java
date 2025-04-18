package com.example.fyp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

public class ImagePopupDialog extends DialogFragment {

    private static final String IMAGE_URL_KEY = "imageUrl";

    public static ImagePopupDialog newInstance(String imageUrl) {
        ImagePopupDialog dialog = new ImagePopupDialog();
        Bundle args = new Bundle();
        args.putString(IMAGE_URL_KEY, imageUrl);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image_popup, container, false);

        // 獲取圖片 URL
        String imageUrl = getArguments() != null ? getArguments().getString(IMAGE_URL_KEY) : null;

        // 設置圖片
        ImageView popupImageView = view.findViewById(R.id.popupImageView);
        if (imageUrl != null) {
            Glide.with(requireContext())
                    .load(imageUrl)
                    .override(1080, 1920) // 限制圖片大小
                    .placeholder(R.drawable.placeholder_image)
                    .into(popupImageView);
        }

        // 點擊彈窗外部關閉
        view.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            // 設置窗口大小為屏幕的 90%
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;

            dialog.getWindow().setLayout((int) (screenWidth * 0.9), (int) (screenHeight * 0.9));
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}