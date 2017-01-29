package com.yoggo.dleandroidclient.serverapi;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.yoggo.dleandroidclient.json.AddCategoryJson;
import com.yoggo.dleandroidclient.json.AddCommentsJson;
import com.yoggo.dleandroidclient.json.AddNewsJson;
import com.yoggo.dleandroidclient.json.CategoryContainerJson;
import com.yoggo.dleandroidclient.json.CategoryJson;
import com.yoggo.dleandroidclient.json.CommentJson;
import com.yoggo.dleandroidclient.json.GroupContainerJson;
import com.yoggo.dleandroidclient.json.GroupJson;
import com.yoggo.dleandroidclient.json.GroupMainContainer;
import com.yoggo.dleandroidclient.json.NewsJson;
import com.yoggo.dleandroidclient.json.TokenJson;
import com.yoggo.dleandroidclient.json.UserJson;

public interface OneDLEApi {

	public static final String mobileKey = "bIENnium5gs";
	
	@GET("/engine/onedle.php")
	public void getToken(@Query("login") String login, @Query("password") String password,
			@Query("android_passw") String androidPassw, Callback<TokenJson> response);
	
	@GET("/engine/onedle.php")
	public void getNews(@Query("token") String token, @Query("action") String action,
			@Query("column_order") String columnOrder,
			@Query("order") String order,
			@Query("start") String start,
			@Query("limit") String limit,
			@Query("columns") String columns,
			Callback<List<NewsJson>> response);
	
	@GET("/engine/onedle.php")
	public void getOneNews(@Query("token") String token, @Query("action") String action,
			@Query("news_id") String newsId,
			@Query("columns") String columns,
			Callback<List<NewsJson>> response);
	
	@GET("/engine/onedle.php")
	public void getCats(@Query("token") String token,
			@Query("action") String action,
			Callback<List<CategoryJson>> response);
	
	@GET("/engine/onedle.php")
	public void getGroups(@Query("token") String token,
			@Query("action") String action,
			Callback<List<GroupJson>>  response);
	
	@GET("/engine/onedle.php")
	public void getUser(@Query("token") String token,
			@Query("action") String action,
			@Query("columns") String columns,
			@Query("user_id") String userId,
			Callback<UserJson>  response);
	
	@POST("/engine/onedle.php")
	public void addNews(@Query("token") String token,
			@Query("action") String action,
			@Query("short_story") String shortStory,
			@Query("full_story") String fullStory,
			@Query("catlist") String catList,
			@Query("title") String title,
			Callback<AddNewsJson>  response);
	
	@POST("/engine/onedle.php")
	public void editNews(@Query("token") String token,
			@Query("action") String action,
			@Query("id") String id,
			@Query("short_story") String shortStory,
			@Query("full_story") String fullStory,
			@Query("catlist") String catList,
			@Query("title") String title,
			Callback<AddNewsJson>  response);
	
	@POST("/engine/onedle.php")
	public void deleteNews(@Query("token") String token,
			@Query("action") String action,
			@Query("id") String id,
			Callback<AddNewsJson>  response);
	
	@POST("/engine/onedle.php")
	public void addComments(@Query("token") String token,
			@Query("action") String action,
			@Query("post_id") String postId,
			@Query("comments") String comments,
			Callback<AddCommentsJson>  response);
	
	@POST("/engine/onedle.php")
	public void editComments(@Query("token") String token,
			@Query("action") String action,
			@Query("id") String postId,
			@Query("comm_txt") String comments,
			Callback<AddCommentsJson>  response);
	
	@POST("/engine/onedle.php")
	public void deleteComments(@Query("token") String token,
			@Query("action") String action,
			@Query("id") String postId,
			Callback<AddCommentsJson>  response);
	
	@POST("/engine/onedle.php")
	public void addCategory(@Query("token") String token,
			@Query("action") String action,
			@Query("parentid") String parentId,
			@Query("cat_name") String catName,
			@Query("alt_cat_name") String catAltName,
			Callback<AddCategoryJson> response);
	
	@POST("/engine/onedle.php")
	public void editCategory(@Query("token") String token,
			@Query("action") String action,
			@Query("parentid") String parentId,
			@Query("catid") String catid,
			@Query("cat_name") String catName,
			@Query("alt_cat_name") String catAltName,
			Callback<AddCategoryJson> response);
	
	@POST("/engine/onedle.php")
	public void deleteCategory(@Query("token") String token,
			@Query("action") String action,
			@Query("catid") String catid,
			Callback<AddCategoryJson> response);
	
	@GET("/engine/onedle.php")
	public void getComments(@Query("token") String token, @Query("news_id") String newsId,
			@Query("action") String action,
			@Query("column_order") String columnOrder,
			@Query("order") String order,
			@Query("start") String start,
			@Query("limit") String limit,
			@Query("columns") String columns,
			Callback<List<CommentJson>> response);
}
