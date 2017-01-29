package com.yoggo.dleandroidclient.json;

import com.google.gson.annotations.SerializedName;

public class NewsJson {
	@SerializedName("news_id")
	public String newsId;
	
	@SerializedName("title")
	public String title;
	
	@SerializedName("short_story")
	public String shortStory;
	
	@SerializedName("full_story")
	public String fullStory;
	
	@SerializedName("date")
	public String date;
	
	@SerializedName("category")
	public String category;
	
	@SerializedName("autor")
	public String autor;
	
	@SerializedName("rating")
	public String rating;
	
	@SerializedName("comm_num")
	public String commNum;
	
	@SerializedName("news_read")
	public String newsRead;
	
	@SerializedName("user_id")
	public String userId;
	
}
