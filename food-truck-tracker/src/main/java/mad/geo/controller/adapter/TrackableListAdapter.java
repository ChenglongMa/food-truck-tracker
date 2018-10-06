package mad.geo.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mad.geo.R;
import mad.geo.model.AbstractTrackable;
import mad.geo.model.AsyncTaskResult;
import mad.geo.service.TrackableService;
import mad.geo.view.activity.TrackableDetailActivity;
import mad.geo.view.fragment.TrackableDetailFragment;

/**
 * The data adapter for trackable list
 */
public class TrackableListAdapter
        extends RecyclerView.Adapter<TrackableListAdapter.ViewHolder> {
    private static final String LOG_TAG = TrackableListAdapter.class.getName();
    private List<AbstractTrackable> mValues = new ArrayList<>();
    private AbstractTrackable selectedTrackable;
    private TrackableService trackableService;
    private InsertTask insertTask;
    private RefreshTask refreshTask;
    private DeleteTask deleteTask;
    private Context context;

    private TrackableListAdapter() {
//        refresh();
    }

    public static TrackableListAdapter getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * 从数据库中重新获取一遍数据
     */
    private void refresh() {
        cancelRefreshTaskIfRunning();
        refreshTask = new RefreshTask();
        refreshTask.execute();
    }

    private void cancelInsertTaskIfRunning() {
        if (insertTask != null) {
            insertTask.cancel(true);
        }
    }

    private void cancelRefreshTaskIfRunning() {
        if (refreshTask != null) {
            refreshTask.cancel(true);
        }
    }

    private void cancelDeleteTaskIfRunning() {
        if (deleteTask != null) {
            deleteTask.cancel(true);
        }
    }

    /**
     * Remove the selected item from the data set.
     */
    public void removeSelectedItem() {
        int index = mValues.indexOf(selectedTrackable);
        delete(selectedTrackable);
        notifyItemRemoved(index);
    }

    public void addItem(AbstractTrackable trackable) {
        insert(trackable);
        notifyItemInserted(mValues.indexOf(trackable));
    }

    public AbstractTrackable getSelectItem() {
        return selectedTrackable;
    }

    public void editItem(AbstractTrackable trackable) {
        trackableService.updateTrackable(trackable);
//        notifyChanged();
        notifyItemChanged(mValues.indexOf(trackable));

    }

//    /**
//     * Update the data set and notify the UI
//     */
//    @Deprecated
//    private void notifyChanged() {
//        mValues = trackableService.getTrackables();
//        notifyDataSetChanged();
//    }

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

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        trackableService = TrackableService.getSingletonInstance(recyclerView.getContext());
        this.context = recyclerView.getContext();
        refresh();
    }

    public void setAll(List<AbstractTrackable> trackables) {
        mValues = trackables;
        notifyDataSetChanged();
    }

    /**
     * 删除数据
     */
    private void delete(AbstractTrackable... trackables) {
        cancelInsertTaskIfRunning();
        deleteTask = new DeleteTask();
        deleteTask.execute(trackables);
    }

    /**
     * 插入数据
     */
    private void insert(AbstractTrackable... trackables) {
        cancelInsertTaskIfRunning();
        insertTask = new InsertTask(trackables);
        insertTask.execute();
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
            selectedTrackable = (AbstractTrackable) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, TrackableDetailActivity.class);
            intent.putExtra(TrackableDetailFragment.TRACKABLE_ID, selectedTrackable.getId());
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            selectedTrackable = (AbstractTrackable) view.getTag();
            return false;
        }

    }

    private class InsertTask extends AsyncTask<Void, Void, AsyncTaskResult<AbstractTrackable>> {
        private AbstractTrackable[] trackables;

        InsertTask(AbstractTrackable... trackables) {
            this.trackables = trackables;
        }

        @Override
        protected AsyncTaskResult<AbstractTrackable> doInBackground(Void... voids) {
            try {
                for (AbstractTrackable trackable : trackables) {
                    trackableService.addTrackable(trackable);
                }
                return null;
            } catch (Exception e) {
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<AbstractTrackable> result) {
            if (result != null && result.getError() != null) {
                Toast.makeText(context, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                refresh();
            }
            super.onPostExecute(result);
        }

        //
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            refresh();
//        }
    }

    private class RefreshTask extends AsyncTask<Void, Void, List<AbstractTrackable>> {
        @Override
        protected List<AbstractTrackable> doInBackground(Void... params) {
            return trackableService.getTrackables();
        }

        @Override
        protected void onPostExecute(List<AbstractTrackable> trackables) {
            super.onPostExecute(trackables);
            if (trackables != null) {
                setAll(trackables);
            }
        }
    }

    private class DeleteTask extends AsyncTask<AbstractTrackable, Void, Void> {
        @Override
        protected Void doInBackground(AbstractTrackable... trackables) {
            for (AbstractTrackable trackable : trackables) {
                trackableService.removeTrackable(trackable);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            refresh();
        }
    }
}
