package mad.geo.utils;

import android.os.AsyncTask;

import mad.geo.model.AsyncTaskResult;

/**
 * An AsyncTask model that can deal with exception of job doing in background
 * @author : Chenglong Ma
 */
public abstract class ExceptionAsyncTask<T> extends AsyncTask<T, Void, AsyncTaskResult<T>> {
    protected abstract void doInBackground(T t);

    @SafeVarargs
    @Override
    protected final AsyncTaskResult<T> doInBackground(T... ts) {
        try {
            for (T t : ts) {
                doInBackground(t);
            }
            return null;
        } catch (Exception e) {
            return new AsyncTaskResult<>(e);
        }
    }

    @Override
    protected final void onPostExecute(AsyncTaskResult<T> result) {
        super.onPostExecute(result);
        if (result != null && result.getError() != null) {
            dealWithException(result.getError());
        } else postExecute(result);
    }

    protected abstract void dealWithException(Exception e);

    protected abstract void postExecute(AsyncTaskResult<T> result);
}
