package mad.geo.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mad.geo.R;
import mad.geo.view.activity.MainActivity;
import mad.geo.controller.adapter.TrackingListAdapter;
import mad.geo.model.AbstractTracking;
import mad.geo.service.TrackableService;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class TrackingFragment extends Fragment {

    private TrackingListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrackingFragment() {
    }

    @SuppressWarnings("unused")
    public static TrackingFragment newInstance() {
        return new TrackingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            registerForContextMenu(recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            // hard code some trackings
            List<AbstractTracking> trackings = new ArrayList<>();//TODO
            mAdapter = new TrackingListAdapter(trackings);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    public TrackingListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.tracking_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_tracking_edit:
                ((MainActivity) getActivity()).showTrackingDialog(mAdapter.getSelectedItem());
                break;
            case R.id.context_tracking_remove:
                mAdapter.removeSelectedItem();
                break;
            default:
                //do nothing
        }
        return super.onContextItemSelected(item);

    }

}
