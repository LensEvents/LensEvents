package me.lensevents.lensevents;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.lensevents.dto.EventMessageDto;
import me.lensevents.model.Event;

public class CreateMessageFragment extends Fragment {

    private static final String EVENT_KEY = "eventKey";

    private String eventKey;
    private FirebaseUser firebaseUser;
    private Event event;
    private OnFragmentInteractionListener mListener;

    private EditText mMessageText;
    private Button mMessageButton;

    public CreateMessageFragment() {
        // Required empty public constructor
    }

    public static CreateMessageFragment newInstance(String eventKey) {
        CreateMessageFragment fragment = new CreateMessageFragment();
        Bundle args = new Bundle();
        args.putString(EVENT_KEY, eventKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventKey = getArguments().getString(EVENT_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_message, container, false);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        FirebaseDatabase.getInstance().getReference().child("Events").orderByKey().equalTo(eventKey)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        event = dataSnapshot.getValue(Event.class);
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
                });

        mMessageText = (EditText) view.findViewById(R.id.message_text);
        mMessageButton = (Button) view.findViewById(R.id.create_message_button);
        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMessage();
            }
        });


        return view;
    }

    private void createMessage() {
        EventMessageDto eventMessageDto = new EventMessageDto();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        eventMessageDto.setDate(formatter.format(new Date()));
        eventMessageDto.setSender(firebaseUser.getUid());
        eventMessageDto.setText(mMessageText.getText().toString());
        if (event.getEventMessages() != null) {
            event.getEventMessages().add(eventMessageDto);
        } else {
            List<EventMessageDto> eventMessageDtos = new ArrayList<>();
            eventMessageDtos.add(eventMessageDto);
            event.setEventMessages(eventMessageDtos);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Events/" + eventKey, event.toMap());

        databaseReference.updateChildren(childUpdates);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        MessageFragment messageFragment = MessageFragment.newInstance(eventKey);
        transaction.replace(R.id.content_frament_to_replace, messageFragment);
        transaction.commit();

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
