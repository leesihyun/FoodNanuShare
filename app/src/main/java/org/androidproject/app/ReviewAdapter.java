package org.androidproject.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>  {

    List<ReviewItem> mItem;
    String stEmail;
    Context context;

    private StorageReference mStorageRef;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        String TAG=getClass().getSimpleName();

        public TextView post_title_text;
       // public ImageView post_image;
        public String key_value;


        public ViewHolder(View itemView) {
            super(itemView);
            post_title_text = (TextView)itemView.findViewById(R.id.post_title);
            //post_image = (ImageView)itemView.findViewById(R.id.post_image);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            //Toast.makeText(v.getContext(), key_value +" click", Toast.LENGTH_SHORT).show();
/*
            Log.w(TAG, "go to ReviewActivity");
            Intent intent = new Intent(v.getContext() , ReviewActivity.class);
            intent.putExtra("key_value", key_value);
            v.getContext().startActivity(intent);*/

        }


    }

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ReviewItem> ReviewList = new ArrayList<ReviewItem>() ;


    // ListViewAdapter의 생성자
    public ReviewAdapter(List<ReviewItem> mItem, Context context) {
        this.mItem = mItem;
        this.context = context;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_review, parent, false);

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

        holder.post_title_text.setText(mItem.get(position).getReviewTitle());

        //String stPhoto = mItem.get(position).getPhoto();
        //holder.key_value = stPhoto;
       // StorageReference mySRef = mStorageRef.child("posts").child(stPhoto+".jpg");

       /* mySRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
              //  Picasso.with(context).load(uri).into(holder.post_image);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

*/
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
