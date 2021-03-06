package edu.berkeley.cs160.teamk;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONException;
import org.json.JSONObject;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.provider.MediaStore;
import android.net.Uri;


public class FHActivity extends Activity {
	
	Button btn_picture, btn_reject, btn_invite, btn_help;
	String act_name = "";
	String img_filename = "";
	String response = "";
	int score = 0;
	int index = 0;
	int id = 0;
	int event_created = -1;
	SharedPreferences.Editor editor = Utility.mPrefs.edit();
	
	//---the images to display---
	String[] myRemoteImages;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fhactivity);
		Bundle extras = getIntent().getExtras();
		img_filename = Utility.mPrefs.getString("act_img_filename", "");
		

		if (extras != null) {
			act_name = extras.getString("name");
			score = extras.getInt("score");
			index = extras.getInt("index");
			id = extras.getInt("id");
			Log.d("friendHealthA", "Act id is: " + id);
			event_created = Utility.mPrefs.getInt("event_created"+index, -1);
			Log.d("friendHealthFHA", "Event_created is: "+event_created);
			editor.putString("act_name", act_name);
			editor.putInt("act_score", score);
			editor.putInt("act_index", index);
			editor.commit();
			
		}
		String[] photoids = Utility.dbAdapter.getPhotoByID(id);
		myRemoteImages = new String[photoids.length];
		for(int i = 0; i < photoids.length; i++){
			try {
				String jsonPic = Utility.facebook.request(photoids[i]);
				JSONObject obj = Util.parseJson(jsonPic);
				myRemoteImages[i] = obj.optString("source");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FacebookError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		TextView txt_ActTitle = (TextView) findViewById(R.id.txt_ActTitle);
		TextView txt_ActPt = (TextView) findViewById(R.id.txt_ActPt);
		txt_ActTitle.setText(act_name);
		txt_ActPt.setText("(+ "+ score + " points)");
			
		Gallery gallery = (Gallery) findViewById(R.id.activityGallery);
		
		
		gallery.setAdapter(new ImageAdapter(this));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(
					AdapterView<?> parent, View v,
					int position, long id) {
				/*Toast.makeText(getBaseContext(),
						"pic" + (position + 1) +  " selected",
						LENGTH_LONG).show();*/
				
				//---display the images selected---
				ImageView imageView = (ImageView) findViewById(R.id.img_fhAct);
				 try {
                     /* Open a new URL and get the InputStream to load data from it. */
                     URL aURL = new URL(myRemoteImages[position]);
                     URLConnection conn = aURL.openConnection();
                     conn.connect();
                     InputStream is = conn.getInputStream();
                     /* Buffered is always good for a performance plus. */
                     BufferedInputStream bis = new BufferedInputStream(is);
                     /* Decode url-data to a bitmap. */
                     Bitmap bm = BitmapFactory.decodeStream(bis);
                     bis.close();
                     is.close();
                     /* Apply the Bitmap to the ImageView that will be returned. */
                     imageView.setImageBitmap(bm);
             } catch (IOException e) {
            	 Log.d("friendHealthFHA", "Remote Image Exception", e);
             }
			}
		});
		
		//---btn_picture---
		btn_reject = (Button) findViewById(R.id.btn_ActReject);
		btn_help = (Button) findViewById(R.id.btn_ActHelp);
		
		final Toast help1 = Toast.makeText(this, Html.fromHtml("<font color='white'><big>+++  </big></font><font color='red'><big>Welcome </big></font>" +
        		"<big><font color = 'yellow'>to the </font><font color = 'green'>Activity </font>" +
        		"<font color = 'blue'>Screen! </font><font color = 'purple'> +++</big></font>"), Toast.LENGTH_LONG);
		help1.setGravity(Gravity.BOTTOM, 0, 200);
		
		final Toast help2 = Toast.makeText(this, Html.fromHtml("<font color='white'><big>Here your task is to take a picture " +
				"of yourself performing the task and submit it to </big></font><font color='blue'><big>Facebook.</big></font>"), Toast.LENGTH_LONG);
		help2.setGravity(Gravity.BOTTOM, 0, 200);
		
		
		final Toast help3 = Toast.makeText(this, Html.fromHtml("<font color='white'><big>Before we get to that, " +
				"let us go over some basic functions in this page</big></font>"), Toast.LENGTH_LONG);
		help3.setGravity(Gravity.BOTTOM, 0, 200);
		
		
		final Toast help4 = Toast.makeText(this, Html.fromHtml("<font color='white'><big>Before you decide to perform the " +
				"activity, you can review all the pictures other users who performed this task have taken.</big></font>"), Toast.LENGTH_LONG);
		help4.setGravity(Gravity.BOTTOM, 0, 200);
		
		
		final Toast help5 = Toast.makeText(this, Html.fromHtml("<big><font color='white'>Also, if you are no longer " +
				"interested in performing this task, simply tap </font><font color = 'red'>REJECT</font></big>"), Toast.LENGTH_LONG);
		help5.setGravity(Gravity.BOTTOM, 0, 200);
		
		
		final Toast help6 = Toast.makeText(this, Html.fromHtml("<big><font color='white'>In addition, you can make the activity " +
		"a </font><font color = 'blue'>Facebook event </font><font color='white'>and invite your friends to join</font></big>"), Toast.LENGTH_LONG);
		help6.setGravity(Gravity.BOTTOM, 0, 200);
		
		
		final Toast help7 = Toast.makeText(this, Html.fromHtml("<big><font color='white'>And most importantly, when you are ready to take a picture, launch the " +
				"camera by tapping</font><font color = 'blue'> TAKE PICTURE</font></big>"), Toast.LENGTH_LONG);
		help7.setGravity(Gravity.BOTTOM, 0, 200);
		
		final Toast help8 = Toast.makeText(this, Html.fromHtml("<big><font color='white'>Finally, Help will launch this demo again if you are still not sure" +
				" how this page operates.</font></big>"), Toast.LENGTH_LONG);
		
		help8.setGravity(Gravity.BOTTOM, 0, 200);
		
		final Animation animation = new AlphaAnimation(1, 0);
	    final Animation animation2 = new AlphaAnimation(1, 0);
	    animation.setDuration(500);
	    animation2.setDuration(200);
	    animation.setInterpolator(new LinearInterpolator());
	    animation2.setInterpolator(new LinearInterpolator());
	    animation.setRepeatCount(6);
	    animation2.setRepeatCount(6);
	    animation.setRepeatMode(Animation.REVERSE);
	    animation2.setRepeatMode(Animation.REVERSE);
		
		
		// Handle click of button.
		btn_reject.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.d("friendHealthFHA", "Rejecting Activity");
				SharedPreferences.Editor editor = Utility.mPrefs.edit();
				editor.putInt("event_created"+index, 0);
				editor.commit();
				
				Intent data = new Intent();
				Bundle extras = new Bundle();
        		extras.putInt("index", index);
        		extras.putString("result", "rejected");
        		data.putExtras(extras);
        		setResult(RESULT_OK, data);
        		finish();
			}
		});
		
		btn_help.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				help1.show();
				
				new CountDownTimer(6000, 1000)
			    {

			        public void onTick(long millisUntilFinished) 
			        {
			        	help2.show();
			        }
			        public void onFinish() 
			        {
			        	help2.show();
			        }
			    }.start();
			    
				new CountDownTimer(12000, 1000)
			    {

			        public void onTick(long millisUntilFinished) 
			        {
			        	help3.show();
			        }
			        public void onFinish() 
			        {
			        	help3.show();
			        }
			    }.start();
			    
				new CountDownTimer(18000, 1000)
			    {

			        public void onTick(long millisUntilFinished) 
			        {
			        	help4.show();
			        }
			        public void onFinish() 
			        {
			        	help4.show();
			        	
			        }
			    }.start();
				
				new CountDownTimer(24000, 1000)
			    {

			        public void onTick(long millisUntilFinished) 
			        {
			        	help5.show();
			        }
			        public void onFinish() 
			        {
			        	help5.show();
			        	btn_reject.startAnimation(animation);
			        }
			    }.start();
			    
				new CountDownTimer(30000, 1000)
			    {

			        public void onTick(long millisUntilFinished) 
			        {
			        	help6.show();
			        }
			        public void onFinish() 
			        {
			        	help6.show();
			        	btn_invite.startAnimation(animation);
			        }
			    }.start();
			    
				new CountDownTimer(36000, 1000)
			    {

			        public void onTick(long millisUntilFinished) 
			        {
			        	help7.show();
			        }
			        public void onFinish() 
			        {
			        	help7.show();
			        	btn_picture.startAnimation(animation);
			        }
			    }.start();
				
				new CountDownTimer(40000, 1000)
			    {

			        public void onTick(long millisUntilFinished) 
			        {
			        	help8.show();
			        }
			        public void onFinish() 
			        {
			        	help8.show();
			        	btn_help.startAnimation(animation);
			        }
			    }.start();
			}
		});
		
		//---btn_picture---
		btn_picture = (Button) findViewById(R.id.btn_ActTakePicture);
		// Handle click of button.
		btn_picture.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.d("friendHealthFHA", "Taking Image from Activity");
				
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri fileUri = Camera.getOutputMediaFileUri(getBaseContext(), Utility.MEDIA_TYPE_IMAGE, act_name);
				img_filename = fileUri.toString();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				
				SharedPreferences.Editor editor = Utility.mPrefs.edit();
				editor.putString("act_img_filename", img_filename);
				editor.commit();
				
				Log.d("friendHealthFHA", "Image name: " + img_filename);
				
				// start the image capture Intent.
				startActivityForResult(intent, 
						Utility.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		});
		
		//---btn_invite---
		btn_invite = (Button) findViewById(R.id.btn_ActInvite);
		// Handle click of button.
		if(event_created == 1){
			btn_invite.getBackground().setColorFilter(Color.rgb(198, 235, 152), PorterDuff.Mode.MULTIPLY);
			btn_invite.setEnabled(false);
		}
		btn_invite.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				/*Intent intent = new Intent(
						"edu.berkeley.cs160.teamk.BallyhooActivity");
				intent.putExtras(getIntent().getExtras());
				startActivityForResult(intent, Utility.RC_INVITE);*/
				Bundle bundle = new Bundle();
				Log.d("friendHealthFHA", "event_created in button: "+event_created);
				if(event_created <= 0){
				try {
					long unixTime = System.currentTimeMillis() / 1000L;
					long oneWeekUnixTime = unixTime + 604800;
					bundle.putString("name", act_name);
					bundle.putString("start_time", String.valueOf(unixTime));
					bundle.putString("end_time", String.valueOf(oneWeekUnixTime));
					bundle.putString("privacy_type", "OPEN");
					response = Utility.facebook.request("me/events", bundle, "POST");
					Log.d("friendHealthFHA", "Response: " + response);
					
					if(response.indexOf("OAuthException") > -1) {
						response = "Invitation Failed";
						Toast.makeText(getBaseContext(),
								"Invitation Failed",
								Toast.LENGTH_LONG).show();
					} else {
						response = "Invitation Successful";
						SharedPreferences.Editor editor = Utility.mPrefs.edit();
						editor.putInt("event_created"+index, 1);
						editor.commit();
						Toast.makeText(getBaseContext(),
								"Invitation Successful",
								Toast.LENGTH_LONG).show();
						btn_invite.getBackground().setColorFilter(Color.rgb(198, 235, 152), PorterDuff.Mode.MULTIPLY);
						btn_invite.setEnabled(false);
					}
				} catch (Exception e) {
					Log.e("friendHealthFHA", e.getMessage());
				}
				}else{
					Log.d("friendHealthFHA", "Inside else of invite button");
					btn_invite.getBackground().setColorFilter(Color.rgb(198, 235, 152), PorterDuff.Mode.MULTIPLY);
					SharedPreferences.Editor editor = Utility.mPrefs.edit();
					editor.putInt("event_created"+index, 1);
					editor.commit();
				}
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("friendHealthFHA", "Entered onActivityResult");
		if (resultCode == RESULT_OK) {
			if (requestCode == Utility.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
				Log.d("friendHealthFHA", "Entered Utility.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE");
				Log.d("friendHealthFHA", act_name + " image taken.");
				
				Intent intent = new Intent(
						"edu.berkeley.cs160.teamk.ActivitySubmission");
				Bundle extras = new Bundle();
				extras.putString("name", act_name);
				Log.d("friendHealthFHA", "score: " + score);
				extras.putInt("score", score);
				Log.d("friendHealthFHA", "img_filename: " + img_filename);
				extras.putString("filename", img_filename);
				extras.putInt("id", id);
				extras.putInt("index", index);
				extras.putString("result", "completed");
				intent.putExtras(extras);
				
				
				
				Log.d("friendHealthFHA", "Starting submission activity");
				startActivityForResult(intent, Utility.RC_ACTIVITYSUBMISSION);
			} else if (requestCode == Utility.RC_INVITE) {
				Log.d("friendHealthFHA", "Utility.RC_INVITE");
				Log.d("friendHealthFHA", "Showing toast and setting invite background color");
				response = Utility.mPrefs.getString("act_response", "");
				Bundle extras = data.getExtras();
				
				if (extras != null) {
					response = extras.getString("response");
					editor.putString("act_response", response);
					Toast.makeText(this, response,
							Toast.LENGTH_LONG).show();
				}
				
				btn_invite.setBackgroundColor(0xFF00FF00);
			} else if (requestCode == Utility.RC_ACTIVITYSUBMISSION) {
				Log.d("friendHealthFHA", "Utility.RC_ACTIVITYSUBMISSION");
				Intent output = new Intent();
				Bundle extras = new Bundle();
				extras.putString("result", "completed");
				extras.putInt("index", index);
				output.putExtras(extras);
				setResult(RESULT_OK, output);
				Log.d("friendHealthFHA", "Completing picture");
				finish();
			}
		}
	}
	
	public class ImageAdapter extends BaseAdapter {
		private Context context;
		
		public ImageAdapter(Context c) {
			context = c;
			//---setting the style---
			TypedArray a = obtainStyledAttributes(R.styleable.ActivityGallery);
			a.getResourceId(R.styleable.ActivityGallery_android_galleryItemBackground, 0);
			a.recycle();
		}
		
		//---returns the number of images---
		public int getCount() {
			return myRemoteImages.length;
		}
		
		//---returns the ID of an item---
		public Object getItem(int position) {
			return position;
		}
		
		//---returns the ID of an time---
		public long getItemId(int position) {
			return position;
		}
		
		//---returns an ImageView view---
		public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(this.context);
 
            try {
	            /* Open a new URL and get the InputStream to load data from it. */
	            URL aURL = new URL(myRemoteImages[position]);
	            URLConnection conn = aURL.openConnection();
	            conn.connect();
	            InputStream is = conn.getInputStream();
	            /* Buffered is always good for a performance plus. */
	            BufferedInputStream bis = new BufferedInputStream(is);
	            /* Decode url-data to a bitmap. */
	            Bitmap bm = BitmapFactory.decodeStream(bis);
	            bis.close();
	            is.close();
	            /* Apply the Bitmap to the ImageView that will be returned. */
	            i.setImageBitmap(bm);
            } catch (IOException e) {
            	Log.d("DEBUGTAG", "Remote Image Exception: ", e);
            }
	   
            /* Image should be scaled as width/height are set. */
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            /* Set the Width/Height of the ImageView. */
            i.setLayoutParams(new Gallery.LayoutParams(150, 150));
            return i;
        }
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	OptionsMenu om = new OptionsMenu();
    	om.CreateMenu(menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	OptionsMenu om = new OptionsMenu();
    	return om.MenuChoice(this, item);
    }

}
