package me.lensevents.lensevents;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.lensevents.dto.EventMessageDto;


public class MessageFragment extends Fragment {

    private static final String KEY = "key";
    private String key;
    private OnFragmentInteractionListener mListener;

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(String key) {
        MessageFragment fragment = new MessageFragment();
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
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.message_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        MessageRecyclerViewAdapter messageRecyclerViewAdapter = new MessageRecyclerViewAdapter(key, mListener);
        recyclerView.setAdapter(messageRecyclerViewAdapter);

        FloatingActionButton mAddMessage = (FloatingActionButton) view.findViewById(R.id.add_message);
//        mAddMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.content_frament_to_replace, CreateMessageFragment.newInstance());
//            }
//        });

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
        void onFragmentInteraction(EventMessageDto eventMessageDto);
    }
}
