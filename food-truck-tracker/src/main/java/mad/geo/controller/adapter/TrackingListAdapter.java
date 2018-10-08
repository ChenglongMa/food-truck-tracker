package mad.geo.controller.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.model.AbstractTracking;
import mad.geo.service.TrackableService;
import mad.geo.service.TrackingService;

import static mad.geo.utils.DateHelper.dateToString;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AbstractTracking}
 */
public class TrackingListAdapter extends AbstractAdapter<AbstractTracking, TrackingListAdapter.ViewHolder> {
    private TrackableService trackableService;

    private TrackingListAdapter() {
        super();
    }

    public static TrackingListAdapter getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        trackableService = TrackableService.getSingletonInstance(recyclerView.getContext());
        super.onAttachedToRecyclerView(recyclerView);


    }

    @Override
    public void setAll(List<AbstractTracking> data) {
        data.sort(Comparator.comparing(AbstractTracking::getMeetTime));
        super.setAll(data);
    }

    @Override
    protected void updateInBackground(AbstractTracking tracking) {
        trackableService.updateTracking(tracking);
    }

    @Override
    protected void insertInBackground(AbstractTracking tracking) {
        trackableService.addTracking(tracking);
    }

    @Override
    protected void deleteInBackground(AbstractTracking tracking) {
        trackableService.removeTracking(tracking);
    }

    @Override
    protected List<AbstractTracking> refreshInBackground() {
        return trackableService.getTrackings();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tracking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        AbstractTracking tracking = mValues.get(position);
        holder.mLocation.setText(tracking.getMeetLocation());
        holder.mMeetTime.setText(dateToString(tracking.getMeetTime()));
        AbstractTrackable trackable = trackableService.getTrackableById(tracking.getTrackableId());
        holder.mTrackableName.setText(trackable.getName());
        holder.mTitle.setText(tracking.getTitle());
        holder.itemView.setTag(tracking);
    }

    private static class LazyHolder {
        static final TrackingListAdapter INSTANCE = new TrackingListAdapter();
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener {

        final View mView;
        final TextView mLocation;
        final TextView mMeetTime;
        final TextView mTrackableName;
        final TextView mTitle;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mLocation = (TextView) view.findViewById(R.id.item_location);
            mMeetTime = (TextView) view.findViewById(R.id.item_meet_time);
            mTrackableName = (TextView) view.findViewById(R.id.item_trackable_name);
            mTitle = (TextView) view.findViewById(R.id.item_title);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            selectedItem = (AbstractTracking) view.getTag();
            return false;
        }
    }
}
