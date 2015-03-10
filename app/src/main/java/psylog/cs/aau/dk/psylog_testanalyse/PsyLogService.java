package psylog.cs.aau.dk.psylog_testanalyse;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// One time tasks should be IntentService?
public class PsyLogService extends Service {

    ContentResolver resolver;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flag, int startid)
    {
        analysisParameters(intent);
        testAnalysis();
        return Service.START_NOT_STICKY;
    }

    private void testAnalysis()
    {
        resolver = getApplicationContext().getContentResolver();
        String uriBase = "content://dk.aau.cs.psylog.psylog";
        Uri read = Uri.parse(uriBase + "/illuminance");
        Uri write = Uri.parse(uriBase + "/testAnalyse");

        String[] projection = {"lux"};
        Cursor cursor = resolver.query(read, projection , null, null, null);
        float report = analyse(cursor);
        ContentValues contentValues = new ContentValues();
        contentValues.put("testdata", report);
        resolver.insert(write, contentValues);
        Log.i("TestAnalyse", "avg light readings " + report + " lux");
        String[] projection2 = {"testdata"};
        Cursor cursor2 = resolver.query(write, projection2, null, null, null);
        if (cursor2.moveToFirst())
        {
            Log.i("test", "starting reads");
            do {
                float result = cursor2.getFloat(0);
                Log.i("test", "Read from analysis module: " + result);
            } while (cursor2.moveToNext());
        }
    }

    public float analyse(Cursor cursor)
    {
        //android.os.Debug.waitForDebugger();
        List<Float> content = new ArrayList<>();
        if (cursor.moveToFirst())
        {
            do {
                float result = cursor.getFloat(0);
                content.add(result);
            } while (cursor.moveToNext());
        }

        if (!content.isEmpty())
        {
            float sum = 0;
            int j = 0;
            for (; j < content.size(); j++)
            {
                sum += content.get(j);
            }
            float output = sum / (float)j;
            return output;
        }
        return 0;
    }

    public void analysisParameters(Intent intent)
    {

    }
}
