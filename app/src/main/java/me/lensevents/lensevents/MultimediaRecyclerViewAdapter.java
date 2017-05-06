package me.lensevents.lensevents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import me.lensevents.lensevents.MultimediaFragment.OnListFragmentInteractionListener;

public class MultimediaRecyclerViewAdapter extends RecyclerView.Adapter<MultimediaRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;

    private final OnListFragmentInteractionListener mListener;
    private final Query query;

    public MultimediaRecyclerViewAdapter(Query query, OnListFragmentInteractionListener listener) {
        this.query = query;
        mListener = listener;
        mValues = new ArrayList<>();
        this.query.addChildEventListener(valueEventListener);
    }

    ChildEventListener valueEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String imageRef = dataSnapshot.getValue(String.class);
            mValues.add(imageRef);
            notifyItemInserted(mValues.size() - 1);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String imageRef = dataSnapshot.getValue(String.class);
            int index = mValues.indexOf(imageRef);
            mValues.set(index, imageRef);
            notifyItemChanged(index);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String imageRef = dataSnapshot.getValue(String.class);
            int index = mValues.indexOf(imageRef);
            mValues.remove(index);
            notifyItemRemoved(index);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_multimedia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        RequestForImageTask requestForImageTask = new RequestForImageTask();
        requestForImageTask.execute(mValues.get(position), holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.multimedia_picture);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}
