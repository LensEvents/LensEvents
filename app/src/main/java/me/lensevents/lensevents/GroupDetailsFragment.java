package me.lensevents.lensevents;

import android.content.Context;
import android.graphics.Bitmap;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import me.lensevents.dto.GroupDto;
import me.lensevents.model.Group;

public class GroupDetailsFragment extends Fragment {

    private static final String GROUP = "group";
    private static final String KEY = "key";
    private GroupDto group;
    private String key;
    private FirebaseUser principal;

    private OnFragmentInteractionListener mListener;

    public GroupDetailsFragment() {
        // Required empty public constructor
    }

    public static GroupDetailsFragment newInstance(GroupDto group, String key) {
        GroupDetailsFragment fragment = new GroupDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(GROUP, group);
        args.putString(KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = (GroupDto) getArguments().getSerializable(GROUP);
            key = getArguments().getString(KEY);
        }
        principal = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_details, container, false);
        ImageView mImage = (ImageView) view.findViewById(R.id.user_image);
        TextView mName = (TextView) view.findViewById(R.id.group_name);
        TextView mCategory = (TextView) view.findViewById(R.id.group_category);
        TextView mDescription = (TextView) view.findViewById(R.id.group_description);
        final TextView mNumberUsers = (TextView) view.findViewById(R.id.group_number_users);
        final TextView mViewUsers = (Button) view.findViewById(R.id.group_viewUsers_button);
        TextView mAccessCode = (TextView) view.findViewById(R.id.group_access_code);
        TextView mAdministratorsTitle = (TextView) view.findViewById(R.id.group_administrators_title);
        final FloatingActionButton mJoinButton = (FloatingActionButton) view.findViewById(R.id.group_join);
        //TODO: Botón para ir a la información multimedia

        RequestForImageTask requestForImageTask = new RequestForImageTask();
        Bitmap image = null;
        try {
            image = requestForImageTask.execute(group).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.getStackTraceString(e);
        }
        if (image != null) {
            mImage.setImageBitmap(image);
        }
        mName.setText(group.getName());
        String[] categoriesAsString = getResources().getStringArray(R.array.categoriesAsString);
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories));
        int index = categories.indexOf(group.getCategory().toString());
        String auxCategory = categoriesAsString[index];
        mCategory.setText(auxCategory);
        mDescription.setText(group.getDescription());
        Integer numberOfMembers = group.getMembers() != null ? group.getMembers().size() : 0;
        mNumberUsers.setText(numberOfMembers + " " + getString(R.string.members));
        if (numberOfMembers == 0) {
            mViewUsers.setVisibility(View.INVISIBLE);
        }
        mViewUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frament_to_replace, UserFragment.newInstance("members", group, key));
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //If the user is an administrator
        if (group.getAdministrators().contains(principal.getUid())) {
            if (group.getAccessCode() != null) {
                mAccessCode.setVisibility(View.VISIBLE);
                mAccessCode.setText(group.getAccessCode());
            }
            mAdministratorsTitle.setVisibility(View.VISIBLE);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frament_user_to_replace, UserFragment.newInstance("administrators", group, key));
            transaction.commit();
        }

        if (!group.getMembers().contains(principal.getUid())) {
            mJoinButton.setVisibility(View.VISIBLE);
            if (group.getAccessCode() == null) {
                mJoinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Groups").child(key)/*.child("members")*/;
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
                                List<String> members = groupDto.getMembers();
                                members.add(principal.getUid());
                                Map<String, Object> map = new ArrayMap<>();
                                map.put("members", members);
                                query.updateChildren(map);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        mJoinButton.setVisibility(View.GONE);
                        Integer number = Integer.valueOf(mNumberUsers.getText().toString().split(" ")[0]);
                        number = number + 1;
                        mNumberUsers.setText(number.toString() + " " + getString(R.string.members));
                    }
                });
            } //TODO: Add else and handle private groups
        }

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
