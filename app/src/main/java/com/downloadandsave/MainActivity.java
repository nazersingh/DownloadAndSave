package com.downloadandsave;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    public static DownloadTask downloadTask;
    //    ProgressDialog pDialog;
    int format;
     int id = 1;
    //    NotificationManager mNotifyManager;
//    NotificationCompat.Builder mBuilder;
     NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    RemoteViews notificationView;
    AsyncTask asyncTask;
    Notification notif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.click);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadTask = new DownloadTask(MainActivity.this);
//        downloadTask.execute("  https://www.smashingmagazine.com/wp-content/uploads/2015/06/10-dithering-opt.jpg");
//        downloadTask.execute("http://www.stephaniequinn.com/Music/Commercial%20DEMO%20-%2005.mp3");
                downloadTask.execute("http://techslides.com/demos/sample-videos/small.mp4");
            }
        });
//        pDialog=new ProgressDialog(MainActivity.this);
//        pDialog.show();


//        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        mBuilder = new NotificationCompat.Builder(this);
//        mBuilder.setContentTitle("video Download")
//                .setContentText("Download in progress")
//                .setSmallIcon(R.mipmap.ic_launcher);


    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createDownloadNotification();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(" ", "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // Displays the progress bar for the first time.

                // download the file
                input = connection.getInputStream();
//                SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
//                format = Integer.parseInt(s.format(new Date()));
                File folder = new File(Environment.getExternalStorageDirectory() + "/foldername");
                Log.e("folder path", "" + folder.getAbsolutePath());
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/foldername/" + format + ".mp4");
                byte data[] = new byte[4096];
                int total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
//                    builder.setProgress(fileLength, total, false);
//                    notificationManager.notify(id, builder.build());
                    notificationView.setProgressBar(R.id.pb_progress, fileLength, total, false);
//                    notificationManager.notify(id, builder.build());
                    notificationManager.notify(id,notif);

                    output.write(data, 0, count);
                }
//                while ((count = input.read(data)) != -1) {
//                    // allow canceling with back button
//                    if (isCancelled()) {
//                        input.close();
//                        return null;
//                    }
//                    total += count;
//                    mBuilder.setProgress(fileLength, count, false);
//                    mNotifyManager.notify(id, mBuilder.build());
//                   // publishProgress((int)((total*100)/fileLength));
//                    // publishing the progress....
//                    if (fileLength > 0) // only if total length is known
//                        publishProgress((int) (total * 100 / fileLength));
//                    int now= (int) (total * 100 / fileLength);
//
//                    output.write(data, 0, count);
//                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            builder.setOngoing(false);
            notificationManager.cancel(id);
        }
        //        protected void onProgressUpdate(int... progress) {
//            // setting progress percentage
//            pDialog.setProgress(progress[0]);
//        }


        @Override
        protected void onPostExecute(String result) {
//            mWakeLock.release();
            Log.e("result is ", "" + result);
//            File imageFile = new File(Environment.getExternalStorageDirectory() +"/foldername/"+format+".mp4");
//            Log.e("Image path",""+imageFile.getAbsolutePath());
//            //add file to video gallery
//            MediaScannerConnection.scanFile(MainActivity.this, new String[]{imageFile.getPath()}, new String[]{"video/mp4"}, null);
            // pDialog.setVisibility(View.GONE);
//            pDialog.dismiss();
            // Removes the progress bar
//            builder.setContentText("Download complete").setProgress(0,0,false);
//            mNotifyManager.notify(id, builder.build());
            notificationView.setTextViewText(R.id.description, "Download complete");
            notificationView.setProgressBar(R.id.pb_progress, 0, 0, false);
            builder.setOngoing(false);
//            notificationManager.notify(id, builder.build());
            notificationManager.notify(id,notif);
            Toast.makeText(MainActivity.this, "File saved", Toast.LENGTH_SHORT).show();
        }
    }

    public static void cancelDownload() {
        try {

            if (downloadTask != null) {

                downloadTask.cancel(true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createDownloadNotification() {
        Intent closeButton = new Intent("Download_Cancelled");
        closeButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, closeButton, 0);

        notificationView = new RemoteViews(getPackageName(), R.layout.widget_update_notification);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setTicker("Ticker Text ").setContent(notificationView);
//		int color = 0xff123456;
//		int color = getResources().getColor(R.color.Red);
//        int color = ContextCompat.getColor(getApplicationContext(), R.color.Red);
//        builder.setColor(color);
        notificationView.setProgressBar(R.id.pb_progress, 100, 0, false);
        notificationView.setImageViewResource(R.id.img, R.mipmap.ic_launcher);
        notificationView.setTextViewText(R.id.title, "hello title");
        notificationView.setTextViewText(R.id.description, "hello how r u ?");
        notificationView.setOnClickPendingIntent(R.id.btn_close, pendingSwitchIntent);

//        builder.setCustomBigContentView(notificationView);
//        builder.setStyle(new NotificationCompat.BigPictureStyle(builder).bigPicture(
//                BitmapFactory.decodeResource(getResources(),
//                        R.mipmap.ic_launcher)));
        notif = builder.build();


        builder.setOngoing(true);
        notificationManager.notify(1, notif);

    }

    public static class DownloadCancelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("broad ", "Received Cancelled Event");
            if (downloadTask != null) {
                cancelDownload();
            }
        }
    }
}