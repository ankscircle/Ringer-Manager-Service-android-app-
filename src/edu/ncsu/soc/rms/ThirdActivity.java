package edu.ncsu.soc.rms;

import edu.ncsu.soc.rms.RingerManagerService.RingerServiceBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;



public class ThirdActivity extends Activity {
	public int direct;
	public int msg;
	RingerManagerService mService;
	boolean mBound = false;
	@Override
	  public void onCreate(Bundle savedInstanceState) {
		startService(new Intent(this, RingerManagerService.class));
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.third);
	    addListenerOnButton();
	    
		
	  }
	
	
	
	 private void addListenerOnButton() {
		Button btnSubmit = (Button) findViewById(R.id.btnSubmit1);
		
			
			btnSubmit.setOnClickListener(new OnClickListener() {
		 
				
			  public void onClick(View v) {
		 
				  
				   mService.setArguments(msg);
				   gotomain();
			  }

			});

			
	}
	public void gotomain()
	{
		Intent intent = new Intent(this,RingerManagerActivity.class);
		   
		//start the second Activity
		this.startActivity(intent);
	}
	public void onRadioButtonClicked1(View v) {
	    // Perform action on clicks
	    RadioButton rb = (RadioButton) v;
	    Toast.makeText(this, rb.getId(), Toast.LENGTH_SHORT).show();
	    if(rb.getText().equals("Activity"))
	    	msg=0;
	    else if(rb.getText().equals("Place"))
	    	msg=1;
	    else if(rb.getText().equals("Place + Activity"))
	    	msg=2;
	   
	}
	 @Override
	  public void onStart() {
	    super.onStart();
	    Intent intent = new Intent(this, RingerManagerService.class);
	    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    if (mBound) {
	      unbindService(mConnection);
	      mBound = false;
	    }
	  }


	  /** Defines callbacks for service binding, passed to bindService() */
	  private final ServiceConnection mConnection = new ServiceConnection() {

	    public void onServiceConnected(ComponentName className, IBinder service) {
	    	RingerServiceBinder binder = (RingerServiceBinder) service;
	      mService = binder.getService();
	      mBound = true;
	    }

	    public void onServiceDisconnected(ComponentName arg0) {
	      mBound = false;
	    }
	  };
	

	 
  
}
