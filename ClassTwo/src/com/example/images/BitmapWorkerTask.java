package com.example.images;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Logger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	private final WeakReference<ImageView> imageViewReference;
	private ImageCache cache;
	private String imageUrl;

	public BitmapWorkerTask(ImageView imageView) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		cache= ImageCache.getInstance();
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitMap = null;
		imageUrl = params[0];
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.connect();
			InputStream stream = conn.getInputStream();

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			bitMap = BitmapFactory.decodeStream(stream, null, options);
			cache.addBitmapToCache(imageUrl, bitMap);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitMap;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		Log.d("AsyncTask","onPost "+ isCancelled());
		if (isCancelled()) {
			bitmap = null;
		}
		Log.d("AsyncTask","onPost "+ imageViewReference + " - " + bitmap );
		if (imageViewReference != null && bitmap != null) {
			ImageView imageView = imageViewReference.get();
			BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
			Log.d("AsyncTask","onPost "+ bitmapWorkerTask + " - " + imageView );
			if (this == bitmapWorkerTask && imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}
}