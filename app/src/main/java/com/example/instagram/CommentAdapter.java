package com.example.instagram;//package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private List<ArrayList<String>> mComments;
    Context context;

    public CommentAdapter(List<ArrayList<String>> comments){
        mComments = comments;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView username;
        public TextView comment;

        public ViewHolder(View itemView)  {
            super(itemView);
            username = itemView.findViewById(R.id.tvUsername);
            comment = itemView.findViewById(R.id.tvComment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_comment, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String username = mComments.get(i).get(0);
        String comment = mComments.get(i).get(1);
        viewHolder.username.setText(username);
        viewHolder.comment.setText(comment);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

}
