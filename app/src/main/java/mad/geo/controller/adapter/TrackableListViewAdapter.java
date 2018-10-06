package mad.geo.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.controller.TrackableDetailActivity;
import mad.geo.controller.TrackableDetailFragment;
import mad.geo.controller.TrackableListActivity;

/**
 * GeoTracking
 *
 * @author : Charles Ma
 * @date : 31-08-2018
 * @time : 16:57
 * @description :
 */
public class TrackableListViewAdapter
        extends RecyclerView.Adapter<TrackableListViewAdapter.ViewHolder> {

    private final TrackableListActivity mParentActivity;
    private final List<AbstractTrackable> mValues;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AbstractTrackable item = (AbstractTrackable) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(TrackableDetailFragment.TRACKABLE_ID, item.getId());
                TrackableDetailFragment fragment = new TrackableDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.trackable_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, TrackableDetailActivity.class);
                intent.putExtra(TrackableDetailFragment.TRACKABLE_ID, item.getId());

                context.startActivity(intent);
            }
        }
    };

    private TrackableListViewAdapter(TrackableListActivity parent,
                                     List<AbstractTrackable> items,
                                     boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    public static TrackableListViewAdapter getSingletonInstance(TrackableListActivity parent,
                                                                List<AbstractTrackable> items,
                                                                boolean twoPane) {
        return LazyHolder.getINSTANCE(parent, items, twoPane);
    }

    private static class LazyHolder {

        static TrackableListViewAdapter getINSTANCE(TrackableListActivity parent,
                                                    List<AbstractTrackable> items,
                                                    boolean twoPane) {
            return new TrackableListViewAdapter(parent, items, twoPane);
        }
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
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mNameView;
        final TextView mCategory;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.item_id);
            mNameView = view.findViewById(R.id.item_name);
            mCategory = view.findViewById(R.id.item_category);
        }
    }
}
