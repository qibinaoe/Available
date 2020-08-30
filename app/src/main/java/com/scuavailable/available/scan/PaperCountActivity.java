package com.scuavailable.available.scan;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.scuavailable.available.R;

public class PaperCountActivity extends AppCompatActivity {

    private static String TAG = "PaperCountActivity";
    ImageButton mBackIb;
    private TextView mCurrMatrixTv;
    private Matrix mCurrentDisplayMatrix = null;

    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";
    private PhotoView mPhotoView;

    private int dotCount = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_count);

        initViews();
    }

    private void initViews() {
        mCurrMatrixTv = findViewById(R.id.tv_current_matrix);

        mPhotoView = (PhotoView) findViewById(R.id.photo_view_count);
        mPhotoView.setImageResource(R.drawable.bg_scu);
        mPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
        mPhotoView.setOnPhotoTapListener(new PhotoTapListener());

        mBackIb = findViewById(R.id.ib_count_back);

    }

    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            //x 与 y 有进行过归一化
            //获取图片宽高
            BitmapFactory.Options options = new BitmapFactory.Options();
            BitmapDrawable bitmapDrawable = (BitmapDrawable) mPhotoView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            Log.i(TAG,String.valueOf(options.inSampleSize));

            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            Log.i(TAG," height " + String.valueOf(height) + " width " + String.valueOf(width));
            float xPos = x*width;
            float yPos = y*height;
            Log.i(TAG," xPos " + String.valueOf(xPos) + " yPos " + String.valueOf(yPos));

            //添加点
            Paint dotPaint = new Paint();
            dotPaint.setAntiAlias(true);
            dotPaint.setColor(Color.GREEN);
            Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
            Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(mutableBitmap);
            canvas.drawCircle(xPos, yPos, 10, dotPaint);

            mPhotoView.setImageBitmap(mutableBitmap);

            //改变计数

        }
    }



    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
            mCurrMatrixTv.setText(rect.toString());
        }
    }
}
