package edu.uark.rtsorge.restclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostActivity extends AppCompatActivity {

    TextView textView;
    TextView userNameTextView;
    ListView listView;
    Button commentButton;
    int postId;

    JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        initComponents();
        final int position;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Gets position from clicked item
        Intent intent = getIntent();
        position = intent.getExtras().getInt("POSITION");
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        //
        getPost(position);

        //Textview onclick listener for the clickable username
        userNameTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(PostActivity.this, "Opening profile for " + userNameTextView.getText(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), UserActivity.class);
                intent.putExtra("POSITION", position);
                intent.putExtra("NAME", userNameTextView.getText());
                startActivity(intent);

            }
        });

        //Comment button used to create a comment
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(getBaseContext(), CommentOnPost.class);
                commentIntent.putExtra("POSITION", position);
                startActivityForResult(commentIntent, 1);
            }
        });
    }

    private void initComponents()
    {
        textView = findViewById(R.id.actualPost);
        listView = findViewById(R.id.listview2);
        userNameTextView = findViewById(R.id.textView4);
        commentButton = findViewById(R.id.commentButton);
    }

    //Takes position as a pareameter
    //Gets List of Posts
    //Sets the current post title and text from selected post
    //Calls getUser and getComments
    private void getPost(final int position) {
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    textView.setText("Code: " + response.code());
                    return;
                }

                List<Post> posts = response.body();
                Log.e("POSTID", String.valueOf(posts.get(position).getId()));
                postId = posts.get(position).getId();
                String content = "";
                content += "Post Title: " + posts.get(position).getTitle() + "\n\n";
                content += "Post Body: " + posts.get(position).getText() + "\n";
                Log.e("post user id", String.valueOf(posts.get(position).getUserId()));
                textView.setText(content);
                Log.e("Before", "before get user gets called");
                getUser(posts.get(position).getUserId());
                Log.e("After", "after get user gets called");
                getComments(posts.get(position).getId());

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });
    }

    //Takes the userId as a parameter from getPosts
    //Matches the userid to a name
    //Sets the textview for the username
    private void getUser(final int userId) {
        Log.e("During", "get user gets called" + userId);
        Call<List<User>> call = jsonPlaceHolderApi.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful()) {
                    Log.e("userOnResponse", String.valueOf(response.code()));
                    return;
                }
                List<User> users = response.body();
                Log.e("getUserOnResponse", "got it");
                for (int i = 0; i < users.size(); i++) {
                    Log.e("in loop", String.valueOf(i) + " userId: " + users.get(i).getId());
                    if (userId == users.get(i).getId()) {
                        userNameTextView.setText(users.get(i).getName());
                        Log.e("Name", users.get(i).getName());
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("getUserOnFailure", t.toString());
            }
        });

    }

    //Takes postID as a parameter from getPosts
    //Get request all the comments related to that post
    //Puts them into listview with email, title, comment.
    private void getComments(final int postId) {
        Call<List<Comment>> call = jsonPlaceHolderApi.getComments(postId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                List<Comment> comments = response.body();
                String[] commentsList = new String[comments.size()];
                for (int i = 0; i < commentsList.length; i++)
                {
                    String content = "";
                    content += "\nEmail: " + comments.get(i).getEmail() + "\n\n";
                    content += "Title: " + comments.get(i).getName() + "\n\n";
                    content += "Comment:" + comments.get(i).getText() + "\n\n";
                    commentsList[i] = content;

                }

                //Listview of comments
                listView.setAdapter(new ArrayAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        commentsList
                ));

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

            }
        });
    }

    //Gets the result from the CommentOnPost activity
    //Calls createComment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("NAME");
                String email = data.getStringExtra("EMAIL");
                String title = data.getStringExtra("TITLE");
                String comment = data.getStringExtra("COMMENT");
                Log.e("CREATECOMMENTPOSTID", String.valueOf(postId));
                createComment(postId, name, email, title, comment);


            }
        }
    }

    //Creates a comment using POST
    //Gets postId and posts a new comment to specified post
    private void createComment(final int postIdMethod, String name, String email, String title, String userComment) {
        Comment comment = new Comment(postId, name, email, title, userComment);
        Call<Comment> call = jsonPlaceHolderApi.createComment(comment);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (!response.isSuccessful()) {
                    Log.e("Error:", "Creating comment");
                    return;
                }
                Comment postComment = response.body();
                String content = "";
                //content += "Code: " + response.code() +"\n";
                content += "\nEmail: " + postComment.getEmail() + "\n\n";
                content += "Title: " + postComment.getName() + "\n\n";
                content += "Comment: " + postComment.getText() + "\n\n";

                getComments(postIdMethod, content);
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {

            }
        });
    }

    //Used to get refreshed list of comments after POST of new comment
    private void getComments(final int postId, final String newComment) {
        Log.e("GET COMMENTS POST ID", String.valueOf(postId));
        Call<List<Comment>> call = jsonPlaceHolderApi.getComments(postId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                List<Comment> comments = response.body();
                //String[] commentsList = new String[comments.size() + 2];
                ArrayList<String> commentsList = new ArrayList<String>();
                Log.e("NEW COMMENT", newComment);
                commentsList.add(newComment);
                for (int i = 0; i < comments.size(); i++)
                {
                    String content = "";
                    content += "\nEmail: " + comments.get(i).getEmail() + "\n\n";
                    content += "Title: " + comments.get(i).getName() + "\n\n";
                    content += "Comment:" + comments.get(i).getText() + "\n\n";
                    commentsList.add(content);
                }

                String[] commentArray = commentsList.toArray(new String[commentsList.size()]);


                listView.setAdapter(new ArrayAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        commentArray
                ));

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

            }
        });
    }
}
