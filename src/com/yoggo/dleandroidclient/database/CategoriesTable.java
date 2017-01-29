package com.yoggo.dleandroidclient.database;

import android.provider.BaseColumns;

public class CategoriesTable {
	public static final String TABLE_NAME= "categories";
	public static final String TITLE_COLUMN = "title";
	public static final String CONTENT_COLUMN = "content";
	public static final String DATE_COLUMN = "date";
	
	

	
	public static String createTable(){
		return "CREATE TABLE "
				+ TABLE_NAME + " (" + BaseColumns._ID 
				+ " integer primary key autoincrement, " + TITLE_COLUMN
				+ " text not null, " + CONTENT_COLUMN + " text, " + DATE_COLUMN 
				+ " text not null);";
	}
	
	public static String getCount(){
		return "SELECT COUNT(*) FROM " + TABLE_NAME;
	}
	
	public static String getAll(){
		return "SELECT * FROM " + TABLE_NAME;
	}
}
