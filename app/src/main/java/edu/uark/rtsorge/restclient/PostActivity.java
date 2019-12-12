package edu.uark.rtsorge.restclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

    JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        textView = findViewById(R.id.actualPost);
        listView = findViewById(R.id.listview2);
        userNameTextView = findViewById(R.id.textView4);
        int position;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Intent intent = getIntent();
        position = intent.getExtras().getInt("POSITION");
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        getPost(position);
    }

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
                for(int i = 0; i < users.size(); i++)
                {
                    Log.e("in loop", String.valueOf(i) + " userId: " + users.get(i).getId());
                    if(userId == users.get(i).getId())
                    {
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

    private void getComments(final int postId)
    {
        Call<List<Comment>> call = jsonPlaceHolderApi.getComments(postId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                List<Comment> comments = response.body();
                String[] commentsList = new String[comments.size()];
                for(int i = 0; i < commentsList.length; i++)
                {
                    String content = "";
                    content += "\nEmail: " + comments.get(i).getEmail() + "\n\n";
                    content += "Title: " + comments.get(i).getName() + "\n\n";
                    content += "Comment:" + comments.get(i).getText() + "\n\n";
                    commentsList[i] = content;

                }

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
}
