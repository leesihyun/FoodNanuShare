package org.androidproject.app;

//import android.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.androidproject.app.data.StaticConfig;
import org.androidproject.app.service.ServiceUtils;

import java.util.TimerTask;

public class TabActivity extends AppCompatActivity{
    private static String TAG = "TabActivity";
    Fragment fragment;
    long lastPressed;

    private TimerTask second;
    private int timer_sec;
    private int count;
    private final Handler handler = new Handler();
    private CountDownTimer mCountDown = null;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    public static Activity TabActivity;
    MainActivity MActivity = (MainActivity) MainActivity.MainActivity;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    getSupportActionBar().setTitle("푸드나누쉐어");
                        //getSupportActionBar().setTitle("홈화면");
                    switchFragment(fragment);
                        return true;

                case R.id.navigation_friends:
                    fragment = new FriendsFragment();
                    getSupportActionBar().setTitle("채팅");
                    switchFragment(fragment);
                    return true;

                case R.id.navigation_profile:
                    fragment = new UserProfileFragment();
                    getSupportActionBar().setTitle("사용자정보");
                    switchFragment(fragment);
                    return true;

                case R.id.navigation_write:
                    fragment = new WriteFragment();
                    getSupportActionBar().setTitle("게시글쓰기");
                    switchFragment(fragment);
                    return true;

                case R.id.navigation_foodbank:
                    fragment = new MapsFragment();
                    getSupportActionBar().setTitle("푸드뱅크");
                    //fragment = new MapsFragment();
                    switchFragment(fragment);
                    return true;
            }
            return true;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        TabActivity = TabActivity.this;

        getSupportActionBar().setTitle("푸드나누쉐어");
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HomeFragment fragment = new HomeFragment();
        fragmentTransaction.add(R.id.main_container, fragment);
        fragmentTransaction.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initFirebase();
    }

    public void switchFragment(Fragment fragment){
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();// Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.main_container, fragment);
        // Commit the transaction
        transaction.commit();
    }

    private void initFirebase() {
        //Khoi tao thanh phan de dang nhap, dang ky
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    StaticConfig.UID = user.getUid();
                } else {
                    TabActivity.this.finish();
                    // User is signed in
                    startActivity(new Intent(TabActivity.this, LoginActivity.class));
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        ServiceUtils.stopServiceFriendChat(getApplicationContext(), false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        ServiceUtils.startServiceFriendChat(getApplicationContext());
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - lastPressed < 1500){
            finish();
            MActivity.finish();
        }
        Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        lastPressed = System.currentTimeMillis();

    }

    /*public void testStart() {

        timer_sec = 0;

        count = 0;

        second = new TimerTask() {




            @Override

            public void run() {

                Log.i("Test", "Timer start");

                timer_sec++;

            }

        };

        Timer timer = new Timer();

        timer.schedule(second, 0, 1000);

    }*/
}