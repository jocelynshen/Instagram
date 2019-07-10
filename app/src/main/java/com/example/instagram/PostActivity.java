package com.example.instagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

public class PostActivity extends AppCompatActivity {
    ImageView ivPost;
    TextView caption;
    TextView username;
    TextView timePosted;
    CommentAdapter commentAdapter;
    ArrayList<ArrayList<String>> comments;
    RecyclerView rvComments;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        post = (Post) getIntent().getExtras().get("post");
        ivPost = findViewById(R.id.ivPost);
        caption = findViewById(R.id.postCaption);
        username = findViewById(R.id.userPosted);
        timePosted = findViewById(R.id.timePosted);
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments);
        rvComments = findViewById(R.id.rvComments);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(layoutManager);
        rvComments.setAdapter(commentAdapter);
        loadContent();
    }

    public void loadContent(){
        caption.setText(post.getDescription());
        username.setText(post.getUser().getUsername());
        Date d = post.getCreatedAt();
        String dateText;
        if (d==null){
            dateText = "0s";
        } else {
            dateText = PostAdapter.getRelativeTimeAgo(d.toString());
        }
        timePosted.setText(dateText);
        Glide.with(this).load(post.getImage().getUrl()).into(ivPost);
        loadTopComments();
    }

    private void loadTopComments(){
        final Post.Query postQuery = new Post.Query();
        postQuery.getInBackground(post.getObjectId(), new GetCallback<Post>() {
            @Override
            public void done(Post object, ParseException e) {
                ArrayList<ArrayList<String>> refreshed_comments = (ArrayList<ArrayList<String>>) object.get("comments");
                for (ArrayList<String> comment : refreshed_comments){
                    comments.add(comment);
                    commentAdapter.notifyItemInserted(comments.size()-1);
                }
            }
        });
    }

    public void close(View v){
        finish();
    }

    public void postComment(View v){
        EditText et = findViewById(R.id.etAddComment);
        String username = ParseUser.getCurrentUser().getUsername();
        ArrayList<String> info = new ArrayList<String>();
        info.add(username);
        info.add(et.getText().toString());
        if (post.get("comments") == null){
            final ArrayList<ArrayList<String>> newArrayList = new ArrayList<ArrayList<String>>();
            newArrayList.add(info);
            System.out.println(newArrayList);
            post.put("comments", newArrayList);
            post.saveInBackground();
        } else{
            final ArrayList<ArrayList<String>> current = (ArrayList<ArrayList<String>>) post.get("comments");
            current.add(info);
            System.out.println(current);
            post.put("comments", current);
            post.saveInBackground();
        }
        comments.add(info);
        commentAdapter.notifyItemInserted(comments.size()-1);
        et.setText("");
    }
}
