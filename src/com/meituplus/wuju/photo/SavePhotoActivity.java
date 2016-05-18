package com.meituplus.wuju.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.meituplus.wuju.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class SavePhotoActivity extends Activity {

	protected static final String TAG = "SavePhotoActivity";
	byte[] byteArray = null;
	FileOutputStream fos = null;
	EditText filename;
	String strFileCaregory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save_photo);
		Log.i(TAG,"SavePhotoActivity->on");
		ImageView img = (ImageView) findViewById(R.id.imgsavephoto);
		Button btnSave = (Button) findViewById(R.id.button_save);
		Button btnCancel = (Button) findViewById(R.id.button_cancel);
		/************ 获得图片并且显示 *****************/
		Bundle extras = getIntent().getExtras();
		byteArray = extras.getByteArray("bitmap");
		Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0,
				byteArray.length);
		//Bitmap newbmp=rotaingImageView(90, bmp);//得到旋转90度以后的图像
		img.setImageBitmap(bmp);
		btnSave.setOnClickListener(listener);
		btnCancel.setOnClickListener(listener);

		/*************************************/

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

	private OnClickListener listener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_save:
				/************** 保存按钮监听事件-弹出保存对话框 ********************/

				AlertDialog.Builder builder = new AlertDialog.Builder(
						SavePhotoActivity.this);
				builder.setIcon(R.drawable.ic_launcher);
				builder.setTitle("文件名和保存类别");
				// 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
				View view = LayoutInflater.from(SavePhotoActivity.this)
						.inflate(R.layout.savephotodialog, null);
				// 设置我们自己定义的布局文件作为弹出框的Content
				builder.setView(view);

				filename = (EditText) view.findViewById(R.id.filename);

				Spinner filecategory = (Spinner) view
						.findViewById(R.id.filecategory);
				// 建立数据源
				String[] mItems = getResources().getStringArray(
						R.array.addthing_array);
				// 建立Adapter并且绑定数据源
				ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(
						SavePhotoActivity.this, R.layout.drop_down_item, mItems);// 绑定数据到R.layout.drop_down_item
				// 绑定 Adapter到控件
				Log.i(TAG, "绑定数据部分");
				filecategory.setAdapter(_Adapter);

				// Spinner点击事件
				filecategory
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								strFileCaregory = parent.getItemAtPosition(
										position).toString();
								Toast.makeText(SavePhotoActivity.this,
										"您选择的是:" + strFileCaregory, 200).show();
							}

							public void onNothingSelected(AdapterView<?> parent) {
								// TODO Auto-generated method stub
							}
						});

				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								if ("".equals(filename.getText().toString()
										.trim())) {
									Toast.makeText(SavePhotoActivity.this,
											"文件名不能为空！请重新保存。", 200).show();
									return;
								} else {
									/******************* 保存图片 **********************/
									File dir = new File(Environment
											.getExternalStorageDirectory()
											+ "/com.meituplus.wuju/"
											+ strFileCaregory);
									if (!dir.exists()) {
										try {
											// 按照指定的路径创建文件夹
											dir.mkdirs();
										} catch (Exception e) {
											Toast.makeText(
													SavePhotoActivity.this,
													"创建文件夹失败，请检查存储卡。", 500)
													.show();
										}
									}
									File pictureFile = new File(dir, filename
											.getText().toString() + ".jpg");

									try {
										fos = new FileOutputStream(pictureFile);
									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									try {
										fos.write(byteArray);
										Log.i(TAG, "保存成功");
										Toast.makeText(SavePhotoActivity.this,
												"图片保存成功~", 200).show();
										fos.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}

							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				builder.show();
				break;
			case R.id.button_cancel:
				/************** 取消按钮监听事件-弹出保存对话框 ********************/
				Intent intent = new Intent(SavePhotoActivity.this,
						TakePhotoActivity.class);
				startActivity(intent);
				SavePhotoActivity.this.finish();
				break;
			default:
				break;
			}

		}
	};
}
