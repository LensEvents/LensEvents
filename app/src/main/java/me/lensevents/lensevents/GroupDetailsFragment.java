package me.lensevents.lensevents;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.lensevents.model.Group;

public class GroupDetailsFragment extends Fragment {

    private static final String GROUP = "group";
    private Group group;

    private OnFragmentInteractionListener mListener;

    public GroupDetailsFragment() {
        // Required empty public constructor
    }

    public static GroupDetailsFragment newInstance(Group group) {
        GroupDetailsFragment fragment = new GroupDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(GROUP, group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group = (Group) getArguments().getSerializable(GROUP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_details, container, false);
        ImageView mImage = (ImageView) view.findViewById(R.id.group_image);
        TextView mName = (TextView) view.findViewById(R.id.group_name);
        TextView mCategory = (TextView) view.findViewById(R.id.group_category);
        TextView mDescription = (TextView) view.findViewById(R.id.group_description);
        TextView mNumberUsers = (TextView) view.findViewById(R.id.group_number_users);
        TextView mViewUsers = (Button) view.findViewById(R.id.group_viewUsers_button);
        TextView mAccessCode = (TextView) view.findViewById(R.id.group_access_code);

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
                //TODO: Reemplazar fragmento por listado de usuarios relacionados
            }
        });
        if (group.getAccessCode() != null) {
            //TODO: Comprobar que el usuario sea administrador
            mAccessCode.setVisibility(View.VISIBLE);
            mAccessCode.setText(group.getAccessCode());
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
