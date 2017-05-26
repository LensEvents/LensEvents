package me.lensevents.lensevents;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.lensevents.dto.GroupDto;
import me.lensevents.model.Category;
import me.lensevents.model.Group;
import me.lensevents.model.User;

public class EditGroupFragment extends Fragment {
    public static final int REQUEST_CODE = 2;
    private static final String KEY = "key";
    private String key;
    private OnFragmentInteractionListener mListener;

    private List<String> users;
    private List<String> usersIds;
    private EditText mGroupName;
    private EditText mGroupDescription;
    private Spinner mGroupCategory;
    private MultiAutoCompleteTextView mGroupAdministrators;
    private MultiAutoCompleteTextView mGroupMembers;
    private String photoName;
    private Boolean photoAdded;
    private Button mGroupButton;

    public EditGroupFragment() {
        // Required empty public constructor
    }

    public static EditGroupFragment newInstance(String key) {
        EditGroupFragment fragment = new EditGroupFragment();
        Bundle args = new Bundle();
        args.putString(KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            key = getArguments().getString(KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        users = new ArrayList<>();
        usersIds = new ArrayList<>();
        mGroupAdministrators = (MultiAutoCompleteTextView) view.findViewById(R.id.create_group_administrators);
        mGroupMembers = (MultiAutoCompleteTextView) view.findViewById(R.id.create_group_members);
        mGroupCategory = (Spinner) view.findViewById(R.id.create_group_category);

        FirebaseDatabase.getInstance().getReference().child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                users.add(user.getDisplayName());
                usersIds.add(user.getUid());
                String[] arr = users.toArray(new String[users.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, arr);
                mGroupAdministrators.setAdapter(adapter);
                mGroupAdministrators.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
                mGroupMembers.setAdapter(adapter);
                mGroupMembers.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

                FirebaseDatabase.getInstance().getReference("Groups").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
                        String auxAdmins = "";
                        if (groupDto.getAdministrators() != null) {
                            for (String admin : groupDto.getAdministrators()) {
                                if (usersIds.contains(admin)) {
                                    int index = usersIds.indexOf(admin);
                                    auxAdmins += users.get(index) + ", ";
                                }
                            }
                        }
                        mGroupAdministrators.setText(auxAdmins);

                        String auxMembers = "";
                        if (groupDto.getMembers() != null) {
                            for (String member : groupDto.getMembers()) {
                                if (usersIds.contains(member)) {
                                    int index = usersIds.indexOf(member);
                                    auxMembers += users.get(index) + ", ";
                                }
                            }
                        }
                        mGroupMembers.setText(auxMembers);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categoriesAsString, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroupCategory.setAdapter(arrayAdapter);

        mGroupName = (EditText) view.findViewById(R.id.create_group_name);
        mGroupDescription = (EditText) view.findViewById(R.id.create_group_description);
        mGroupButton = (Button) view.findViewById(R.id.create_group_button);
        mGroupButton.setText(R.string.edit_group_button);
        photoAdded = false;

        mGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoAdded && photoName == null) {
                    Toast.makeText(getContext(), R.string.must_wait_to_photo, Toast.LENGTH_SHORT).show();
                } else {
                    editGroup();
                }

            }
        });

        Button button = (Button) view.findViewById(R.id.group_add_photo);
        button.setText(R.string.group_edit_photo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
            }
        });

        retrieveData();

        return view;
    }

    private void retrieveData() {
        FirebaseDatabase.getInstance().getReference("Groups").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
                mGroupName.setText(groupDto.getName());
                mGroupDescription.setText(groupDto.getDescription());
                List<String> auxArray = Arrays.asList(getResources().getStringArray(R.array.categories));
                int index = auxArray.indexOf(groupDto.getCategory().toString());
                mGroupCategory.setSelection(index);
                photoName = groupDto.getPhoto();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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

    private void editGroup() {
        GroupDto groupDto = new GroupDto();

        List<String> members = new ArrayList<>();
        List<String> administrators = new ArrayList<>();

        String administratorsAsString = mGroupAdministrators.getText().toString();
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

        String membersAsString = mGroupMembers.getText().toString();
        if (!membersAsString.isEmpty()) {
            String[] auxMembers = membersAsString.split("\\,");
            for (String member : auxMembers) {
                if (users.contains(member.trim())) {
                    int index = users.indexOf(member.trim());
                    String id = usersIds.get(index);
                    if (!members.contains(id))
                        members.add(id);
                }
            }
        }

        groupDto.setName(mGroupName.getText().toString());
        groupDto.setDescription(mGroupDescription.getText().toString());
        groupDto.setAdministrators(administrators);
        groupDto.setMembers(members);
        groupDto.setMedia(new ArrayList<String>());

        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories));
        int position = mGroupCategory.getSelectedItemPosition();
        Category category1 = Category.valueOf(categories.get(position));
        groupDto.setCategory(category1);

        groupDto.setPhoto(photoName);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(key);
        databaseReference.setValue(groupDto);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        GroupFragment groupFragment = GroupFragment.newInstance(category1);
        transaction.replace(R.id.content_frament_to_replace, groupFragment);
        transaction.commit();

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
