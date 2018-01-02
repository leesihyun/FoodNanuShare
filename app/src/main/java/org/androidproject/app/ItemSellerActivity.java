package org.androidproject.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

public class ItemSellerActivity extends AppCompatActivity {

    private String key_value;

    private TextView content_title;
    private ImageView content_image;
    private TextView content_foodName;
    private TextView content_category;
    private TextView content_openDate;
    private TextView content_expDate;
    private TextView content_cost;
    private TextView content_area;
    private TextView content_state;

    String Ititle = "";
    String IfoodName = "";
    String Icategory = "";
    String IopenDate = "";
    String IexpDate = "";
    String Icost = "";
    String Iarea = "";
    String Istate = "";

    private boolean content_check = true;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mStorageRef;

    Button btn_ch;
    Button btn1;
    Button btn2;

    ListViewItem citem;

    boolean isImagefitToScreen = false;

    public static Activity ItemSellerActivity;
    MainActivity MActivity = (MainActivity) MainActivity.MainActivity;
    TabActivity TActivity = (TabActivity) TabActivity.TabActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_seller);

        ItemSellerActivity = ItemSellerActivity.this;

        Intent intent = getIntent();
        key_value = intent.getStringExtra("key_value");

        content_title = (TextView)findViewById(R.id.content_title);
        content_state = (TextView)findViewById(R.id.content_state);
        content_foodName = (TextView)findViewById(R.id.content_foodName);
        content_category = (TextView)findViewById(R.id.content_category);
        content_openDate = (TextView)findViewById(R.id.content_openDate);
        content_expDate = (TextView)findViewById(R.id.content_expDate);
        content_cost = (TextView)findViewById(R.id.content_cost);
        content_area = (TextView)findViewById(R.id.content_area);

        content_image = (ImageView)findViewById(R.id.content_image);

        btn_ch = (Button) findViewById(R.id.btn_state_change);
        btn1 = (Button) findViewById(R.id.item_button1);
        btn2 = (Button) findViewById(R.id.item_button2);

        btn1.setText("게시글 삭제");
        btn2.setText("게시글 수정");

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
                    Toast.makeText(ItemSellerActivity.this, "사진을 한번 더 클릭해 보세요", Toast.LENGTH_SHORT).show();

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
            Ititle = citem.getTitle();
            IfoodName = citem.getFoodName();
            Icategory = citem.getCategory();
            IopenDate = citem.getOpenDate();
            IexpDate = citem.getExpDate();
            Icost = citem.getCost();
            Iarea = citem.getArea();
            Istate = citem.getState();

            String stPhoto = citem.getPhoto();
            StorageReference mySRef = mStorageRef.child("posts").child(stPhoto + ".jpg");

            mySRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Picasso.with(ItemSellerActivity.this).load(uri).into(content_image);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        else{
            Ititle = "";
            IfoodName = "";
            Icategory = "";
            IopenDate = "";
            IexpDate = "";
            Icost = "";
            Iarea = "";
            Istate = "";
        }

        content_title.setText(Ititle);
        if(Istate.equals("비완료"))
            content_state.setText("판매중");
        else
            content_state.setText("판매완료");
        content_foodName.setText(IfoodName);
        content_category.setText(Icategory);
        content_openDate.setText(IopenDate);
        content_expDate.setText(IexpDate);
        content_cost.setText(Icost);
        content_area.setText(Iarea);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TActivity.finish();
        MActivity.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void btnChClick(View v){
        Toast.makeText(this, "게시글 상태 변환", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent (this, StateChangeActivity.class);
        intent.putExtra("key_value", key_value);
        startActivity(intent);
    }


    public void btn1Click(View v){
        //게시글 삭제
        Toast.makeText(this, "게시글 삭제", Toast.LENGTH_SHORT).show();
        content_check = false;

        TActivity.finish();
        MActivity.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        mFirebaseDatabase.child("posts").child(key_value).removeValue();
        finish();
    }

    public void btn2Click(View v){
        //게시글 수정

        Toast.makeText(this, "게시글 수정하기", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ReWriteActivity.class);
        intent.putExtra("key_value", key_value);
        startActivity(intent);
        finish();


    }


}
