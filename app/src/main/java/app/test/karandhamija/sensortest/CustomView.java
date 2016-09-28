package app.test.karandhamija.sensortest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.FileWriter;

public class CustomView extends View {

    private Paint mPaint;
    private Canvas mCanvas;
    private Path mPath;
    private Bitmap DrawBitmap;
    private Paint DrawBitmapPaint;
    private String mFileName = "touchevents.txt";
    private String mFolderName = "EventsFolder";
    private FileWriter mWriter;

    private String mFolderpath = Environment.getExternalStorageDirectory()+File.separator+mFolderName;
    private String mFilePath = mFolderpath + File.separator + mFileName;
        public CustomView(Context c) {

            super(c);

            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();

            display.getSize(size);

            DrawBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_4444);

            mCanvas = new Canvas(DrawBitmap);

            mPath = new Path();
            DrawBitmapPaint = new Paint(Paint.DITHER_FLAG);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(getResources()
                    .getColor(android.R.color.holo_green_dark));
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(20);

            if(isFolderExist()){

                File mFile = new File(mFilePath);
                if(!mFile.exists()){

                    try {
                        mFile.createNewFile();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }else {
                createFolder();
            }

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            setDrawingCacheEnabled(true);
            canvas.drawBitmap(DrawBitmap, 0, 0, DrawBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawRect(mY, 0, mY, 0, DrawBitmapPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;

            String mString = "x: " + x + " y: " + y;
            if(mWriter != null){
                try{
                    mWriter.append(mString);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);

            mCanvas.drawPath(mPath, mPaint);

            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    openFile();
                    touch_start(x, y);
                    writeFile(x,y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    writeFile(x,y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    writeFile(x,y);
                    closeFile();
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }

    public boolean isFolderExist(){
        File root = new File(mFolderpath);

        Log.v("karan", "path is " + mFolderpath);

        if (!root.exists())
        {
            return false;
        }

        return true;
    }

    public void createFolder(){
        File root = new File(mFolderpath);
        root.mkdir();
        try{
            File mFile = new File(root, mFileName);
            Boolean mCreated = mFile.createNewFile();
            Log.v("karan", "Is File Created " + mCreated);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void closeFile(){
        try{
            mWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void openFile(){
        try{
            mWriter = new FileWriter(new File(mFilePath));
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void writeFile(float x, float y){
        String mString = "x: " + x + " y: " + y + "\n";
        if(mWriter != null){
            try{
                mWriter.append(mString);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
