package me.lensevents.lensevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import me.lensevents.dto.GroupDto;
import me.lensevents.model.Category;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private Query mDatabaseReference;
    private Category category;
    private final GroupFragment.OnListFragmentInteractionListener mListener;

    private List<String> mGroupsIds = new ArrayList<>();
    private List<GroupDto> mGroups = new ArrayList<>();

    public GroupRecyclerViewAdapter(final Context context, Category category, GroupFragment.OnListFragmentInteractionListener mListener) {
        mContext = context;
        this.category = category;
        if (category != null) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Groups").orderByChild("category").equalTo(category.toString());
            mDatabaseReference.addChildEventListener(childGroupListener);
        } else {
            requestForPrincipal();
        }
        this.mListener = mListener;
    }

    private void requestForPrincipal() {
        FirebaseDatabase.getInstance().getReference().child("Groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
                if (groupDto.getMembers() != null && groupDto.getMembers().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    mGroups.add(groupDto);
                    mGroupsIds.add(dataSnapshot.getKey());
                    notifyItemInserted(mGroups.size() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
                int index = mGroupsIds.indexOf(dataSnapshot.getKey());
                if (groupDto.getMembers() != null && groupDto.getMembers().contains(FirebaseAuth.getInstance().getCurrentUser().getUid()) && index != -1) {
                    mGroups.set(index, groupDto);
                    notifyItemChanged(index);
                } else if (index != -1) {
                    mGroups.remove(index);
                    mGroupsIds.remove(index);
                    notifyItemRemoved(index);
                } else {
                    mGroups.add(groupDto);
                    mGroupsIds.add(dataSnapshot.getKey());
                    notifyItemInserted(mGroups.size() - 1);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = mGroupsIds.indexOf(dataSnapshot.getKey());
                if (index != -1) {
                    mGroups.remove(index);
                    mGroupsIds.remove(index);
                    notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    ChildEventListener childGroupListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
            mGroups.add(groupDto);
            mGroupsIds.add(dataSnapshot.getKey());
            notifyItemInserted(mGroups.size() - 1);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
            int index = mGroupsIds.indexOf(dataSnapshot.getKey());
            mGroups.set(index, groupDto);
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.fragment_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mGroups.get(position);
        GroupDto group = mGroups.get(position);
        final String key = mGroupsIds.get(position);

        Bitmap image = null;
        RequestForImageTask requestForImageTask = new RequestForImageTask();
        requestForImageTask.execute(group, holder.imageView);

        holder.nameView.setText(group.getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    Boolean isFromCategory = category != null ? true : false;
                    mListener.onListFragmentInteraction(holder.mItem, key, isFromCategory);
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
        public GroupDto mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.user_image);
            nameView = (TextView) itemView.findViewById(R.id.group_name);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " '" + "'";
    }

}
