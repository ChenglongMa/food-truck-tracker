package mad.geo.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.List;

import mad.geo.R;
import mad.geo.view.activity.MainActivity;
import mad.geo.controller.adapter.TrackableListAdapter;
import mad.geo.model.AbstractTrackable;
import mad.geo.service.TrackableService;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class TrackableFragment extends Fragment
        implements SearchView.OnQueryTextListener {

    public static final String ARG_FILTER_MODE = "filter-mode";
    private static final String LOG_TAG = TrackableFragment.class.getName();
    private TrackableService trackableService;
    private int mFilterMode;
    private TrackableListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TrackableFragment() {
    }

    @SuppressWarnings("unused")
    public static TrackableFragment newInstance(int filterMode) {
        TrackableFragment fragment = new TrackableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FILTER_MODE, filterMode);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackableListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilterMode = getArguments().getInt(ARG_FILTER_MODE);
        }
        Activity activity = this.getActivity();
        if (activity == null) {
            return;
        }
        trackableService = TrackableService.getSingletonInstance(activity);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trackable_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            registerForContextMenu(recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = TrackableListAdapter.getInstance();
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.trackable_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_add_tracking:
                if (trackableService.getTrackingInfo(mAdapter.getSelectItem()).isEmpty()) {
                    Toast.makeText(getContext(), "There is no available tracking info", Toast.LENGTH_SHORT).show();
                    break;
                }
                ((MainActivity) getActivity()).showTrackingDialog(mAdapter.getSelectItem());
                break;
            case R.id.context_trackable_edit:
                ((MainActivity) getActivity()).showTrackableDialog(R.string.edit_trackable_dialog, mAdapter.getSelectItem());
                break;
            case R.id.context_trackable_remove:
                mAdapter.removeSelectedItem();
                break;
            default:
                //do nothing
        }
        return super.onContextItemSelected(item);

    }

    public boolean onQueryTextSubmit(String query) {
        return filterBy(query);
    }

    public boolean onQueryTextChange(String newText) {
        newText = newText.isEmpty() ? TrackableService.FILTER_ALL : newText;
        return filterBy(newText);
    }

    /**
     * Filter the data set by the query key words and filter type
     * @param query
     * @return
     */
    private boolean filterBy(String query) {
        mFilterMode = getArguments().getInt(ARG_FILTER_MODE);
        switch (mFilterMode) {
            default:
                Toast.makeText(getActivity(), "Please select filter mode first", Toast.LENGTH_SHORT).show();
                return false;
            case R.id.action_filter_category:
                // multiple filters applied with delimiters (; or space)
                String[] cates = query.split(";|\\s");
                List<AbstractTrackable> trackables = trackableService.getTrackablesByCategory(cates);
                mAdapter.setAll(trackables);
                return true;
            case R.id.action_filter_name:
                //TODO: to be implemented for assignment 2
                return true;
        }
    }
}
