package me.lensevents.lensevents;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import me.lensevents.dto.GroupDto;
import me.lensevents.model.Category;
import me.lensevents.model.User;

public class CreateGroupFragment extends Fragment {
    private static final String CATEGORY = "category";
    private Category category;
    private OnFragmentInteractionListener mListener;

    private List<String> users;
    private List<String> usersIds;
    private EditText mGroupName;
    private EditText mGroupDescription;
    private Spinner mGroupCategory;
    private MultiAutoCompleteTextView mGroupAdministrators;
    private MultiAutoCompleteTextView mGroupMembers;
    private RadioGroup mGroupRadioButtonGroup;
    private Button mGroupButton;

    public CreateGroupFragment() {
        // Required empty public constructor
    }

    public static CreateGroupFragment newInstance(Category category) {
        CreateGroupFragment fragment = new CreateGroupFragment();
        Bundle args = new Bundle();
        args.putSerializable(CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = (Category) getArguments().getSerializable(CATEGORY);
        }
    }

    //TODO: Posibilidad de añadir aquí la foto directamente
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
        mGroupRadioButtonGroup = (RadioGroup) view.findViewById(R.id.create_group_radioButtonGroup);
        mGroupButton = (Button) view.findViewById(R.id.create_group_button);

        mGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGroup();
            }
        });

        return view;
    }

    private void createGroup() {
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
        int id = mGroupRadioButtonGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) getView().findViewById(id);
        if (radioButton.getText().toString().equals(getString(R.string.private_group))) {
            String uuid = UUID.randomUUID().toString();
            groupDto.setAccessCode(uuid);
        }

        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories));
        int position = mGroupCategory.getSelectedItemPosition();
        Category category1 = Category.valueOf(categories.get(position));
        groupDto.setCategory(category1);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");
        databaseReference.push().setValue(groupDto);

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
