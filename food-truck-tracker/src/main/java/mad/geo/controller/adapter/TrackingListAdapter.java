package mad.geo.controller.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.model.AbstractTracking;
import mad.geo.service.TrackableService;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AbstractTracking}
 */
public class TrackingListAdapter extends RecyclerView.Adapter<TrackingListAdapter.ViewHolder> {
    private static final String LOG_TAG = TrackingListAdapter.class.getName();
    private List<AbstractTracking> mValues;
    private AbstractTracking selectedTracking;
    private TrackableService trackableService;

    public TrackingListAdapter(List<AbstractTracking> items) {
        items.sort(new Comparator<AbstractTracking>() {
            @Override
            public int compare(AbstractTracking t1, AbstractTracking t2) {
                return t1.getMeetTime().compareTo(t2.getMeetTime());
            }
        });
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tracking, parent, false);
        trackableService = TrackableService.getSingletonInstance(view.getContext());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        AbstractTracking tracking = mValues.get(position);
        holder.mLocation.setText(tracking.getMeetLocation());
        Date date = tracking.getMeetTime();
        String meetTime = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.MEDIUM).format(date);
        holder.mMeetTime.setText(meetTime);
        AbstractTrackable trackable = trackableService.getTrackableById(tracking.getTrackableId());
        holder.mTrackableName.setText(trackable.getName());
        holder.mTitle.setText(tracking.getTitle());
        holder.itemView.setTag(tracking);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void addItem(AbstractTracking tracking) {
        trackableService.addTracking(tracking);
        notifyChanged();
        notifyItemInserted(mValues.indexOf(tracking));
    }

    public void removeSelectedItem() {
        int index = mValues.indexOf(selectedTracking);
        trackableService.deleteTracking(selectedTracking);
        notifyChanged();
        notifyItemRemoved(index);
    }

    public AbstractTracking getSelectedItem() {
        return selectedTracking;
    }

    public void editItem(AbstractTracking tracking) {
        trackableService.updateTracking(tracking);
        notifyChanged();
        notifyItemChanged(mValues.indexOf(tracking));
    }

    private void notifyChanged() {
        mValues = trackableService.getTrackings();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
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
            selectedTracking = (AbstractTracking) view.getTag();
            return false;
        }
    }
}
