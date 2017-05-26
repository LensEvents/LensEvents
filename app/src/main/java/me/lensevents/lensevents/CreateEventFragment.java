package me.lensevents.lensevents;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.lensevents.dto.EventDto;
import me.lensevents.dto.GroupDto;
import me.lensevents.model.Location;
import me.lensevents.model.User;

import static me.lensevents.lensevents.R.string.members;

public class CreateEventFragment extends Fragment {
    public static final int REQUEST_CODE = 23;
    private OnFragmentInteractionListener mListener;

    private List<String> users;
    private List<String> usersIds;
    private List<String> groupsIds;
    private List<String> groups;
    private EditText mEventName;
    private EditText mEventDate;
    private EditText mEventDescription;
    private MultiAutoCompleteTextView mEventAdministrators;
    private MultiAutoCompleteTextView mEventMembers;
    private AutoCompleteTextView mGroup;
    private EditText mEventLocationCountry;
    private EditText mEventLocationProvince;
    private EditText mEventLocationCity;
    private EditText mEventLocationStreet;
    private String photoName;
    private Boolean photoAdded;
    private Button mEventButton;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    public static CreateEventFragment newInstance() {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        users = new ArrayList<>();
        usersIds = new ArrayList<>();
        groupsIds = new ArrayList<>();
        groups = new ArrayList<>();
        mEventAdministrators = (MultiAutoCompleteTextView) view.findViewById(R.id.create_event_administrators);
        mEventMembers = (MultiAutoCompleteTextView) view.findViewById(R.id.create_event_members);
        mGroup = (AutoCompleteTextView) view.findViewById(R.id.create_event_group);

        FirebaseDatabase.getInstance().getReference().child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                users.add(user.getDisplayName());
                usersIds.add(user.getUid());
                String[] arr = users.toArray(new String[users.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, arr);
                mEventAdministrators.setAdapter(adapter);
                mEventAdministrators.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                mEventMembers.setAdapter(adapter);
                mEventMembers.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
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

        FirebaseDatabase.getInstance().getReference().child("Groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GroupDto group = dataSnapshot.getValue(GroupDto.class);
                String key = dataSnapshot.getKey();
                if (group.getAdministrators() != null) {
                    for (String admin : group.getAdministrators()) {
                        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(admin)) {
                            if (!groupsIds.contains(key)) {
                                groups.add(group.getName());
                                groupsIds.add(key);
                            }
                        }
                    }
                }
                if (group.getMembers() != null) {
                    for (String member : group.getMembers()) {
                        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(member)) {
                            if (!groupsIds.contains(key)) {
                                groups.add(group.getName());
                                groupsIds.add(key);
                            }
                        }
                    }
                }
                String[] arr = groups.toArray(new String[groups.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_single_choice, arr);
                mGroup.setAdapter(adapter);
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


        mEventName = (EditText) view.findViewById(R.id.create_event_name);
        mEventDescription = (EditText) view.findViewById(R.id.create_event_description);
        mEventButton = (Button) view.findViewById(R.id.create_event_button);
        mEventDate = (EditText) view.findViewById(R.id.create_event_date);
        mEventLocationCity = (EditText) view.findViewById(R.id.create_event_location_city);
        mEventLocationCountry = (EditText) view.findViewById(R.id.create_event_location_country);
        mEventLocationStreet = (EditText) view.findViewById(R.id.create_event_location_street);
        mEventLocationProvince = (EditText) view.findViewById(R.id.create_event_location_province);
        photoAdded = false;

        mEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoAdded && photoName == null) {
                    Toast.makeText(getContext(), R.string.must_wait_to_photo, Toast.LENGTH_SHORT).show();
                } else {
                    String stringDate = mEventDate.getText().toString();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    boolean res;
                    try {
                        formatter.parse(stringDate);
                        res = true;
                    } catch (ParseException e) {
                        res = false;
                    }
                    if (res) {
                        createEvent();
                    } else {
                        Toast.makeText(getContext(), R.string.wrong_date_format, Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        Button button = (Button) view.findViewById(R.id.event_add_photo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                photoAdded = true;
                Random random = new Random();
                final String generatedName = "IMG-" + Long.toString(random.nextLong());
                FirebaseStorage.getInstance().getReference(generatedName).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        photoName = generatedName;
                    }
                });
            }
        }
    }

    private void createEvent() {
        final EventDto eventDto = new EventDto();

        List<String> assistants = new ArrayList<>();
        List<String> administrators = new ArrayList<>();

        String administratorsAsString = mEventAdministrators.getText().toString();
        if (!administratorsAsString.isEmpty()) {
            String[] admins = administratorsAsString.split("\\,");
            for (String admin : admins) {
                if (users.contains(admin.trim())) {
                    int index = users.indexOf(admin.trim());
                    String id = usersIds.get(index);
                    if (!administrators.contains(id))
                        administrators.add(usersIds.get(index));
                }
            }
        }

        String membersAsString = mEventMembers.getText().toString();
        if (!membersAsString.isEmpty()) {
            String[] auxMembers = membersAsString.split("\\,");
            for (String member : auxMembers) {
                if (users.contains(member.trim())) {
                    int index = users.indexOf(member.trim());
                    String id = usersIds.get(index);
                    if (!assistants.contains(id))
                        assistants.add(id);
                }
            }
        }

        eventDto.setDate(mEventDate.getText().toString());
        eventDto.setName(mEventName.getText().toString());
        eventDto.setDescription(mEventDescription.getText().toString());
        eventDto.setAdministrators(administrators);
        eventDto.setAssistants(assistants);
        eventDto.setPhoto(photoName);

        Location location = new Location();
        location.setCity(mEventLocationCity.getText().toString());
        location.setStreet(mEventLocationStreet.getText().toString());
        location.setProvince(mEventLocationProvince.getText().toString());
        location.setCountry(mEventLocationCountry.getText().toString());

        eventDto.setLocation(location);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Events");
        final String eventKey = databaseReference.push().getKey();
        databaseReference.child(eventKey).setValue(eventDto);

        int groupIndex = groups.indexOf(mGroup.getText().toString());
        if (groupIndex != -1) {
            final String key = groupsIds.get(groupIndex);
            final DatabaseReference query = FirebaseDatabase.getInstance().getReference("Groups").child(key);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
                    List<String> events = groupDto.getEvents();
                    if (events == null) {
                        events = new ArrayList<String>();
                    }
                    events.add(eventKey);
                    Map<String, Object> map = new ArrayMap<>();
                    map.put("events", events);
                    query.updateChildren(map);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    EventFragment eventFragment = new EventFragment();
                    transaction.replace(R.id.content_frament_to_replace, eventFragment);
                    transaction.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            EventFragment eventFragment = new EventFragment();
            transaction.replace(R.id.content_frament_to_replace, eventFragment);
            transaction.commit();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
