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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.lensevents.dto.EventMessageDto;
import me.lensevents.model.Event;
import me.lensevents.model.User;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private Query mDatabaseReference;
    private String eventKey;
    private ChildEventListener childEventListener;
    private final MessageFragment.OnFragmentInteractionListener mListener;

    private List<User> mUsers = new ArrayList<>();
    private List<EventMessageDto> mMessages = new ArrayList<>();

    public MessageRecyclerViewAdapter(String eventKey, MessageFragment.OnFragmentInteractionListener mListener) {


        this.childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event.getEventMessages() != null) {
                    List<EventMessageDto> eventMessageDtos = event.getEventMessages();
                    for (EventMessageDto e : eventMessageDtos) {
                        applyUserListener(e);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        };
        this.eventKey = eventKey;
        this.mListener = mListener;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Events").orderByKey().equalTo(eventKey);
        mDatabaseReference.addChildEventListener(childEventListener);

    }

    public void applyUserListener(EventMessageDto e) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("uid").equalTo(e.getSender());
        applyEventUserListener(query, e);
    }

    private void applyEventUserListener(Query query, final EventMessageDto e) {

        ChildEventListener childEventListenerUser = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                mUsers.add(user);
                mMessages.add(e);
                notifyItemInserted(mMessages.size() - 1);
                notifyItemInserted(mUsers.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        };
        query.addChildEventListener(childEventListenerUser);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (!mMessages.isEmpty()) {
            holder.mItem = mMessages.get(position);
            EventMessageDto eventMessage = mMessages.get(position);
            User user = mUsers.get(position);
            Bitmap image = null;
            RequestForImageTask requestForImageTask = new RequestForImageTask();
            try {
                image = requestForImageTask.execute(user).get();
            } catch (InterruptedException | ExecutionException e) {
                Log.getStackTraceString(e);
            }

            holder.imageView.setImageBitmap(image);
            holder.textView.setText(eventMessage.getText());
            holder.dateView.setText(eventMessage.getDate());
            holder.userNameView.setText(user.getDisplayName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onFragmentInteraction(holder.mItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView noMessageText;
        public TextView userNameView;
        public TextView dateView;
        public final View mView;
        public EventMessageDto mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.message_user_image);
            textView = (TextView) itemView.findViewById(R.id.message_text);
            dateView = (TextView) itemView.findViewById(R.id.message_date);
            userNameView = (TextView) itemView.findViewById(R.id.message_user);
            noMessageText = (TextView) itemView.findViewById(R.id.message_no_content);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " '" + "'";
    }

}
