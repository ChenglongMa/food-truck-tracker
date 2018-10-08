package mad.geo.view.fragment;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import mad.geo.controller.adapter.TrackingListAdapter;
import mad.geo.model.AbstractTracking;
import mad.geo.service.service.NotificationService;
import mad.geo.view.activity.MainActivity;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class TrackingFragment extends Fragment {

    public final static String ACTION_TYPE_SERVICE = "action.type.service";
    public final static String ACTION_TYPE_THREAD = "action.type.thread";
    public final static String NOTIFICATION_MSG = "message";
    public final static String NOTIFICATION_TITLE = "title";
    private static final int NOTIFICATION_ID = 1;
    private TrackingListAdapter mAdapter;
    private NotificationBroadcastReceiver mBroadcastReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mBroadcastReceiver = new NotificationBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_TYPE_SERVICE);
        intentFilter.addAction(ACTION_TYPE_THREAD);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
        Intent startIntent = new Intent(getActivity(), NotificationService.class);
        getActivity().startService(startIntent);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            registerForContextMenu(recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = TrackingListAdapter.getInstance();
            recyclerView.setAdapter(mAdapter);
        }
        return view;
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

    public TrackingListAdapter getAdapter() {
        return mAdapter;
    }

    private void notification(String msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle(getText(R.string.title_activity_main))
                        .setContentText(msg);
//                        .setContentIntent(makeIntent());
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra(NOTIFICATION_MSG);
            notification(msg);
        }

    }
}
