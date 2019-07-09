package com.example.instagram;//package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private List<Post> mPosts;
    Context context;

    public PostAdapter(List<Post> tweets){
        mPosts = tweets;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivPost;
        public TextView caption;
        public TextView username;
        public TextView timePosted;

        public ViewHolder(View itemView)  {
            super(itemView);
            ivPost = itemView.findViewById(R.id.ivPost);
            caption = itemView.findViewById(R.id.postCaption);
            username = itemView.findViewById(R.id.userPosted);
            timePosted = itemView.findViewById(R.id.timePosted);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Post post = mPosts.get(position);
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("post", (Serializable) post);
                ((Activity) context).startActivityForResult(intent, 20);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_post, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Post post = mPosts.get(i);
        viewHolder.caption.setText(post.getDescription());
        viewHolder.username.setText(post.getUser().getUsername());
        Date d = post.getCreatedAt();
        String dateText;
        if (d==null){
            dateText = "0s";
        } else {
            dateText = getRelativeTimeAgo(d.toString());
        }
        viewHolder.timePosted.setText(dateText);
        Glide.with(context).load(post.getImage().getUrl()).into(viewHolder.ivPost);
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        /*
        Calculates relative time
         */
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

}
