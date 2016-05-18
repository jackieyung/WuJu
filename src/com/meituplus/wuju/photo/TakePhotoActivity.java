package com.meituplus.wuju.photo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.meituplus.wuju.R;
import com.meituplus.wuju.activity.MainActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class TakePhotoActivity extends Activity implements OnTouchListener
{
	private ImageButton take_photo_button;
	private ImageButton back;
	private ImageButton openimg;
	private SurfaceHolder mHolder;
	private String path;
	private static Camera mCamera;
	public File photoFile;
	public Handler mHandler = new Handler();
	private CameraPreview mPreview;
	private FrameLayout preview;
	int displayRotation;
	int picRotate = 0;
	private DrawCaptureRect mDraw;
	private int width = 0;
	private int height = 0;
	private boolean isfocusing = false;
	private boolean isfocuseed = false;
	private int x = 0;
	private int y = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.take_photo_layout);

		new OrientationListener(TakePhotoActivity.this).enable();

		mPreview = new CameraPreview(this, mCamera);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.setOnTouchListener(this);
		preview.addView(mPreview);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		width = dm.widthPixels;
		height = dm.heightPixels;
		mDraw = new DrawCaptureRect(TakePhotoActivity.this, width / 2 - 100,
				height / 2 - 100, 200, 200, getResources()
						.getColor(R.color.red));
		preview.addView(mDraw);
		take_photo_button = (ImageButton) findViewById(R.id.img_take_photo);
		back = (ImageButton) findViewById(R.id.img_back);
		openimg = (ImageButton) findViewById(R.id.img_openimg);
		take_photo_button.setOnClickListener(Listener);
		back.setOnClickListener(Listener);
		openimg.setOnClickListener(Listener);

	}

	private OnClickListener Listener = new OnClickListener()
	{

		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.img_take_photo://拍照
				if (isfocuseed)
				{
					mCamera.takePicture(null, null, mPicture);
					isfocusing = false;
					isfocuseed = false;
				}
				else if (isfocusing)
				{
					Toast.makeText(TakePhotoActivity.this, "正在聚焦，请稍等",
							Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(TakePhotoActivity.this, "请触摸屏幕对焦后再拍照",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.img_back://返回主界面
				Intent intent = new Intent(TakePhotoActivity.this,
						MainActivity.class);
				startActivity(intent);
				TakePhotoActivity.this.finish();
				break;
			case R.id.img_openimg://打开图片文件
				Toast.makeText(TakePhotoActivity.this, "待添加",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}

		}
	};

	private Rect calculateTapArea(float x, float y, float coefficient)
	{
		float focusAreaSize = 200;
		int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
		int centerX = (int) ((x / width) * 2000 - 1000);
		int centerY = (int) ((y / height) * 2000 - 1000);
		int left = clamp(centerX - (areaSize / 2), -1000, 1000);
		int top = clamp(centerY - (areaSize / 2), -1000, 1000);
		RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
		return new Rect(Math.round(rectF.left), Math.round(rectF.top),
				Math.round(rectF.right), Math.round(rectF.bottom));
	}

	private int clamp(int x, int min, int max)
	{
		if (x > max)
		{
			return max;
		}
		if (x < min)
		{
			return min;
		}
		return x;
	}

	// 快门声音

	@SuppressLint("NewApi")
	public void getCameraInstance()
	{
		try
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
			{
				mCamera = Camera.open(0);
				// i=0 表示后置相机
			}
			else
				mCamera = Camera.open();
		}
		catch (Exception e)
		{
		}
	}

	// 设置预览参数
	public class CameraPreview extends SurfaceView implements
			SurfaceHolder.Callback
	{
		public CameraPreview(Context context, Camera camera)
		{
			super(context);
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void surfaceCreated(SurfaceHolder holder)
		{
			try
			{
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			}
			catch (IOException e)
			{
				Log.d("TakePhoto",
						"Error setting camera preview: " + e.getMessage());
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder)
		{
			resetCamera();
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height)
		{
			if (mHolder.getSurface() == null)
			{
				return;
			}
			try
			{
				mCamera.stopPreview();
			}
			catch (Exception e)
			{
			}
			try
			{
				mCamera.setDisplayOrientation(getCameraDisplayOrientation(0));
				Camera.Parameters params = mCamera.getParameters();
				Size PreviewSize = getBestSupportedSize(params
						.getSupportedPreviewSizes());
				Size PictureSize = getBestSupportedSize(params
						.getSupportedPictureSizes());
				params.setPictureFormat(ImageFormat.JPEG);
				params.setPreviewSize(PreviewSize.width, PreviewSize.height);
				params.setPictureSize(PictureSize.width, PictureSize.height);
				params.setJpegQuality(100);
				mCamera.setParameters(params);
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			}
			catch (Exception e)
			{
				resetCamera();
				Log.d("takePhoto",
						"Error starting camera preview: " + e.getMessage());
			}
		}
	}

	// 获取图片数据
	private PictureCallback mPicture = new PictureCallback()
	{
		public void onPictureTaken(byte[] data, Camera camera)
		{
			Matrix matrix = new Matrix();
			matrix.setRotate(picRotate);
			Bitmap bitmap = DecodeImageUtils.decodeImage(data,
					TakePhotoActivity.this, matrix);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");// 转换格式
			String picName = sdf.format(new Date()) + ".jpg";
			path = getPicPath() + picName;
			File pictureFile = new File(getPicPath());
			if (!pictureFile.exists())
			{
				pictureFile.mkdirs();
			}
			pictureFile = new File(path);
			try
			{
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(pictureFile));
				bitmap.compress(CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
				if (bitmap != null && !bitmap.isRecycled())
				{
					bitmap.recycle();
					System.gc();
					System.out.println("图片资源已回收，控制内存占用");
				}
				camera.stopPreview();
				Intent intent = new Intent(TakePhotoActivity.this,
						CutPhotoActivity.class);
				intent.putExtra("imgPath", path);
				startActivity(intent);
			}
			catch (FileNotFoundException e)
			{
				Log.d("TakePhoto", "File not found: " + e.getMessage());
			}
			catch (IOException e)
			{
				Log.d("TakePhoto", "Error accessing file: " + e.getMessage());
			}
		}
	};

	private class OrientationListener extends OrientationEventListener
	{
		public OrientationListener(Context context)
		{
			super(context);
		}

		public OrientationListener(Context context, int rate)
		{
			super(context, rate);
		}

		@SuppressLint("NewApi")
		@Override
		public void onOrientationChanged(int orientation)
		{
			if (orientation == ORIENTATION_UNKNOWN)
				return;
			android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
			android.hardware.Camera.getCameraInfo(0, info);
			orientation = (orientation + 45) / 90 * 90;
			int rotation = 0;
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT)
			{
				rotation = (info.orientation - orientation + 360) % 360;
			}
			else
			{ // back-facing camera
				rotation = (info.orientation + orientation) % 360;
			}
			picRotate = rotation;
		}
	}

	@SuppressLint("NewApi")
	public int getCameraDisplayOrientation(int cameraId)
	{
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation)
		{
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		}
		else
		{ // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}

	private Size getBestSupportedSize(List<Size> sizes)
	{
		// 取能适用的最大的SIZE
		Size largestSize = sizes.get(0);
		int largestArea = sizes.get(0).height * sizes.get(0).width;
		for (Size s : sizes)
		{
			int area = s.width * s.height;
			if (area > largestArea)
			{
				largestArea = area;
				largestSize = s;
			}
		}
		return largestSize;
	}

	private void resetCamera()
	{
		if (mCamera != null)
		{
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	public static String getPicPath()
	{
		File defaultDir = Environment.getExternalStorageDirectory();
		String path = defaultDir.getAbsolutePath() + File.separator + "DXM"
				+ File.separator;
		return path;
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		resetCamera();
		getCameraInstance();
	}

	@Override
	protected void onRestart()
	{
		// TODO Auto-generated method stub
		super.onRestart();
		resetCamera();
		getCameraInstance();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		resetCamera();
	}

	@SuppressLint("NewApi")
	public boolean onTouch(View v, final MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			isfocusing = true;
			x = (int) event.getX() - 100;
			y = (int) event.getY() - 100;
			preview.removeView(mDraw);
			mDraw = new DrawCaptureRect(TakePhotoActivity.this, x, y, 200, 200,
					getResources().getColor(R.color.red));
			preview.addView(mDraw);
			Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY(),
					1f);
			Rect meteringRect = calculateTapArea(event.getRawX(),
					event.getRawY(), 1.5f);
			Camera.Parameters parameters = mCamera.getParameters();
			List<String> focusModes = parameters.getSupportedFocusModes();
			System.out.println("支持的变焦模式" + focusModes);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			if (parameters.getMaxNumFocusAreas() > 0)
			{
				List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
				focusAreas.add(new Camera.Area(focusRect, 600));
				parameters.setFocusAreas(focusAreas);
			}
			if (parameters.getMaxNumMeteringAreas() > 0)
			{
				List<Area> meteringAreas = new ArrayList<Area>();
				meteringAreas.add(new Camera.Area(meteringRect, 1000));
				parameters.setMeteringAreas(meteringAreas);
			}
			mCamera.cancelAutoFocus();
			mCamera.setParameters(parameters);
			mCamera.autoFocus(new AutoFocusCallback()
			{
				public void onAutoFocus(boolean success, Camera camera)
				{
					if (success)
					{
						mDraw = new DrawCaptureRect(TakePhotoActivity.this, x,
								y, 200, 200, getResources().getColor(
										R.color.green));
						preview.addView(mDraw);
						isfocuseed = true;
						isfocusing = false;
					}
					else
					{
						mDraw = new DrawCaptureRect(TakePhotoActivity.this, x,
								y, 200, 200, getResources().getColor(
										R.color.red));
						preview.addView(mDraw);
						isfocuseed = false;
						isfocusing = true;
					}
				}
			});
		}
		return true;
	}
}
