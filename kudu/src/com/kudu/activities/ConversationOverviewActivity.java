package com.kudu.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kudu.models.Session;

public class ConversationOverviewActivity extends Activity{
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mListTitles;

    private long backPressedTime = 0;    // used by onBackPressed()
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout);
		
		mTitle = mDrawerTitle = getTitle();
		mListTitles = getResources().getStringArray((R.array.nav_drawer_items));
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mListTitles));
    
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,  
                R.drawable.action_drawer,
                R.string.drawer_open,
                R.string.drawer_close 
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

	private void selectItem(int position) {
		Fragment fragment;
		switch (position) {
			case 0: // ConversationOverviewFragment
				fragment = new ConversationOverviewActivityFragment();
				fragmentManager(fragment);
				closeDrawer(position);
				return;
			case 1: // ProfileFragment
				fragment = new ProfileActivityFragment();
				fragmentManager(fragment);
				closeDrawer(position);
				return;
			case 2: // ContactsFragment
				fragment = new ContactsActivityFragment();
				fragmentManager(fragment);
				closeDrawer(position);
				return;
			case 3: // LogOut Fragment
				Session currSession = MainActivity.db.checkSessionExists();
				MainActivity.db.deleteSession(currSession.getUuid(), currSession.getUsername());
				Intent myIntent = new Intent(ConversationOverviewActivity.this,
						MainActivity.class);
				ConversationOverviewActivity.this.startActivity(myIntent);
				return;
			default: // ConversationOverviewFragment
				fragment = new ConversationOverviewActivityFragment();
				fragmentManager(fragment);
				closeDrawer(position);
		}
    }
	
	private void fragmentManager(Fragment fragment) {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
	}
	
	private void closeDrawer(int position) {
		mDrawerList.setItemChecked(position, true);
		setTitle(mListTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
       
    	FragmentManager fragmentManager = getFragmentManager();
    	
    	if(fragmentManager.getBackStackEntryCount() == 1){
    	long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            Toast.makeText(this, "Press back again to logout",
                                Toast.LENGTH_SHORT).show();
        } else {    
            super.onBackPressed(); 
        }
    	}else{
    		super.onBackPressed();
    	}
    }
    
}
