package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {
    private List<Board> boardList;
    private OnBoardClickListener listener;

    public BoardAdapter(List<Board> boardList, OnBoardClickListener listener) {
        this.boardList = boardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_item, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        Board board = boardList.get(position);

        holder.boardName.setText(board.getBoardName());
        holder.boardDescription.setText(board.getDescription());
        holder.followersCount.setText(board.getFollowersCount() + " Followers");

        // 設置按鈕狀態
        holder.followButton.setText(board.isFollowed() ? "Unfollow" : "Follow");

        // 關注按鈕點擊事件
        holder.followButton.setOnClickListener(v -> listener.onFollowClick(board.getBoardId(), !board.isFollowed()));

        // 點擊分類進入詳情頁
        holder.itemView.setOnClickListener(v -> listener.onBoardClick(board.getBoardId()));
    }

    @Override
    public int getItemCount() {
        return boardList.size();
    }

    public static class BoardViewHolder extends RecyclerView.ViewHolder {
        TextView boardName, boardDescription, followersCount;
        Button followButton;

        public BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            boardName = itemView.findViewById(R.id.boardName);
            boardDescription = itemView.findViewById(R.id.boardDescription);
            followersCount = itemView.findViewById(R.id.followersCount);
            followButton = itemView.findViewById(R.id.followButton);
        }
    }

    public interface OnBoardClickListener {
        void onFollowClick(int boardId, boolean follow);
        void onBoardClick(int boardId);
    }
}