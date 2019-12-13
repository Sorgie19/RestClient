package edu.uark.rtsorge.restclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity {

    //API
    JsonPlaceHolderApi jsonPlaceHolderApi;
    //Listview used for posts
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);

        //Retrofit init
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


        getUsers();

        //Listview click listener that passes position of clicked item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
                Intent intent = new Intent(view.getContext(), PostActivity.class);
                intent.putExtra("POSITION", i);
                Log.e("POSITION", String.valueOf(i));
                startActivity(intent);
            }
        });
    }

    //Get request and pulls all the posts
    //Displays them in a listview with the username
    //Compares posts userid to the hashmap of userid's and username
    private void getPosts(final HashMap<Integer, String> userHashMap)
    {
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response)
            {
                if(!response.isSuccessful())
                {
                    Log.e("Not successfull", String.valueOf(response.code()));
                    return;
                }

                //List of posts
                List<Post> posts = response.body();
                //Creating array of posts for listview
                String[] postsList = new String[posts.size()];
                for(int i = 0; i < postsList.length; i++)
                {
                    //Checks if userid matches hashmap key value paair
                    String userName = null;
                    if(userHashMap.containsKey(posts.get(i).getUserId()))
                        userName = userHashMap.get(posts.get(i).getUserId());
                    //Constructing string
                    String content = "";
                    content += "\nPost ID: " + posts.get(i).getId() + "\n";
                    content += "UserName: " + userName + "\n";
                    content += "Title: " + posts.get(i).getTitle() + "\n";
                    postsList[i] = content;

                }

                //Create listview from array of posts
                listView.setAdapter(new ArrayAdapter<String>(
                        getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        postsList
                ));
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t)
            {
                Log.e("onFailure", t.getMessage());
            }
        });
    }

    //Pulls list of users
    //Creates a hashmap with user id parings and names
    //Calls getPosts which gets all the posts
    //Passes the hashmap to display username
    private void getUsers()
    {
        Call<List<User>> call = jsonPlaceHolderApi.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (!response.isSuccessful()) {
                    Log.e("userOnResponse", String.valueOf(response.code()));
                    return;
                }
                
                List<User> users = response.body();
                HashMap<Integer, String> map = new HashMap<>();
                for(User user : users)
                    map.put(user.getId(), user.getUsername());
                getPosts(map);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("getUsers Main Activity", t.toString());

            }
        });
    }
}