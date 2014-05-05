package com.example.adapters;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.classtwo.R;
import com.example.domains.Item;
import com.example.images.AsyncDrawable;
import com.example.images.BitmapWorkerTask;
import com.example.images.ImageCache;

public class ItemListAdapter extends ArrayAdapter<Item> {

	private ImageCache cache;
	private Resources resources;

	public ItemListAdapter(Context context, int resource,
			List<Item> objects) {
		super(context, resource, objects);
		this.resources = context.getResources();
		cache = ImageCache.getInstance();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Item item = this.getItem(position);
		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.item_view, null);

			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.price = (TextView) convertView.findViewById(R.id.price);
			holder.image = (ImageView) convertView.findViewById(R.id.picture);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.title.setText(item.getTitle());
		holder.price.setText("$" + item.getPrice());
		loadBitmap(item.getThumbnailURL(), holder.image);
		return convertView;
	}

	public void loadBitmap(String imageUrl, ImageView imageView) {
		Bitmap bitmap = cache.getBitmapFromCache(imageUrl);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			if (cancelPotentialWork(imageUrl, imageView)) {
				Bitmap nopic = BitmapFactory.decodeResource(resources,R.drawable.nopic);
				BitmapWorkerTask task = new BitmapWorkerTask(imageView);
				BitmapDrawable drawable= new AsyncDrawable(resources,nopic, task);
				imageView.setImageDrawable(drawable);
				task.execute(imageUrl);
			}
		}
	}

	public boolean cancelPotentialWork(String imageUrl, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			String bitmapUrl = bitmapWorkerTask.getImageUrl();
			Log.d("Adapter","cancel "+ bitmapUrl + " - " + imageUrl );
			if (imageUrl.equals(bitmapUrl)) {
				return false;
			} else {
				bitmapWorkerTask.cancel(true);
			}
		}
		return true;
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	private static class ViewHolder {
		public TextView title;
		public TextView price;
		public ImageView image;
	}
}
