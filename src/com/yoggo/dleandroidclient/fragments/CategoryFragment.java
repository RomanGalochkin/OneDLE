package com.yoggo.dleandroidclient.fragments;

import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yoggo.dleandroidclient.FullNewsActivity;
import com.yoggo.dleandroidclient.MainActivity;
import com.yoggo.dleandroidclient.R;
import com.yoggo.dleandroidclient.NavigationDrawerFragment.NavigationDrawerCallbacks;
import com.yoggo.dleandroidclient.adapters.CategoriesAdapter;
import com.yoggo.dleandroidclient.database.Categories;
import com.yoggo.dleandroidclient.database.CategoriesDao;
import com.yoggo.dleandroidclient.database.DaoMaster;
import com.yoggo.dleandroidclient.database.DaoMaster.DevOpenHelper;
import com.yoggo.dleandroidclient.database.DaoSession;
import com.yoggo.dleandroidclient.database.DatabaseManager;
import com.yoggo.dleandroidclient.interfaces.OldNewsLoadListener;

public class CategoryFragment extends Fragment implements OnRefreshListener,
OldNewsLoadListener{
	
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String COMMENTS_TAG = "comments_tag";
	private static final String OPEN_COMMENTS_TAG = "comments_tag";
	public static final int CATEGORY_FRAGMENT = 2;
	
	private ListView newsListView;
	private CategoriesAdapter categoriesAdapter;
	private DatabaseManager dbManager;
	private static SwipeRefreshLayout swipeLayout;
	
	private DaoMaster daoMaster;
	private static DaoSession daoSession;
	private SQLiteDatabase db;
	
	
	public static CategoryFragment newInstance() {
		CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, CATEGORY_FRAGMENT);
        fragment.setArguments(args);
        return fragment;
    }

    public CategoryFragment() {
    }
	
	 @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_category, container, false);

         dbManager = new DatabaseManager(getActivity());
 		
 		 DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "onedle.db", null);
         db = helper.getWritableDatabase();
         daoMaster = new DaoMaster(db);
         daoSession = daoMaster.newSession();
 		
 		 CategoriesDao catDao = daoSession.getCategoriesDao();
 		
 		 List<Categories> cats = catDao.queryBuilder().orderDesc(CategoriesDao.Properties.Id).list();
         
         categoriesAdapter = new CategoriesAdapter(getActivity(), cats);
         newsListView = (ListView) rootView.findViewById(R.id.newsListView);
         newsListView.setAdapter(categoriesAdapter);

         swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.category_refresh);
 		swipeLayout.setOnRefreshListener(this);
 		swipeLayout.setProgressViewOffset(false, FullNewsActivity.dpToPx(35),
 				FullNewsActivity.dpToPx(90));
         newsListView.setOnScrollListener(new OnScrollListener() {
 			int mLastFirstVisibleItem = 0;

 			@Override
 			public void onScrollStateChanged(AbsListView view, int scrollState) {
 			}
 			
 			//при скролле пр€чем скроллбар
 			public void onScroll(AbsListView view, int firstVisibleItem,
 					int visibleItemCount, int totalItemCount) {
 				if (view.getId() == newsListView.getId()) {
 					ActionBarActivity aba = (ActionBarActivity) getActivity();
 					ActionBar ab = aba.getSupportActionBar();
 					int currentFirstVisibleItem = newsListView
 							.getFirstVisiblePosition() - 1;

 					if (currentFirstVisibleItem > mLastFirstVisibleItem
 							&& ab.isShowing()) {
 						// getSherlockActivity().getSupportActionBar().hide();
 						ab.hide();

 					} else if (currentFirstVisibleItem < mLastFirstVisibleItem
 							&& !ab.isShowing()) {
 						// getSherlockActivity().getSupportActionBar().show();
 						ab.show();
 					}

 					boolean enable = false;
 					if (newsListView != null
 							&& newsListView.getChildCount() > 0) {
 						boolean firstItemVisible = newsListView
 								.getFirstVisiblePosition() == 0;
 						boolean topOfFirstItemVisible = newsListView
 								.getChildAt(0).getTop() == 0;
 						enable = firstItemVisible && topOfFirstItemVisible;
 					}
 					swipeLayout.setEnabled(enable);

 					mLastFirstVisibleItem = currentFirstVisibleItem;
 				}
 			}
 		});
         return rootView;
     }
	 
	 
     @Override
     public void onAttach(Activity activity) {
         super.onAttach(activity);
         ((MainActivity) activity).onSectionAttached(
                 getArguments().getInt(ARG_SECTION_NUMBER));
     }

	@Override
	public void loadNews() {
	}

	@Override
	public void onRefresh() {
        try {
            MainActivity main = (MainActivity) getActivity();
            main.updateCategories(swipeLayout, categoriesAdapter);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
		
	}
	
	
}
