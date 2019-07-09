package com.example.instagram;//package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;

import java.util.List;

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

        public ViewHolder(View itemView)  {
            super(itemView);
            ivPost = itemView.findViewById(R.id.ivPost);
            caption = itemView.findViewById(R.id.postCaption);
            username = itemView.findViewById(R.id.userPosted);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
//                Post post = mPosts.get(position);
//                Intent intent = new Intent(context, HomeActivity.class);
//                intent.putExtra("post", post);
//                context.startActivity(intent);
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
        // TODO: set text
        viewHolder.caption.setText(post.getDescription());
        viewHolder.username.setText(post.getUser().getUsername());
        Glide.with(context).load(post.getImage().getUrl()).into(viewHolder.ivPost);

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }
}
