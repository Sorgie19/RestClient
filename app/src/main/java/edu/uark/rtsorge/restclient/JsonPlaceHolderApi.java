package edu.uark.rtsorge.restclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi
{
    //Relative URL
    @GET("posts")
    Call<List<Post>> getPosts();
}
