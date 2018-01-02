package org.androidproject.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReviewActivity extends AppCompatActivity {
    private String key_value;

    private TextView content_title;
    private TextView content_review;


    String review_title = "";
    String review_content = "";


    private boolean content_check = true;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mStorageRef;

    Button btn1;
    Button btn2;

    ReviewItem citem;


    /* @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_review);
     }*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        key_value = intent.getStringExtra("key_value");

        content_title = (TextView)findViewById(R.id.content_title);
        content_review = (TextView)findViewById(R.id.content_review);



        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        getData();

    }

    public void getData() {

        mFirebaseDatabase.child("reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                citem = dataSnapshot.child(key_value).getValue(ReviewItem.class);

                setData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void setData(){

        if(content_check) {
            review_title = citem.getReviewTitle();
            review_content = citem.getReviewContent();

        }
        else{
            review_title = "";
            review_content = "";

        }

        content_title.setText(review_title);
        content_review.setText(review_content);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}




