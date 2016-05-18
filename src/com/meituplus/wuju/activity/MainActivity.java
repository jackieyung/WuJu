package com.meituplus.wuju.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import com.meituplus.wuju.R;
import com.meituplus.wuju.fragment.ThingFragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity 
{

    private View containerView;
	public static final String TAG = "MainActivity";
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mPlanetTitles;
	static ArrayList name;
	
	private ThingFragment thingFragment;
	FragmentManager fManager;
	
	private Bitmap bitmap;
	
	public static MainActivity newInstance(int resId) {
		MainActivity contentFragment = new MainActivity();
        Bundle bundle = new Bundle();
        bundle.putInt(Integer.class.getName(), resId);
        return contentFragment;
    }


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		fManager = getFragmentManager();
		mTitle = mDrawerTitle = getTitle();
		mPlanetTitles = getResources().getStringArray(R.array.thing_array);// 得到数组数据
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);// 得到listview控件

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mPlanetTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		)
		{
			public void onDrawerClosed(View view)
			{
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView)
			{
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null)
		{
			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		// Handle action buttons
		switch (item.getItemId())
		{
		case R.id.action_websearch:
			// create intent to perform web search for this planet
			// Intent intent = new
			// Intent(Intent.ACTION_WEB_SEARCH);//默认的打开浏览器，是www.google.com
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://www.baidu.com")); // 指定特定的网址显示
			intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
			// catch event that there's no activity to handle intent
			if (intent.resolveActivity(getPackageManager()) != null)
			{
				startActivity(intent);
			}
			else
			{
				Toast.makeText(this, R.string.app_not_available,
						Toast.LENGTH_LONG).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener
	{
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			selectItem(position);
		}
	}

	private void selectItem(int position)
	{
		// update the main content by replacing fragments
		Fragment fragment = new ThingFragment();
		Bundle args = new Bundle();
		args.putInt(ThingFragment.ARG_PLANET_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
	/*
	 * 获取SDCard中某个目录下图片集合
	 */
	/*public static  List<String> getPictures(final String strPath)
	{
		List<String> list = new ArrayList<String>();
		File file = new File(strPath);
		File[] allfiles = file.listFiles();
		if (allfiles == null)
		{
			return null;
		}
		for (int k = 0; k < allfiles.length; k++)
		{
			final File fi = allfiles[k];
			if (fi.isFile())
			{
				int idx = fi.getPath().lastIndexOf(".");
				if (idx <= 0)
				{
					continue;
				}
				String suffix = fi.getPath().substring(idx);
				if (suffix.toLowerCase().equals(".jpg"))
				{
					list.add(fi.getPath());
				}
			}
		}
		return list;
	}

	
	public static class ThingFragment extends Fragment
	{
		public static final String ARG_PLANET_NUMBER = "planet_number";

		private GridView mGridView;
		private ImageView mImageView;

		// private int[] imageRes = { R.drawable.png1, R.drawable.earth,
		// R.drawable.mars };

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			int i = getArguments().getInt(ARG_PLANET_NUMBER);
			String planet = getResources().getStringArray(R.array.thing_array)[i];
			View view = null;

			if (i != 0)
			{
				File path = new File(Environment.getExternalStorageDirectory()
						+ "/com.meituplus.wuju/" + planet);
				if (!path.exists())
				{
					try
					{
						Toast.makeText(getActivity(), "该分类下无图片！请先拍照保存。", 500)
								.show();
						;
					}
					catch (Exception e)
					{

					}
				}
			}

			switch (i)
			{

			case 0:
			{

				view = inflater.inflate(R.layout.take_photo, container, false);
				this.mImageView = (ImageView) view
						.findViewById(R.id.imgtakephoto);
				mImageView.setOnClickListener(new OnClickListener()
				{

					public void onClick(View v)
					{
						takePhoto();// 照相

					}


					private void takePhoto()
					{
						Intent intent = new Intent(getActivity(),
								TakePhotoActivity.class);
						startActivity(intent);

					}

				});
				getActivity().setTitle(planet);
				break;
			}
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			{
				List<String> list = getPictures(Environment
						.getExternalStorageDirectory()
						+ "/com.meituplus.wuju/"
						+ planet);
				String[] itemName = new String[list.size()];// 定义图片名数组
				final String[] imageRes = new String[list.size()];// 定义图片ID数组
				Log.d(TAG, "list.size = " + list.size());
				for (int j = 0; j < list.size(); j++)
				{
					String strFileName = list.get(j).substring(
							list.get(j).lastIndexOf("/") + 1,
							list.get(j).lastIndexOf("."));// substring：获得路径中的文件名称
					itemName[j] = strFileName;
					imageRes[j] = list.get(j);
				}
				view = inflater.inflate(R.layout.gridview_main, container,
						false);
				this.mGridView = (GridView) view.findViewById(R.id.MyGridView);
				PictureAdapter adapter = new PictureAdapter(itemName, imageRes,
						getActivity());
				mGridView.setAdapter(adapter);
				mGridView.setOnItemClickListener(new OnItemClickListener()
				{
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id)
					{
						Intent intent = new Intent(getActivity(),
								ImageViewActivity.class);
						intent.putExtra("imgPath", imageRes[position]);
						startActivity(intent);
					}
				});
				getActivity().setTitle(planet);
				break;
			}

			default:
				break;

			}

			return view;

		}
	}*/

}
