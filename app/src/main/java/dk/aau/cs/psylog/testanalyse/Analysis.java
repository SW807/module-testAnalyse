package dk.aau.cs.psylog.testanalyse;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Analysis {

    private ContentResolver resolver;
    private Uri read;
    private Uri write;

    private Timer timer;
    private TimerTask timerTask;
    private long delay;
    private long period;

    public Analysis(Context context) {
        resolver = context.getApplicationContext().getContentResolver();
        String uriBase = "content://dk.aau.cs.psylog.psylog";
        read = Uri.parse(uriBase + "/illuminance");
        write = Uri.parse(uriBase + "/testAnalyse");

        timerTask = new TimerTask() {
            @Override
            public void run() {
                String[] projection = {"lux"};
                Cursor cursor = resolver.query(read, projection, null, null, null);

                float report = analyse(cursor);

                ContentValues contentValues = new ContentValues();
                contentValues.put("testdata", report);
                resolver.insert(write, contentValues);

                Log.i("TestAnalyse", "avg light readings " + report + " lux");

                String[] projection2 = {"testdata"};
                LogDatabaseContent(projection2);
            }
        };
    }

    /**
     * Log to database for debugging purposes
     *
     * @param projection2
     */
    private void LogDatabaseContent(String[] projection2) {
        Cursor cursor2 = resolver.query(write, projection2, null, null, null);
        if (cursor2.moveToFirst()) {
            Log.i("test", "starting reads");
            do {
                float result = cursor2.getFloat(0);
                Log.i("test", "Read from analysis module: " + result);
            } while (cursor2.moveToNext());
        }
    }

    public float analyse(Cursor cursor) {
        List<Float> content = new ArrayList<Float>();
        if (cursor.moveToFirst()) {
            do {
                float result = cursor.getFloat(0);
                content.add(result);
            } while (cursor.moveToNext());
        }

        if (!content.isEmpty()) {
            float sum = 0;
            int j = 0;
            for (; j < content.size(); j++) {
                sum += content.get(j);
            }
            float output = sum / (float) j;
            return output;
        }
        return 0;
    }

    public void startAnalysis() {
        if (timer == null) {
            timer = new Timer();
        }
        // skal muligvis stoppes inden reschedule
        timer.schedule(timerTask, delay, period);
    }

    public void stopAnalysis() {
        timer.cancel();
        timer.purge();
    }

    public void analysisParameters(Intent intent) {
        period = intent.getIntExtra("period", 1000);
        delay = intent.getIntExtra("delay", 0);
    }
}
