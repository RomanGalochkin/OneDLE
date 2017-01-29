package com.yoggo.dleandroidclient;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.yoggo.dleandroidclient.NavigationDrawerFragment.NavigationDrawerCallbacks;
import com.yoggo.dleandroidclient.account.AccountSettingsManager;
import com.yoggo.dleandroidclient.adapters.CategoriesAdapter;
import com.yoggo.dleandroidclient.database.DaoMaster;
import com.yoggo.dleandroidclient.database.DaoMaster.DevOpenHelper;
import com.yoggo.dleandroidclient.database.Categories;
import com.yoggo.dleandroidclient.database.CategoriesDao;
import com.yoggo.dleandroidclient.database.DaoSession;
import com.yoggo.dleandroidclient.database.DatabaseManager;
import com.yoggo.dleandroidclient.database.Groups;
import com.yoggo.dleandroidclient.database.GroupsDao;
import com.yoggo.dleandroidclient.fragments.CategoryFragment;
import com.yoggo.dleandroidclient.fragments.NewsFragment;
import com.yoggo.dleandroidclient.fragments.SettingsFragment;
import com.yoggo.dleandroidclient.interfaces.DrawerListUpdateCallback;
import com.yoggo.dleandroidclient.json.CategoryJson;
import com.yoggo.dleandroidclient.json.GroupJson;
import com.yoggo.dleandroidclient.json.UserJson;
import com.yoggo.dleandroidclient.requests.GetCategories;
import com.yoggo.dleandroidclient.requests.GetGroups;
import com.yoggo.dleandroidclient.requests.GetUser;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerCallbacks {

	public static final String NEWS_ITEM_TAG = "NEWS_ITEM_TAG";
	public static final String CATEGORIES_ITEM_TAG = "CATEGORIES_ITEM_TAG";

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private DatabaseManager dbManager;
	private AccountSettingsManager accountManager;

	// интерфейс для обновления меню
	private List<DrawerListUpdateCallback> listUpdateCallbacks;

	// текущий фрагмент
	private Fragment fragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Log.d("MainActivity", "onCreate");
		setContentView(R.layout.activity_main);

		listUpdateCallbacks = new ArrayList<DrawerListUpdateCallback>();
		fragment = NewsFragment.newInstance();
		listUpdateCallbacks.add(((NewsFragment) fragment));

		dbManager = new DatabaseManager(getApplicationContext());
		// подрезаем локальную БД до 20 записей
		dbManager.clearNews(20);
		accountManager = new AccountSettingsManager(getApplicationContext());
		// обновляем группы
		updateGroups();
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		listUpdateCallbacks.add(mNavigationDrawerFragment);
		mTitle = getTitle();

		// устанавливаем drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * FragmentManager fragmentManager = getSupportFragmentManager();
		 * fragmentManager.beginTransaction() .replace(R.id.container,
		 * NewsFragment.newInstance()) .commit();
		 */
	}

	@Override
	public void onNavigationDrawerItemSelected(int position, String tag) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		// в зависимости от статуса пользователь/админ показываем
		// соответствующее меню
		String[] navMenuTitles = getResources().getStringArray(
				R.array.nav_drawer_admin_items);

		if (tag == null || tag.equals(navMenuTitles[0])) {
			fragment = NewsFragment.newInstance();
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment).commit();
		} else if (tag != null && tag.equals(navMenuTitles[1])) {
			fragment = CategoryFragment.newInstance();
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment).commit();
		} else if (tag != null && tag.equals(navMenuTitles[2])) {
			fragment = SettingsFragment.newInstance();
			fragmentManager.beginTransaction()
					.replace(R.id.container, fragment).commit();
		}
	}

	public void updateGroups() {
		AccountSettingsManager account = new AccountSettingsManager(this);
		Callback<List<GroupJson>> callback = new Callback<List<GroupJson>>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(),
						"Ошибка при загрузке групп", Toast.LENGTH_SHORT).show();
				Log.d("ERROR", arg0.getMessage());
				// обновляем инфо о пользователе
				updateUserInfo();
			}

			@Override
			public void success(List<GroupJson> list, Response arg1) {
				if (list != null) {
					dbManager.insertToGroups(list);
				} else {
					Toast.makeText(getApplicationContext(),
							"Ошибка при загрузке групп", 200).show();
				}
				// обновляем инфо о пользователе
				updateUserInfo();

			}

		};
		new GetGroups(account.getSite(), account.getToken(), callback);

	}

	private void updateUserInfo() {
		Callback<UserJson> callback = new Callback<UserJson>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(),
						"Ошибка при загрузке пользователя", Toast.LENGTH_SHORT)
						.show();
				Log.d("ERROR", arg0.getMessage());
				// обновляем категории
				updateCategories();

			}

			@Override
			public void success(UserJson list, Response arg1) {
				if (list != null) {
					// устанавливаем в аккаунт
					accountManager.setUserGroup(list.userGroup);
					accountManager.setUserId(list.userId);
					// обновляем меню
					supportInvalidateOptionsMenu();
				} else {
					Toast.makeText(getApplicationContext(),
							"Ошибка при загрузке пользователя", 200).show();
				}
				// обновляем категории
				updateCategories();
			}

		};
		new GetUser(accountManager.getSite(), accountManager.getToken(),
				callback);
	}

	public void updateCategories() {
		Callback<List<CategoryJson>> callback = new Callback<List<CategoryJson>>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(),
						"Ошибка при загрузке категорий", Toast.LENGTH_SHORT)
						.show();
				Log.d("ERROR", arg0.getMessage());

			}

			@Override
			public void success(List<CategoryJson> list, Response arg1) {
				if (list != null) {
					dbManager.insertToCategories(list);
					for (DrawerListUpdateCallback callback : listUpdateCallbacks) {
						// дергаем интерфейс
						callback.updateList();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							"Ошибка при загрузке категорий", Toast.LENGTH_SHORT)
							.show();
				}

			}

		};
		new GetCategories(accountManager.getSite(), accountManager.getToken(),
				callback);
	}

	public void updateCategories(final SwipeRefreshLayout swipeLayout,
			final CategoriesAdapter categoriesAdapter) {
		Callback<List<CategoryJson>> callback = new Callback<List<CategoryJson>>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(),
						"Ошибка при загрузке категорий", Toast.LENGTH_SHORT)
						.show();
				Log.d("ERROR", arg0.getMessage());

			}

			@Override
			public void success(List<CategoryJson> list, Response arg1) {
				if (list != null) {
					dbManager.insertToCategories(list);
					for (DrawerListUpdateCallback callback : listUpdateCallbacks) {
						callback.updateList();
					}

					if (swipeLayout.isShown()) {
						swipeLayout.setRefreshing(false);
					}
					DaoMaster daoMaster;
					DaoSession daoSession;
					SQLiteDatabase db;
					DevOpenHelper helper = new DaoMaster.DevOpenHelper(
							getApplicationContext(), DatabaseManager.DATABASE_NAME, null);
					db = helper.getWritableDatabase();
					daoMaster = new DaoMaster(db);
					daoSession = daoMaster.newSession();
					CategoriesDao comDao = daoSession.getCategoriesDao();
					try {
						List<Categories> coms = comDao.queryBuilder()
								.orderDesc(CategoriesDao.Properties.Id).list();
						//обновляем лист
						categoriesAdapter.setList(coms);
						categoriesAdapter.notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							"Ошибка при загрузке категорий", Toast.LENGTH_SHORT)
							.show();
				}

			}

		};
		 new GetCategories(accountManager.getSite(),
				 accountManager.getToken(), callback);
	}

	public void onSectionAttached(int number) {
		// выбираем заголовок в зависимости от выбранного пункт меню
		switch (number) {
		case NewsFragment.NEWS_FRAGMENT:
			mTitle = getString(R.string.title_section_news);
			break;
		case CategoryFragment.CATEGORY_FRAGMENT:
			mTitle = getString(R.string.title_section_categories);
			break;
		case SettingsFragment.SETTINGS_FRAGMENT:
			mTitle = getString(R.string.title_section_settings);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			//в зависимости от фрагмента разворачиваем меню
			if (fragment instanceof NewsFragment) {
				if (accountManager.isUserCanAddNews()) {
					getMenuInflater().inflate(R.menu.menu_add_news, menu);
				}
			} else if (fragment instanceof CategoryFragment) {
				if (accountManager.isUserAdminCategories()) {
					getMenuInflater().inflate(R.menu.menu_add_category, menu);
				}
			}
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_logout) {
			//выход из аккаунта
			Intent intent = new Intent(MainActivity.this,
					AuthorizationActivity.class);
			AccountSettingsManager account = new AccountSettingsManager(
					getApplicationContext());
			account.setToken("");
			account.setAccountLang("");
			account.setAccountSiteRu("");
			getApplicationContext().deleteDatabase("onedle.db");
			startActivity(intent);
			finish();
			return true;
		} else if (id == R.id.action_add_news) {
			Intent intent = new Intent(MainActivity.this, AddNewsActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.action_add_category) {
			Intent intent = new Intent(MainActivity.this,
					AddCategoryActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
