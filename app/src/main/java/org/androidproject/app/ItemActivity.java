package org.androidproject.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.androidproject.app.model.User;
import org.androidproject.app.util.ImageUtils;

public class ItemActivity extends AppCompatActivity {

    private String key_value;

    private TextView content_state;
    private TextView content_title;
    private ImageView content_image;
    private TextView content_foodName;
    private TextView content_category;
    private TextView content_openDate;
    private TextView content_expDate;
    private TextView content_cost;
    private TextView content_area;

    String Istate = "";
    String Ititle = "";
    String IfoodName = "";
    String Icategory = "";
    String IopenDate = "";
    String IexpDate = "";
    String Icost = "";
    String Iarea = "";

    public String w_name;
    public String w_avata="defalut";
    public String w_gender;

    private boolean content_check = true;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mStorageRef;

    LinearLayout btn3;
    User Writer;
    ImageView avatar;
    private String writer_key;
    TextView tv_username;
    ListViewItem citem;

    boolean isImagefitToScreen = false;

    MainActivity MActivity = (MainActivity) MainActivity.MainActivity;
    TabActivity TActivity = (TabActivity) TabActivity.TabActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent intent = getIntent();
        key_value = intent.getStringExtra("key_value");

        content_state = (TextView)findViewById(R.id.content_state);
        content_title = (TextView)findViewById(R.id.content_title);
        content_foodName = (TextView)findViewById(R.id.content_foodName);
        content_category = (TextView)findViewById(R.id.content_category);
        content_openDate = (TextView)findViewById(R.id.content_openDate);
        content_expDate = (TextView)findViewById(R.id.content_expDate);
        content_cost = (TextView)findViewById(R.id.content_cost);
        content_area = (TextView)findViewById(R.id.content_area);

        content_image = (ImageView)findViewById(R.id.content_image);

        btn3 = (LinearLayout) findViewById(R.id.writer);

        avatar = (ImageView)findViewById(R.id.img_avatar);
        tv_username = (TextView)findViewById(R.id.tv_username);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        getData();

        content_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
                int size =  dm.widthPixels;
                //Toast.makeText(ItemActivity.this, "화면 크기 : "+size, Toast.LENGTH_SHORT).show();
                if(isImagefitToScreen){
                    //Toast.makeText(ItemActivity.this, "꽉 찬 화면입니다/화면을 축소시키겠습니다.", Toast.LENGTH_SHORT).show();
                    isImagefitToScreen = false;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(400,400);
                    layoutParams.gravity = Gravity.CENTER;
                    content_image.setLayoutParams(layoutParams);
                    //content_image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.));
                    content_image.setAdjustViewBounds(true);
                }
                else{
                    //Toast.makeText(ItemActivity.this, "축소된 화면입니다/화면을 확대시키겠습니다", Toast.LENGTH_SHORT).show();
                    isImagefitToScreen = true;
                    content_image.setScaleType(ImageView.ScaleType.FIT_XY);
                    content_image.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                    Toast.makeText(ItemActivity.this, "사진을 한번 더 클릭해 보세요", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void getData() {

        mFirebaseDatabase.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                citem = dataSnapshot.child(key_value).getValue(ListViewItem.class);

                setData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void setData(){

        if(content_check) {
            Istate = citem.getState();
            Ititle = citem.getTitle();
            IfoodName = citem.getFoodName();
            Icategory = citem.getCategory();
            IopenDate = citem.getOpenDate();
            IexpDate = citem.getExpDate();
            Icost = citem.getCost();
            Iarea = citem.getArea();
            writer_key = citem.getUser_key();

            String stPhoto = citem.getPhoto();
            StorageReference mySRef = mStorageRef.child("posts").child(stPhoto + ".jpg");

            mySRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Picasso.with(ItemActivity.this).load(uri).into(content_image);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });


        }
        else{
            Istate = "";
            Ititle = "";
            IfoodName = "";
            Icategory = "";
            IopenDate = "";
            IexpDate = "";
            Icost = "";
            Iarea = "";
            w_avata = "defalut";
            w_gender = "";
            w_name = "";
        }

        if(Istate.equals("비완료")){
            content_state.setText("판매중");
        }
        else
            content_state.setText("판매완료");
        content_title.setText(Ititle);
        content_foodName.setText(IfoodName);
        content_category.setText(Icategory);
        content_openDate.setText(IopenDate);
        content_expDate.setText(IexpDate);
        content_cost.setText(Icost);
        content_area.setText(Iarea);
        tv_username.setText(key_value);

        mFirebaseDatabase.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Writer = dataSnapshot.child(writer_key).getValue(User.class);
                w_name = Writer.getName();
                w_avata = Writer.getAvata();
                setImageAvatar(w_avata);
                tv_username.setText(w_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TActivity.finish();
        MActivity.finish();
        finish();
    }


    public void btn3Click(View v){

        Toast.makeText(this, "게시자 정보보기", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WriterInfoActivity.class);
        intent.putExtra("key_value", writer_key);
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
