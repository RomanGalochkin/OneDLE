package com.yoggo.dleandroidclient.json;

import com.google.gson.annotations.SerializedName;

public class AddNewsJson {
	@SerializedName("result")
	public String result;
	
	@SerializedName("news_id")
	public String newsId;
	
	@SerializedName("moderation")
	public String moderation;
	
	@SerializedName("error")
	public String error;
}
