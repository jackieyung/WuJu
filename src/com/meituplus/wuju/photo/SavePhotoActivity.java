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
		/************ ���ͼƬ������ʾ *****************/
		Bundle extras = getIntent().getExtras();
		byteArray = extras.getByteArray("bitmap");
		Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0,
				byteArray.length);
		//Bitmap newbmp=rotaingImageView(90, bmp);//�õ���ת90���Ժ��ͼ��
		img.setImageBitmap(bmp);
		btnSave.setOnClickListener(listener);
		btnCancel.setOnClickListener(listener);

		/*************************************/

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

	private OnClickListener listener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_save:
				/************** ���水ť�����¼�-��������Ի��� ********************/

				AlertDialog.Builder builder = new AlertDialog.Builder(
						SavePhotoActivity.this);
				builder.setIcon(R.drawable.ic_launcher);
				builder.setTitle("�ļ����ͱ������");
				// ͨ��LayoutInflater������һ��xml�Ĳ����ļ���Ϊһ��View����
				View view = LayoutInflater.from(SavePhotoActivity.this)
						.inflate(R.layout.savephotodialog, null);
				// ���������Լ�����Ĳ����ļ���Ϊ�������Content
				builder.setView(view);

				filename = (EditText) view.findViewById(R.id.filename);

				Spinner filecategory = (Spinner) view
						.findViewById(R.id.filecategory);
				// ��������Դ
				String[] mItems = getResources().getStringArray(
						R.array.addthing_array);
				// ����Adapter���Ұ�����Դ
				ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(
						SavePhotoActivity.this, R.layout.drop_down_item, mItems);// �����ݵ�R.layout.drop_down_item
				// �� Adapter���ؼ�
				Log.i(TAG, "�����ݲ���");
				filecategory.setAdapter(_Adapter);

				// Spinner����¼�
				filecategory
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								strFileCaregory = parent.getItemAtPosition(
										position).toString();
								Toast.makeText(SavePhotoActivity.this,
										"��ѡ�����:" + strFileCaregory, 200).show();
							}

							public void onNothingSelected(AdapterView<?> parent) {
								// TODO Auto-generated method stub
							}
						});

				builder.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								if ("".equals(filename.getText().toString()
										.trim())) {
									Toast.makeText(SavePhotoActivity.this,
											"�ļ�������Ϊ�գ������±��档", 200).show();
									return;
								} else {
									/******************* ����ͼƬ **********************/
									File dir = new File(Environment
											.getExternalStorageDirectory()
											+ "/com.meituplus.wuju/"
											+ strFileCaregory);
									if (!dir.exists()) {
										try {
											// ����ָ����·�������ļ���
											dir.mkdirs();
										} catch (Exception e) {
											Toast.makeText(
													SavePhotoActivity.this,
													"�����ļ���ʧ�ܣ�����洢����", 500)
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
										Log.i(TAG, "����ɹ�");
										Toast.makeText(SavePhotoActivity.this,
												"ͼƬ����ɹ�~", 200).show();
										fos.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}

							}
						});
				builder.setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				builder.show();
				break;
			case R.id.button_cancel:
				/************** ȡ����ť�����¼�-��������Ի��� ********************/
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
