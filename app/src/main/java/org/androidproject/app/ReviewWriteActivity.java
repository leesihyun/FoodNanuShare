package org.androidproject.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ReviewWriteActivity extends AppCompatActivity implements View.OnClickListener {

    private String reviewID;
    private EditText inputTitle;
    private String reviewTitle;
    //  private String category;
    //  private String openDate;
    private EditText inputFoodName;
    private String reviewContent;
    //private EditText inputCost;
    //private String cost;
    //private String expDate;
    //private String area;
    private String user_key;

    // private int year, month, day;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    //ArrayAdapter<CharSequence> adspin1, adspin2,adspin3;
    // String choice_si="";
    // String choice_gu="";
    // String choice_dong="";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);
        inputTitle = (EditText) findViewById(R.id.title_writing);
        inputFoodName = (EditText)findViewById(R.id.item_writing);
        //inputCost = (EditText) view.findViewById(R.id.cost_writing);
        // Button date_pick_btn = (Button) view.findViewById(R.id.date_pick_button);
        //Button date_pick_btn2 = (Button) view.findViewById(R.id.date_pick_button2);
    //    Button image_pick_btn = (Button)findViewById(R.id.image_pick_button);
        Button insert_btn = (Button) findViewById(R.id.insert);

        //date_pick_btn.setOnClickListener(this);
        //date_pick_btn2.setOnClickListener(this);
       // image_pick_btn.setOnClickListener((View.OnClickListener) this);
      //  image_pick_btn.setOnClickListener(this);
        insert_btn.setOnClickListener(this);
      //  sp.setAdapter(adapter);
    }

//import android.app.Fragment;

    /**
     * Created by seahyun on 2017-05-09.
     */

      /*  @Override

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


            final View view = inflater.inflate(R.layout.activity_review_write, container, false);

            final Spinner spin1 = (Spinner) view.findViewById(R.id.spinner);
            final Spinner spin2 = (Spinner) view.findViewById(R.id.spinner2);
            final Spinner spin3 = (Spinner) view.findViewById(R.id.spinner3);

            String[] str = getResources().getStringArray(R.array.category);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, str);


            Spinner sp = (Spinner)view.findViewById(R.id.select_category);
        //    final ArrayList<LocationInfo> mLocation = new ArrayList<>();
         //   final ArrayList<String> mLocation_SI = new ArrayList<>();
          //  final ArrayList<String> mLocation_GU = new ArrayList<>();
           // final ArrayList<String> mLocation_DONG = new ArrayList<>();


            //progressbar = (ProgressBar)view.findViewById(R.id.pbLogin);
            //progressbar.setVisibility(view.VISIBLE);
            // pbLogin = (ProgressBar) view.findViewById(R.id.pbLogin);


            //카테고리 선택
            inputTitle = (EditText) view.findViewById(R.id.title_writing);
            inputFoodName = (EditText) view.findViewById(R.id.item_writing);
            //inputCost = (EditText) view.findViewById(R.id.cost_writing);

           // Button date_pick_btn = (Button) view.findViewById(R.id.date_pick_button);
            //Button date_pick_btn2 = (Button) view.findViewById(R.id.date_pick_button2);
            Button image_pick_btn = (Button) view.findViewById(R.id.image_pick_button);
            Button insert_btn = (Button) view.findViewById(R.id.insert);

            //date_pick_btn.setOnClickListener(this);
            //date_pick_btn2.setOnClickListener(this);
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
        }*/
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

       //     Fragment fragment = null;
            //GregorianCalendar calendar = new GregorianCalendar();
/*
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            final TextView etPostPeriod = (TextView)findViewById(R.id.post_period_writing);
            final TextView etSellByDate = (TextView)findViewById(R.id.sell_by_date_writing);
            //날짜 선택 버튼 클릭
            if(v.getId() == R.id.date_pick_button) {
                Toast.makeText(getActivity() "날짜선택버튼 클릭", Toast.LENGTH_SHORT).show();
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
            }*/
            //사진 등록 버튼 클릭
        /*    if(v.getId() == R.id.image_pick_button){
                Toast.makeText(getApplicationContext(), "이미지 선택버튼 클릭", Toast.LENGTH_SHORT).show();
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
                new AlertDialog.Builder(getApplicationContext()).setTitle("업로드 할 이미지 선택").setPositiveButton("사진 촬영", cameraListener).setNeutralButton("앨범선택", albumListener).setNegativeButton("취소", cancelListener).show();
            }*/
            //등록 버튼 클릭
            if(v.getId()== R.id.insert){

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
                Toast.makeText(getApplicationContext(), "Result is not ok", Toast.LENGTH_SHORT).show();
                return;
            }

            switch(requestCode) {
                case PICK_FROM_ALBUM: {
                    Toast.makeText(getApplicationContext(), "PICK_FROM_ALBUM", Toast.LENGTH_SHORT).show();
                    mImageCaptureUri = data.getData();
                    Toast.makeText(getApplicationContext(), mImageCaptureUri.getPath().toString(), Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getApplicationContext(), "PICK_FROM_CAMERA", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), mImageCaptureUri.getPath().toString(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "이미지 선택완료", Toast.LENGTH_SHORT).show();
                    final Bundle extras = data.getExtras();
                    //Bitmap photo = data.getExtras().get("data");

                    iv_UserPhoto = (ImageView)findViewById(R.id.user_image);
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

                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
                out.flush();
                out.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        public void createPost() throws FileNotFoundException {
            reviewTitle = inputTitle.getText().toString();
            reviewContent = inputFoodName.getText().toString();
          //  cost = inputCost.getText().toString();
           // area = choice_si + " " + choice_gu + " " + choice_dong;

            if(reviewTitle == null || reviewContent == null )
            {
                Toast.makeText(getApplicationContext(), "게시물 다시 등록", Toast.LENGTH_SHORT).show();
            }
            else {

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("reviews");
                // mStorageRef = FirebaseStorage.getInstance().getReference("posts");

                reviewID = myRef.push().getKey();

                mStorageRef = FirebaseStorage.getInstance().getReference();

                //myRef.child(reviewID).child("state").setValue("비완료");
                myRef.child(reviewID).child("reviewTitle").setValue(reviewTitle);
                myRef.child(reviewID).child("reviewContent").setValue(reviewContent);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Name, email address, and profile photo Url
                    user_key=user.getUid();
                    Log.d("user_email,key", user.getEmail());

                }
                myRef.child(reviewID).child("user_key").setValue(user_key);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
               // photo_storage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                StorageReference mySRef = mStorageRef.child("reviews").child(reviewID + ".jpg");
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

             //   myRef.child(reviewID).child("photo").setValue(reviewID);

                Toast.makeText(getApplicationContext(), "게시물이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                startActivity(intent);
            }

        }

    }






