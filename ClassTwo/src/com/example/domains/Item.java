package com.example.domains;

public class Item {

	private String itemId;
	private String title;
	private Double price;
	private String currency;
	
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
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder(title);
		sb.append('(').append(price).append(currency).append(')');
		return sb.toString();
	}
}
