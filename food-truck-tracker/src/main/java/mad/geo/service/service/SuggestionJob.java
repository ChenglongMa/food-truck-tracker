package mad.geo.service.service;

import android.app.AlertDialog;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import mad.geo.view.activity.MapsActivity;

/**
 * @author : Chenglong Ma
 */
public class SuggestionJob extends JobService {
    private final String LOG_TAG = this.getClass().getName();
    private JobThread jobThread;
//    private AlertDialog.Builder dialog = new AlertDialog.Builder();//TODO

    @Override
    public boolean onStartJob(JobParameters params) {
//        dialog.setTitle("Suggestion");
//        dialog.setMessage("");
        jobThread = new JobThread(params);
        jobThread.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(LOG_TAG, "Job finished - calling interrupt() on worker thread");
        if (jobThread != null)
            jobThread.interrupt();
        return false;
    }

    private class JobThread extends Thread {
        private static final int SECS = 10;
        private JobParameters params;

        public JobThread(JobParameters params) {
            this.params = params;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= SECS; i++) {
                    // just to be safe
                    if (isInterrupted())
                        throw new InterruptedException();
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                    Log.d(LOG_TAG, String.format("Running job: %d of %d secs, time: %s",
                            i, SECS, DateFormat.getTimeInstance().format(new Date())));
                }
            } catch (InterruptedException e) {
                // cancel work loop
                Log.d(LOG_TAG, "Interrupted");
            }

            // inform that we have finished (can be called from any thread)
            jobFinished(params, false);
        }
    }
}
