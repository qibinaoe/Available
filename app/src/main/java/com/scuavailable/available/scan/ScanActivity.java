package com.scuavailable.available.scan;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.scuavailable.available.R;

import java.util.Random;

public class ScanActivity extends AppCompatActivity {

    private static String TAG = "ScanActivity";
    ImageButton mBackIb;
    private TextView mCurrMatrixTv;
    private Matrix mCurrentDisplayMatrix = null;

    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";
    static final String FLING_LOG_STRING = "Fling velocityX: %.2f, velocityY: %.2f";
    private PhotoView mPhotoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Log.i(TAG,"Create");
        mCurrMatrixTv = findViewById(R.id.tv_current_matrix);

        mPhotoView = (PhotoView) findViewById(R.id.photo_view);
        mPhotoView.setImageResource(R.drawable.bg_scu);
        mPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
        mPhotoView.setOnPhotoTapListener(new PhotoTapListener());
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());
        mBackIb = findViewById(R.id.ib_scan_back);
        mBackIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();

                float minScale = mPhotoView.getMinimumScale();
                float maxScale = mPhotoView.getMaximumScale();
                float randomScale = minScale + (r.nextFloat() * (maxScale - minScale));
                mPhotoView.setScale(randomScale, true);

                String text = (String.format(SCALE_TOAST_STRING, randomScale));
                Toast.makeText(ScanActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;
            String text = String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId());
            Toast.makeText(ScanActivity.this, text, Toast.LENGTH_SHORT).show();

        }
    }

    private class SingleFlingListener implements OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY));
            return true;
        }
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
            mCurrMatrixTv.setText(rect.toString());
        }
    }
}
