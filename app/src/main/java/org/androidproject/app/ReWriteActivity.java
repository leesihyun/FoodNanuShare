package org.androidproject.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ReWriteActivity extends AppCompatActivity implements Button.OnClickListener {

    String TAG =getClass().getSimpleName();

    private String postID;

    private EditText reinputTitle;
    private String retitle;
    private String recategory;
    private String reopenDate;
    private EditText reinputFoodName;
    private String refoodName;
    private EditText reinputCost;
    private String recost;
    private String reexpDate;
    private String rearea;

    TextView etPostPeriod;
    TextView etSellByDate;

    private int year, month, day;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    final ArrayList<LocationInfo> mLocation = new ArrayList<>();
    final ArrayList<String> mLocation_SI = new ArrayList<>();
    final ArrayList<String> mLocation_GU = new ArrayList<>();
    final ArrayList<String> mLocation_DONG = new ArrayList<>();

    ArrayAdapter adspin1;
    ArrayAdapter adspin2;
    ArrayAdapter adspin3;

    Spinner spin1;
    Spinner spin2;
    Spinner spin3;

    boolean spinner_check = false;
    String choice_si="";
    String choice_gu="";
    String choice_dong="";
    String[] setLoca;
    int SI_num = 0;
    int GU_num = 0;
    int DONG_num = 0;

    private Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    private String absolutePath;
    Bitmap photo_storage;

    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;

    ListViewItem reItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_write);

        Intent intent = getIntent();
        postID =  intent.getStringExtra("key_value");

        spin1 = (Spinner) findViewById(R.id.respinner);
        spin2 = (Spinner) findViewById(R.id.respinner2);
        spin3 = (Spinner) findViewById(R.id.respinner3);

        String[] str = getResources().getStringArray(R.array.category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, str);

        final Spinner sp = (Spinner)findViewById(R.id.reselect_category);

        reinputTitle = (EditText) findViewById(R.id.retitle_writing);
        reinputFoodName = (EditText) findViewById(R.id.reitem_writing);
        reinputCost = (EditText) findViewById(R.id.recost_writing);

        Button date_pick_btn = (Button) findViewById(R.id.redate_pick_button);
        Button date_pick_btn2 = (Button) findViewById(R.id.redate_pick_button2);
        Button image_pick_btn = (Button) findViewById(R.id.reimage_pick_button);
        Button insert_btn = (Button) findViewById(R.id.reinsert);

        etPostPeriod = (TextView) findViewById(R.id.repost_period_writing);
        etSellByDate = (TextView) findViewById(R.id.resell_by_date_writing);

        iv_UserPhoto = (ImageView)findViewById(R.id.reuser_image);

        date_pick_btn.setOnClickListener(this);
        date_pick_btn2.setOnClickListener(this);
        image_pick_btn.setOnClickListener(this);
        insert_btn.setOnClickListener(this);


        if(spinner_check) {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("location");

            myRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                        //String si = dataSnapshot2.getValue(Location.class).getSI();
                        //Toast.makeText(MainActivity.this, "aaa", Toast.LENGTH_SHORT).show();
                        LocationInfo location = dataSnapshot2.getValue(LocationInfo.class);
                        mLocation.add(location);
                        if (mLocation_SI.contains(location.getSI()) == false)
                            mLocation_SI.add(location.getSI());
                    }

                    //progressbar.setVisibility(view.GONE);
                    adspin1 = new ArrayAdapter(ReWriteActivity.this, android.R.layout.simple_spinner_dropdown_item, mLocation_SI);

                    adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spin1.setAdapter(adspin1);
                    spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            choice_si = adspin1.getItem(position).toString();
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor prefsEditor = prefs.edit();
                            prefsEditor.putString("si", parent.getItemAtPosition(position).toString());
                            prefsEditor.commit();
                            mLocation_GU.clear();
                            mLocation_DONG.clear();
                            for (int j = 0; j < mLocation.size(); j++) {

                                if (choice_si.equals(mLocation.get(j).getSI())) {
                                    if (mLocation_GU.contains(mLocation.get(j).getGU()) == false) {
                                        mLocation_GU.add(mLocation.get(j).getGU());
                                        //Toast.makeText(getActivity(), mLocation.get(j).getGU(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            adspin2 = new ArrayAdapter(ReWriteActivity.this, android.R.layout.simple_spinner_dropdown_item, mLocation_GU);
                            adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            spin2.setAdapter(adspin2);
                            //Toast.makeText(getActivity(), "aaa", Toast.LENGTH_SHORT).show();
                            spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    choice_gu = adspin2.getItem(position).toString();
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor prefsEditor = prefs.edit();
                                    prefsEditor.putString("gu", parent.getItemAtPosition(position).toString());
                                    prefsEditor.commit();
                                    mLocation_DONG.clear();
                                    //Toast.makeText(getActivity(), "bbb", Toast.LENGTH_SHORT);
                                    for (int j = 0; j < mLocation.size(); j++) {
                                        if ((choice_si.equals(mLocation.get(j).getSI())) && (choice_gu.equals(mLocation.get(j).getGU()))) {
                                            if (mLocation_DONG.contains(mLocation.get(j).getDONG()) == false)
                                                mLocation_DONG.add(mLocation.get(j).getDONG());
                                        }
                                    }
                                    //Toast.makeText(getActivity(), "ccc", Toast.LENGTH_SHORT);
                                    adspin3 = new ArrayAdapter(ReWriteActivity.this, android.R.layout.simple_spinner_dropdown_item, mLocation_DONG);
                                    adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                    spin3.setAdapter(adspin3);
                                    spin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            choice_dong = adspin3.getItem(position).toString();
                                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor prefsEditor = prefs.edit();
                                            prefsEditor.putString("dong", parent.getItemAtPosition(position).toString());
                                            prefsEditor.commit();
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            //Toast.makeText(getActivity(), "아무것도 선택x", Toast.LENGTH_SHORT).show();
                        }

                    });
                    // mLocation_SI.clear();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected
                            (AdapterView<?>parent, View view, int pos, long id){
                        recategory = parent.getItemAtPosition(pos).toString();
                        //Toast.makeText(getActivity().getApplicationContext(), parent.getItemAtPosition(pos).toString()+"선택", Toast.LENGTH_SHORT).show();
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor prefsEditor = prefs.edit();
                        prefsEditor.putString("recategory", parent.getItemAtPosition(pos).toString());
                        prefsEditor.commit();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent){

                    }
                }
        );

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("posts");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        myRef.child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                reItem = dataSnapshot.getValue(ListViewItem.class);

                reinputTitle.setText(reItem.getTitle());
                retitle = reItem.getTitle();

                recategory = reItem.getCategory();
                sp.setSelection(checkCategory());

                reopenDate = reItem.getOpenDate();
                String[] setDate_1 = reopenDate.split("-");
                etPostPeriod.setText(String.format("현재 ~ \n"+setDate_1[0]+" / "+ setDate_1[1]+" / "+setDate_1[2]));

                reinputFoodName.setText(reItem.getFoodName());
                refoodName = reItem.getFoodName();

                reinputCost.setText(reItem.getCost());
                recost = reItem.getCost();

                reexpDate = reItem.getExpDate();
                String[] setDate_2 = reexpDate.split("-");
                etSellByDate.setText(String.format("현재 ~ \n"+setDate_2[0]+" / "+ setDate_2[1]+" / "+setDate_2[2]));

                rearea = reItem.getArea();
                setLoca = rearea.split(" ");

                Log.d(TAG, "Loca[0]"+setLoca[0]);
                Log.d(TAG, "Loca[1]"+setLoca[1]);
                Log.d(TAG, "Loca[2]"+setLoca[2]);

                choice_si = setLoca[0];
                choice_gu = setLoca[1];
                choice_dong = setLoca[2];

                checkLocation();
                //checkSI();
                // spin1.setSelection(checkSI());

                StorageReference mySRef = mStorageRef.child("posts").child(postID+".jpg");

                mySRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Picasso.with(ReWriteActivity.this).load(uri).into(iv_UserPhoto);
                        BitmapDrawable d = (BitmapDrawable)iv_UserPhoto.getDrawable();
                        photo_storage = d.getBitmap();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

                          }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }



    @Override
    public void onClick(View v) {
        GregorianCalendar calendar = new GregorianCalendar();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //날짜 선택 버튼 클릭
        if(v.getId() == R.id.redate_pick_button) {
            Toast.makeText(this, "날짜선택버튼 클릭", Toast.LENGTH_SHORT).show();
            final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    //String getDate = sdf.format(date);
                    reopenDate = String.valueOf(year) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth);
                    try {
                        Date select_date = dateFormat.parse(reopenDate);
                        if(date.after(select_date)) {
                            Toast.makeText(ReWriteActivity.this, "현재날짜 이후의 날짜를 입력하세요", Toast.LENGTH_SHORT).show();
                            reopenDate = null;
                        }
                        else{
                            etPostPeriod.setText(String.format("현재 ~ \n%d / %d / %d", year, monthOfYear + 1, dayOfMonth));
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            };
            new DatePickerDialog(this, dateSetListener, year, month, day).show();
        }
        else if(v.getId() == R.id.redate_pick_button2) {
            Toast.makeText(this, "날짜선택버튼 클릭", Toast.LENGTH_SHORT).show();
            final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    reexpDate = String.valueOf(year) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth);
                    try {
                        Date select_date = dateFormat.parse(reexpDate);
                        if(date.after(select_date)) {
                            Toast.makeText(ReWriteActivity.this, "현재날짜 이후의 날짜를 입력하세요", Toast.LENGTH_SHORT).show();
                            reexpDate = null;
                        }
                        else{
                            etSellByDate.setText(String.format("%d / %d / %d 까지", year, monthOfYear + 1, dayOfMonth));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

            };
            new DatePickerDialog(this, dateSetListener, year, month, day).show();
        }
        //사진 등록 버튼 클릭
        else if(v.getId() == R.id.reimage_pick_button){
            Toast.makeText(this, "이미지 선택버튼 클릭", Toast.LENGTH_SHORT).show();
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    //카메라를 통해 사진 찍기
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String url = "tmp_"+String.valueOf(System.currentTimeMillis())+".jpg";
                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    startActivityForResult(intent, PICK_FROM_CAMERA);

                }
            };
            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    //앨범에서 사진 가져오기
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_FROM_ALBUM);
                }
            };
            DialogInterface.OnClickListener cancelListener = new  DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //취소 버튼 클릭
                    dialog.dismiss();
                }
            };
            new AlertDialog.Builder(this).setTitle("업로드 할 이미지 선택").setPositiveButton("사진 촬영", cameraListener).setNeutralButton("앨범선택", albumListener).setNegativeButton("취소", cancelListener).show();
        }
        //등록 버튼 클릭
        else if(v.getId()==R.id.reinsert){

            try {
                createPost();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK){
            Toast.makeText(this, "Result is not ok", Toast.LENGTH_SHORT).show();
            return;
        }

        switch(requestCode) {
            case PICK_FROM_ALBUM: {
                Toast.makeText(this, "PICK_FROM_ALBUM", Toast.LENGTH_SHORT).show();
                mImageCaptureUri = data.getData();
                Toast.makeText(this, mImageCaptureUri.getPath().toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX" , 400);
                intent.putExtra("outputY" , 400);
                intent.putExtra("aspectX" , 1);
                intent.putExtra("aspectY" , 1);

                intent.putExtra("scale" , true);
                intent.putExtra("return-data" , true);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }
            case PICK_FROM_CAMERA:
            {
                Toast.makeText(this, "PICK_FROM_CAMERA", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, mImageCaptureUri.getPath().toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                intent.putExtra("outputX" , 400);
                intent.putExtra("outputY" , 400);
                intent.putExtra("aspectX" , 1);
                intent.putExtra("aspectY" , 1);
                intent.putExtra("scale" , true);
                intent.putExtra("return-data" , true);
                //intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }
            case CROP_FROM_IMAGE:
            {
                Toast.makeText(this, "이미지 선택완료", Toast.LENGTH_SHORT).show();
                final Bundle extras = data.getExtras();
                //Bitmap photo = data.getExtras().get("data");

                if(resultCode != Activity.RESULT_OK){
                    //Toast.makeText(getActivity(), "Result is not ok", Toast.LENGTH_SHORT).show();
                    return;
                }
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel/"+System.currentTimeMillis()+".jpg";
                if(extras != null) {

                    photo_storage = (Bitmap)data.getExtras().get("data");

                    iv_UserPhoto.setImageBitmap(photo_storage);
                    storeCropImage(photo_storage, filePath);
                    absolutePath = filePath;
                    break;
                }

                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }

            }
        }
    }

    private void storeCropImage(Bitmap bitmap, String filePath){
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";
        File directory_SmartWheel = new File(dirPath);
        if(!directory_SmartWheel.exists())
            directory_SmartWheel.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try{
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
            out.flush();
            out.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void createPost() throws FileNotFoundException {
        retitle = reinputTitle.getText().toString();
        refoodName = reinputFoodName.getText().toString();
        recost = reinputCost.getText().toString();
        //area = choice_si + " " + choice_gu + " " + choice_dong;

        if(retitle == null || recategory == null || reopenDate == null || refoodName == null || recost == null || reexpDate == null  || rearea == null || photo_storage == null )
        {
            Toast.makeText(this, "게시물 다시 등록", Toast.LENGTH_SHORT).show();
        }
        else {

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("posts");
            // mStorageRef = FirebaseStorage.getInstance().getReference("posts");

            mStorageRef = FirebaseStorage.getInstance().getReference();

            myRef.child(postID).child("state").setValue("비완료");
            myRef.child(postID).child("title").setValue(retitle);
            myRef.child(postID).child("category").setValue(recategory);
            myRef.child(postID).child("openDate").setValue(reopenDate);
            myRef.child(postID).child("foodName").setValue(refoodName);
            myRef.child(postID).child("cost").setValue(recost);
            myRef.child(postID).child("expDate").setValue(reexpDate);
            myRef.child(postID).child("area").setValue(rearea);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo_storage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();


            StorageReference mySRef = mStorageRef.child("posts").child(postID + ".jpg");
            UploadTask uploadTask = mySRef.putBytes(data);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    myRef.child(postID).child("photo").setValue(postID);
                }
            });


            Toast.makeText(this, "게시물이 수정되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ItemSellerActivity.class);
            intent.putExtra("key_value", postID);
            startActivity(intent);
            finish();

        }

    }

    public int checkCategory(){
        int category_num = 0;
        String[] a = getResources().getStringArray(R.array.category);

        while(!recategory.equals(a[category_num])){
            category_num++;
        }

        return category_num;
    }
    /*
        public int checkSI(){
            int SI_num = 0;
    s
            if(mLocation_SI.size() != 0) {
                while (!mLocation_SI.get(SI_num).equals(setLoca[0])) {
                    SI_num++;
                }
            }

            return SI_num;

        }
    */
    public void checkLocation(){

        database = FirebaseDatabase.getInstance();
        DatabaseReference myLRef = database.getReference("location");

        mLocation.clear();
        myLRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {

                    LocationInfo location = dataSnapshot2.getValue(LocationInfo.class);
                    mLocation.add(location);
                    if (mLocation_SI.contains(location.getSI()) == false)
                        mLocation_SI.add(location.getSI());
                }

                int bbb = mLocation.size();
                String bb = String.valueOf(bbb);
                Log.d(TAG, "mLocation size : "+bb);

                int ccc = mLocation_SI.size();
                String cc = String.valueOf(ccc);
                Log.d(TAG, "SI size : "+cc);

                if(mLocation_SI.size() != 0) {
                    SI_num = 0;
                    while (!mLocation_SI.get(SI_num).equals(setLoca[0])) {
                        SI_num++;
                    }
                }

                mLocation_GU.clear();
                for (int j = 0; j < mLocation.size(); j++) {

                    if (choice_si.equals(mLocation.get(j).getSI())) {
                        if (mLocation_GU.contains(mLocation.get(j).getGU()) == false) {
                            mLocation_GU.add(mLocation.get(j).getGU());
                            //Toast.makeText(getActivity(), mLocation.get(j).getGU(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                int aaa = mLocation_GU.size();
                String aa = String.valueOf(aaa);
                Log.d(TAG, "GU SIZE : " + aa);

                if(mLocation_GU.size() != 0) {
                    GU_num = 0;
                    while (!mLocation_GU.get(GU_num).equals(setLoca[1])) {
                        GU_num++;
                    }
                }

                mLocation_DONG.clear();
                for (int j = 0; j < mLocation.size(); j++) {
                    if ((choice_si.equals(mLocation.get(j).getSI())) && (choice_gu.equals(mLocation.get(j).getGU()))) {
                        if (mLocation_DONG.contains(mLocation.get(j).getDONG()) == false)
                            mLocation_DONG.add(mLocation.get(j).getDONG());
                    }
                }

                int ddd = mLocation_DONG.size();
                String dd = String.valueOf(ddd);
                Log.d(TAG, "DONG size : "+dd);

                if(mLocation_DONG.size() != 0) {
                    DONG_num = 0;
                    while (!mLocation_DONG.get(DONG_num).equals(setLoca[2])) {
                        DONG_num++;
                    }
                }


                String qq = String.valueOf(SI_num);
                String ww = String.valueOf(GU_num);
                String ee = String.valueOf(DONG_num);


                Log.d(TAG, "SI : "+qq+" GU : "+ww+" DONG : "+ee);

                final ArrayAdapter adspin1 = new ArrayAdapter(ReWriteActivity.this, android.R.layout.simple_spinner_dropdown_item, mLocation_SI);
                adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin1.setAdapter(adspin1);
                spin1.setSelection(SI_num);
                Log.d(TAG,"시 완료");

                adspin2 = new ArrayAdapter(ReWriteActivity.this, android.R.layout.simple_spinner_dropdown_item, mLocation_GU);
                adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin2.setAdapter(adspin2);
                spin2.setSelection(GU_num);
                Log.d(TAG,"구 완료");

                adspin3 = new ArrayAdapter(ReWriteActivity.this, android.R.layout.simple_spinner_dropdown_item, mLocation_DONG);
                adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin3.setAdapter(adspin3);
                spin3.setSelection(DONG_num);
                Log.d(TAG,"동 완료");

                spinner_check = true;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}


