package com.example.INFS3605App.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.INFS3605App.R;
import com.example.INFS3605App.api.Utils;
import com.example.INFS3605App.utils.Article;

import java.util.List;


public class ApiAdapter extends RecyclerView.Adapter<ApiAdapter.MyViewHolder> {

    private List<Article> articles ;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ApiAdapter(List<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.world_crisis_news_item, parent, false);
        return new MyViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holders, int position) {
    final MyViewHolder holder = holders;
    Article utils = articles.get(position);


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();


        Glide.with(context)
                .load(utils.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);

        holder.title.setText(utils.getTitle());
        holder.desc.setText(utils.getDescription());
        holder.source.setText(utils.getSource().getName());
        holder.time.setText((" \u2022" + Utils.DateToTimeFormat(utils.getPublishedAt())));
        holder.published_ad.setText(Utils.DateFormat(utils.getPublishedAt()));
        holder.author.setText(utils.getAuthor());
        holder.url.setText(utils.getUrl());

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title, desc,author, published_ad,source, time,url;
        ImageView imageView;
        ProgressBar progressBar;
        OnItemClickListener onItemClickListener;
        public MyViewHolder(View itemView, OnItemClickListener onItemClickListener ){
            super(itemView);
                                                              // LINK UP PROPERLY
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.title);
              desc = itemView.findViewById(R.id.desc);
            author = itemView.findViewById(R.id.author);
            published_ad = itemView.findViewById(R.id.publishedAt);
             source = itemView.findViewById(R.id.source);
            time = itemView.findViewById(R.id.time);
            imageView = itemView.findViewById(R.id.img);
            progressBar = itemView.findViewById(R.id.progress_load_photo);
            url = itemView.findViewById(R.id.url);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url.getText().toString()));
                    context.startActivity(browserIntent);

                }
            });

        }


        @Override
        public void onClick (View v){
            //onItemClickListener.onItemClick(v , getAdapterPosition());

        }

    }
}
