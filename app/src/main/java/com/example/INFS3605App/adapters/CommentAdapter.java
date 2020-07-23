package com.example.INFS3605App.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.INFS3605App.R;
import com.example.INFS3605App.utils.Comment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_comment, parent, false);

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Glide.with(mContext).load(mData.get(position).getUserDp()).apply(RequestOptions.circleCropTransform()).into(holder.addedCommentUserDp);
        holder.addedCommentContent.setText(mData.get(position).getContent());
        holder.addedCommentUser.setText(mData.get(position).getUsername());
        holder.addedCommentTimestamp.setText(timestampToString((Long)mData.get(position).getTimestamp()));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        public ImageView addedCommentUserDp;
        public TextView addedCommentUser, addedCommentContent,addedCommentTimestamp;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            addedCommentUserDp = itemView.findViewById(R.id.addedCommentUserDp);
            addedCommentUser = itemView.findViewById(R.id.addedCommentUser);
            addedCommentContent = itemView.findViewById(R.id.addedCommentContent);
            addedCommentTimestamp = itemView.findViewById(R.id.addedCommentTimestamp);

        }
    }

    public String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm dd-MM",calendar).toString();
        return date;
    }
}
