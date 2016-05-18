package com.meituplus.wuju.activity;

import java.util.ArrayList;
import java.util.List;

import com.meituplus.wuju.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	private List<Picture> pictures;

	public PictureAdapter(String[] titles, String[] images, Context context)
	{
		super();
		pictures = new ArrayList<Picture>();
		inflater = LayoutInflater.from(context);
		for (int i = 0; i < images.length; i++)
		{
			Picture picture = new Picture(titles[i], images[i]);
			pictures.add(picture);
		}
	}

	public int getCount()
	{
		if (null != pictures)
		{
			return pictures.size();
		} else
		{
			return 0;
		}
	}

	public Object getItem(int position)
	{
		return pictures.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.gridview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.ItemTextView);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.ItemImageView);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(pictures.get(position).getTitle());
		Bitmap bitmap = BitmapFactory.decodeFile(pictures.get(position).getImageId(), null);//读取路径中文件转换成图片
		viewHolder.image.setImageBitmap(bitmap);//控件绑定图片
		return convertView;
	}

}

class ViewHolder
{
	public TextView title;
	public ImageView image;
}

class Picture
{
	private String title;
	private String imageId;

	public Picture()
	{
		super();
	}

	public Picture(String title, String imageId)
	{
		super();
		this.title = title;
		this.imageId = imageId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getImageId()
	{
		return imageId;
	}

	public void setImageId(String imageId)
	{
		this.imageId = imageId;
	}
}
