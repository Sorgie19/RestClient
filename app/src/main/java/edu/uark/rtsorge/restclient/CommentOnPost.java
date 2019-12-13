package edu.uark.rtsorge.restclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentOnPost extends AppCompatActivity {

    TextView prompt;
    EditText nameInput;
    EditText emailInput;
    EditText titleComment;
    EditText commentInput;
    Button buttonSubmit;
    int position;
    JsonPlaceHolderApi jsonPlaceHolderApi;

    //Used to enter data for the comment.
    //Then passes it back to previous activity to POST
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_on_post);
        initComponents();
        Intent thisIntent = getIntent();
        position = thisIntent.getExtras().getInt("POSITION");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameInput.getText().toString();
                String email = emailInput.getText().toString();
                String title = titleComment.getText().toString();
                String comment = commentInput.getText().toString();
                //Requires all fields to not be empty
                if (name.isEmpty() || email.isEmpty() || title.isEmpty() ||comment.isEmpty()) {
                    Toast.makeText(CommentOnPost.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("NAME", name);
                    intent.putExtra("EMAIL", email);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("COMMENT", comment);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    public void initComponents() {
        prompt = (TextView) findViewById(R.id.promptText);
        nameInput = (EditText) findViewById(R.id.nameInput);
        emailInput = (EditText) findViewById(R.id.emailInput);
        commentInput = (EditText) findViewById(R.id.commentInput);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        titleComment = (EditText)findViewById(R.id.titeComment);
    }
}
