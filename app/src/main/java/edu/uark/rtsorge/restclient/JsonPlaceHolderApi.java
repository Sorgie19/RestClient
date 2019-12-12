package edu.uark.rtsorge.restclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi
{
    @GET("users")
    Call<List<User>> getUsers();

    //Relative URL
    @GET("posts")
    Call<List<Post>> getPosts();

    @GET("posts")
    Call<List<Post>> getPosts(@Query("userId") int userId);

    @GET("comments")
    Call<List<Comment>> getComments(@Query("postId") int postId);

    @POST("comments")
    Call<Comment>createComment(@Body Comment comment);









}
