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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.lensevents.dto.GroupDto;
import me.lensevents.lensevents.EventFragment.OnFragmentInteractionListener;
import me.lensevents.model.Event;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private Query mDatabaseReference;
    private final OnFragmentInteractionListener mListener;
    private String key;

    private List<Event> mEvents = new ArrayList<>();
    private List<String> mEventsIds = new ArrayList<>();

    public EventRecyclerViewAdapter(String key, OnFragmentInteractionListener listener) {
        mListener = listener;
        this.key = key;
        if (key == null) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Events").orderByChild("date");
            mDatabaseReference.addChildEventListener(childEventListener);
        } else {
            //TODO: Eventos por grupo
            requestForGroup();
        }


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
                        mEvents.clear();
                        mEventsIds.clear();
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
        if (groupDto.getEvents() != null) {
            for (String key : groupDto.getEvents()) {
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Events").orderByKey().equalTo(key);
                mDatabaseReference.addChildEventListener(childEventListener);
            }
        }
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Event event = dataSnapshot.getValue(Event.class);
            boolean aux = false;
            if (key != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date eventDate = null;
                try {
                    eventDate = formatter.parse(event.getDate());
                } catch (ParseException e) {
                    Log.getStackTraceString(e);
                }
                Date now = new Date();
                if (eventDate != null & eventDate.after(now)) {
                    aux = true;
                }
            } else {
                aux = true;
            }

            if (aux) {
                mEventsIds.add(dataSnapshot.getKey());
                mEvents.add(event);
                notifyItemInserted(mEvents.size() - 1);
            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Event event = dataSnapshot.getValue(Event.class);
            if (key == null) {
                int index = mEventsIds.indexOf(dataSnapshot.getKey());
                mEvents.set(index, event);
                notifyItemChanged(index);
            } else {
                int index = mEventsIds.indexOf(dataSnapshot.getKey());
                if (index != -1) {
                    mEvents.set(index, event);
                    notifyItemChanged(index);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date eventDate = null;
                    try {
                        eventDate = formatter.parse(event.getDate());
                    } catch (ParseException e) {
                        Log.getStackTraceString(e);
                    }
                    Date now = new Date();
                    if (eventDate != null & !eventDate.after(now)) {
                        mEvents.remove(index);
                        mEventsIds.remove(index);
                        notifyItemRemoved(index);
                    }
                }

            }
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