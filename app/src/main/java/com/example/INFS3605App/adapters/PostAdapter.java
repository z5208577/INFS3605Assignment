package com.example.INFS3605App.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.INFS3605App.R;
import com.example.INFS3605App.fragments.PostDetailFragment;
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
        holder.postContent.setText("by " + mData.get(position).getUser() + " | "
                + mData.get(position).getLikes() + " Likes");
        if(!mData.get(position).getImage().equals("defaultPostImage")){
            Glide.with(mContext).load(mData.get(position).getImage()).into(holder.postImage);
        }
        if (!mData.get(position).getUserDp().equals("default")){
            Glide.with(mContext).load(mData.get(position).getUserDp()).apply(RequestOptions.circleCropTransform()).into(holder.postUserDp);
        } else {
            Glide.with(mContext).load("https://firebasestorage.googleapis.com/v0/b/infs3605-32bdc.appspot.com/o/userDps%2FdefaultUser.jpg?alt=media&token=d0ae4498-18f3-4195-a07f-e9ee351273e2")
                    .apply(RequestOptions.circleCropTransform()).into(holder.postUserDp);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView postTitle,postContent;
        ImageView postUserDp, postImage;

        public MyViewHolder(final View view){
            super(view);
            postTitle = view.findViewById(R.id.postTitle);
            postUserDp = view.findViewById(R.id.postUserDP);
            postImage = view.findViewById(R.id.postImage);
            postContent = view.findViewById(R.id.postContent);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    PostDetailFragment postDetailFragment= new PostDetailFragment();

                    Bundle args = new Bundle();
                    args.putString("postTitle", mData.get(position).getTitle());
                    args.putString("postImage", mData.get(position).getImage());
                    args.putString("postContent", mData.get(position).getContent());
                    args.putString("postId", mData.get(position).getPostId());
                    args.putString("postUserDp", mData.get(position).getUserDp());
                    args.putString("postUser", mData.get(position).getUser());
                    args.putInt("postLikes", mData.get(position).getLikes());
                    long timestamp = (long)mData.get(position).getTimeStamp();
                    args.putLong("postDate", timestamp);
                    postDetailFragment.setArguments(args);

                    FragmentManager fM = activity.getSupportFragmentManager();
                    FragmentTransaction transaction = fM.beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

                    transaction.replace(R.id.mainFragment, postDetailFragment, "postDetailFragment")
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }
}
