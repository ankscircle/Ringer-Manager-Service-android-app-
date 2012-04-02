package edu.ncsu.soc.rms;

import static edu.ncsu.mas.platys.applications.constants.PlatysConstants.REMOTE_PACKAGE_NAME;
import static edu.ncsu.mas.platys.applications.constants.PlatysConstants.REMOTE_SERVICE_NAME;

import android.provider.CallLog.Calls;
import android.provider.ContactsContract;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import edu.ncsu.mas.platys.android.stub.IPlatysMiddlewareRemoteService;

public class RingerManagerService extends Service {
  static final String TAG = "RingerManagerService";
  List<String> Hold= new ArrayList<String>();
  public List<String> list = new ArrayList<String>();
  private final IBinder mBinder = new RingerServiceBinder();
  List<String> finalList= new ArrayList<String>();
  public String usrname;
  public String passwd;
  public int direct;
  public int msg;
  
  
  
  public void setArguments( int msg)
  {
	  	  this.msg= msg;
	  Log.i(TAG, "Msg:"+msg);
  }
  public class RingerServiceBinder extends Binder {
    RingerManagerService getService() {
      return RingerManagerService.this;
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  static final String APP_NAME = "RingerManagerService";
  static final String APP_SUMMARY = "A Ringer Manager client with a service";

  static final String ACTION_REQUEST_PLACE_UPDATES = "edu.ncsu.soc.rms.request_place_updates";
static int onlyOnce=0;
  static String privateKey;
  static String currentPlace;
  static List<String> pla;
  AlarmManager am;

  @Override
  public void onCreate() {
    super.onCreate();
    am  = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent.getAction() != null && intent.getAction().equals(ACTION_REQUEST_PLACE_UPDATES)) {
      Log.i(TAG, "Update current place");
      updateCurrentPlace();
    }

    return START_STICKY;
  }

  private IPlatysMiddlewareRemoteService mService = null;

  ServiceConnection mConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      mService = IPlatysMiddlewareRemoteService.Stub.asInterface(service);
      try {
        privateKey = mService.registerApplication(APP_NAME, APP_SUMMARY);
        Log.i(TAG, "Private key : " + privateKey);
        schedulePlaceUpdates();
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }

    public void onServiceDisconnected(ComponentName className) {
      stopPlaceUpdates();
      privateKey = null;
      mService = null;
    }
  };

  public void registerWithPlatysMiddleware() {
	  Intent intent = new Intent();
	    intent.setClassName(REMOTE_PACKAGE_NAME, REMOTE_SERVICE_NAME);
	    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	    
	 
  }

