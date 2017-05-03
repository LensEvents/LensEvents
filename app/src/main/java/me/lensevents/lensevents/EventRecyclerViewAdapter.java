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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.lensevents.lensevents.EventFragment.OnFragmentInteractionListener;
import me.lensevents.model.Event;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private DatabaseReference mDatabaseReference;
    private ChildEventListener mValueEventListener;
    private final OnFragmentInteractionListener mListener;

    private List<Event> mEvents = new ArrayList<>();
    private List<String> mEventsIds = new ArrayList<>();

    public EventRecyclerViewAdapter(OnFragmentInteractionListener listener) {
        mListener = listener;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Events");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                mEventsIds.add(dataSnapshot.getKey());
                mEvents.add(event);
                notifyItemInserted(mEvents.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                int index = mEventsIds.indexOf(dataSnapshot.getKey());
                mEvents.set(index, event);
                notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = mEventsIds.indexOf(dataSnapshot.getKey());
                mEvents.remove(index);
                mEventsIds.remove(index);
                notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(childEventListener);
        mValueEventListener = childEventListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mEvent = mEvents.get(position);
        Event event = mEvents.get(position);
        holder.mNameView.setText(event.getName());

        Bitmap bitmap = null;
        RequestForImageTask requestForImageTask = new RequestForImageTask();
        try {
            bitmap = requestForImageTask.execute(event).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.getStackTraceString(e);
        }

        holder.mImageView.setImageBitmap(bitmap);
        holder.mLocationView.setText(event.getLocation().getCountry() + ", " + event.getLocation().getProvince());
        holder.mDateView.setText(event.getDate());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFragmentInteraction(holder.mEvent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mNameView;
        public final TextView mLocationView;
        public final TextView mDateView;
        public Event mEvent;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.event_image);
            mNameView = (TextView) view.findViewById(R.id.event_name);
            mLocationView = (TextView) view.findViewById(R.id.event_location);
            mDateView = (TextView) view.findViewById(R.id.event_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
