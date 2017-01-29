package com.yoggo.dleandroidclient.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.provider.BaseColumns;
import android.util.Log;

import com.yoggo.dleandroidclient.database.DaoMaster.DevOpenHelper;
import com.yoggo.dleandroidclient.json.CategoryJson;
import com.yoggo.dleandroidclient.json.CommentJson;
import com.yoggo.dleandroidclient.json.GroupContainerJson;
import com.yoggo.dleandroidclient.json.GroupJson;
import com.yoggo.dleandroidclient.json.NewsJson;

public class DatabaseManager {

	// название БД
	public static final String DATABASE_NAME = "onedle.db";

	private Context context;

	private SQLiteDatabase db;
	DevOpenHelper helper;
	private DaoMaster daoMaster;
	private DaoSession daoSession;

	public DatabaseManager(Context context) {
		this.context = context;
		initDb();
	}

	/*
	 * Инициализируем БД
	 */
	private void initDb() {
		helper = new DaoMaster.DevOpenHelper(context, "onedle.db", null);
		if (db == null) {
			db = helper.getWritableDatabase();
		} else {
			if (!db.isOpen()) {
				db = helper.getWritableDatabase();
			}
		}
	}

	/*
	 * Закрываем БД
	 */
	private void closeDb() {
		if (db != null) {
			db.close();
		}
	}

