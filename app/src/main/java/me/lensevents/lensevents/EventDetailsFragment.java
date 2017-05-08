package me.lensevents.lensevents;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import me.lensevents.model.Event;


public class EventDetailsFragment extends Fragment {
    private static final String EVENT = "event";
    private static final String KEY = "key";
    private Event event;
    private String key;
    private FirebaseUser principal;
    private View.OnClickListener joinEventListener;
    private View.OnClickListener dissociateEventListener;

    private OnFragmentInteractionListener mListener;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    public static EventDetailsFragment newInstance(Event event, String key) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(EVENT, event);
        args.putString(KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(EVENT);
            key = getArguments().getString(KEY);
        }

        principal = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        final ImageView eventImage = (ImageView) view.findViewById(R.id.event_image);
        TextView eventName = (TextView) view.findViewById(R.id.event_name);
        FloatingActionButton deleteButton = (FloatingActionButton) view.findViewById(R.id.delete_button);
        TextView eventLocation = (TextView) view.findViewById(R.id.event_location);
        TextView eventDate = (TextView) view.findViewById(R.id.event_date);
        TextView eventConfirmationDate = (TextView) view.findViewById(R.id.event_confirmation_date);
        ImageView eventConfirmation = (ImageView) view.findViewById(R.id.imageView4);
        TextView eventDescription = (TextView) view.findViewById(R.id.event_description);
        TextView eventAssistants = (TextView) view.findViewById(R.id.event_assistants_number);
        Button viewAssistantsButton = (Button) view.findViewById(R.id.event_viewAssistants_button);
        Button eventJoinButton = (Button) view.findViewById(R.id.event_join);
        View eventAdmins = view.findViewById(R.id.event_admins);
        Button eventMessagesButton = (Button) view.findViewById(R.id.event_messages_button);

        RequestForImageTask requestForImageTask = new RequestForImageTask();
        requestForImageTask.execute(event, eventImage);

        eventName.setText(event.getName());
        eventLocation.setText(event.getLocation().getCountry() + ", "
                + event.getLocation().getProvince() + ", " + event.getLocation().getCity() + ", "
                + event.getLocation().getStreet());
        eventDate.setText(event.getDate());
        if (event.getConfirmationDate() != null) {
            eventConfirmationDate.setVisibility(View.VISIBLE);
            eventConfirmation.setVisibility(View.VISIBLE);
            eventConfirmationDate.setText(event.getConfirmationDate());
        }
        eventDescription.setText(event.getDescription());
        if (event.getAssistants() != null) {
            eventAssistants.setText(event.getAssistants().size() + " asistentes");
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
