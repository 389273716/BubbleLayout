package com.tc.bubblelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * author：   tc
 * date：      2018/3/14 & 10:04
 * version    1.0
 * description 圆角view，用于gif圆角裁剪，fresco不支持gif圆角
 * modify by
 */
public class RoundCornerSimpleDraweeView extends SimpleDraweeView {
    private Path mSrcPath ;
    private int mHeight;
    private int mWidth;
    private Paint mPaint;
    private RectF mRoundRect;

    /**
     * 右上角圆角的半径
     */
    private int mRoundRadius;
    private PorterDuffXfermode mPorterDuffXfermode;
    /**
     * 加载时背景色
     */
    private int mLoadingBackColor;
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
    private Bitmap mBubbleBitmap;
    private Canvas mBubbleCanvas;

    public RoundCornerSimpleDraweeView(Context context) {
        this(context, null);
    }

    public RoundCornerSimpleDraweeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundCornerSimpleDraweeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.BubbleView);
        mLoadingBackColor = attr.getColor(R.styleable.BubbleView_BubbleView_backgroundColor, 0);
        //圆角的半径
        mRoundRadius = attr.getDimensionPixelOffset(R.styleable.BubbleView_BubbleView_roundRadius,
                DensityUtil.dip2px(context, 8));
        attr.recycle();
        mSrcPath = new Path();
        mBubbleCanvas = new Canvas();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRoundRect = new RectF();
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint
                .FILTER_BITMAP_FLAG);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }


    public void setLoadingBackColor(int loadingBackColor) {
        if (loadingBackColor <= 0) {
            mLoadingBackColor = 0;
            return;
        }
        mLoadingBackColor = getResources().getColor(loadingBackColor);
    }



    public void updateView() {
        invalidate();
    }

    /**
     * 设置圆角的半径
     *
     * @param roundRadius
     */
    public void setRoundRadius(int roundRadius) {
        mRoundRadius = DensityUtil.dip2px(getContext(), roundRadius);
    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
        //创建气泡布局
        createBubbleLayout();

    }

    private void createBubbleLayout() {
        if (mBubbleBitmap != null && !mBubbleBitmap.isRecycled()) {
            mBubbleBitmap.recycle();
        }
        mBubbleBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mBubbleCanvas.setBitmap(mBubbleBitmap);
        drawBubblePath(mBubbleCanvas);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(mPaintFlagsDrawFilter);
        int saveCount = canvas.saveLayerAlpha(0, 0, mWidth, mHeight, 255,
                Canvas.ALL_SAVE_FLAG);
        drawBackColor(canvas);
        super.onDraw(canvas);

        mPaint.setXfermode(mPorterDuffXfermode);
        //绘制气泡部分，和 super.onDraw(canvas);绘制的画面利用xfermode做叠加计算
        canvas.drawBitmap(mBubbleBitmap, 0, 0, mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
    }


    private void drawBackColor(Canvas canvas) {
        if (mLoadingBackColor != 0) {
            canvas.drawColor(mLoadingBackColor);
        }
    }

    /**
     * 绘制气泡路径
     */
    private void drawBubblePath(Canvas canvas) {
        mSrcPath.reset();
        mRoundRect.set(0, 0, mWidth, mHeight);
        mSrcPath.addRoundRect(mRoundRect, mRoundRadius, mRoundRadius, Path.Direction.CW);

        //绘制path所形成的图形，清除形成透明效果，露出这一区域
        canvas.drawPath(mSrcPath, mPaint);

    }


}
