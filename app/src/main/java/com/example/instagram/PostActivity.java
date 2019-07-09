package com.example.instagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.model.Post;

import java.util.Date;

public class PostActivity extends AppCompatActivity {
    ImageView ivPost;
    TextView caption;
    TextView username;
    TextView timePosted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ivPost = findViewById(R.id.ivPost);
        caption = findViewById(R.id.postCaption);
        username = findViewById(R.id.userPosted);
        timePosted = findViewById(R.id.timePosted);
        loadContent();
    }

    public void loadContent(){
        Post post = (Post) getIntent().getExtras().get("post");
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
    }

    public void close(View v){
        finish();
    }
}
