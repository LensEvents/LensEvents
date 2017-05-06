package me.lensevents.lensevents;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import me.lensevents.dto.GroupDto;
import me.lensevents.lensevents.dummy.DummyContent;
import me.lensevents.lensevents.dummy.DummyContent.DummyItem;

import java.util.List;

public class MultimediaFragment extends Fragment {

    public static String GROUP = "group";
    public static String KEY = "key";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private GroupDto groupDto;
    private String key;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MultimediaFragment() {
    }

    public static MultimediaFragment newInstance(GroupDto groupDto, String key) {
        MultimediaFragment fragment = new MultimediaFragment();
        Bundle args = new Bundle();
        args.putSerializable(GROUP, groupDto);
        args.putString(KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            key = getArguments().getString(KEY);
            groupDto = (GroupDto) getArguments().getSerializable(GROUP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multimedia_list, container, false);

        // Set the adapter

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_multimedia);
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

        Query query = FirebaseDatabase.getInstance().getReference("Groups").child(key).child("media");
        recyclerView.setAdapter(new MultimediaRecyclerViewAdapter(query, mListener));

        FloatingActionButton mAddGroup = (FloatingActionButton) view.findViewById(R.id.add_photo);
        mAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Handle upload photo
            }
        });


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
        void onListFragmentInteraction(String pictureRef);
    }
}
