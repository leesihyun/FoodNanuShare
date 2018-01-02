package org.androidproject.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import java.util.regex.Pattern;

//import android.app.Fragment;

/**
 * Created by seahyun on 2017-05-09.
 */

public class WriteFragment extends Fragment implements View.OnClickListener{


    private String postID;
    private EditText inputTitle;
    private String title;
    private String category;
    private String openDate;
    private String user_key;
    private EditText inputFoodName;
    private String foodName;
    private EditText inputCost;
    private String cost;
    private String expDate;
    private String area;

    private int year, month, day;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    //ArrayAdapter<CharSequence> adspin1, adspin2,adspin3;
    String choice_si="";
    String choice_gu="";
    String choice_dong="";

    private Uri mImageCaptureUri;
    private ImageView iv_UserPhoto;
    //private  int id_view;
    private String absolutePath;
    Bitmap photo_storage;

    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;
    //ProgressBar progressbar;

    String TAG = getClass().getSimpleName();

    MainActivity MActivity = (MainActivity) MainActivity.MainActivity;
    TabActivity TActivity = (TabActivity) TabActivity.TabActivity;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        final View view = inflater.inflate(R.layout.write_frg, container, false);

        final Spinner spin1 = (Spinner) view.findViewById(R.id.spinner);
        final Spinner spin2 = (Spinner) view.findViewById(R.id.spinner2);
        final Spinner spin3 = (Spinner) view.findViewById(R.id.spinner3);

        String[] str = getResources().getStringArray(R.array.category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, str);


        Spinner sp = (Spinner)view.findViewById(R.id.select_category);
        final ArrayList<LocationInfo> mLocation = new ArrayList<>();
        final ArrayList<String> mLocation_SI = new ArrayList<>();
        final ArrayList<String> mLocation_GU = new ArrayList<>();
        final ArrayList<String> mLocation_DONG = new ArrayList<>();


        //progressbar = (ProgressBar)view.findViewById(R.id.pbLogin);
        //progressbar.setVisibility(view.VISIBLE);
       // pbLogin = (ProgressBar) view.findViewById(R.id.pbLogin);

        //지역선택
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("location");
        Log.d(TAG, "addvalueEventListener_before()");
        final ProgressDialog dialog = ProgressDialog.show(getActivity(),"","로딩 중입니다. 잠시만 기다려주세요", true);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.hide();
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                    //String si = dataSnapshot2.getValue(Location.class).getSI();
                    //Toast.makeText(MainActivity.this, "aaa", Toast.LENGTH_SHORT).show();
                    LocationInfo location = dataSnapshot2.getValue(LocationInfo.class);
                    mLocation.add(location);

