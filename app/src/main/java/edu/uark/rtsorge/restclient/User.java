package edu.uark.rtsorge.restclient;

import com.google.gson.annotations.SerializedName;

public class User
{
    private int userId;
    private String name;
    private String email;
    private String[] address;
    private double[] geo;
    @SerializedName("username")
    private String username;


    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String[] getAddress() {
        return address;
    }

    public double[] getGeo() {
        return geo;
    }
}
