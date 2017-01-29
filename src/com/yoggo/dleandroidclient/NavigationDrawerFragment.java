package com.yoggo.dleandroidclient;

import java.net.IDN;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.yoggo.dleandroidclient.account.AccountSettingsManager;
import com.yoggo.dleandroidclient.adapters.NavDrawerListAdapter;
import com.yoggo.dleandroidclient.interfaces.DrawerListUpdateCallback;

public class NavigationDrawerFragment extends Fragment implements
		DrawerListUpdateCallback {

	/**
	 * Запоминаем позицию выбранного элемента
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * 
	 * Показываем drawer при запуске (guideline)
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * Ссылка на текущий callbacks (Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;

	private ArrayList<NavDrawerItem> items;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private AccountSettingsManager accountManager;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		accountManager = new AccountSettingsManager(getActivity()
				.getApplicationContext());

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}

		// Выбираем дефолтный item(0) или последний выбранный
		selectItem(mCurrentSelectedPosition, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/*
	 * Определяем кодировку сайта
	 */
	@SuppressLint("NewApi")
	private String siteEncoding(AccountSettingsManager accountManager) {
		if (accountManager.getAccountLang().equals("RU")) {
			return accountManager.getAccountSiteRu();
		} else {
			return accountManager.getSite();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		AccountSettingsManager accountManager = new AccountSettingsManager(
				getActivity().getApplicationContext());
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.my_drawer,
				null);
		// устанавливаем UserBox
		((TextView) view.findViewById(R.id.user_textView))
				.setText(accountManager.getName());
		((TextView) view.findViewById(R.id.site_textView))
				.setText(siteEncoding(accountManager));
		((LinearLayout) view.findViewById(R.id.userBoxLayout))
				.setOnClickListener(null);
		mDrawerListView = (ListView) view.findViewById(R.id.listview);
		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position, view.getTag().toString());
					}
				});
		setDrawerMenu();
		return view;
	}

	private void setDrawerMenu() {
		if (accountManager != null) {
			// получаем массив иконок для пунктов меню
			navMenuIcons = getResources().obtainTypedArray(
					R.array.nav_drawer_icons);
			// инициализируем массив с пунктами меню
			items = new ArrayList<NavDrawerItem>();
			// если id = 1, то админ
			if (accountManager.getUserId().equals("1")) {
				navMenuTitles = getResources().getStringArray(
						R.array.nav_drawer_admin_items);
				items.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
						.getResourceId(0, -1)));
				items.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
						.getResourceId(1, -1)));
			} else {// если не админ
				navMenuTitles = getResources().getStringArray(
						R.array.nav_drawer_admin_items);
				items.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
						.getResourceId(0, -1)));
			}
			items.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
					.getResourceId(2, -1)));
			// инициализируем адаптер
			NavDrawerListAdapter adapter = new NavDrawerListAdapter(
					getActivity(), items);
			mDrawerListView.setAdapter(adapter);
			mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		}
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.commit();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}
		};

		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(int position, String tag) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			if (tag != null) {
				mCallbacks.onNavigationDrawerItemSelected(position, tag);
			} else {
				mCallbacks.onNavigationDrawerItemSelected(position, null);
			}

		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	public static interface NavigationDrawerCallbacks {

		void onNavigationDrawerItemSelected(int position, String tag);
	}

	public void setDrawerListUpdateCallback(DrawerListUpdateCallback callback) {

	}

	@Override
	public void updateList() {
		if (mDrawerListView != null) {
			setDrawerMenu();
			mDrawerListView.invalidateViews();
		} else {
			Log.e(NavigationDrawerFragment.class.getName(),
					"DrawerListView is null");
		}

	}
}
