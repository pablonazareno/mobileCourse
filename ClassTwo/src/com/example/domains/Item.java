package com.example.domains;

import java.io.Serializable;

public class Item implements Serializable{

	private static final long serialVersionUID = 1l;
	
	private String itemId;
	private String title;
	private String subTitle;
	private Double price;
	private String currency;
	private String thumbnailURL;
	private Integer soldQuantity;
	private String pictureURL;
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSoldQuantity(Integer soldQuantity) {
		this.soldQuantity = soldQuantity;
	}
	public Integer getSoldQuantity() {
		return soldQuantity;
	}
	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}
	public String getPictureURL() {
		return pictureURL;
	}
	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}
	public String getThumbnailURL() {
		return thumbnailURL;
	}
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder("Item(").append(itemId).append(')');
		return sb.toString();
	}
}
