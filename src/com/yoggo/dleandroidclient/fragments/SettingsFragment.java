package com.yoggo.dleandroidclient.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.yoggo.dleandroidclient.AboutActivity;
import com.yoggo.dleandroidclient.MainActivity;
import com.yoggo.dleandroidclient.R;
import com.yoggo.dleandroidclient.database.DaoMaster;
import com.yoggo.dleandroidclient.database.DaoSession;

public class SettingsFragment extends Fragment implements OnClickListener{
	private static final String ARG_SECTION_NUMBER = "section_number";
	public static final int SETTINGS_FRAGMENT = 3;
	
   private Button settingsButton;
	
	private DaoMaster daoMaster;
	private static DaoSession daoSession;
	private SQLiteDatabase db;
	
	
	public static SettingsFragment newInstance() {
		SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, SETTINGS_FRAGMENT);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
    }
	
	 @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
         settingsButton = (Button) rootView.findViewById(R.id.settings_button);
         settingsButton.setOnClickListener(this);
         return rootView;
     }
	 
	 @Override
	 public void onClick(View v){
		 switch(v.getId()){
		 case R.id.settings_button:
			 Intent about = new Intent(getActivity(), AboutActivity.class);
			 startActivity(about);
			 break;
		 }
	 }

     @Override
     public void onAttach(Activity activity) {
         super.onAttach(activity);
         ((MainActivity) activity).onSectionAttached(
                 getArguments().getInt(ARG_SECTION_NUMBER));
     }
}
