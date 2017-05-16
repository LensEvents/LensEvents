package me.lensevents.lensevents;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.lensevents.model.Event;


public class EventDetailsFragment extends Fragment {
    private static final String EVENT = "event";
    private static final String KEY = "key";
    private Event event;
    private String key;
    private FirebaseUser principal;
    private View.OnClickListener joinEventListener;
    private View.OnClickListener dissociateEventListener;

    private OnFragmentInteractionListener mListener;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    public static EventDetailsFragment newInstance(Event event, String key) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(EVENT, event);
        args.putString(KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(EVENT);
            key = getArguments().getString(KEY);
        }

        principal = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        final ImageView eventImage = (ImageView) view.findViewById(R.id.event_image);
        TextView eventName = (TextView) view.findViewById(R.id.event_name);
        FloatingActionButton deleteButton = (FloatingActionButton) view.findViewById(R.id.delete_button);
        TextView eventLocation = (TextView) view.findViewById(R.id.event_location);
        TextView eventDate = (TextView) view.findViewById(R.id.event_date);
        TextView eventDescription = (TextView) view.findViewById(R.id.event_description);
        final TextView eventAssistants = (TextView) view.findViewById(R.id.event_assistants_number);
        final Button viewAssistantsButton = (Button) view.findViewById(R.id.event_viewAssistants_button);
        final Button eventJoinButton = (Button) view.findViewById(R.id.event_join);
        Button eventMessagesButton = (Button) view.findViewById(R.id.event_messages_button);
        final FloatingActionButton confirmButton = (FloatingActionButton) view.findViewById(R.id.confirm_button);

        RequestForImageTask requestForImageTask = new RequestForImageTask();
        requestForImageTask.execute(event, eventImage);

        eventName.setText(event.getName());
        eventLocation.setText(event.getLocation().getCountry() + ", "
                + event.getLocation().getProvince() + ", " + event.getLocation().getCity() + ", "
                + event.getLocation().getStreet());
        eventDate.setText(event.getDate());
        eventDescription.setText(event.getDescription());
        if (event.getAssistants() != null) {
            eventAssistants.setText(event.getAssistants().size() + " asistentes");
            viewAssistantsButton.setVisibility(View.VISIBLE);
        } else {
            viewAssistantsButton.setVisibility(View.GONE);
        }

        viewAssistantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frament_to_replace, UserFragment.newInstance("members", false, key));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        if (event.getAdministrators() != null && event.getAdministrators().contains(principal.getUid())) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (event.getAssistants() == null || (event.getAssistants() != null &&
                            event.getAssistants().isEmpty())) {
                        FirebaseDatabase.getInstance().getReference("Events").child(key).removeValue();
                        getFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "No se puede borrar un evento que tiene miembros", Toast.LENGTH_LONG).show();
                    }
                }
            });
            final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date = null;
            try {
                date = formatter.parse(event.getDate());
            } catch (ParseException e) {
                Log.getStackTraceString(e);
            }
            if (event.getConfirmationDate() == null && date != null && date.after(new Date())) {
                confirmButton.setVisibility(View.VISIBLE);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseReference query = FirebaseDatabase.getInstance().getReference()
                                .child("Events").child(key);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Event event = dataSnapshot.getValue(Event.class);
                                String confirmationDate = formatter.format(new Date());
                                Map<String, Object> map = new ArrayMap<>();
                                map.put("confirmationDate", confirmationDate);
                                query.updateChildren(map);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        confirmButton.setVisibility(View.GONE);
                    }
                });
            }
        }

        joinEventListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventJoinButton.setText(R.string.group_dissociate);
                eventJoinButton.setOnClickListener(dissociateEventListener);
                final DatabaseReference query = FirebaseDatabase.getInstance().getReference()
                        .child("Events").child(key);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event event = dataSnapshot.getValue(Event.class);
                        List<String> assistants = event.getAssistants();
                        if (assistants == null) {
                            assistants = new ArrayList<>();
                        }
                        assistants.add(principal.getUid());
                        Map<String, Object> map = new ArrayMap<>();
                        map.put("assistants", assistants);
                        query.updateChildren(map);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                Integer number = Integer.valueOf(eventAssistants.getText().toString().split(" ")[0]);
                number = number + 1;
                if (number == 1) {
                    viewAssistantsButton.setVisibility(View.VISIBLE);
                }
                eventAssistants.setText(number.toString() + " " + "asistentes");
            }
        };

        dissociateEventListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventJoinButton.setText(R.string.group_join);
                eventJoinButton.setOnClickListener(joinEventListener);
                final DatabaseReference query = FirebaseDatabase.getInstance().getReference()
                        .child("Events").child(key);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Event event = dataSnapshot.getValue(Event.class);
                        List<String> assistants = event.getAssistants();
                        assistants.remove(principal.getUid());
                        Map<String, Object> map = new ArrayMap<>();
                        map.put("assistants", assistants);
                        query.updateChildren(map);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                Integer number = Integer.valueOf(eventAssistants.getText().toString().split(" ")[0]);
                number = number - 1;
                if (number == 0) {
                    viewAssistantsButton.setVisibility(View.GONE);
                }
                eventAssistants.setText(number.toString() + " " + "asistentes");
            }
        };

        eventJoinButton.setOnClickListener(joinEventListener);

        if (event.getAssistants() == null || (event.getAssistants() != null &&
                !event.getAssistants().contains(principal.getUid()))) {
            eventJoinButton.setText(R.string.group_join);
            eventJoinButton.setOnClickListener(joinEventListener);
        } else {
            eventJoinButton.setText(R.string.group_dissociate);
            eventJoinButton.setOnClickListener(dissociateEventListener);
        }

        eventMessagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frament_to_replace, MessageFragment.newInstance(key));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
