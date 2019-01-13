package mad.geo.controller.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

import mad.geo.model.AsyncTaskResult;
import mad.geo.service.TrackableService;
import mad.geo.utils.ExceptionAsyncTask;

public abstract class AbstractAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected static final String LOG_TAG = AbstractAdapter.class.getName();
    protected Context context;
    List<T> mValues;
    T selectedItem;
    private InsertTask insertTask;
    private RefreshTask refreshTask;
    private DeleteTask deleteTask;
    private UpdateTask updateTask;
    TrackableService trackableService;

    AbstractAdapter() {
        //TODO: lazyHolder
    }

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
        int index = mValues.indexOf(selectedItem);
        delete(selectedItem);
        notifyItemRemoved(index);
    }

    /**
     * Add new trackable to list view
     *
     * @param t
     */
    public void addItem(T t) {
        insert(t);
//        notifyItemInserted(mValues.indexOf(t));
    }

    public T getSelectedItem() {
        return selectedItem;
    }


    public void editItem(T t) {
        update(t);
        notifyItemChanged(mValues.indexOf(t));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mValues == null) {
            return 0;
        }
        return mValues.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.context = recyclerView.getContext();
        trackableService = TrackableService.getSingletonInstance(context);
        refresh();
    }

    public void setAll(List<T> data) {
        mValues = data;
        notifyDataSetChanged();
    }

    @SafeVarargs
    private final void delete(T... ts) {
        cancelDeleteTaskIfRunning();
        deleteTask = new DeleteTask();
        deleteTask.execute(ts);
    }

    @SafeVarargs
    private final void update(T... ts) {
        cancelUpdateTaskIfRunning();
        updateTask = new UpdateTask();
        updateTask.execute(ts);

    }

    @SafeVarargs
    private final void insert(T... ts) {
        cancelInsertTaskIfRunning();
        insertTask = new InsertTask();
        insertTask.execute(ts);
    }

    protected abstract void updateInBackground(T t);

    protected abstract void insertInBackground(T t);

    protected abstract void deleteInBackground(T t);

    protected abstract List<T> refreshInBackground();

    private class UpdateTask extends ExceptionAsyncTask<T> {


        @Override
        protected void doInBackground(T t) {
            updateInBackground(t);
        }

        @Override
        protected void dealWithException(Exception e) {
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void postExecute(AsyncTaskResult<T> result) {
            refresh();
        }
    }

    private class InsertTask extends ExceptionAsyncTask<T> {

        @Override
        protected void doInBackground(T t) {
            insertInBackground(t);
        }

        @Override
        protected void dealWithException(Exception e) {
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void postExecute(AsyncTaskResult<T> result) {
            refresh();
        }
    }

    private class RefreshTask extends AsyncTask<Void, Void, List<T>> {
        @Override
        protected List<T> doInBackground(Void... params) {
            return refreshInBackground();
        }

        @Override
        protected void onPostExecute(List<T> ts) {
            super.onPostExecute(ts);
            if (ts != null) {
                setAll(ts);
            }
        }
    }

    private class DeleteTask extends AsyncTask<T, Void, Void> {
        @SafeVarargs
        @Override
        protected final Void doInBackground(T... ts) {
            for (T t : ts) {
                deleteInBackground(t);
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
