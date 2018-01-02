package org.androidproject.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StateChangeActivity extends Activity {

    String key_value;
    String stateStr;

    RadioGroup rg;
    RadioButton rbtn1;
    RadioButton rbtn2;
    Button btn_cancel;
    Button btn_ok;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    ItemSellerActivity ISActivity = (ItemSellerActivity) ItemSellerActivity.ItemSellerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_state_change);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int sizeWidth =  dm.widthPixels;

        getWindow().getAttributes().width = sizeWidth - 10;

        Intent intent = getIntent();
        key_value = intent.getStringExtra("key_value");



        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rbtn1 = (RadioButton) findViewById(R.id.radio1);
        rbtn2 = (RadioButton) findViewById(R.id.radio2);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_ok = (Button) findViewById(R.id.btn_ok);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference();

        mFirebaseDatabase.child("posts").child(key_value).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListViewItem item = dataSnapshot.getValue(ListViewItem.class);

                stateStr = item.getState();

                if(stateStr.equals("비완료")){
                    rg.check(rbtn1.getId());
                }
                else {
                    rg.check(rbtn2.getId());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void btn_cancel_click(View v){
        //ISActivity.finish();
        //Intent intent = new Intent(this, ItemSellerActivity.class);
        //intent.putExtra("key_value", key_value);
        //startActivity(intent);
        finish();
    }

    public void btn_ok_click(View v){
        int id = rg.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(id);
        String state_str = rb.getText().toString();
        String str;

        if(state_str.equals("판매중"))
            str = "비완료";
        else
            str = "완료";

        mFirebaseDatabase.child("posts").child(key_value).child("state").setValue(str);

        ISActivity.finish();
        Intent intent = new Intent(this, ItemSellerActivity.class);
        intent.putExtra("key_value", key_value);
        startActivity(intent);
        finish();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }


}
