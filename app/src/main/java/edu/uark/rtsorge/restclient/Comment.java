package edu.uark.rtsorge.restclient;

import com.google.gson.annotations.SerializedName;

public class Comment {
    private int postId;
    private Integer id;
    private String name;
    private String email;

    @SerializedName("body")
    private String text;

    public int getPostId() {
        return postId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {return id;}

    public String getText() {
        return text;
    }

    public Comment(int postId, String name, String email, String title,String text) {
        this.postId = postId;
        this.name = name;
        this.email = email;
        this.text = text;
        this.name = title;
    }
}
