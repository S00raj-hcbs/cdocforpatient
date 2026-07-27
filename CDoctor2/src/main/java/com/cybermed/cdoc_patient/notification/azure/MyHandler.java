package com.cybermed.cdoc_patient.notification.azure;
//import com.microsoft.windowsazure.notifications.NotificationsHandler;

//public class MyHandler extends NotificationsHandler {
//    public static final int NOTIFICATION_ID = 1;
//    private NotificationManager mNotificationManager;
//    NotificationCompat.Builder builder;
//    Context ctx;
//
//    @Override
//    public void onReceive(Context context, Bundle bundle) {
//        ctx = context;
//        String nhMessage = bundle.getString("message");
//        sendNotification(nhMessage);
//        if (FragmentMainActivity.isVisible) {
//            FragmentMainActivity.fragmentMainActivity.ToastNotify(nhMessage);
//        }
//    }
//
//    private void sendNotification(String msg) {
//
//        Intent intent = new Intent(ctx, FragmentMainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        mNotificationManager = (NotificationManager)
//                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
//                intent, PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(ctx)
//                        .setSmallIcon(R.drawable.cdoc_icon)
//                        .setContentTitle("Notification Hub Demo")
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText(msg))
//                        .setSound(defaultSoundUri)
//                        .setContentText(msg);
//
//        mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//    }
//}
