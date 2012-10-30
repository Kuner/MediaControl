package org.zju.ese.mediacontrol;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MediaControlActivity extends Activity {
	EditText addressEditText;
	EditText portEditText;
	TextView statusText;
	Button openButton;
	Button closeButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_control);
        addressEditText = (EditText)findViewById(R.id.addressEditText);
        portEditText = (EditText)findViewById(R.id.portEditText);
        openButton = (Button)findViewById(R.id.open_button);
        closeButton = (Button)findViewById(R.id.close_button);
        statusText = (TextView)findViewById(R.id.statusView);
        
        if(isServiceRunning(this,"org.zju.ese.mediacontrol.MediaControlService"))
        	statusText.setText("已开启");
        else
        	statusText.setText("已关闭");
        
        openButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				statusText.setText("已开启");
				Intent intent = new Intent(MediaControlActivity.this, MediaControlService.class);
				intent.putExtra("address", addressEditText.getText().toString());
				intent.putExtra("port",Integer.parseInt(portEditText.getText().toString()));
		        startService(intent);
			}
        });
        
        closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				statusText.setText("已关闭");
				Intent intent = new Intent(MediaControlActivity.this, MediaControlService.class);
		        stopService(intent);
			}
        });
    }
    
    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
        		mContext.getSystemService(Context.ACTIVITY_SERVICE); 
        List<ActivityManager.RunningServiceInfo> serviceList 
        = activityManager.getRunningServices(30);
       if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
        	String name = serviceList.get(i).service.getClassName();
            if (name.equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_media_control, menu);
        return true;
    }
}
