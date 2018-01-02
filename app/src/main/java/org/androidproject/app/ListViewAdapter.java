package org.androidproject.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder>  {

    List<ListViewItem> mItem;
    String stEmail;
    Context context;

    private StorageReference mStorageRef;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        String TAG=getClass().getSimpleName();

        public TextView post_state_text;
        public TextView post_title_text;
        public TextView post_foodName_text;
        public TextView post_cost_text;
        public TextView post_area_text;
        public ImageView post_image;
        public String key_value;
        //게시글을 작성한 user key 값
        public String key_user;
        //로그인 되고 있는 user key 값
        public String user_key;



        public ViewHolder(View itemView) {
            super(itemView);

            post_state_text = (TextView)itemView.findViewById(R.id.state_text);
            post_title_text = (TextView)itemView.findViewById(R.id.post_title);
            post_foodName_text = (TextView)itemView.findViewById(R.id.post_foodName);
            post_cost_text = (TextView)itemView.findViewById(R.id.post_cost);
            post_area_text = (TextView)itemView.findViewById(R.id.post_area);
            post_image = (ImageView)itemView.findViewById(R.id.post_image);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            //Toast.makeText(v.getContext(), key_value +" click", Toast.LENGTH_SHORT).show();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                user_key=user.getUid();
                Log.d("user_email,key", user.getEmail());

            }

            if(user_key.equals(key_user))
            {
                Intent intent = new Intent(v.getContext() , ItemSellerActivity.class);
                intent.putExtra("key_value", key_value);
                v.getContext().startActivity(intent);
            }
            else{
                Intent intent = new Intent(v.getContext() , ItemActivity.class);
                intent.putExtra("key_value", key_value);
                v.getContext().startActivity(intent);
            }
        }


    }

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;


    // ListViewAdapter의 생성자
    public ListViewAdapter(List<ListViewItem> mItem, Context context) {
        this.mItem = mItem;
        this.context = context;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_item, parent, false);

        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        mStorageRef = FirebaseStorage.getInstance().getReference();

        if(mItem.get(position).getState().equals("비완료"))
            holder.post_state_text.setText("");
        else
            holder.post_state_text.setText("판매완료");

        holder.post_title_text.setText(mItem.get(position).getTitle());
        holder.post_foodName_text.setText(mItem.get(position).getFoodName());
        holder.post_cost_text.setText(mItem.get(position).getCost()+" 원");
        holder.post_area_text.setText(mItem.get(position).getArea());
        holder.key_user = mItem.get(position).getUser_key();

        String stPhoto = mItem.get(position).getPhoto();
        holder.key_value = stPhoto;
        StorageReference mySRef = mStorageRef.child("posts").child(stPhoto+".jpg");

        mySRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.with(context).load(uri).into(holder.post_image);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        /*
        final long ONE_MEGABYTE = 1024 * 1024;
        mySRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.post_image.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
*/

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mItem.size();
    }
}
