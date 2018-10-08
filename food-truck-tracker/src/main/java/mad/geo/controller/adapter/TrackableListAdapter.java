package mad.geo.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.view.activity.MapsActivity;

/**
 * The data adapter for trackable list
 */
public class TrackableListAdapter
        extends AbstractAdapter<AbstractTrackable, TrackableListAdapter.ViewHolder> {

    private TrackableListAdapter() {
        super();
    }

    public static TrackableListAdapter getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public void setAll(List<AbstractTrackable> data) {
        super.setAll(data);
        trackableService.setFilterTrackables(data);
    }

    @Override
    protected void updateInBackground(AbstractTrackable trackable) {
        trackableService.updateTrackable(trackable);
    }

    @Override
    protected void insertInBackground(AbstractTrackable trackable) {
        trackableService.addTrackable(trackable);
    }

    @Override
    protected void deleteInBackground(AbstractTrackable trackable) {
        trackableService.removeTrackable(trackable);
    }

    @Override
    protected List<AbstractTrackable> refreshInBackground() {
        return trackableService.getTrackables();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trackable_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mIdView.setText(mValues.get(position).getIdString());
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mCategory.setText(mValues.get(position).getCategory());
        holder.itemView.setTag(mValues.get(position));
    }

    private static class LazyHolder {
        static final TrackableListAdapter INSTANCE = new TrackableListAdapter();
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        final TextView mIdView;
        final TextView mNameView;
        final TextView mCategory;


        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.item_id);
            mNameView = view.findViewById(R.id.item_name);
            mCategory = view.findViewById(R.id.item_category);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            selectedItem = (AbstractTrackable) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra(MapsActivity.TRACKABLE_ID, selectedItem.getId());
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            selectedItem = (AbstractTrackable) view.getTag();
            return false;
        }

    }
}
