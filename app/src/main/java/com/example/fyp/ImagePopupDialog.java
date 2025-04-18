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
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}