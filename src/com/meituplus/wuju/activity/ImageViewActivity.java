package com.meituplus.wuju.activity;

import com.meituplus.wuju.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageViewActivity extends Activity
{
	private ImageView imageView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageview);
		imageView = (ImageView) findViewById(R.id.imageView);
		TextView imageViewTxt = (TextView) findViewById(R.id.imageviewname);
		Intent intent = getIntent();
		String imagePath = intent.getStringExtra("imgPath");// 得到传递过来的路径
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, null);// 读取路径中文件转换成图片
		Bitmap newbmp=rotaingImageView(90, bitmap);//得到旋转90度以后的图像
		imageView.setImageBitmap(newbmp);// 控件绑定图片
		String strFileName = imagePath.substring(
				imagePath.lastIndexOf("/") + 1, imagePath.lastIndexOf("."));// substring：获得路径中的文件名称
		imageViewTxt.setText(strFileName);//文件名赋值给控件
	}
	/*
	 * 旋转图片
	 * 
	 * @param angle
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
}
