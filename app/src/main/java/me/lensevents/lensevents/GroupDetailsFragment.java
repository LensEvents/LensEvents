package me.lensevents.lensevents;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import me.lensevents.dto.GroupDto;

public class GroupDetailsFragment extends Fragment {

    private static final String GROUP = "group";
    private static final String KEY = "key";
    private GroupDto group;
    private String key;
    private FirebaseUser principal;
    private View.OnClickListener joinGroupListener;
    private View.OnClickListener dissociateGroupListener;

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
        final ImageView mImage = (ImageView) view.findViewById(R.id.user_image);
        TextView mName = (TextView) view.findViewById(R.id.group_name);
        TextView mCategory = (TextView) view.findViewById(R.id.group_category);
        TextView mDescription = (TextView) view.findViewById(R.id.group_description);
        final TextView mNumberUsers = (TextView) view.findViewById(R.id.group_number_users);
        final TextView mViewUsers = (Button) view.findViewById(R.id.group_viewUsers_button);
        TextView mAccessCode = (TextView) view.findViewById(R.id.group_access_code);
        TextView mAdministratorsTitle = (TextView) view.findViewById(R.id.group_administrators_title);
        View mGroupsAdmins = view.findViewById(R.id.group_admins);
        final Button mJoinButton = (Button) view.findViewById(R.id.group_join);
        Button mDeleteGroupButton = (Button) view.findViewById(R.id.group_deleteGroup);
        Button mGroupViewMedia = (Button) view.findViewById(R.id.group_view_media);

        RequestForImageTask requestForImageTask = new RequestForImageTask();
        requestForImageTask.execute(group, mImage);

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
            mViewUsers.setVisibility(View.GONE);
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

        mGroupViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frament_to_replace, MultimediaFragment.newInstance(group, key), "multimediaFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //If the user is an administrator
        if (group.getAdministrators().contains(principal.getUid())) {
            mAdministratorsTitle.setVisibility(View.VISIBLE);
            mGroupsAdmins.setVisibility(View.VISIBLE);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frament_user_to_replace, UserFragment.newInstance("administrators", group, key));
            transaction.commit();

            mDeleteGroupButton.setVisibility(View.VISIBLE);
            mDeleteGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (group.getMembers() == null || (group.getMembers() != null && group.getMembers().isEmpty())) {
                        FirebaseDatabase.getInstance().getReference("Groups").child(key).removeValue();
                        getFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), R.string.cannot_delete_group, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        joinGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJoinButton.setText(R.string.group_dissociate);
                mJoinButton.setOnClickListener(dissociateGroupListener);
                final DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Groups").child(key)/*.child("members")*/;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
                        List<String> members = groupDto.getMembers();
                        if (members == null) {
                            members = new ArrayList<String>();
                        }
                        members.add(principal.getUid());
                        Map<String, Object> map = new ArrayMap<>();
                        map.put("members", members);
                        query.updateChildren(map);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                Integer number = Integer.valueOf(mNumberUsers.getText().toString().split(" ")[0]);
                number = number + 1;
                if (number == 1) {
                    mViewUsers.setVisibility(View.VISIBLE);
                }
                mNumberUsers.setText(number.toString() + " " + getString(R.string.members));
            }
        };

        dissociateGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mJoinButton.setText(R.string.group_join);
                mJoinButton.setOnClickListener(joinGroupListener);
                final DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Groups").child(key)/*.child("members")*/;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
                        List<String> members = groupDto.getMembers();
                        members.remove(principal.getUid());
                        Map<String, Object> map = new ArrayMap<>();
                        map.put("members", members);
                        query.updateChildren(map);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                Integer number = Integer.valueOf(mNumberUsers.getText().toString().split(" ")[0]);
                number = number - 1;
                if (number == 0) {
                    mViewUsers.setVisibility(View.GONE);
                }
                mNumberUsers.setText(number.toString() + " " + getString(R.string.members));
            }
        };
        mJoinButton.setText(R.string.group_join);
        mJoinButton.setOnClickListener(joinGroupListener);

        if (group.getMembers() == null || (group.getMembers() != null && !group.getMembers().contains(principal.getUid()))) {
            mJoinButton.setText(R.string.group_join);
            mJoinButton.setOnClickListener(joinGroupListener);
        } else {
            mJoinButton.setText(R.string.group_dissociate);
            mJoinButton.setOnClickListener(dissociateGroupListener);
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        EventFragment eventFragment = EventFragment.newInstance(key);
        transaction.replace(R.id.content_frament_event_to_replace, eventFragment);
        transaction.commit();

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