	public void insertToNews(List<NewsJson> list) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		NewsDao newsDao = daoSession.getNewsDao();
		for (NewsJson newsJson : list) {
			News news = newsDao.load(Long.valueOf(newsJson.newsId));
			if (news == null) {
				news = new News(Long.valueOf(newsJson.newsId));
			}
			news.setAuthor(newsJson.autor);
			news.setDate(newsJson.date);
			news.setTitle(escapeCharacters(newsJson.title));
			news.setShortStory(escapeCharacters(newsJson.shortStory));
			news.setCategory(escapeCharacters(newsJson.category));
			newsDao.insertOrReplace(news);
		}
		closeDb();
	}

	private String escapeCharacters(String characters) {
		return characters.replace("\\", "").replace("\"", "");
	}

	public void clearNews(int limit) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		NewsDao newsDao = daoSession.getNewsDao();
		List<News> newsToDelete = null;
		if (newsDao.count() - limit > 1) {
			newsToDelete = newsDao.queryBuilder()
					.orderDesc(NewsDao.Properties.Date).limit(limit).list();
			newsDao.deleteAll();
			newsDao.insertOrReplaceInTx(newsToDelete);
		}
		closeDb();
	}

	public void deleteNewsFromLocalDB(Long id) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		NewsDao newsDao = daoSession.getNewsDao();
		newsDao.deleteByKey(id);
		closeDb();
	}

	public void updateNewsFromLocalDB(Long id, String title, String shortNews,
			String fullNews) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		NewsDao newsDao = daoSession.getNewsDao();
		News news = newsDao.load(id);
		news.setTitle(title);
		news.setShortStory(shortNews);
		news.setFullStory(fullNews);
		newsDao.insertOrReplace(news);
		closeDb();
	}

	public void deleteCommentsFromLocalDB(Long id) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		CommentariesDao commentariesDao = daoSession.getCommentariesDao();
		commentariesDao.deleteByKey(id);
		closeDb();
	}

	public void deleteCategoryFromLocalDB(Long id) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		CategoriesDao commentariesDao = daoSession.getCategoriesDao();
		commentariesDao.deleteByKey(id);
		closeDb();
	}

	public String getCategoryNameById(String id) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		CategoriesDao categoriesDao = daoSession.getCategoriesDao();
		Categories cat = null;
		try {
			cat = categoriesDao.load(Long.valueOf(id));
		} catch (Exception e) {
			String first = id.substring(0, 1);
			cat = categoriesDao.load(Long.valueOf(first));
		}

		closeDb();
		if (cat != null) {
			return cat.getName();
		} else {
			return "";
		}

	}

	public void insertToNews(NewsJson newsJson) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		NewsDao newsDao = daoSession.getNewsDao();
		News news = newsDao.load(Long.valueOf(newsJson.newsId));
		news.setAuthor(newsJson.autor);
		news.setDate(newsJson.date);
		news.setTitle(newsJson.title);
		news.setShortStory(newsJson.shortStory);
		news.setFullStory(newsJson.fullStory);
		news.setNewsRead(newsJson.newsRead);
		news.setCommNum(newsJson.commNum);
		news.setRating(newsJson.rating);
		news.setUserId(newsJson.userId);
		newsDao.insertOrReplace(news);
		closeDb();
	}

	private boolean getBool(String boolString) {
		if (boolString.equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	public void insertToGroups(List<GroupJson> list) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		GroupsDao groupsDao = daoSession.getGroupsDao();
		for (GroupJson groupJson : list) {
			Groups groups = new Groups(Long.valueOf(groupJson.id));
			groups.setGroupName(groupJson.groupName);
			groups.setAllowAddNews(getBool(groupJson.allowAdds));
			groups.setAllowAddCommentary(getBool(groupJson.allowAddc));
			groups.setAllowEditCommentary(getBool(groupJson.allowEditc));
			groups.setAllowDeleteCommentary(getBool(groupJson.allowDelc));
			groups.setAllowEditAllCommentary(getBool(groupJson.editAllc));
			groups.setAllowDeleteAllCommentary(getBool(groupJson.delAllc));
			groups.setAdminCategories(getBool(groupJson.adminCategories));
			groups.setCatAllowAddNews(groupJson.catAllowAddNews);
			groupsDao.insertOrReplace(groups);
		}
		closeDb();
	}

	public void insertToCategories(List<CategoryJson> list) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		CategoriesDao categoriesDao = daoSession.getCategoriesDao();
		categoriesDao.deleteAll();
		for (CategoryJson categoryJson : list) {
			Categories category = new Categories(Long.valueOf(categoryJson.id));
			category.setName(categoryJson.name);
			category.setAltName(categoryJson.altName);
			category.setParentId(categoryJson.parentId);
			categoriesDao.insertOrReplace(category);
		}
		closeDb();

	}

	public void insertToCategories(CategoryJson categoryJson) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		CategoriesDao categoriesDao = daoSession.getCategoriesDao();
		Categories category = new Categories(Long.valueOf(categoryJson.id));
		category.setName(categoryJson.name);
		categoriesDao.insertOrReplace(category);
		closeDb();
	}

	public void insertToGroups(GroupJson groupJson) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		GroupsDao groupsDao = daoSession.getGroupsDao();
		Groups groups = new Groups(Long.valueOf(groupJson.id));
		groups.setGroupName(groupJson.groupName);
		groups.setAllowAddNews(getBool(groupJson.allowAdds));
		groups.setAllowAddCommentary(getBool(groupJson.allowAddc));
		groups.setAllowEditCommentary(getBool(groupJson.allowEditc));
		groups.setAllowDeleteCommentary(getBool(groupJson.allowDelc));
		groups.setAllowEditAllCommentary(getBool(groupJson.editAllc));
		groups.setAllowDeleteAllCommentary(getBool(groupJson.delAllc));
		groups.setCatAllowAddNews(groupJson.catAllowAddNews);
		groupsDao.insertOrReplace(groups);
		closeDb();
	}

	public void insertToComments(List<CommentJson> list, String newsId) {
		initDb();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		CommentariesDao comDao = daoSession.getCommentariesDao();

		for (CommentJson comment : list) {
			Commentaries com = new Commentaries(Long.valueOf(comment.id));
			com.setNewsId(Long.valueOf(newsId));
			com.setContent(comment.text);
			com.setAuthor(comment.autor);
			com.setDate(comment.date);
			comDao.insertOrReplace(com);
		}
		closeDb();
	}

}
