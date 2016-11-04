import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HaloView extends View {

    private Bitmap mHalo = null;
    private MyHalo myHalo[] = new MyHalo[12];
    private Integer[] offsetX;// X轴每次移动像素
    private Integer[] offsetY;// Y轴每次移动像素
    private Random r = new Random();
    private Matrix m = new Matrix();
    private Paint p = new Paint();

    private int mW, mH;
    private float de;
    private final int moveDuration = 50;
    private Timer timer;

    public void setOffset() {
        DisplayMetrics dis = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dis);
        de = dis.density;

        offsetX = new Integer[]{(int) (int) (1 * de), (int) (2 * de),};
        offsetY = new Integer[]{(int) (1 * de), (int) (2 * de),};
    }

    private void init() {
        setBackgroundColor(Color.parseColor("#1A1A1A"));
        setOffset();
        loadFlower();
        post(new Runnable() {
            @Override
            public void run() {
                mW = getWidth();
                mH = getHeight();
                addRect();
                start();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDetachedFromWindow();
    }

    public HaloView(Context context) {
        super(context);
        init();
    }

    public HaloView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HaloView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        for (int i = 0; i < myHalo.length; i++) {
            MyHalo myHaloItem = myHalo[i];

            int t = myHaloItem.t;
            t--;
            if (t <= 0) {

                if (myHaloItem.flagX == 0) {
                    myHaloItem.x -= myHaloItem.speedX;
                    if (myHaloItem.x < -mW * 0.5)
                        myHaloItem.flagX = 1;
                } else {
                    myHaloItem.x += myHaloItem.speedX;
                    if (myHaloItem.x > mW + mW * 0.5)
                        myHaloItem.flagX = 0;
                }
                if (myHaloItem.flagY == 0) {
                    myHaloItem.y -= myHaloItem.speedY;
                    if (myHaloItem.y < -mH * 0.5)
                        myHaloItem.flagY = 1;
                } else {
                    myHaloItem.y += myHaloItem.speedY;
                    if (myHaloItem.y > mH + mH * 0.5)
                        myHaloItem.flagY = 0;
                }

                canvas.save();
                m.reset();
                m.setScale(myHaloItem.s, myHaloItem.s);
                canvas.setMatrix(m);
                p.setAlpha(myHaloItem.alpha);
                canvas.drawBitmap(mHalo, myHaloItem.x, myHaloItem.y, p);
                canvas.restore();
            }
            // 当t过小时，重置t
            if (t == -10000)
                t = -1;
            myHaloItem.t = t;
        }
        super.onDraw(canvas);
    }

    private void loadFlower() {
        Resources r = this.getContext().getResources();
        mHalo = ((BitmapDrawable) r.getDrawable(R.drawable.ic_fog_circle))
                .getBitmap();
    }

    private void recly() {
        if (mHalo != null && !mHalo.isRecycled()) {
            mHalo.recycle();
        }
    }

    private void addRect() {
        for (int i = 0; i < myHalo.length; i++) {
            myHalo[i] = new MyHalo();
        }
    }

    private void inva() {
        invalidate();
    }

    private void start() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        inva();
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, moveDuration);
    }

    class MyHalo {
        int x;// 距离屏幕左边距离
        int y;// 距离屏幕右边距离
        float s;// 放大倍数
        int alpha;// 透明度
        int t;// 光晕开始运动时间延迟
        int speedX;// X轴动画速度（每次移动的像素）
        int speedY;// Y轴动画速度（每次移动的像素）

        int flagX, flagY;// 记录X,Y轴移动方向(0为向上向左，1为向下向右)

        public void init() {

            this.x = r.nextInt(mW);
            this.y = r.nextInt(mH);

            this.s = r.nextFloat();
            // 防止过小
            if (this.s < 0.1f)
                s += 0.2f;
                // 防止过大
            else if (this.s > 0.5f)
                s -= 0.2f;

            this.alpha = r.nextInt(155) + 100;
            this.t = r.nextInt(80);

            this.speedX = offsetX[r.nextInt(offsetX.length)];
            this.speedY = offsetY[r.nextInt(offsetY.length)];
            // 初始化移动方向
            this.flagX = r.nextInt(2);
            this.flagY = r.nextInt(2);
        }

        public MyHalo() {
            super();
            init();
        }

    }
}
