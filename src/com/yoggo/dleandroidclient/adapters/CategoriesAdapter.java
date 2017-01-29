package com.yoggo.dleandroidclient.adapters;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yoggo.dleandroidclient.AddCategoryActivity;
import com.yoggo.dleandroidclient.R;
import com.yoggo.dleandroidclient.account.AccountSettingsManager;
import com.yoggo.dleandroidclient.database.Categories;
import com.yoggo.dleandroidclient.database.CategoriesDao;
import com.yoggo.dleandroidclient.database.Commentaries;
import com.yoggo.dleandroidclient.database.CommentariesDao;
import com.yoggo.dleandroidclient.database.DaoMaster;
import com.yoggo.dleandroidclient.database.DaoMaster.DevOpenHelper;
import com.yoggo.dleandroidclient.database.DaoSession;
import com.yoggo.dleandroidclient.database.DatabaseManager;
import com.yoggo.dleandroidclient.json.AddCategoryJson;
import com.yoggo.dleandroidclient.json.AddCommentsJson;
import com.yoggo.dleandroidclient.requests.DeleteCategory;
import com.yoggo.dleandroidclient.requests.DeleteCommentary;

public class CategoriesAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Categories> categories;
	private AccountSettingsManager accountManager;
	private DatabaseManager dbManager;
	private ListView listView;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db;

	public CategoriesAdapter(Context context, List<Categories> categories) {
		this.context = context;
		this.categories = categories;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		accountManager = new AccountSettingsManager(context);
		dbManager = new DatabaseManager(context);
	}
	
	public void setList(List<Categories> categories){
		this.categories = categories;
	}

	class ViewHolder {
		long id;
		long parentId;
		String altName;
		TextView titleTextView;
		TextView altNameTextView;
		TextView content;
		ImageButton button;
		ListView commentsListView;
		boolean isShowComments;
	}
	
	/*
	 * Вызов меню по долгому тапу
	 * */
	OnItemLongClickListener longItemClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder != null) {
				openPopupMenu(view, holder);
			}
			return false;
		}
	};
	
	public void openPopupMenu(View view, final ViewHolder holder) {
		PopupMenu popup = new PopupMenu(context, view);
		if ( accountManager.isUserAdminCategories()) {
			popup.getMenuInflater().inflate(R.menu.popup_menu_edit_category,
					popup.getMenu());
			popup.getMenuInflater().inflate(R.menu.popup_menu_delete_category,
					popup.getMenu());
		}
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				int id = item.getItemId();
				if (id == R.id.action_edit_category) {
					Intent edit = new Intent(context, AddCategoryActivity.class);
					//передаем параметры для редактирования категории
					edit.putExtra(AddCategoryActivity.CATEGORY_ID, String.valueOf(holder.id));
					edit.putExtra(AddCategoryActivity.CATEGORY_NAME,
							holder.titleTextView.getText().toString());
					edit.putExtra(AddCategoryActivity.CATEGORY_ALT_NAME,
							holder.altName);
					edit.putExtra(AddCategoryActivity.CATEGORY_PARENT_ID,
							String.valueOf(holder.parentId));
					edit.putExtra(AddCategoryActivity.IS_EDIT,
							true);
					context.startActivity(edit);
					return true;
				} else if (id == R.id.action_delete_category) {
					deleteCategory(holder.id);
					return true;
				}
				return true;
			}
		});
		popup.show();
	}
	
	private void deleteCategory(final long comId) {
		Callback<AddCategoryJson> callback = new Callback<AddCategoryJson>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(context, "Ошибка при удалении категории", Toast.LENGTH_SHORT)
						.show();
				Log.d("ERROR", arg0.getMessage());

			}

			@Override
			public void success(AddCategoryJson list, Response arg1) {
				if (list != null) {
					if (list.result != null) {
						Log.d("result", list.result);
					}
					if (list.error != null) {
						Toast.makeText(context, list.error, 200).show();
						return;
					}
					dbManager.deleteCategoryFromLocalDB(comId);
					DevOpenHelper helper = new DaoMaster.DevOpenHelper(context,
							"onedle.db", null);
					db = helper.getWritableDatabase();
					daoMaster = new DaoMaster(db);
					daoSession = daoMaster.newSession();
					CategoriesDao comDao = daoSession.getCategoriesDao();
					categories = comDao.queryBuilder().orderDesc(CategoriesDao.Properties.Id).list();
					//обновляем адаптер
					notifyDataSetChanged();

				} else {
					Toast.makeText(context, "Ошибка при удалении категории",
							Toast.LENGTH_SHORT).show();
				}
			}

		};
		new DeleteCategory(accountManager.getSite(),
				accountManager.getToken(), String.valueOf(comId), callback);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (listView == null) {
			listView = (ListView) parent;
			listView.setOnItemLongClickListener(longItemClick);
		}
		// используем созданные, но не используемые view
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.category_layout, parent, false);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.site_textView);
			holder.altNameTextView = (TextView) convertView.findViewById(R.id.alt_name_textView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Categories cat = (Categories) getItem(position);

		holder.id = Long.valueOf(cat.getId());
		holder.parentId = Long.valueOf(cat.getParentId());
		holder.altName = cat.getAltName();
		 holder.titleTextView.setTag(position);
		
		holder.titleTextView.setText(cat.getName());
		if(cat.getParentId() != null){
			holder.altNameTextView.setText(dbManager.getCategoryNameById(cat.getParentId()));
		}else{
			holder.altNameTextView.setText("");
		}
		// если первый элемент, добавляем отступ сверху (для оверлэя
		// actionbar'а)
		if (holder.titleTextView.getTag() != null
				&& (Integer) holder.titleTextView.getTag() == 0) {
			final TypedArray styledAttributes = context.getTheme()
					.obtainStyledAttributes(
							new int[] { android.R.attr.actionBarSize });
			int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
			Log.d("actionbar height", "" + mActionBarSize);
			convertView.setPadding(0, mActionBarSize + dpToPx(15), 0, 0);
		} else {
			convertView.setPadding(0, 0, 0, 0);
		}
		return convertView;
	}
	
	public static int dpToPx(int dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	@Override
	public int getCount() {
		return categories.size();
	}

	@Override
	public Object getItem(int position) {
		return categories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
