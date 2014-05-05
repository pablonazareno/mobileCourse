package com.example.images;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageCache {

	private static ImageCache instance= null;
	private static Object look= new Object();
	private LruCache<String, Bitmap> memoryCache;
	
	private ImageCache(){
		super();
		int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
		memoryCache= new LruCache<String, Bitmap>(maxMemory/10){
			@Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            return bitmap.getByteCount() / 1024;
	        }
		};
	}
	
	public void addBitmapToCache(String key, Bitmap bitmap) {
	    if (getBitmapFromCache(key) == null) {
	        memoryCache.put(key, bitmap);
	    }
	}
	
	public Bitmap getBitmapFromCache(String key){
		 return memoryCache.get(key);
	}
	
	public static ImageCache getInstance(){
		if(instance==null){
			synchronized (look) {
				instance= new ImageCache();
			}
		}
		return instance;
	}
	
}
