package me.lensevents.lensevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
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
import java.util.zip.Inflater;

import me.lensevents.dto.EventDto;
import me.lensevents.dto.EventMessageDto;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private Query mDatabaseReference;
    private String eventKey;
    private ChildEventListener childEventListener;
    private final MessageFragment.OnFragmentInteractionListener mListener;

    private List<String> mMessagesIds = new ArrayList<>();
    private List<EventMessageDto> mMessages = new ArrayList<>();

    public MessageRecyclerViewAdapter(String eventKey, MessageFragment.OnFragmentInteractionListener mListener) {

        this.childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EventMessageDto eventMessageDto = dataSnapshot.getValue(EventMessageDto.class);
                mMessages.add(eventMessageDto);
                mMessagesIds.add(dataSnapshot.getKey());
                notifyItemInserted(mMessages.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                EventMessageDto eventMessageDto = dataSnapshot.getValue(EventMessageDto.class);
                int index = mMessagesIds.indexOf(dataSnapshot.getKey());
                mMessages.set(index, eventMessageDto);
                notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = mMessagesIds.indexOf(dataSnapshot.getKey());
                mMessages.remove(index);
                mMessagesIds.remove(index);
                notifyItemRemoved(index);
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mMessages.get(position);
        EventMessageDto eventMessage = mMessages.get(position);
        final String key = mMessagesIds.get(position);

//        Bitmap image = null;
//        RequestForImageTask requestForImageTask = new RequestForImageTask();
//        requestForImageTask.execute(eventMessage, holder.imageView);

        holder.textView.setText(eventMessage.getText());
        holder.dateView.setText(eventMessage.getDate());
        holder.userNameView.setText(eventMessage.getSender());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                   mListener.onFragmentInteraction(holder.mItem, key);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
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
        }
    }

    @Override
    public String toString() {
        return super.toString() + " '" + "'";
    }

}
