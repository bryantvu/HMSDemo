
package com.example.hmsdemo.cameraKit.video;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import java.io.File;
import java.util.Comparator;
import java.util.List;

/**
 * camerakit
 */
public class CameraKitHelper {
    private static final String IMAGE_SAVE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
        + File.separator + "DCIM/HwCameraKit" + File.separator;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    private static final String TAG = CameraKitHelper.class.getSimpleName();

    private static final int ROTATION_DEGREE_0 = 0;

    private static final int ROTATION_DEGREE_90 = 90;

    private static final int ROTATION_DEGREE_180 = 180;

    private static final int ROTATION_DEGREE_270 = 270;

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;

    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;

    private static final double RATIO_TOLERANCE = 0.05;

    private static final int MAX_RESOLUTION_VALUE = 65536;

    private static final int TRANSFORM_ORIENTATION_DIFFERENCE = 2;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, ROTATION_DEGREE_90);
        ORIENTATIONS.append(Surface.ROTATION_90, ROTATION_DEGREE_0);
        ORIENTATIONS.append(Surface.ROTATION_180, ROTATION_DEGREE_270);
        ORIENTATIONS.append(Surface.ROTATION_270, ROTATION_DEGREE_180);
    }

    private CameraKitHelper() {
    }

    public static int getOrientation(int sensorOrientation, int rotation) {
        switch (sensorOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                return ORIENTATIONS.get(rotation);
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                return INVERSE_ORIENTATIONS.get(rotation);
            default:
                return 0;
        }
    }

    public static void checkImageDirectoryExists() {
        File imageDirectory = new File(IMAGE_SAVE_DIR);
        if (!imageDirectory.exists()) {
            if (imageDirectory.mkdirs()) {
                Log.d(TAG, "IMAGE_SAVE_DIR:" + IMAGE_SAVE_DIR);
            }
        }
    }

    public static String getVideoName() {
        Log.d(TAG, "setmFileName: ");
        String prefixFile;
        prefixFile = "Default";
        String videoFile = IMAGE_SAVE_DIR + prefixFile + "_video_" + System.currentTimeMillis() + ".mp4";
        return videoFile;
    }

    public static Size getOptimalVideoPreviewSize(Context context, Size videoSize, List<Size> cameraPreviewSupports) {
        if ((context == null) || (videoSize == null) || (cameraPreviewSupports == null)) {
            Log.e(TAG, "getOptimalVideoPreviewSize: error, some parameter is null");
            return null;
        }

        Object obj = context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager wm = null;
        if (obj instanceof WindowManager) {
            wm = (WindowManager) obj;
        }
        if (wm == null) {
            return null;
        }
        Display display = wm.getDefaultDisplay();
        if (display == null) {
            return null;
        }
        Point displaySize = new Point();
        display.getSize(displaySize);

        int maxPreviewWidth = displaySize.x;
        int maxPreviewHeight = displaySize.y;

        Size optimalSize = null;
        int maxValue = MAX_RESOLUTION_VALUE;
        double videoRatio = ((double) (videoSize.getWidth())) / ((double) (videoSize.getHeight()));
        for (Size size : cameraPreviewSupports) {
            if ((size.getWidth() > maxPreviewWidth) && (size.getHeight() > maxPreviewHeight)) {
                continue;
            }
            if (size.getHeight() > maxValue) {
                continue;
            } else if (Math
                .abs(((double) (size.getWidth())) / ((double) (size.getHeight())) - videoRatio) > RATIO_TOLERANCE) {
                continue;
            } else if (optimalSize == null) {
                optimalSize = size;
                continue;
            }
            if ((Math.abs(size.getHeight() - videoSize.getHeight()) <= Math
                .abs(optimalSize.getHeight() - videoSize.getHeight()))
                && (Math.abs(size.getWidth() - videoSize.getWidth()) <= Math
                    .abs(optimalSize.getWidth() - videoSize.getWidth()))) {
                optimalSize = size;
            }
        }
        Log.i(TAG, "optimal video preview size = "
            + ((optimalSize == null) ? "null" : (optimalSize.getWidth() + "x" + optimalSize.getHeight())));
        return optimalSize;
    }

    public static void configureTransform(Activity activity, TextureView view, Size previewSize, Size viewSize) {
        if ((activity == null) || (view == null) || (previewSize == null) || (viewSize == null)) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewSize.getWidth(), viewSize.getHeight());
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if ((Surface.ROTATION_90 == rotation) || (Surface.ROTATION_270 == rotation)) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(((float) viewSize.getHeight()) / previewSize.getHeight(),
                ((float) viewSize.getWidth()) / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(SENSOR_ORIENTATION_DEFAULT_DEGREES * (rotation - TRANSFORM_ORIENTATION_DIFFERENCE),
                centerX, centerY);
        }
        view.setTransform(matrix);
    }

    /**
     * Record state
     *
     * @since 2019-06-06
     */
    public enum RecordState {
        /**
         * idle state
         */
        IDLE,
        /**
         * process state
         */
        PRE_PROCESS,
        /**
         * recording state
         */
        RECORDING,
        /**
         * pause state
         */
        PAUSED
    }

    public static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }
}