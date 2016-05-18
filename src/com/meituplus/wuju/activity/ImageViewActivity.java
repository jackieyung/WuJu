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
		String imagePath = intent.getStringExtra("imgPath");// �õ����ݹ�����·��
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, null);// ��ȡ·�����ļ�ת����ͼƬ
		Bitmap newbmp=rotaingImageView(90, bitmap);//�õ���ת90���Ժ��ͼ��
		imageView.setImageBitmap(newbmp);// �ؼ���ͼƬ
		String strFileName = imagePath.substring(
				imagePath.lastIndexOf("/") + 1, imagePath.lastIndexOf("."));// substring�����·���е��ļ�����
		imageViewTxt.setText(strFileName);//�ļ�����ֵ���ؼ�
	}
	/*
	 * ��תͼƬ
	 * 
	 * @param angle
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// ��תͼƬ ����
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		// �����µ�ͼƬ
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
}
