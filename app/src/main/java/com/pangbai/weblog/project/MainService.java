package com.pangbai.weblog.project;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.pangbai.weblog.execute.NodeExer;

public class MainService extends Service {
  final   public  static String action= "action";
  Process livePreview;
  public enum Type{
      live_preview
  }
    public MainService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      Type type;
      try {
          type = Type.valueOf(intent.getStringExtra(action));
      }catch (Exception e){
          return super.onStartCommand(intent, flags, startId);
      }
      if (type==Type.live_preview){
       // livePreview=  NodeExer.hexoServer(false);
      }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}