package me.lensevents.lensevents;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabCalendarFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public TabAdapter adapter;
    private TabLayout tabs;
    private ViewPager viewPager;

    public TabCalendarFragment() {
        // Required empty public constructor
    }

    public static TabCalendarFragment newInstance() {
        TabCalendarFragment fragment = new TabCalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_calendar, container, false);

        adapter = new TabAdapter(getChildFragmentManager());

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabs.setVisibility(View.VISIBLE);
        tabs.setupWithViewPager(viewPager);

        return view;

    }


    @Override
    public void onPause() {
        super.onPause();
        tabs.setVisibility(View.GONE);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(EventFragment.newInstance(true, null), getString(R.string.my_events));
        adapter.addFragment(new GroupFragment(), getString(R.string.my_groups));
        viewPager.setAdapter(adapter);
    }

    public void replaceFragment(Fragment fragment) {
        adapter.replaceFragment(1, fragment);
    }
}
