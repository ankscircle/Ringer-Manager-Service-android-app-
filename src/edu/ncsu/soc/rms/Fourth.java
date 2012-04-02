package edu.ncsu.soc.rms;

import java.util.ArrayList;
import java.util.List;

import edu.ncsu.soc.rms.RingerManagerService.RingerServiceBinder;


import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class Fourth extends Activity implements OnClickListener {
  private static final String TAG = "SAMPLE_ACIVITY";
RingerManagerService mService;
  boolean mBound = false;
  private Spinner spinner1, spinner2;
  private Button btnSubmit;
 private Button upmenu;
 Context con =getBaseContext();
 RingerManagerService exp = new RingerManagerService();
 List<String> list = new ArrayList<String>();
  /** Called when the activity is first created. */
 
 
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    startService(new Intent(this, RingerManagerService.class));
    setContentView(R.layout.fourth);
    Log.i(TAG, "checking Activity: ");
    spinner1 = (Spinner) findViewById(R.id.spinner1);
	spinner2 = (Spinner) findViewById(R.id.spinner2);
	btnSubmit = (Button) findViewById(R.id.btnSubmit);
	upmenu = (Button) findViewById(R.id.upMenu);
	spinner1.setEnabled(false);
    spinner2.setEnabled(false);
    btnSubmit.setEnabled(false);
   addItemsOnSpinner1();
	addListenerOnButton();
addListenerOnSpinnerItemSelection();
	   
  }
  
  
  
  public void addItemsOnSpinner1() {
	 try{
		spinner1 = (Spinner) findViewById(R.id.spinner1);
			
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(dataAdapter);
	 }catch (Exception e) {
	      e.printStackTrace();
	      Log.i(TAG, "Active: " + e.toString());
	    }
	  }
	 
	  public void addListenerOnSpinnerItemSelection() {
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	  }
	  public void addListenerOnButton() {
		   
			
			spinner1 = (Spinner) findViewById(R.id.spinner1);
			spinner2 = (Spinner) findViewById(R.id.spinner2);
			btnSubmit = (Button) findViewById(R.id.btnSubmit);
			upmenu = (Button) findViewById(R.id.upMenu);
			
			btnSubmit.setOnClickListener(new OnClickListener() {
		 
				
			  public void onClick(View v) {
		 
			    Toast.makeText(Fourth.this,
				"OnClickListener : " + 
		                "\nSpinner 2 : "+ String.valueOf(spinner2.getSelectedItem()) + 
		                "\nSpinner 1 : "+ String.valueOf(spinner1.getSelectedItem()),
					Toast.LENGTH_SHORT).show();
			    putInDB();
			   
			  }

			
			  
		 
			});
			
			upmenu.setOnClickListener(new OnClickListener() {
				

				public void onClick(View v) {
					// TODO Auto-generated method stub
					  Toast.makeText(Fourth.this, "Works!!!", Toast.LENGTH_SHORT).show();
					 list.clear();
					 list.addAll(mService.getPlacesNotAdded());
					addItemsOnSpinner1();
					addListenerOnSpinnerItemSelection();
					addListenerOnButton();
					spinner1.setEnabled(true);
				    spinner2.setEnabled(true);
				    btnSubmit.setEnabled(true);
				    //for (String strings :list) {
						// Log.i(TAG, "checking Activity: " + strings);
						 // Toast.makeText(SamplePlatysClientActivity.this, "Places: "+list.toString(), Toast.LENGTH_SHORT).show();
					// }
					//	addListenerOnButton();
					//	addListenerOnSpinnerItemSelection();
						//setContentView(R.layout.second);
				}
			
			  		 
			});
			
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

  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    switch (item.getItemId()) {
    case R.id.register:
      if (mBound) {
        mService.registerWithPlatysMiddleware();
        
       /* Cursor c =this.getContentResolver().query(RingerModes.CONTENT_URI, null, null, null, null);
        if (c.moveToFirst()) {
                      do{
                    	  Log.i(TAG, "Sample: " + c.getString(c.getColumnIndex(RingerModes.PLACES)));
                        Toast.makeText(this, "Places: "+c.getString(c.getColumnIndex(RingerModes.PLACES)), Toast.LENGTH_SHORT).show();
                      } while (c.moveToNext());
                   }*/
      }
      break;
    case R.id.unregister:
      if (mBound) {
        mService.unRegisterWithPlatysMiddleware();
      }
      break;
   
    }

    return true;
  }
  public boolean putInDB()
  {
	  
	  ContentValues values = new ContentValues();
  	Cursor c =this.getContentResolver().query(RingerModes.CONTENT_URI, null, null, null, null);
	// values.put(RingerModes.PLACES, String.valueOf(spinner1.getSelectedItem()));
	 
	 String temp =String.valueOf(spinner1.getSelectedItem());
	values.put(RingerModes.RINGER_MODE, String.valueOf(spinner2.getSelectedItem()));
	int uri = this.getContentResolver().update(RingerModes.CONTENT_URI,values, " places="+temp, null);
	list.clear();
	 list.addAll(mService.getAllPlacesAdded());
	
	 
	addItemsOnSpinner1();
	addListenerOnSpinnerItemSelection();
	addListenerOnButton();
	return true;
  }



public void onClick(View arg0) {
	if(arg0.getId() == R.id.buttonClick){
		//define a new Intent for the second Activity
		Intent intent = new Intent(this,SecondActivity.class);
 
		//start the second Activity
		this.startActivity(intent);
	}
	
}

}
