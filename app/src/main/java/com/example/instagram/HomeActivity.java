package com.example.instagram;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public final String APP_TAG = "Instagram";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    PostAdapter postAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;
    private SwipeRefreshLayout swipeContainer; // handling swipe refresh
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts);
        rvPosts = findViewById(R.id.rvPosts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setReverseLayout(true);
        rvPosts.setLayoutManager(layoutManager);
        rvPosts.setAdapter(postAdapter);
        loadTopPosts();
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_purple,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };
        rvPosts.addOnScrollListener(scrollListener);
    }

    private void createPost(String description, ParseFile imageFile, ParseUser user){
        final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(imageFile);
        newPost.setUser(user);
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d("HomeActivity", "Create post success");
                } else {
                    e.printStackTrace();
                }
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
        posts.add(0, newPost);
        postAdapter.notifyItemInserted(0);
        rvPosts.scrollToPosition(0);
    }

    public void onProfile(View v){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void loadTopPosts(){
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null){
                    for (int i = objects.size() - 1; i >= 0; i--){
                        posts.add(objects.get(i));
                        postAdapter.notifyItemInserted(posts.size() - 1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onLaunchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(HomeActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.item_compose, null);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                ImageView dismiss = mView.findViewById(R.id.dismiss);
                ImageView sendPost = mView.findViewById(R.id.sendPost);
                final EditText etCaption = mView.findViewById(R.id.etCaption);
                dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ImageView ivPreview = mView.findViewById(R.id.ivPost);
                ivPreview.setImageBitmap(takenImage);
                sendPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String caption = etCaption.getText().toString();
                        File photoFile = getPhotoFileUri(photoFileName);
                        ParseFile parseFile = new ParseFile(photoFile);
                        ParseUser user = ParseUser.getCurrentUser();
                        createPost(caption, parseFile, user);
                        dialog.dismiss();

                    }
                });
                dialog.show();
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void fetchTimelineAsync(int page) {
        /*
        Handles refreshing
         */
        postAdapter.clear();
        loadTopPosts();
        swipeContainer.setRefreshing(false);
    }

    public void onHome(View v){
        rvPosts.scrollToPosition(0);
    }

    public void loadNextDataFromApi(int offset) {
        loadTopPosts();
    }
}
