package org.androidproject.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidproject.app.model.User;
import org.androidproject.app.util.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class WriterInfoActivity extends AppCompatActivity {
    private  String post;
    // Button btnWrite = (Button)findViewById(R.id.btnWrite);
    //  Intent intent = getIntent();
    //  post = intent.getStringExtra("key_value");
    //intent.getStringExtra("KEY_NAME");


    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    List<ReviewItem> mItem;
    public String w_name;
    public String w_email;
    public String w_avata="defalut";
    public String w_gender;

    String TAG =getClass().getSimpleName();
    ReviewAdapter mAdapter;

    String writer_key;
    User Writer;
    TextView tvUserName;
    TextView tvUseremail;
    TextView tvUsergender;
    ImageView avatar;
    User myAccount;

    boolean check = false;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseInstance;


    // Context context=getActivity().getApplicationContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_info);
        mRecyclerView = (RecyclerView)findViewById(R.id.reviewBoard);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        // mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //  list_cate = this.getResources().getStringArray(R.array.list_category);
        // list_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list_cate);

        //list_sp = (Spinner)v.findViewById(R.id.list_select_category);
        // search_btn = (Button)v.findViewById(R.id.list_search);

        // list_sp.setAdapter(list_adapter);

        mItem = new ArrayList<>();

        mAdapter = new ReviewAdapter(mItem, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        getItemData();

        Intent intent = getIntent();
        writer_key = intent.getStringExtra("key_value");

        tvUserName = (TextView)findViewById(R.id.tv_username);
        tvUseremail = (TextView)findViewById(R.id.tv_useremail);
        tvUsergender = (TextView)findViewById(R.id.tv_usergender);

        avatar = (ImageView)findViewById(R.id.img_avatar);

        myRef.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Writer = dataSnapshot.child(writer_key).getValue(User.class);
                w_name = Writer.getName();
                w_avata = Writer.getAvata();
                w_email=Writer.getEmail();
                w_gender=Writer.getGender();
                setImageAvatar(w_avata);
                tvUserName.setText(w_name);
                tvUseremail.setText(w_email);
                tvUsergender.setText(w_gender);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getItemData() {

        mItem.clear();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        myRef = mFirebaseInstance.getReference();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue().toString();
                if(value.contains("reviews")) {

                    myRef.child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot_post) {

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                            long now = System.currentTimeMillis();
                            Date current_date = new Date(now);
                            Date open_date = null;

                            String value_post = dataSnapshot_post.getValue().toString();
                            Log.d(TAG, "Value_post is: " + value_post);

                            for (DataSnapshot dataSnapshot2 : dataSnapshot_post.getChildren()) {
                                String value2 = dataSnapshot2.getValue().toString();
                                Log.d(TAG, "Value_child is: " + value2);

                                ReviewItem item = dataSnapshot2.getValue(ReviewItem.class);


                                mItem.add(item);
                                mAdapter.notifyItemInserted(mItem.size() - 1);


                            }

                            if(mItem.size() == 0) {
                                Toast.makeText(getApplicationContext(), "비완료인 게시글 없음", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Collections.reverse(mItem);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else{
                    Toast.makeText(getApplicationContext(), "posts 없음", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    public void btnWriteClick(View v){
        //게시자와 채팅하기

        Toast.makeText(this, "리뷰 글쓰기", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ReviewWriteActivity.class);
        // intent.putExtra("key_value", postID);
        startActivity(intent);
        finish();


    }

    private void setImageAvatar(String imgBase64) {
        Context context = getApplicationContext();
        try {
            Resources res = getResources();
            Bitmap src;
            if (imgBase64.equals("default")) {
                src = BitmapFactory.decodeResource(res, R.drawable.default_avata);
            } else {
                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }

            avatar.setImageDrawable(ImageUtils.roundedImage(context, src));
        } catch (Exception e) {
        }
    }

}