package mad.geo.service.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import mad.geo.R;

/**
 * @author : Chenglong Ma
 */
public class SuggestionJob extends JobService {
    private static final int NOTIFICATION_ID = R.string.notifications_title;
    private final String LOG_TAG = this.getClass().getName();
    SharedPreferences sharedPreferences;
    private JobThread jobThread;

    @Override
    public boolean onStartJob(JobParameters params) {
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

    private void notification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(SuggestionJob.this)
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle(getText(R.string.title_activity_main))
                        .setContentText("又有新的内容上线了，快来我们app看看吧!")
                        .setContentIntent(makeIntent());
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private PendingIntent makeIntent() {
//        PendingIntent contentIntent = PendingIntent.getActivity(
//                MainActivity.class,
//                0,
//                new Intent(this, AddEditTrackingDialog.class).setFlags(
//                        Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("moodimg", moodId),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        return contentIntent;
        return null;//TODO: to be finished.
    }

    private class JobThread extends Thread {
        private static final int SECS = 10;
        private JobParameters params;

        JobThread(JobParameters params) {
            this.params = params;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    notification();
                    Thread.sleep(5000);
                }
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Exception in NotificationService onStartJob");
            }
//            try {
//                for (int i = 1; i <= SECS; i++) {
//                    // just to be safe
//                    if (isInterrupted())
//                        throw new InterruptedException();
//                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
//                    Log.d(LOG_TAG, String.format("Running job: %d of %d secs, time: %s",
//                            i, SECS, DateFormat.getTimeInstance().format(new Date())));
//                }
//            } catch (InterruptedException e) {
//                // cancel work loop
//                Log.d(LOG_TAG, "Interrupted");
//            }

            // inform that we have finished (can be called from any thread)
            jobFinished(params, false);
        }
    }
}
