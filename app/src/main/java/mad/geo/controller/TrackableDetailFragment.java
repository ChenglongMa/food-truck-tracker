package mad.geo.controller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.service.TrackableService;

/**
 * A fragment representing a single AbstractTrackable detail screen.
 * This fragment is either contained in a {@link TrackableListActivity}
 * in two-pane mode (on tablets) or a {@link TrackableDetailActivity}
 * on handsets.
 */
public class TrackableDetailFragment extends Fragment {
    private final TrackableService trackableService = TrackableService.getSingletonInstance(this.getContext());
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String TRACKABLE_ID = "item_id";

    private AbstractTrackable selectedTrackable;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrackableDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(TRACKABLE_ID)) {
            selectedTrackable = trackableService.getTrackableById(args.getInt(TRACKABLE_ID));
            Activity activity = this.getActivity();
            if (activity == null) {
                return;
            }
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(selectedTrackable.getName());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.trackable_detail, container, false);

        View view = inflater.inflate(R.layout.trackable_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (selectedTrackable != null) {
//            ((TextView) rootView.findViewById(R.id.trackable_detail)).setText(selectedTrackable.toString());
//            ((TextView) view.findViewById(R.id.id)).setText();
            ((TextView) view.findViewById(R.id.id_value)).setText(selectedTrackable.getIdString());
            ((TextView) view.findViewById(R.id.name_value)).setText(selectedTrackable.getName());
            ((TextView) view.findViewById(R.id.cate_value)).setText(selectedTrackable.getCategory());
            ((TextView) view.findViewById(R.id.web_value)).setText(selectedTrackable.getWebSiteUrl());
            ((TextView) view.findViewById(R.id.des_value)).setText(selectedTrackable.getDescription());

        }

//        return rootView;
        return view;
    }
}
