package edu.ncsu.soc.rms;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;




public class RingerManagerActivity extends Activity implements OnClickListener {
 
 
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.main);
    
View v = findViewById(R.id.buttonClick);
View v1 = findViewById(R.id.thirdClick);
View v2 = findViewById(R.id.seClick);
	//set event listener
       v.setOnClickListener(this);
       
       v1.setOnClickListener(this);
       v2.setOnClickListener(this);
	   
  }
  
  

public void onClick(View arg0) {
	if(arg0.getId() == R.id.buttonClick){
		//define a new Intent for the second Activity
		Intent intent = new Intent(this,SecondActivity.class);
 
		//start the second Activity
		this.startActivity(intent);
	}if(arg0.getId() == R.id.thirdClick){
		//define a new Intent for the second Activity
		Intent intent = new Intent(this,ThirdActivity.class);
 
		//start the second Activity
		this.startActivity(intent);
	}
	if(arg0.getId() == R.id.seClick){
		//define a new Intent for the second Activity
		Intent intent = new Intent(this,Fourth.class);
 
		//start the second Activity
		this.startActivity(intent);
	}
	
}

}
