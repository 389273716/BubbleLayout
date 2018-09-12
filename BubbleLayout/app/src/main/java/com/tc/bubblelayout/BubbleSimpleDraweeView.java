package com.tc.bubblelayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * author：   tc
 * date：      2018/3/14 & 10:04
 * version    1.0
 * description 透明气泡view
 * modify by
 */
public class BubbleSimpleDraweeView extends SimpleDraweeView {
    private Path mSrcPath = new Path();
    private int mHeight;
    private int mWidth;
    private Paint mPaint;
    private RectF mRoundRect;
    /**
     * 上弧线控制点和下弧线控制点
     */
    private PointF topControl, bottomControl;

    /**
     * 气泡图形右侧留空区域宽度
     */
    private int mWidthDiff;
    /**
     * 右上角圆角的半径
     */
    private int mRoundRadius;
    /**
     * 是否是右侧气泡
     */
    private boolean mIsRightPop;
    private PorterDuffXfermode mPorterDuffXfermode;
    private Bitmap mShowButtonBitmap;
    /**
     * 加载时背景色
     */
    private int mLoadingBackColor;
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
    private Bitmap mBubbleBitmap;
    private Canvas mBubbleCanvas;
    private int mDefaultCornerPadding;

    public BubbleSimpleDraweeView(Context context) {
        super(context);
        init();
    }

    public BubbleSimpleDraweeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleSimpleDraweeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBubbleCanvas = new Canvas();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        topControl = new PointF(0, 0);
        bottomControl = new PointF(0, 0);
        mRoundRect = new RectF();
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint
                .FILTER_BITMAP_FLAG);
    }


    private void initValues() {
        mDefaultCornerPadding = DensityUtil.dip2px(getContext(), 3);
        //左侧或右侧留出的空余区域
        mWidthDiff = DensityUtil.dip2px(getContext(), 7);
        //圆角的半径
        mRoundRadius = DensityUtil.dip2px(getContext(), 8);
        if (mIsRightPop) {
            //设置犄角的控制横坐标xy
            topControl.x = mWidth - DensityUtil.dip2px(getContext(), 2);
            topControl.y = mRoundRadius;
            bottomControl.x = mWidth - DensityUtil.dip2px(getContext(), 1);
            bottomControl.y = mRoundRadius + DensityUtil.dip2px(getContext(), 6);
        } else {
            //设置犄角的控制横坐标xy
            topControl.x = DensityUtil.dip2px(getContext(), 2);
            topControl.y = mRoundRadius;
            bottomControl.x = DensityUtil.dip2px(getContext(), 1);
            bottomControl.y = mRoundRadius + DensityUtil.dip2px(getContext(), 6);
        }
    }

    public void setLoadingBackColor(int loadingBackColor) {
        mLoadingBackColor = getResources().getColor(loadingBackColor);
    }

    /**
     * 设置要展示的图片提示按钮的图片drawable值
     *
     * @param showButtonImg
     */
    public void setShowButtonImg(int showButtonImg) {
        int showButtonImg1 = showButtonImg;
        mShowButtonBitmap = BitmapFactory.decodeResource(getResources(), showButtonImg1);
    }

    /**
     * 把右下角要展示的图片重置，不再显示
     */
    public void resetShowButtonImg() {
        mShowButtonBitmap = null;
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


    /**
     * 是否是右侧气泡
     *
     * @param rightPop 是否是右侧气泡 false则为左侧气泡
     */
    public void setRightPop(boolean rightPop) {
        mIsRightPop = rightPop;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
        initValues();
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
        drawTipIcon(canvas);

        mPaint.setXfermode(mPorterDuffXfermode);
        //绘制气泡部分，和 super.onDraw(canvas);绘制的画面利用xfermode做叠加计算
        canvas.drawBitmap(mBubbleBitmap, 0, 0, mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
    }

    private void drawTipIcon(Canvas canvas) {
        if (mShowButtonBitmap != null) {
            int bitmapHeight = mShowButtonBitmap.getHeight();
            int bitmapWidth = mShowButtonBitmap.getWidth();
            int top = mHeight - bitmapHeight - DensityUtil.dip2px(getContext(), 5);
            if (mIsRightPop) {
                canvas.drawBitmap(mShowButtonBitmap, DensityUtil.dip2px
                        (getContext(), 5), top, mPaint);
            } else {
                canvas.drawBitmap(mShowButtonBitmap, mWidth - bitmapWidth - DensityUtil.dip2px
                        (getContext(), 5), top, mPaint);
            }
        }
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
        if (mIsRightPop) {
            mRoundRect.set(0, 0, mWidth - mWidthDiff, mHeight);
        } else {
            mRoundRect.set(mWidthDiff, 0, mWidth, mHeight);
        }

        mSrcPath.addRoundRect(mRoundRect, mRoundRadius, mRoundRadius, Path.Direction.CW);

        if (mIsRightPop) {
            //给path增加右侧的犄角，形成气泡效果
            mSrcPath.moveTo(mWidth - mWidthDiff, mRoundRadius);
            mSrcPath.quadTo(topControl.x, topControl.y, mWidth, mRoundRadius - mDefaultCornerPadding);
            mSrcPath.quadTo(bottomControl.x, bottomControl.y, mWidth - mWidthDiff,
                    mRoundRadius + mWidthDiff);
        } else {
            //给path增加右侧的犄角，形成气泡效果
            mSrcPath.moveTo(mWidthDiff, mRoundRadius);
            mSrcPath.quadTo(topControl.x, topControl.y, 0, mRoundRadius - mDefaultCornerPadding);
            mSrcPath.quadTo(bottomControl.x, bottomControl.y, mWidthDiff, mRoundRadius + mWidthDiff);
        }
        mSrcPath.close();
        //绘制path所形成的图形，清除形成透明效果，露出这一区域
        canvas.drawPath(mSrcPath, mPaint);

    }


}
