package me.lensevents.lensevents;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import me.lensevents.dto.GroupDto;
import me.lensevents.lensevents.dummy.DummyContent;
import me.lensevents.lensevents.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MultimediaFragment extends Fragment {

    public static int REQUEST_CODE = 233;
    public static String GROUP = "group";
    public static String KEY = "key";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private GroupDto groupDto;
    private String key;
    private MultimediaRecyclerViewAdapter adapter;

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
        adapter = new MultimediaRecyclerViewAdapter(query, mListener);
        recyclerView.setAdapter(adapter);

        FloatingActionButton mAddGroup = (FloatingActionButton) view.findViewById(R.id.add_photo);
        mAddGroup.setOnClickListener(new View.OnClickListener() {
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
            if (data.getData() != null) {
                Uri uri = data.getData();
                Random random = new Random();
                final String generatedName = "IMG-" + Long.toString(random.nextLong());
                FirebaseStorage.getInstance().getReference(generatedName).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        final DatabaseReference query = FirebaseDatabase.getInstance().getReference().child("Groups").child(key)/*.child("members")*/;
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GroupDto groupDto = dataSnapshot.getValue(GroupDto.class);
                                List<String> media = groupDto.getMedia();
                                if (media == null) {
                                    media = new ArrayList<String>();
                                }
                                media.add(generatedName);
                                Map<String, Object> map = new ArrayMap<>();
                                map.put("media", media);
                                query.updateChildren(map);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
            }
        }
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
