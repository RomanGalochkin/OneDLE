package com.yoggo.dleandroidclient.json;

import com.google.gson.annotations.SerializedName;

public class CategoryJson {
	@SerializedName("id")
	public String id;
	
	@SerializedName("name")
	public String name;
	
	@SerializedName("alt_name")
	public String altName;
	
	@SerializedName("parentid")
	public String parentId;
}
