package org.zju.ese.mediacontrol;

import java.io.IOException;
import org.zju.ese.mediacontrol.ShakeDetector.OnShakeListener;
import org.zju.ese.model.RequestMessage;

import com.android.music.IMediaPlaybackService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

public class MediaControlService extends Service implements OnShakeListener,Runnable {
	private MediaPlayerServiceConnection conn;
	private boolean inSync = false;
	private BroadcastReceiver receiver;
	private String picturePath;
	private String serverAddress;
	private int minaPort;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(android.content.Intent intent, int startId) {
		serverAddress = intent.getExtras().getString("address");
		minaPort = intent.getExtras().getInt("port");
        Intent i = new Intent();
		i.setClassName("com.android.music","com.android.music.MediaPlaybackService");
		conn = new MediaPlayerServiceConnection();
        this.bindService(i, conn, Context.BIND_AUTO_CREATE);    
        
        ShakeDetector detector = new ShakeDetector(this);
        detector.registerOnShakeListener(this);
        detector.start();
        
        receiver = new PictureBroadcastReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("org.zju.ese.broadcast");
        this.registerReceiver(receiver, intentFilter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(receiver);
	}
	
	String getCurrentPackageName()
	{
		ActivityManager am= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		RunningTaskInfo task = am.getRunningTasks(1).get(0);
		return task.topActivity.getPackageName();
	}
	
	void syncFiles()
	{
		Thread thread = new Thread(this);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onShake() {
		// TODO Auto-generated method stub
		if(inSync)
			return;
		inSync = true;

		//Toast.makeText(this, getCurrentPackageName(), Toast.LENGTH_LONG).show();
		String packageName = getCurrentPackageName();
		if(packageName.equals("com.android.music"))
		{
			Toast.makeText(this, "开始同步", Toast.LENGTH_LONG).show();
			String path = null;
			try {
				path = conn.mService.getAlbumName();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			syncFiles();
			
			RequestMessage request = new RequestMessage();
			request.setCommand(RequestMessage.OPENFILE);
			request.setPath(path);
			RequestThread requestThread = new RequestThread(serverAddress,minaPort,request);
			requestThread.start();
			try {
				requestThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(packageName.equals("com.android.photostore"))
		{
			Toast.makeText(this, "开始同步", Toast.LENGTH_LONG).show();
			syncFiles();
			RequestMessage request = new RequestMessage();
			request.setCommand(RequestMessage.PICTURE);
			request.setPath(picturePath);
			RequestThread requestThread = new RequestThread(serverAddress,minaPort,request);
			requestThread.start();
			try {
				requestThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	inSync = false;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		SynchronizedManager manager = new SynchronizedManager(serverAddress,minaPort+100);
		try {
			manager.startSynchronize();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	private class MediaPlayerServiceConnection implements ServiceConnection {
		public IMediaPlaybackService mService;
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mService = IMediaPlaybackService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

	}

	private class PictureBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("org.zju.ese.broadcast")) {
				String tmp = intent.getStringExtra("picturePath");
				if(tmp != null)
					picturePath = tmp;

				// 在android端显示接收到的广播内容
				//Toast.makeText(MediaControlService.this, path, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
