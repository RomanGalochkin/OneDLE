package com.yoggo.dleandroidclient.account;

import com.yoggo.dleandroidclient.database.DaoMaster;
import com.yoggo.dleandroidclient.database.DaoSession;
import com.yoggo.dleandroidclient.database.Groups;
import com.yoggo.dleandroidclient.database.GroupsDao;
import com.yoggo.dleandroidclient.database.DaoMaster.DevOpenHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;

public class AccountSettingsManager {
	
	//константы для работы с sharedPreferences
	public static final String ACCOUNT_SETTINGS = "DLEOnePreferences";
	public static final String ACCOUNT_TOKEN = "Token";
	public static final String ACCOUNT_NAME = "Name";
	public static final String ACCOUNT_SITE = "Site";
	public static final String ACCOUNT_GROUP_NUMBER = "GroupNumber";
	public static final String ACCOUNT_GROUP_NAME = "GroupName";
	public static final String ACCOUNT_USER_ID = "UserId";
	public static final String ACCOUT_USER_GROUP = "UserGroup";
	
	public static final String ACCOUNT_LANG = "Lang";
	public static final String ACCOUNT_SITE_RU = "SiteRu";
	
	public static final String ACCOUNT_EDIT_ALL_COMENTARY = "EditAllComentary";
	public static final String ACCOUNT_DELETE_ALL_COMENTARY = "DeleteAllComentary";
	
	private Context context;
	private SharedPreferences settings;
	
	SQLiteDatabase db;
	DaoMaster daoMaster;
    DaoSession daoSession;
	DevOpenHelper helper;
	
	private Editor editor;

	
	public AccountSettingsManager(Context context){
		this.context = context;
		settings = context.getSharedPreferences(ACCOUNT_SETTINGS, Context.MODE_PRIVATE);
		editor = settings.edit();
		helper = new DaoMaster.DevOpenHelper(context, "onedle.db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
	}
	
	public String getToken(){
		String token = "";
		if(settings.contains(ACCOUNT_TOKEN)){
			token = settings.getString(ACCOUNT_TOKEN, "");
		}
		return token;
	}
	
	public String getName(){
		String name = "";
		if(settings.contains(ACCOUNT_NAME)){
			name = settings.getString(ACCOUNT_NAME, "");
		}
		return name;
	}
	
	public String getSite(){
		String site = "";
		if(settings.contains(ACCOUNT_SITE)){
			site = settings.getString(ACCOUNT_SITE, "");
		}
		return site;
	}
	
	public String getUserGroup(){
		String group = "";
		if(settings.contains(ACCOUT_USER_GROUP)){
			group = settings.getString(ACCOUT_USER_GROUP, "");
		}
		return group;
	}
	
	public String getUserId(){
		String user = "";
		if(settings.contains(ACCOUNT_USER_ID)){
			user = settings.getString(ACCOUNT_USER_ID, "");
		}
		return user;
	}
	
	
	public String getAccountEditAllComentary() {
		String editAllComments = "";
		if(settings.contains(ACCOUNT_EDIT_ALL_COMENTARY)){
			editAllComments = settings.getString(ACCOUNT_EDIT_ALL_COMENTARY, "");
		}
		return editAllComments;
	}
	
	public String getAccountDeleteAllComentary() {
		String deleteAllComments = "";
		if(settings.contains(ACCOUNT_DELETE_ALL_COMENTARY)){
			deleteAllComments = settings.getString(ACCOUNT_DELETE_ALL_COMENTARY, "");
		}
		return deleteAllComments;
	}
	
	public String getAccountLang() {
		String accountLang = "";
		if(settings.contains(ACCOUNT_LANG)){
			accountLang = settings.getString(ACCOUNT_LANG, "");
		}
		return accountLang;
	}
	
	public String getAccountSiteRu() {
		String siteRu = "";
		if(settings.contains(ACCOUNT_SITE_RU)){
			siteRu = settings.getString(ACCOUNT_SITE_RU, "");
		}
		return siteRu;
	}
	
	public void setToken(String token){
		editor.putString(ACCOUNT_TOKEN, token);
		editor.commit();
	}
	
	public void setName(String name){
		editor.putString(ACCOUNT_NAME, name);
		editor.commit();
	}
	
	public void setSite(String site){
		editor.putString(ACCOUNT_SITE, site);
		editor.commit();
	}
	
	public void setUserId(String id){
		editor.putString(ACCOUNT_USER_ID, id);
		editor.commit();
	}
	
	public void setUserGroup(String group){
		editor.putString(ACCOUT_USER_GROUP, group);
		editor.commit();
	}
	
	
	public void setEditAllComentary(String editAllCommentary){
		editor.putString(ACCOUNT_EDIT_ALL_COMENTARY, editAllCommentary);
		editor.commit();
	}
	
	public void setDeleteAllComentary(String deleteAllComentary){
		editor.putString(ACCOUNT_DELETE_ALL_COMENTARY, deleteAllComentary);
		editor.commit();
	}
	
	public void setAccountLang(String accountLang){
		editor.putString(ACCOUNT_LANG, accountLang);
		editor.commit();
	}
	
	public void setAccountSiteRu(String siteRu){
		editor.putString(ACCOUNT_SITE_RU, siteRu);
		editor.commit();
	}
	
	public boolean isUserCanAddNews(){
		GroupsDao groupsDao = daoSession.getGroupsDao();
		Groups groups = groupsDao.load(Long.valueOf(getUserGroup()));
		if(groups != null){
			return groups.getAllowAddNews();
		}else{
			return false;
		}
	}
	
	public boolean isAdmin(){
		GroupsDao groupsDao = daoSession.getGroupsDao();
		Groups groups = groupsDao.load(Long.valueOf(getUserGroup()));
		if(groups.getId() == 1){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isUserAdminCategories(){
		GroupsDao groupsDao = daoSession.getGroupsDao();
		Groups groups = groupsDao.load(Long.valueOf(getUserGroup()));
		if(groups != null){
			return groups.getAdminCategories();
		}else{
			return false;
		}
	}
	
	public boolean isUserCanAllEdit(){
		GroupsDao groupsDao = daoSession.getGroupsDao();
		Groups groups = groupsDao.load(Long.valueOf(getUserGroup()));
		if(groups != null){
			return groups.getAllowEditAllCommentary();
		}else{
			return false;
		}
	}
	
	public boolean isUserCanAddComments(){
		GroupsDao groupsDao = daoSession.getGroupsDao();
		Groups groups = groupsDao.load(Long.valueOf(getUserGroup()));
		if(groups != null){
			return groups.getAllowAddCommentary();
		}else{
			return false;
		}
	}
	

	
}
