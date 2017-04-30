package me.lensevents.lensevents;

import android.content.Context;
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
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.lensevents.model.Group;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private Query mDatabaseReference;
    private ChildEventListener mValueEventListener;
    private final GroupFragment.OnListFragmentInteractionListener mListener;

    private List<String> mGroupsIds = new ArrayList<>();
    private List<Group> mGroups = new ArrayList<>();

    public GroupRecyclerViewAdapter(final Context context, Query ref, GroupFragment.OnListFragmentInteractionListener mListener) {
        mContext = context;
        mDatabaseReference = ref;
        this.mListener = mListener;

        ChildEventListener valueEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Group group = dataSnapshot.getValue(Group.class);
                mGroupsIds.add(dataSnapshot.getKey());
                mGroups.add(group);
                notifyItemInserted(mGroups.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Group group = dataSnapshot.getValue(Group.class);
                int index = mGroupsIds.indexOf(dataSnapshot.getKey());
                mGroups.set(index, group);
                notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = mGroupsIds.indexOf(dataSnapshot.getKey());
                mGroups.remove(index);
                mGroupsIds.remove(index);
                notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ref.addChildEventListener(valueEventListener);

        mValueEventListener = valueEventListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.fragment_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mGroups.get(position);
        Group group = mGroups.get(position);

        Bitmap image = null;
        RequestForImageTask requestForImageTask = new RequestForImageTask();
        try {
            image = requestForImageTask.execute(group).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.getStackTraceString(e);
        }

        holder.imageView.setImageBitmap(image);
        holder.nameView.setText(group.getName());

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
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameView;
        public final View mView;
        public Group mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.group_image);
            nameView = (TextView) itemView.findViewById(R.id.group_name);
        }
    }

    public void cleanupListener() {
        if (mValueEventListener != null) {
            mDatabaseReference.removeEventListener(mValueEventListener);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " '" + "'";
    }

}