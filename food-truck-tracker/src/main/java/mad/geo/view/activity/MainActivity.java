package mad.geo.view.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.model.AbstractTracking;
import mad.geo.service.service.SuggestionJob;
import mad.geo.view.dialog.AddEditTrackableDialog;
import mad.geo.view.dialog.AddEditTrackingDialog;
import mad.geo.view.fragment.TrackableFragment;
import mad.geo.view.fragment.TrackingFragment;

public class MainActivity extends AppCompatActivity
        implements AddEditTrackingDialog.OnFragmentInteractionListener,
        AddEditTrackableDialog.OnFragmentInteractionListener {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int JOB_ID = 1;
    private static final int DELAY_MS = 1000; // 1 secs delay
    int mFilterMode = -1;
    private ViewPager viewPager;
    private Menu menu;
    private MenuItem menuItem;
    private TrackableFragment trackableFragment;
    private TrackingFragment trackingFragment;
    private JobScheduler jobScheduler;
    /**
     * Navigate to specified page.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_trackable_list:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.nav_tracking_list:
                    viewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    };

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        trackableFragment = TrackableFragment.newInstance(mFilterMode);
        trackingFragment = TrackingFragment.newInstance();
        adapter.addFragment(trackableFragment);
        adapter.addFragment(trackingFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigation.getMenu().getItem(position);
                menuItem.setChecked(true);
                // show or hide the menu based on the active page
                boolean visible = menuItem.getItemId() == R.id.nav_trackable_list;
                menu.findItem(R.id.action_search).setVisible(visible);
                menu.findItem(R.id.action_filter).setVisible(visible);
                menu.findItem(R.id.action_add).setVisible(visible);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
        scheduleJob();
    }

    @Override
    protected void onStop() {
        jobScheduler.cancel(JOB_ID);
        super.onStop();
    }

    private void scheduleJob() {
        // create a builder to make a JobInfo for the JobService so we can schedule it
        // according to certain contraints (note use of ComponentName!)
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(this, SuggestionJob.class));

        // delay start (only if it is not a periodic job!)
        builder.setMinimumLatency(DELAY_MS);
        // minimum periodic is currently 15 minutes so only if you are patient enough to test!
        Log.d(LOG_TAG, String.format("Minimum periodic period (getMinPeriodMillis()): %d mins"
                , TimeUnit.MILLISECONDS.toMinutes(JobInfo.getMinPeriodMillis())));
        // comment out setMinimumLatency() call above to do periodic scheduling
        //builder.setPeriodic(TimeUnit.MINUTES.toMillis(15));
        // requires network .. see API for other options
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        // Schedule job
        Log.d(LOG_TAG, "Scheduling job");
        jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(trackableFragment);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // set up the default icon for filter button
        if (mFilterMode != -1) {
            Drawable icon = menu.findItem(mFilterMode).getIcon();
            menu.findItem(R.id.action_filter).setIcon(icon);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                if (mFilterMode == -1) {
                    mFilterMode = R.id.action_filter_category;
                    Drawable icon = menu.findItem(mFilterMode).getIcon();
                    menu.findItem(R.id.action_filter).setIcon(icon);
                    Toast.makeText(this, "Filter by Category", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case R.id.action_add:
                showTrackableDialog(R.string.add_trackable_dialog, null);
                break;
        }

        return true;
    }

    /**
     * Create the Trackable Dialog to add/edit {@link AbstractTrackable}
     *
     * @param title     add or edit
     * @param trackable the object to be handled
     */
    public void showTrackableDialog(int title, AbstractTrackable trackable) {
        AddEditTrackableDialog dialog = AddEditTrackableDialog.newInstance(title, trackable);
        dialog.show(getFragmentManager(), "AddEditTrackable");
    }

    /**
     * Create the Tracking Dialog to add new tracking info
     *
     * @param trackable the object to be added tracking
     */
    public void showTrackingDialog(AbstractTrackable trackable) {
        AddEditTrackingDialog dialog = AddEditTrackingDialog.newInstance(trackable);
        dialog.show(getFragmentManager(), "AddTracking");
    }

    /**
     * Create the Tracking Dialog to edit the tracking info
     *
     * @param tracking the tracking info to be edit.
     */
    public void showTrackingDialog(AbstractTracking tracking) {
        AddEditTrackingDialog dialog = AddEditTrackingDialog.newInstance(tracking);
        dialog.show(getFragmentManager(), "EditTracking");
    }

    /**
     * This method is specified as an onClick handler in the menu xml and will
     * take precedence over the Activity's onOptionsItemSelected method.
     * See res/menu/actions.xml for more info.
     *
     * @param item
     */
    public void onFilter(MenuItem item) {
        mFilterMode = item.getItemId();
        trackableFragment.getArguments().putInt(TrackableFragment.ARG_FILTER_MODE, mFilterMode);
        invalidateOptionsMenu();
    }

    @Override
    public void onFragmentInteraction(int dialogType, AbstractTrackable trackable) {
        try {
            switch (dialogType) {
                case R.string.add_trackable_dialog:
                    trackableFragment.getAdapter().addItem(trackable);
                    break;
                case R.string.edit_trackable_dialog:
                    trackableFragment.getAdapter().editItem(trackable);
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFragmentInteraction(int dialogType, AbstractTracking tracking) {
        try {
            switch (dialogType) {
                case R.string.add_tracking_dialog:
                    trackingFragment.getAdapter().addItem(tracking);
                    break;
                case R.string.edit_tracking_dialog:
                    trackingFragment.getAdapter().editItem(tracking);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }
}
