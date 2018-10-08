package mad.geo.view.activity;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
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

    public static final String TRACKABLE_IDS = "item_ids";
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int JOB_ID = 1;
    private static final int DELAY_MS = 1000; // 1 secs delay
    int mFilterMode = -1;
    private ViewPager viewPager;
    private Menu menu;
    private MenuItem menuItem;
    private TrackableFragment trackableFragment;
    private TrackingFragment trackingFragment;
    private SettingsActivity.GeneralPreferenceFragment settingsFragment;
//    private JobScheduler jobScheduler;
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
//        adapter.addFragment(settingsFragment);//TODO
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
        final BottomNavigationView navigation = findViewById(R.id.navigation);
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
    }

    @Override
    protected void onStop() {
//        jobScheduler.cancel(JOB_ID);
        super.onStop();
    }

    private void scheduleJob() throws InterruptedException {
        PersistableBundle bundle = new PersistableBundle();

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(this, SuggestionJob.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);

//        builder.setPeriodic(DELAY_MS);//Not working for Android N+
        builder.setMinimumLatency(DELAY_MS);//TODO: read from preference
        builder.setRequiresCharging(false);
        Log.d(LOG_TAG, String.format("Minimum periodic period (getMinPeriodMillis()): %d mins"
                , TimeUnit.MILLISECONDS.toMinutes(JobInfo.getMinPeriodMillis())));
        Log.d(LOG_TAG, "Scheduling job");
//        jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        for (int i = 0; i < 4; i++) {
            bundle.putInt(TRACKABLE_IDS, i);
            builder.setExtras(bundle);
//            jobScheduler.schedule(builder.build());
            Thread.sleep(3000);
        }
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
            case R.id.action_setting:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
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
