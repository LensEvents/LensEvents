package me.lensevents.lensevents;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.lensevents.dto.GroupDto;
import me.lensevents.lensevents.UserFragment.OnListFragmentInteractionListener;
import me.lensevents.model.Group;
import me.lensevents.model.User;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private String key;
    private String mode;

    private List<String> mUsersIds = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();

    public UserRecyclerViewAdapter(final String key, final String mode, OnListFragmentInteractionListener listener) {
        mListener = listener;
        this.key = key;
        this.mode = mode;

        requestForGroup();

    }

    public void requestForGroup() {
        FirebaseDatabase.getInstance().getReference("Groups").orderByKey().equalTo(key)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        applyUserListener(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        mUsers.clear();
                        mUsersIds.clear();
                        notifyDataSetChanged();
                        applyUserListener(dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void applyUserListener(DataSnapshot dataSnapshot) {
        GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
        Query query;
        if (mode == "administrators") {
            for (String uid : groupDto.getAdministrators()) {
                query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("uid").equalTo(uid);
                query.addChildEventListener(userEventListener);
            }
        } else if (mode == "members") {
            if (groupDto.getMembers() == null) {
                groupDto.setMembers(new ArrayList<String>());
            }
            for (String uid : groupDto.getMembers()) {
                query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("uid").equalTo(uid);
                query.addChildEventListener(userEventListener);
            }
        }
    }

    final ChildEventListener userEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            mUsersIds.add(dataSnapshot.getKey());
            mUsers.add(user);
            notifyItemInserted(mUsers.size() - 1);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            User user = dataSnapshot.getValue(User.class);
            int index = mUsersIds.indexOf(dataSnapshot.getKey());
            mUsers.set(index, user);
            notifyItemChanged(index);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            int index = mUsersIds.indexOf(dataSnapshot.getKey());
            mUsers.remove(index);
            mUsersIds.remove(index);
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
                .inflate(R.layout.fragment_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mUsers.get(position);
        User group = mUsers.get(position);

        Bitmap image = null;
        RequestForImageTask requestForImageTask = new RequestForImageTask();
        try {
            image = requestForImageTask.execute(group).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.getStackTraceString(e);
        }

        holder.mImage.setImageBitmap(image);
        holder.mDisplayName.setText(mUsers.get(position).getDisplayName());

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
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImage;
        public final TextView mDisplayName;
        public User mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.user_image);
            mDisplayName = (TextView) view.findViewById(R.id.user_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}