  public void unRegisterWithPlatysMiddleware() {
    try {
      if (mService != null) {
        mService.unregisterApplication(privateKey);
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  public String getCurrentPlace() {
    return currentPlace;
  }

  public void updateCurrentPlace() {
    try {
    	int count=0;
    	int pre=1;
    	int dbcheck=0;
    	int setMe=0;
      if (mService != null && privateKey != null) {
    	
		  Hold = mService.getCurrentActivities(privateKey);
    	  currentPlace = mService.getCurrentPlace(privateKey);
        pla = mService.getAllPlaces(privateKey);
    	  	        for (String strings : pla) {
  	        	ContentValues values = new ContentValues();
  	        	Cursor c =this.getContentResolver().query(RingerModes.CONTENT_URI, null, null, null, null);
  	        	pre=1;
  	        	setMe=0;
  	            if (c.moveToFirst()) {
  	            	count ++;
  	            	dbcheck++;
  	                          do{
  	                        	  if(((c.getString(c.getColumnIndex(RingerModes.PLACES))).equals(strings)))
  	                        	  {
  	                        		  list.remove(strings);
  	                        	  pre=0;
  	                        	  }
  	                        	  
  	                        	//  Log.i(TAG, "Sample...: " + c.getString(c.getColumnIndex(RingerModes.PLACES)));
  	                        	 
  	                        	 for (String str_chk : list)
  	                        	 {
  	                        		 if(str_chk.equals(strings))
  	                        			 setMe=1;
  	                        	 }
  	                        //   Toast.makeText(this, "repeat: "+strings, Toast.LENGTH_SHORT).show();
  	                          } while (c.moveToNext());
  	                       }
  	      //    Log.i(TAG, "String was: " + strings+"Setme: "+setMe+"pre: "+pre+"db: "+dbcheck);
  	            if(pre==1 &&setMe==0&& dbcheck>0)
  	            {
  	              Log.i(TAG, "Sample... added: " +strings);
  	            	this.list.add(strings);
  	    		
  	            }
  	          
  	        }
  	      		 if(count ==0 && onlyOnce == 0)
  	      		 {
  	      	  pla = mService.getAllPlaces(privateKey);
  	      	onlyOnce++;
  	            for (String strings1 : pla) {
  	            	           	
  		           this.list.add(strings1);
                  	  Log.i(TAG, "Listed: " + strings1);
  	                            	 
  	            } 
  	            }
  	      	 Log.i(TAG, "Current place is: " + currentPlace);
  	      }
      if(currentPlace!=null)
      {
      AudioManager mAudioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE); 
      int ringerMode = mAudioManager.getRingerMode();
      Log.i(TAG, "ringerMode value=" + ringerMode);
     Cursor c =this.getContentResolver().query(RingerModes.CONTENT_URI, null, null, null, null);
    	
        if (c.moveToFirst()) {
        	
                      do{
                    		Log.i(TAG, "Check Changing!! "+c.getString(c.getColumnIndex(RingerModes.PLACES))+"Ringer:"+c.getString(c.getColumnIndex(RingerModes.RINGER_MODE)));
                    	  if(currentPlace.equals(c.getString(c.getColumnIndex(RingerModes.PLACES)))&&ringerMode!=c.getColumnIndex(RingerModes.RINGER_MODE))
                          {
                    		  int ringID=ringerMode;
                          	Log.i(TAG, "Tried Changing!! "+currentPlace);
                          	if(c.getString(c.getColumnIndex(RingerModes.RINGER_MODE)).equals("Normal Mode"))
                          		ringID=2;
                          	if(c.getString(c.getColumnIndex(RingerModes.RINGER_MODE)).equals("Silent Mode"))
                          		ringID=0;
                          	if(c.getString(c.getColumnIndex(RingerModes.RINGER_MODE)).equals("Vibration Mode"))
                          		ringID=1;
                          mAudioManager.setRingerMode(ringID);
                          }
                    	  else
                    			Log.i(TAG, "No need to change!! "+currentPlace);
                      } while (c.moveToNext());
                   }
     
       // mAudioManager.setRingerMode(1);
      
     ringerMode = mAudioManager.getRingerMode();
      Log.i(TAG, "changed ringerMode value=" + ringerMode);
   
      
      
      
      
      }
 //missed call
      
      
      final String[] projection = null;
      final String selection = null;
      String pre_name="";
      String callNumber_u="";
      final String[] selectionArgs = null;
      boolean missed_call = false;
      final String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
      Cursor cursor = null;
      int MISSED_CALL_TYPE = android.provider.CallLog.Calls.MISSED_TYPE;
          cursor = this.getContentResolver().query(
                  Uri.parse("content://call_log/calls"),
                  projection,
                  selection,
                  selectionArgs,
                  sortOrder);
          while (cursor.moveToNext()) { 
              String callLogID = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls._ID));
              String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
              String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
              String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
              String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
              String Name = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME)); 
            // Log.i(TAG,"Missed Call Found.......: " + cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
            if(Integer.parseInt(callType) == MISSED_CALL_TYPE&& Integer.parseInt(isCallNew) > 0){
            	
            	ContentValues values = new ContentValues();
            	            	 values.put(android.provider.CallLog.Calls.NEW, Integer.valueOf(0));
            	            	 int temp = this.getContentResolver().update(Uri.parse("content://call_log/calls"),
             	                        values, "_ID="+callLogID, selectionArgs);
            	            	 missed_call=true;
            	            	 pre_name=Name;
            	            	 callNumber_u=callNumber;
            	   Log.i(TAG,"Missed Call Found: " + callNumber+"ID: "+callLogID+ "Name: "+Name+ "New:"+isCallNew);
              
          }
     
      }
         
     
      if(missed_call== true)
      {
    	//  Log.i(TAG,"FoundNumber");
    	  String name="";
    	  String phoneNumber;
    	  String emailAddress="";
    	  
		Cursor cur = this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                  null, null, null, null);
    	   while (cur.moveToNext()) 
    	   {
    	      
    		   String contactId = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
    	       name = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 
    	    
    	       String hasPhone = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
    	       if(name.equals(pre_name))
    	       {
    	    	   Log.i(TAG,"Found Name");
    	       if ( hasPhone.equalsIgnoreCase("1"))
    	           hasPhone = "true";
    	       else
    	           hasPhone = "false" ;

    	       if (Boolean.parseBoolean(hasPhone)) 
    	       {
    	   
    	        	  Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,null, null);
    	    	       while (emails.moveToNext()) 
    	    	       {
    	    	        emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
    	    	        Log.i(TAG,"FoundNumber Email:"+emailAddress);
    	    	        sendEmailNotification(emailAddress);
    	    	       }
    	    	       emails.close();
    	          }
    	       }    	  }    
    	   cur.close();
   	}
    } catch (Exception e) {
      e.printStackTrace();
      Log.i(TAG, "updateplace: " + e.toString());
    }
  }

