package com.yoggo.dleandroidclient.json;

import com.google.gson.annotations.SerializedName;

public class GroupContainerJson {
	
	//public String number;
	
	@SerializedName("1")
	public GroupJson groupJson1;
	@SerializedName("2")
	public GroupJson groupJson2;
	@SerializedName("3")
	public GroupJson groupJson3;
	@SerializedName("4")
	public GroupJson groupJson4;
	@SerializedName("5")
	public GroupJson groupJson5;
	@SerializedName("6")
	public GroupJson groupJson6;
	
	public int size = 6;
}
