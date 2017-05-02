package me.lensevents.lensevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.lensevents.dto.GroupDto;
import me.lensevents.model.Group;

public class GroupDetailsFragment extends Fragment {

    private static final String GROUP = "group";
    private static final String KEY = "key";
    private GroupDto group;
    private String key;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_details, container, false);
        ImageView mImage = (ImageView) view.findViewById(R.id.user_image);
        TextView mName = (TextView) view.findViewById(R.id.group_name);
        TextView mCategory = (TextView) view.findViewById(R.id.group_category);
        TextView mDescription = (TextView) view.findViewById(R.id.group_description);
        TextView mNumberUsers = (TextView) view.findViewById(R.id.group_number_users);
        TextView mViewUsers = (Button) view.findViewById(R.id.group_viewUsers_button);
        TextView mAccessCode = (TextView) view.findViewById(R.id.group_access_code);
        TextView mAdministratorsTitle = (TextView) view.findViewById(R.id.group_administrators_title);

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
        if (group.getAdministrators().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            if (group.getAccessCode() != null) {
                mAccessCode.setVisibility(View.VISIBLE);
                mAccessCode.setText(group.getAccessCode());
            }
            mAdministratorsTitle.setVisibility(View.VISIBLE);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frament_user_to_replace, UserFragment.newInstance("administrators", group, key));
            transaction.commit();
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