public void schedulePlaceUpdates() {
    Log.i(TAG, "Scheduling place updates");
//this.getPlacesNotAdded();
    Intent intent = new Intent(this, RingerManagerService.class);
    intent.setAction(ACTION_REQUEST_PLACE_UPDATES);
    PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
    Log.i(TAG, "Updating as scheduled");
    // Repeat every 10 minutes, starting now
    am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 3* 1000, pendingIntent);
  }

  public void stopPlaceUpdates() {
    Log.i(TAG, "Stop place updates");

    Intent intent = new Intent(this, RingerManagerService.class);
    intent.setAction(ACTION_REQUEST_PLACE_UPDATES);
    PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

    am.cancel(pendingIntent);
  }
  public List<String> getPlacesNotAdded()
  {
	/* if(this.list.isEmpty())
		 return finalList;
	 else
		 {
		 finalList.clear();
		 finalList.addAll(this.list);
	  this.list.clear();
	  Log.i(TAG, "trying updates");
	  return finalList;
	  }*/
	 return this.list;
	 
	
}
  public void sendEmailNotification(String email)
  {
	  
	  String msg_con="";
 
  
  try{
	  if (mService != null && privateKey != null) {
		 
		  if(msg==0|| msg==2)
			  {
			  msg_con= "Activities:";
			  for (String strings1 : Hold) {
    	           	
 		           msg_con = msg_con +", "+ strings1;
 	                            	 
 	            } 
			  if(msg==1)
				  {msg_con= " Places: " + currentPlace;
				  
				  }
			  if(msg==2)
			  {
				  msg_con= msg_con+ "   Places: " + currentPlace;
			  
			  }
			  
			  }
		  		 Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
	                  intent.setType("text/plain");
	                  intent.putExtra(Intent.EXTRA_SUBJECT, "Missed You Call");
	                  intent.putExtra(Intent.EXTRA_TEXT, ""+msg_con);
	                  intent.setData(Uri.parse("mailto:"+email)); // or just "mailto:" for blank
	                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
	                  startActivity(intent);
			 
			
	  }
  
  }catch(Exception e)
  {
	  Log.i(TAG,""+e.toString());
  }
  }

public List<String> getAllPlacesAdded() {
	// TODO Auto-generated method stub
	return pla;
}
}
 
