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
import mad.geo.utils.ExceptionAsyncTask;
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
    private UpdateTask updateTask;
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

    private void cancelUpdateTaskIfRunning() {
        if (updateTask != null) {
            updateTask.cancel(true);
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

    /**
     * Add new trackable to list view
     *
     * @param trackable
     */
    public void addItem(AbstractTrackable trackable) {
        insert(trackable);
        notifyItemInserted(mValues.indexOf(trackable));
    }

    public AbstractTrackable getSelectItem() {
        return selectedTrackable;
    }

    public void editItem(AbstractTrackable trackable) {
        update(trackable);
        notifyItemChanged(mValues.indexOf(trackable));
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

    private void delete(AbstractTrackable... trackables) {
        cancelDeleteTaskIfRunning();
        deleteTask = new DeleteTask();
        deleteTask.execute(trackables);
    }


    private void update(AbstractTrackable... trackables) {
        cancelUpdateTaskIfRunning();
        updateTask = new UpdateTask();
        updateTask.execute(trackables);

    }

    private void insert(AbstractTrackable... trackables) {
        cancelInsertTaskIfRunning();
        insertTask = new InsertTask();
        insertTask.execute(trackables);
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

    private class UpdateTask extends ExceptionAsyncTask<AbstractTrackable> {


        @Override
        protected void doInBackground(AbstractTrackable trackable) {
            trackableService.updateTrackable(trackable);
        }

        @Override
        protected void dealWithException(Exception e) {
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void postExecute(AsyncTaskResult<AbstractTrackable> result) {
            refresh();
        }
    }

    private class InsertTask extends ExceptionAsyncTask<AbstractTrackable> {

        @Override
        protected void doInBackground(AbstractTrackable trackable) {
            trackableService.addTrackable(trackable);
        }

        @Override
        protected void dealWithException(Exception e) {
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void postExecute(AsyncTaskResult<AbstractTrackable> result) {
            refresh();
        }
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
