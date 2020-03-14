package com.gamingwe.cubewerubiksolver.manual;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback,
        Camera.PreviewCallback {

    protected Camera camera;
    Size previewSize;
    //private boolean surfaceWasDestroyed;
    List<Size> supportedPreviewSizes;
    int camImageWidth, camImageHeight;
    private SurfaceHolder surfaceHolder;
    private byte[] data;
    private float[][][][] pixelHSVs;
    private Bitmap[] previewBitmaps;
    private int side;
    private int centerX, centerY, startX, startY, cubieSideLength;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        Log.d("Surface Constructed", "TRUE");
        this.camera = camera;
        supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        // deprecated setting, but required on Android versions < 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        previewBitmaps = new Bitmap[6];
        pixelHSVs = new float[6][3][3][3];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("Surface Measured", "TRUE");
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (supportedPreviewSizes != null) {
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        previewBitmaps = null;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters params = camera.getParameters();
        camImageWidth = params.getPreviewSize().width;
        camImageHeight = params.getPreviewSize().height;

        this.data = data;
    }

    /* Callback that is called when the surface is created or orientation changes */
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        Log.d("Surface Created", "TRUE");
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Took care of releasing the Camera preview in activity.
        Log.d("Surface Destroyed", "TRUE");
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            Log.d("Surface Changed", "FALSE");
            return;
        } else {
            Log.d("Surface Changed", "TRUE");
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        List<String> focusModes = parameters.getSupportedFocusModes();
        if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            Log.d("Supports contin focus", "TRUE");
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        Log.d(TAG, "width: " + previewSize.width);
        Log.d(TAG, "height: " + previewSize.height);

        camera.setParameters(parameters);

        // start preview with new settings
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
            camera.setPreviewCallback(this);


            invalidate();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void setSide(int side) {
        this.side = side;
    }

    public void setGridPositions(int... values) {
        try
        {if(values.length == 5) {
            centerX = values[0];
            centerY = values[1];
            startX = values[3];
            startY = values[2];
            cubieSideLength = values[4];
        }}
        catch (Exception e){

        }
    }

    public void saveCurrentBitmap(int side) {
        // Log.d("Width", "" + camImageWidth);
        //Log.d("Height", "" + camImageHeight);

        //The RenderScript way
        Bitmap bitmap = Bitmap.createBitmap(camImageWidth, camImageHeight, Bitmap.Config.ARGB_8888);
        Allocation bitmapData = renderScriptNV21ToRGBA888(
                getContext(),
                camImageWidth,
                camImageHeight,
                data);

        Log.d("Data null", "" + (data == null));
        Log.d("Bitmap null", "" + (bitmap == null));
        Log.d("Side", "" + side);

        bitmapData.copyTo(bitmap);

        Log.d("Width", "" + bitmap.getWidth());
        Log.d("Height", "" + bitmap.getHeight());

        int y = startY;
        int x = startX;

        for (int j = 0; j < 3; j++, startX += cubieSideLength) {
            for (int k = 0; k < 3; k++, startY += cubieSideLength) {
                float[] colorHSV = new float[3];
                Color.colorToHSV(bitmap
                                .getPixel((int) (startX + 0.5 * cubieSideLength),
                                        (int) (startY + 0.5 * cubieSideLength)),
                        colorHSV);
                pixelHSVs[side][j][k][0] = colorHSV[0];
                pixelHSVs[side][j][k][1] = colorHSV[1];
                pixelHSVs[side][j][k][2] = colorHSV[2];
            }
            startY = y;
        }

        startY = y;
        startX = x;

        Toast.makeText(getContext(), "Picture captured! Select another side.",
                Toast.LENGTH_SHORT).show();
    }

    public char[][][] resolveColors() {
        //First check if any of the Bitmaps are null, can't do comparison
        //TODO: Check if colors inputs complete

        char[] indexColors = {'R', 'Y', 'G', 'B', 'O', 'W'};
        float[][] centerColors = new float[6][];

        for (int i = 0; i < centerColors.length; i++) {
            //Copy the appropriate center's color into the array
            centerColors[i] = pixelHSVs[i][1][1];

            Log.d("Center Hue " + indexColors[i], "" + centerColors[i][0]);
            Log.d("Center Saturation " + indexColors[i], "" + centerColors[i][1]);
            Log.d("Center Value " + indexColors[i], "" + centerColors[i][2]);

        }

        char[][][] colors = new char[6][3][3];
        for (int i = 0; i < pixelHSVs.length; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    float[] colorHSV = pixelHSVs[i][j][k];

                    float hue = colorHSV[0];
                    char color = indexColors[i];
                    //Do not change the color if it is a center color
                    if (j == 1 && k == 1) {
                        colors[i][k][j] = color;
                        Log.d("" + i + ", " + j + ", " + k, " " + color);
                    } else if (colorHSV[1] < 0.3) {
                        //If saturation is very low, it's most likely white
                        colors[i][k][j] = 'W';
                        Log.d("" + i + ", " + j + ", " + k, " " + 'W');
                    } else {
                        int minDiff = (int) (Math.abs(hue - centerColors[i][0]));

                        for (int l = 0; l < centerColors.length; l++) {
                            int diff = (int) (Math.abs(hue - centerColors[l][0]));
                            if (diff < minDiff) {
                                minDiff = diff;
                                color = indexColors[l];
                            }
                        }

                        colors[i][k][j] = color;
                        Log.d("" + i + ", " + j + ", " + k, " " + color);
                    }
                }
            }
        }

        return colors;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private Allocation renderScriptNV21ToRGBA888(Context context, int width, int height, byte[] nv21) {
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(nv21);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        return out;
    }
}