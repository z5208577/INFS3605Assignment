package com.example.INFS3605App.adapters;

import android.content.Context;
import android.media.Image;
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
import com.example.INFS3605App.utils.Post;

import java.util.List;

public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.MyViewHolder>{
    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_post, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.postTitle.setText(mData.get(position).getTitle());
        if(mData.get(position).getImage()!=null)
        Glide.with(mContext).load(mData.get(position).getImage()).apply(RequestOptions.circleCropTransform()).into(holder.postImage);
        Glide.with(mContext).load(mData.get(position).getUserDp()).apply(RequestOptions.circleCropTransform()).into(holder.postUserDp);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView postTitle;
        ImageView postUserDp, postImage;

        public MyViewHolder(View view){
            super(view);
            postTitle = view.findViewById(R.id.postTitle);
            postUserDp = view.findViewById(R.id.postUserDP);
            postImage = view.findViewById(R.id.postImage);
        }
    }
}
