package com.yeksefr.khayam;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {
	AsyncTask<?, ?, ?> splashDelay;

	ImageView imageView;
	RelativeLayout layout;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		layout = (RelativeLayout) findViewById(R.id.container);
		imageView = (ImageView) findViewById(R.id.logo);
		anim();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		splashDelay = new SplashScreenDelay().execute(2);
	}

	private class SplashScreenDelay extends
			AsyncTask<Integer, Integer, Integer> {

		boolean success;
		@Override
		protected Integer doInBackground(Integer... params) {
			long t1 = SystemClock.elapsedRealtime();
			long t2;
			
			publishProgress(1);
			do {
				t2 = SystemClock.elapsedRealtime();
			} while (t2 - t1 < 500);
//			publishProgress(2);
//			t1 = SystemClock.elapsedRealtime();
//			do {
//				t2 = SystemClock.elapsedRealtime();
//			} while (t2 - t1 < 1000);
			return Integer.valueOf(0);
		}

		@Override
		protected void onPostExecute(Integer result) {
			next(success);
		}

		protected void onCancelled() {
//			next();
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values[0] == 1)
			{
				imageView.setImageResource(R.drawable.splash_formula);
//				imageView.setImageBitmap(Theme.images[Theme.IMG_YEKSEFR]);
			} else if (values[0] == 2)
			{
				layout.setBackgroundResource(R.color.white);
				imageView.startAnimation(fadeOut);
			}
		}
	}
	
	Animation fadeIn;
	Animation fadeOut;
	private void anim()
	{
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
		fadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
		fadeOut.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				imageView.setImageResource(R.drawable.splash);
				imageView.startAnimation(fadeIn);
			}
		});
	}
	
	private void next(boolean success)
	{
		startActivity(new Intent(getApplicationContext(), AllInOneActivity.class));
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
}