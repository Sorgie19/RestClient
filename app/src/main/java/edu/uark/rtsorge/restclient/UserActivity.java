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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    JsonPlaceHolderApi jsonPlaceHolderApi;
    TextView profileName;
    TextView profileUsername;
    TextView profileEmail;
    TextView profilePhone;
    TextView profileWebsite;
    ListView profilePosts;
    Button profileMap;
    int p;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initializeComponents();

        Intent thisIntent = getIntent();
        final int position = thisIntent.getExtras().getInt("POSITION");
        p = position;
        final String name = thisIntent.getExtras().getString("NAME");
        profileName.setText(name);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        getOriginalPost(position,1);
    }

    void initializeComponents() {
        profileName = (TextView)findViewById(R.id.profileName);
        profileUsername = (TextView)findViewById(R.id.profileUsername);
        profileEmail = (TextView)findViewById(R.id.profileEmail);
        profilePhone = (TextView)findViewById(R.id.profilePhone);
        profileWebsite = (TextView)findViewById(R.id.profileWebsite);
        profilePosts = (ListView)findViewById(R.id.profilePosts);
        profileMap = (Button)findViewById(R.id.profileMapsButton);
        findViewById(R.id.profileMapsButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileMapsButton:
                Log.e("buttontest", "clicked button");
                getOriginalPost(p, 2);
                break;
            default:
                break;
        }
    }

    //Opens MapActivity psasing in lat and long coords
    public void openMaps(double latitude, double longitude)
    {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        mapIntent.putExtra("LATITUDE", latitude);
        mapIntent.putExtra("LONGITUDE", longitude);
        startActivity(mapIntent);
    }

    //Gets List of posts
    //Takes code as a parameter
    //if Code == 1 we get the get userId
    //if Code == 2 we get the coords from the map from get User
    private void getOriginalPost(final int position, final int code) {
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    Log.e("Code: ", String.valueOf(response.code()));
                    return;
                }
                List<Post> posts = response.body();
                Log.e("post user id", String.valueOf(posts.get(position).getUserId()));
                Log.e("Before", "before get user gets called");
                if(code == 1)//Get the user
                    getUser(posts.get(position).getUserId(), 1);
                if(code == 2)//get the coords for the map
                {
                    getUser(posts.get(position).getUserId(), 2);
                }
                //getListViewPosts(posts.get(position).getUserId());
                Log.e("After", "after get user gets called");
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("OnFailure", t.getMessage());
            }
        });
    }

    //Gets all the users information to be displayed
    private void getUser(final int userId, final int code) {
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
                if(code == 1)
                {
                    int currentUserId = -1;

                    for (int i = 0; i < users.size(); i++) {
                        Log.e("in loop", String.valueOf(i) + " userId: " + users.get(i).getId());
                        if (userId == users.get(i).getId()) {
                            profileName.setText("Name: " + users.get(i).getName());
                            profileUsername.setText("Username: " + users.get(i).getUsername());
                            profileEmail.setText("Email: " + users.get(i).getEmail());
                            profilePhone.setText("Phone: " + users.get(i).getPhone());
                            profileWebsite.setText("Website: " + users.get(i).getWebsite());
                            currentUserId = users.get(i).getId();
                            Log.e("Name", users.get(i).getName());
                            break;
                        }
                    }

                    if (currentUserId >= 0)
                        getListViewPosts(currentUserId);
                }
                else if(code == 2)
                {
                    for (int i = 0; i < users.size(); i++) {
                        Log.e("in loop", String.valueOf(i) + " userId: " + users.get(i).getId());
                        if (userId == users.get(i).getId()) {
                            Log.e("lat", String.valueOf(users.get(i).getAddress().getGeo().getLat()));
                            Log.e("lat", String.valueOf(users.get(i).getAddress().getGeo().getLng()));
                            openMaps(users.get(i).getAddress().getGeo().getLat(), users.get(i).getAddress().getGeo().getLng());
                            break;

                        }
                    }

                }


            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("getUserOnFailure", t.toString());
            }
        });

    }


    //Gets listview of all users posts
    private void getListViewPosts(int userId)
    {
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts(userId);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> posts = response.body();
                String[] postsList = new String[posts.size()];
                for(int i = 0; i < postsList.length; i++)
                {
                    String content = "";
                    content += "\nTitle: \n";
                    content += posts.get(i).getTitle() +"\n";
                    content += "Body: \n";
                    content += posts.get(i).getText() + "\n";
                    postsList[i] = content;
                }

                profilePosts.setAdapter(new ArrayAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        postsList
                ));
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("GetLIstViewPosts", t.getMessage());

            }
        });
    }


}
