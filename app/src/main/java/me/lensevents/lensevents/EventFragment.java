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

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import me.lensevents.model.Event;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String KEY = "key";
    private static final String MY_EVENTS = "myEvents";
    private int mColumnCount = 1;
    private String key;
    private Boolean myEvents;
    private OnFragmentInteractionListener mListener;

    public EventFragment() {
        // Required empty public constructor
    }

    public static EventFragment newInstance(Boolean myEvents, String key) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        if (key != null)
            args.putString(KEY, key);
        if (myEvents != null)
            args.putBoolean(MY_EVENTS, myEvents);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            key = getArguments().getString(KEY);
            myEvents = getArguments().getBoolean(MY_EVENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.event_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        EventRecyclerViewAdapter eventRecyclerViewAdapter = new EventRecyclerViewAdapter(key, myEvents, mListener);
        recyclerView.setAdapter(eventRecyclerViewAdapter);

        FloatingActionButton mAddGroup = (FloatingActionButton) view.findViewById(R.id.create_event);
        mAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frament_to_replace, CreateEventFragment.newInstance(), "createEventFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Event event, String key);
    }
}
