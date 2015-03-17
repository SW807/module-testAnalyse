package dk.aau.cs.psylog.analysis.testanalyse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

// One time tasks should be IntentService?
public class PsyLogService extends Service {

    Analysis analysis;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        analysis = new Analysis(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flag, int startid)
    {
        analysis.analysisParameters(intent);
        analysis.startAnalysis();
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        analysis.stopAnalysis();
    }
}
