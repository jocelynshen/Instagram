package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ExternalProfileActivity extends AppCompatActivity {

    private ImageView settings;
    private ImageView profile;
    private ParseUser user;
    GridAdapter gridAdapter;
    ArrayList<Post> imageUrls;
    RecyclerView rvImages;
    public static final int GET_FROM_GALLERY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        imageUrls = new ArrayList<>();
        gridAdapter = new GridAdapter(imageUrls);
        rvImages = findViewById(R.id.rvPosts);
        rvImages.setLayoutManager(new GridLayoutManager(this, 3));
        rvImages.setAdapter(gridAdapter);

        user = (ParseUser) getIntent().getExtras().get("user");

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ExternalProfileActivity.this, settings);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        ParseUser.logOut();
                        Intent intent = new Intent(ExternalProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });
        profile = findViewById(R.id.ivProfileImage);
        TextView username = findViewById(R.id.username);
        username.setText(user.getUsername());
        if (user.getParseFile("profilePicture")!=null){
            Glide.with(this).load(user.getParseFile("profilePicture").getUrl()).apply(RequestOptions.circleCropTransform()).into(profile);
        }
        loadTopPosts();
    }

    public void onHome(View v){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void loadTopPosts(){
        final Post.Query postQuery = new Post.Query();
        postQuery.withUser().whereEqualTo("user", user);
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null){
                    for (int i = objects.size() - 1; i >= 0; i--){
                        imageUrls.add(objects.get(i));
                        gridAdapter.notifyItemInserted(imageUrls.size() - 1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
