package me.lensevents.lensevents;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.lensevents.dto.GroupDto;
import me.lensevents.model.User;

public class UserFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    public static String MODE = "mode";
    public static String GROUP = "group";
    public static String KEY = "key";
    public static String ISGROUP = "isGroup";

    private Boolean isGroup;
    private String mode;
    private String key;
    private GroupDto groupDto;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserFragment() {
    }

    public static UserFragment newInstance(String mode, GroupDto groupDto, String key) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(MODE, mode);
        args.putSerializable(GROUP, groupDto);
        args.putBoolean(ISGROUP, true);
        args.putString(KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    public static UserFragment newInstance(String mode, Boolean isGroup, String key) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(MODE, mode);
        args.putBoolean(ISGROUP, isGroup);
        args.putString(KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mode = getArguments().getString(MODE);
            key = getArguments().getString(KEY);
            groupDto = (GroupDto) getArguments().getSerializable(GROUP);
            isGroup = getArguments().getBoolean(ISGROUP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            if (isGroup) {
                recyclerView.setAdapter(new UserRecyclerViewAdapter(key, mode, true, mListener));
            } else {
                recyclerView.setAdapter(new UserRecyclerViewAdapter(key, mode, false, mListener));
            }


        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(User user);
    }
}
