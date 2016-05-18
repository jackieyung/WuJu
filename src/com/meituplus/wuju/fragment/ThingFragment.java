package com.meituplus.wuju.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.ant.liao.GifView;
import com.meituplus.wuju.R;
import com.meituplus.wuju.activity.ImageViewActivity;
import com.meituplus.wuju.activity.PictureAdapter;
import com.meituplus.wuju.photo.TakePhotoActivity;

public class ThingFragment extends Fragment
{

	public static List<String> getPictures(final String strPath)
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

	public static final String ARG_PLANET_NUMBER = "planet_number";

	private GridView mGridView;
	private ImageView mImageView;
	private GifView gifTakePhoto;

	// private int[] imageRes = { R.drawable.png1, R.drawable.earth,
	// R.drawable.mars };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		int i = getArguments().getInt(ARG_PLANET_NUMBER);
		String planet = getResources().getStringArray(R.array.thing_array)[i];
		View view = null;

		/******** 判断是否存在该文件夹 *******/
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
		/***************************************/
		switch (i)
		{
		/************************* 拍照添加物品 ************************/
		case 0:
		{
			view = inflater.inflate(R.layout.take_photo_in, container, false);
			// 从xml中得到GifView的句柄  
			this.gifTakePhoto = (GifView) view
							.findViewById(R.id.giftakephoto);
		    // 设置Gif图片源  
			gifTakePhoto.setGifImage(R.drawable.takephoto);  
			//view = inflater.inflate(R.layout.take_photo_in, container, false);
			//this.mImageView = (ImageView) view
			//		.findViewById(R.id.imgtakephoto);
			//代码使其居中
			RelativeLayout.LayoutParams layoutParams=
				      new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				   layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
				   gifTakePhoto.setLayoutParams(layoutParams);
			// 添加监听器  
			gifTakePhoto.setOnClickListener(new OnClickListener()
			{

				public void onClick(View v)
				{
					takePhoto();// 照相

				}

				/****************** 照相 ********************/
				private void takePhoto()
				{
					Intent intent = new Intent(getActivity(),
							TakePhotoActivity.class);
					startActivity(intent);

				}
				/******************************************/
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
			List<String> list = new ArrayList<String>();
		    
			if(getPictures(Environment
					.getExternalStorageDirectory()
					+ "/com.meituplus.wuju/"
					+ planet)!=null)
			{
				list = getPictures(Environment
						.getExternalStorageDirectory()
						+ "/com.meituplus.wuju/"
						+ planet);
				String[] itemName = new String[list.size()];// 定义图片名数组
				final String[] imageRes = new String[list.size()];// 定义图片ID数组
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
			
		}

		default:
			break;

		}

		return view;

	}
}
