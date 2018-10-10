package mad.geo.service.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.widget.Toast;

import mad.geo.R;
import mad.geo.view.activity.MainActivity;

public class DirectReplyReceiveService extends Service {
    public DirectReplyReceiveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle directReplyInfo = RemoteInput.getResultsFromIntent(intent);
        CharSequence message = directReplyInfo.getCharSequence(getString(R.string.DIRECT_REPLY_RESULT_KEY));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        if(intent.hasExtra(getString(R.string.NOTIFICATION_ID_KEY))){
            int notificationID = intent.getIntExtra(getString(R.string.NOTIFICATION_ID_KEY), 0);
            displayConfirmNotification("Message replied successfully!", "Message replied successfully: " + message, notificationID);
        }
        stopSelf();


        return super.onStartCommand(intent, flags, startId);
    }
    //回复后
    private void displayConfirmNotification(String title, String text, int notificationId){

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(getString(R.string.NOTIFICATION_ID_KEY), notificationId);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_android_white_36dp)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification.build());
    }
}
