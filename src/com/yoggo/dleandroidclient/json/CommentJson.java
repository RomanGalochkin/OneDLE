package com.yoggo.dleandroidclient.json;

import com.google.gson.annotations.SerializedName;

public class CommentJson {
	@SerializedName("id")
	public String id;
	
	@SerializedName("text")
	public String text;
	
	@SerializedName("autor")
	public String autor;
	
	@SerializedName("date")
	public String date;
	
	@SerializedName("user_id")
	public String userId;
}
