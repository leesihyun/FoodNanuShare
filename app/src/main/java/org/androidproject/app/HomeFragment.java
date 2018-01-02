package org.androidproject.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//import android.app.Fragment;

public class HomeFragment extends Fragment {

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    List<ListViewItem> mItem;

    String TAG =getClass().getSimpleName();
    ListViewAdapter mAdapter;

    boolean check = false;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseInstance;

    String[] list_cate;
    ArrayAdapter<String> list_adapter;
    String select_category;

    Spinner list_sp;
    Button search_btn;


    public EditText search_str;
    public EditText search_location_str;


    ProgressDialog dialog;



    final List<ListViewItem> filteredList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dialog = ProgressDialog.show(getActivity(),"","로딩 중입니다. 잠시만 기다려주세요", true);
        View v=inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.rvBoard);

        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        list_cate = getResources().getStringArray(R.array.list_category);
        list_adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, list_cate);

        list_sp = (Spinner)v.findViewById(R.id.list_select_category);
        //search_btn = (Button)v.findViewById(R.id.list_search);

        list_sp.setAdapter(list_adapter);

        mItem = new ArrayList<>();

        mAdapter = new ListViewAdapter(mItem, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        list_sp.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected
                            (AdapterView<?>parent, View view, int pos, long id){
                        select_category = parent.getItemAtPosition(pos).toString();
                        //Toast.makeText(getActivity().getApplicationContext(), parent.getItemAtPosition(pos).toString()+"선택", Toast.LENGTH_SHORT).show();
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                        SharedPreferences.Editor prefsEditor = prefs.edit();
                        prefsEditor.putString("select_category", parent.getItemAtPosition(pos).toString());
                        prefsEditor.commit();
                        getItemData();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent){

                    }
                }
        );

        search_str = (EditText)v.findViewById(R.id.search);
        search_location_str = (EditText)v.findViewById(R.id.search_location);
        search_by_string();
        search_by_location();

        return v;
    }

    public void getItemData() {

        mItem.clear();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        myRef = mFirebaseInstance.getReference();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.hide();
                String value = dataSnapshot.getValue().toString();
                if(value.contains("posts")) {

                    myRef.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
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

                                ListViewItem item = dataSnapshot2.getValue(ListViewItem.class);

                                //Toast.makeText(getActivity(), item.getCost(), Toast.LENGTH_SHORT).show();
                                try {
                                    open_date = dateFormat.parse(item.getOpenDate() + " 23:59");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if(select_category.equals("전체")) {
                                    if (open_date.after(current_date)) {
                                        mItem.add(item);
                                        mAdapter.notifyItemInserted(mItem.size() - 1);
                                    }
                                }
                                else {
                                    if (open_date.after(current_date) && select_category.equals(item.getCategory())) {
                                        mItem.add(item);
                                        mAdapter.notifyItemInserted(mItem.size() - 1);
                                    }
                                }


                                //  Log.d(TAG, "찾으려는 타이틀 : "+ search_str.getText());
                                if (open_date.after(current_date) && !(item.getState().equals("완료"))){
                                    // Log.d(TAG, "조건에 만족하는 타이틀 : "+item.getTitle());
                                    if(search_str.getText().toString().contains(item.getFoodName())) {
                                        mItem.clear();
                                        // Log.d(TAG, "추가되려는 타이틀 : " + item.getTitle());
                                        mItem.add(item);
                                        mAdapter.notifyItemInserted(mItem.size() - 1);
                                    }

                                }
                            }

                            if(mItem.size() == 0) {
                                Toast.makeText(getContext(), "선택한 카테고리에 해당하는 게시물 없음", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "posts 없음", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void search_by_string(){



        search_str.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                String Location_searchData;
                String searchData;
                String searchData2;
                //final SharedPreferences search_sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                final SharedPreferences location_sp = getActivity().getSharedPreferences("search_location_string", Activity.MODE_PRIVATE);
                final SharedPreferences search_sp = getActivity().getSharedPreferences("search_string",Activity.MODE_PRIVATE);
                //final String location_str = location_sp.getString("LocationValue", "");

                Log.d(TAG, "지역검색어에 들어있는 값 : "+location_sp.getString("LocationValue", ""));
                Log.d(TAG, "내가 입력한 텍스트 : "+query.toString());
                filteredList.clear();

                for (int i = 0; i < mItem.size(); i++) {
                    searchData = mItem.get(i).getFoodName();
                    searchData2 = mItem.get(i).getTitle();
                    Location_searchData = mItem.get(i).getArea();
                    String keyWord = query.toString();
                    boolean isData = SoundSearcher.matchString(searchData, keyWord);
                    boolean isData2 = SoundSearcher.matchString(searchData2, keyWord);
                    if(location_sp.getString("LocationValue", "").length()==0) {
                        if (isData||isData2) {
                            filteredList.add(mItem.get(i));
                            Log.d(TAG, "리스트뷰에 추가된 아이템\n제목 : " + mItem.get(i).getTitle() + "\n재료명 : " + mItem.get(i).getFoodName());
                        }
                    }
                    else{
                        boolean isData_Location = SoundSearcher.matchString(Location_searchData, location_sp.getString("LocationValue", ""));
                        if((isData||isData2)&&isData_Location){
                            search_sp.edit().commit();
                            filteredList.add(mItem.get(i));
                            Log.d(TAG, "리스트뷰에 추가된 아이템\n제목 : " + mItem.get(i).getTitle() + "\n재료명 : " + mItem.get(i).getFoodName());
                        }
                    }
                }
                Log.d(TAG, "저장할 검색어 : "+query.toString());
                //Gson gson = new Gson();
                //search_sp.edit().putString("SearchValue",gson.toJson(this)).commit();
                search_sp.edit().putString("SearchValue",query.toString()).commit();
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                if(query.length()==0){
                    if(location_sp.getString("LocationValue", "").length()==0) {
                        Log.d(TAG, "검색어 null, 지역검색어 null");

                        //dialog = ProgressDialog.show(getActivity(), "", "로딩 중입니다. 잠시만 기다려주세요", true);

                        //prefs2.edit().remove("Value2").commit();
                        search_sp.edit().remove("SearchValue").commit();
                        dialog = ProgressDialog.show(getActivity(), "", "로딩 중입니다. 잠시만 기다려주세요", true);
                        //Log.d(TAG, "검색어에 들어있는 값 : " + search_sp.getString("SearchValue", ""));
                        getItemData();
                        mAdapter = new ListViewAdapter(mItem, getActivity());
                    }
                    else{
                        Log.d(TAG, "검색어 null, 지역검색어 값 있음");
                    }
                }
                else {
                    Log.d(TAG, "검색어 값 있음 ");
                    mAdapter = new ListViewAdapter(filteredList, getActivity());
                    //search_sp.edit().putString("SearchValue",query.toString());
                    //search_sp.edit().commit();
                    //edt.putString("Value",query.toString());
                    Log.d(TAG, "검색어에 들어있는 값 : "+search_sp.getString("SearchValue", ""));
                    //edt.commit();
                }
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();  // data set changed


            }
        });
    }
    public void search_by_location(){

        //final SharedPreferences search_sp = getActivity().getSharedPreferences("search_string",Activity.MODE_PRIVATE);
        //final String search_str = search_sp.getString("SearchValue", "");

        search_location_str.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                String Location_searchData;
                String searchData;
                String searchData2;

                //final SharedPreferences location_sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                final SharedPreferences location_sp = getActivity().getSharedPreferences("search_location_string", Activity.MODE_PRIVATE);
                final SharedPreferences search_sp = getActivity().getSharedPreferences("search_string",Activity.MODE_PRIVATE);
                //final String str = search_sp.getString("SearchValue", "");

               // Gson gson1 = new Gson();
                //final String str = search_sp.getString("SearchValue", "");
                //MyObject obj = gson1.fromJson(, MyObject.class);

                Log.d(TAG, "검색어에 들어있는 값 : "+search_sp.getString("SearchValue", ""));
                Log.d(TAG, "내가 입력한 텍스트 : "+query.toString());

                filteredList.clear();
                for (int i = 0; i < mItem.size(); i++) {
                    Location_searchData = mItem.get(i).getArea();
                    searchData = mItem.get(i).getFoodName();
                    searchData2 = mItem.get(i).getTitle();
                    String keyWord = query.toString();
                    boolean isData_Location = SoundSearcher.matchString(Location_searchData, keyWord);
                    if(search_sp.getString("SearchValue", "").length()==0) {
                        if (isData_Location) {
                            filteredList.add(mItem.get(i));
                            Log.d(TAG, "리스트뷰에 추가된 아이템\n제목 : " + mItem.get(i).getTitle() + "\n재료명 : " + mItem.get(i).getFoodName());
                        }
                    }
                    else{
                        boolean isData = SoundSearcher.matchString(searchData, search_sp.getString("SearchValue", ""));
                        boolean isData2 = SoundSearcher.matchString(searchData2, search_sp.getString("SearchValue", ""));
                        Log.d(TAG, "검색어 : "+search_sp.getString("SearchValue", ""));
                        if((isData2||isData)&&isData_Location) {
                            filteredList.add(mItem.get(i));
                            Log.d(TAG, "리스트뷰에 추가된 아이템\n제목 : " + mItem.get(i).getTitle() + "\n재료명 : " + mItem.get(i).getFoodName());
                        }
                    }
                }
                Log.d(TAG, "저장할 지역 검색어 : "+query.toString());
                //Gson gson = new Gson();
                location_sp.edit().putString("LocationValue",query.toString()).commit();
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                if(query.length()==0){
                    if(search_sp.getString("SearchValue", "").length()==0) {
                        Log.d(TAG, "검색어 null, 지역검색어 null");
                        location_sp.edit().remove("LocationValue").commit();
                        dialog = ProgressDialog.show(getActivity(), "", "로딩 중입니다. 잠시만 기다려주세요", true);
                        getItemData();

                        //Log.d(TAG, "지역검색어에 들어있는 값 : " + location_sp.getString("LocationValue", ""));

                        mAdapter = new ListViewAdapter(mItem, getActivity());

                    }
                    else{
                        Log.d(TAG, "검색어 값 있음, 지역검색어 null");
                        //search_by_string();
                    }

                }
                else {
                    Log.d(TAG, "지역검색어 값 있음");
                    mAdapter = new ListViewAdapter(filteredList, getActivity());
                    //location_sp.edit().putString("LocationValue",query.toString());
                    //location_sp.edit().commit();
                    Log.d(TAG, "지역검색어에 들어있는 값 : "+location_sp.getString("LocationValue", ""));

                }
                mRecyclerView.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();  // data set changed

            }
        });


    }
}
