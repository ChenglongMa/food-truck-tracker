package mad.geo.service.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import mad.geo.R;
import mad.geo.view.activity.MainActivity;
import mad.geo.view.dialog.AddEditTrackingDialog;
import mad.geo.view.fragment.TrackableFragment;

public class NotificationDisplayService extends Service {

    final int NOTIFICATION_ID = 16;

    public NotificationDisplayService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String msg = intent.getStringExtra(TrackableFragment.SUGGESTION_MSG);
        if (msg != null && !msg.isEmpty()) {
            msg = "Please check your google api key";
        }
        displayNotification("Notification", msg);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void displayNotification(String title, String text) {
        //打开Main Activity的按钮创建
        Intent notificationIntent = new Intent(this, AddEditTrackingDialog.class);
        notificationIntent.putExtra(getString(R.string.NOTIFICATION_ID_KEY), NOTIFICATION_ID);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, 0);
        //toast的创建
//        Intent showToastIntent = new Intent(this, ShowToastService.class);
//        showToastIntent.putExtra(getString(R.string.NOTIFICATION_ID_KEY), NOTIFICATION_ID);
//        PendingIntent showToastPendingIntent = PendingIntent.getService(this, 2, showToastIntent, 0);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_android_white_36dp)
                //.setLargeIcon(BitmapFactory.decodeResource(R.drawable.xyz))
                .setColor(getResources().getColor(R.color.colorAccent))
                .setVibrate(new long[]{0, 300, 300, 300})
                //.setSound()
                .setLights(Color.WHITE, 1000, 5000)
                //.setWhen(System.currentTimeMillis())
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                //to add new tracking dialog
                .addAction(R.drawable.ic_action_open, "Add new Tracking", notificationPendingIntent)
                //显示一个toast
//                .addAction(R.drawable.ic_action_toast, "Toast", showToastPendingIntent)
                //Reply消息
                .addAction(generateDirectReplyAction());

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    private NotificationCompat.Action generateDirectReplyAction() {
        if (Build.VERSION.SDK_INT >= 24) {
            //Reply的创建
            Intent directReplyIntent = new Intent(this, DirectReplyReceiveService.class);
            directReplyIntent.putExtra(getString(R.string.NOTIFICATION_ID_KEY), NOTIFICATION_ID);
            PendingIntent directReplyPendingIntent = PendingIntent.getService(this, 5, directReplyIntent, 0);
            //回复框
            RemoteInput remoteInput = new RemoteInput.Builder(getString(R.string.DIRECT_REPLY_RESULT_KEY))
                    .setLabel("Enter to reply...")
                    .build();

            NotificationCompat.Action directReplyAction = new NotificationCompat.Action.Builder(R.drawable.ic_action_reply, "Reply", directReplyPendingIntent)
                    .addRemoteInput(remoteInput)
                    .build();
            return directReplyAction;

        } else {

            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.putExtra(getString(R.string.NOTIFICATION_ID_KEY), NOTIFICATION_ID);
            PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 1, notificationIntent, 0);

            NotificationCompat.Action directReplyAction = new NotificationCompat.Action.Builder(R.drawable.ic_action_reply, "Reply", notificationPendingIntent)
                    .build();
            return directReplyAction;
        }
    }
}