                    if (mLocation_SI.contains(location.getSI()) == false)
                        mLocation_SI.add(location.getSI());
                }

                //progressbar.setVisibility(view.GONE);


                final ArrayAdapter adspin1 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mLocation_SI);

                adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spin1.setAdapter(adspin1);
                spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        choice_si = adspin1.getItem(position).toString();
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
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
                        final ArrayAdapter adspin2 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mLocation_GU);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spin2.setAdapter(adspin2);
                        //Toast.makeText(getActivity(), "aaa", Toast.LENGTH_SHORT).show();
                        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                choice_gu = adspin2.getItem(position).toString();
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
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
                                final ArrayAdapter adspin3 = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, mLocation_DONG);
                                adspin3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                spin3.setAdapter(adspin3);
                                spin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        choice_dong = adspin3.getItem(position).toString();
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
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

        //카테고리 선택
        inputTitle = (EditText) view.findViewById(R.id.title_writing);
        inputFoodName = (EditText) view.findViewById(R.id.item_writing);
        inputCost = (EditText) view.findViewById(R.id.cost_writing);

        Button date_pick_btn = (Button) view.findViewById(R.id.date_pick_button);
        Button date_pick_btn2 = (Button) view.findViewById(R.id.date_pick_button2);
        Button image_pick_btn = (Button) view.findViewById(R.id.image_pick_button);
        Button insert_btn = (Button) view.findViewById(R.id.insert);

        date_pick_btn.setOnClickListener(this);
        date_pick_btn2.setOnClickListener(this);
        image_pick_btn.setOnClickListener(this);
        insert_btn.setOnClickListener(this);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected
                            (AdapterView<?>parent, View view, int pos, long id){
                        category = parent.getItemAtPosition(pos).toString();
                        //Toast.makeText(getActivity().getApplicationContext(), parent.getItemAtPosition(pos).toString()+"선택", Toast.LENGTH_SHORT).show();
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                        SharedPreferences.Editor prefsEditor = prefs.edit();
                        prefsEditor.putString("category", parent.getItemAtPosition(pos).toString());
                        prefsEditor.commit();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent){

                    }
                }
        );


        //setHasOptionsMenu(true);

        return view;
    }
    /*public void onResume(){
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("[푸드득]게시글쓰기");
    }*/
    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            Toast.makeText(getActivity(), "홈아이콘 클릭", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_search) {
            Toast.makeText(getActivity(), "검색버튼 클릭", Toast.LENGTH_SHORT).show();
            return true;
        }

        // if(id==R.id.write_list){
        //Toast.makeText(this, "글쓰기버튼 클릭", Toast.LENGTH_SHORT).show();
        // return true;
        //}
        return super.onOptionsItemSelected(item);
    }*/
    //버튼 클릭 처리
    public void onClick(View v){

        Fragment fragment = null;
        GregorianCalendar calendar = new GregorianCalendar();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        final TextView etPostPeriod = (TextView) getView().findViewById(R.id.post_period_writing);
        final TextView etSellByDate = (TextView) getView().findViewById(R.id.sell_by_date_writing);
        //날짜 선택 버튼 클릭
        if(v.getId() == R.id.date_pick_button) {
            Toast.makeText(getActivity(), "날짜선택버튼 클릭", Toast.LENGTH_SHORT).show();
            final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    //String getDate = sdf.format(date);
                    openDate = String.valueOf(year) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth);
                    try {
                        Date select_date = dateFormat.parse(openDate);
                        if(date.after(select_date)) {
                            Toast.makeText(getActivity(), "현재날짜 이후의 날짜를 입력하세요", Toast.LENGTH_SHORT).show();
                            openDate = null;
                        }
                        else{
                            etPostPeriod.setText(String.format("현재 ~ \n%d / %d / %d", year, monthOfYear + 1, dayOfMonth));
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            };
            new DatePickerDialog(getActivity(), dateSetListener, year, month, day).show();
        }
        else if(v.getId() == R.id.date_pick_button2) {
            Toast.makeText(getActivity(), "날짜선택버튼 클릭", Toast.LENGTH_SHORT).show();
            final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    expDate = String.valueOf(year) + "-" + String.valueOf(monthOfYear+1) + "-" + String.valueOf(dayOfMonth);
                    try {
                        Date select_date = dateFormat.parse(expDate);
                        if(date.after(select_date)) {
                            Toast.makeText(getActivity(), "현재날짜 이후의 날짜를 입력하세요", Toast.LENGTH_SHORT).show();
                            expDate = null;
                        }
                        else{
                            etSellByDate.setText(String.format("%d / %d / %d 까지", year, monthOfYear + 1, dayOfMonth));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

            };
            new DatePickerDialog(getActivity(), dateSetListener, year, month, day).show();
        }
        //사진 등록 버튼 클릭
        else if(v.getId() == R.id.image_pick_button){
            Toast.makeText(getActivity(), "이미지 선택버튼 클릭", Toast.LENGTH_SHORT).show();
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
            new AlertDialog.Builder(getActivity()).setTitle("업로드 할 이미지 선택").setPositiveButton("사진 촬영", cameraListener).setNeutralButton("앨범선택", albumListener).setNegativeButton("취소", cancelListener).show();
        }
        //등록 버튼 클릭
        else if(v.getId()==R.id.insert){

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
            Toast.makeText(getActivity(), "Result is not ok", Toast.LENGTH_SHORT).show();
            return;
        }

        switch(requestCode) {
            case PICK_FROM_ALBUM: {
                Toast.makeText(getActivity(), "PICK_FROM_ALBUM", Toast.LENGTH_SHORT).show();
                mImageCaptureUri = data.getData();
                Toast.makeText(getActivity(), mImageCaptureUri.getPath().toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX" , 1000);
                intent.putExtra("outputY" , 1000);
                intent.putExtra("aspectX" , 1);
                intent.putExtra("aspectY" , 1);

                intent.putExtra("scale" , true);
                intent.putExtra("return-data" , true);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }
            case PICK_FROM_CAMERA:
            {
                Toast.makeText(getActivity(), "PICK_FROM_CAMERA", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), mImageCaptureUri.getPath().toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                intent.putExtra("outputX" , 1000);
                intent.putExtra("outputY" , 1000);
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
                Toast.makeText(getActivity(), "이미지 선택완료", Toast.LENGTH_SHORT).show();
                final Bundle extras = data.getExtras();
                //Bitmap photo = data.getExtras().get("data");

                iv_UserPhoto = (ImageView)getView().findViewById(R.id.user_image);
                if(resultCode != Activity.RESULT_OK){
                    //Toast.makeText(getActivity(), "Result is not ok", Toast.LENGTH_SHORT).show();
                    return;
                }
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/FoodNanuShare/"+System.currentTimeMillis()+".jpg";
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

            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
            out.flush();
            out.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void createPost() throws FileNotFoundException {
        title = inputTitle.getText().toString();
        foodName = inputFoodName.getText().toString();
        cost = inputCost.getText().toString();
        area = choice_si + " " + choice_gu + " " + choice_dong;

        if(title == null || category == null || openDate == null || foodName == null || cost == null || expDate == null  || area == null ||photo_storage == null )
        {
            Toast.makeText(getActivity(), "입력하시지 않은 부분이 있습니다", Toast.LENGTH_SHORT).show();
        }
        else if(!(Pattern.matches("^[0-9]+$",cost))){
            Toast.makeText(getActivity(), "희망 가격란엔 숫자만 입력 가능합니다",Toast.LENGTH_SHORT).show();
        }
        else {

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("posts");
            // mStorageRef = FirebaseStorage.getInstance().getReference("posts");

            postID = myRef.push().getKey();

            mStorageRef = FirebaseStorage.getInstance().getReference();

            myRef.child(postID).child("state").setValue("비완료");
            myRef.child(postID).child("title").setValue(title);
            myRef.child(postID).child("category").setValue(category);
            myRef.child(postID).child("openDate").setValue(openDate);
            myRef.child(postID).child("foodName").setValue(foodName);
            myRef.child(postID).child("cost").setValue(cost);
            myRef.child(postID).child("expDate").setValue(expDate);
            myRef.child(postID).child("area").setValue(area);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                user_key=user.getUid();
                Log.d("user_email,key", user.getEmail());

            }
            myRef.child(postID).child("user_key").setValue(user_key);


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
                }
            });

            myRef.child(postID).child("photo").setValue(postID);

            TActivity.finish();
            MActivity.finish();
            Toast.makeText(getActivity(), "게시물이 등록되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);

        }

    }

}
