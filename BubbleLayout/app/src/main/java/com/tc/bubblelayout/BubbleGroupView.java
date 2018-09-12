package com.tc.bubblelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * author：   tc
 * date：      2018/3/14 & 10:04
 * version    1.0
 * description 透明气泡view，
 * 注意内部如果有TextView，文本长度要短，不能超出一屏幕，不然在列表控件里可能部分机器显示无内容、空白。
 * 如果遇到这种场景，可以采用BubbleTextView的写法，用clipPath做气泡犄角，不过会导致裁剪边缘出现锯齿
 * modify by
 */
public class BubbleGroupView extends LinearLayout {
    private Path mSrcPath;
    private int mHeight;
    private int mWidth;
    private Paint mPaint;
    private RectF mRoundRect;
    /**
     * 上弧线控制点和下弧线控制点，控制犄角的形状
     */
    private PointF topControl, bottomControl;

    /**
     * 气泡图形留空区域宽度，影响气泡犄角的宽度
     */
    private int mWidthDiff;
    /**
     * 左、右上角圆角的半径,影响气泡的起点
     */
    private int mRoundRadius;
    /**
     * 是否是右侧气泡
     */
    private boolean mIsRightPop;
    private PorterDuffXfermode mPorterDuffXfermode;
    /**
     * 加载时背景色
     */
    private int mLoadingBackColor;
    private int mLeftTextPadding;
    private int mRightTextPadding;
    private Paint mBorderPaint;
    private Path mCornerPath;
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
    private int mBorderColor;
    private Bitmap mBubbleBitmap;
    private Canvas mBubbleCanvas;
    private boolean mIsShowBorder;
    private int mDefaultCornerPadding;

    public BubbleGroupView(Context context) {
        this(context, null);
    }

    public BubbleGroupView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBubbleCanvas = new Canvas();
        mSrcPath = new Path();
        mCornerPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(0.5f);
        mBorderPaint.setColor(getResources().getColor(R.color.color_999999));
        topControl = new PointF(0, 0);
        bottomControl = new PointF(0, 0);
        mRoundRect = new RectF();
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.BubbleGroupView);
        mLoadingBackColor = attr.getColor(R.styleable.BubbleGroupView_BubbleGroupView_backgroundColor, Color.WHITE);
        mIsRightPop = attr.getBoolean(R.styleable.BubbleGroupView_BubbleGroupView_rightPop, true);
        //左侧或右侧留出的空余区域
        mWidthDiff = attr.getDimensionPixelOffset(R.styleable.BubbleGroupView_BubbleGroupView_blank_space_width,
                DensityUtil.dip2px(getContext(), 7));
        //圆角的半径
        mRoundRadius = attr.getDimensionPixelOffset(R.styleable.BubbleGroupView_BubbleGroupView_roundRadius,
                DensityUtil.dip2px(context, 8));
        mLeftTextPadding = attr.getDimensionPixelOffset(R.styleable.BubbleGroupView_BubbleGroupView_leftTextPadding,
                DensityUtil.dip2px(context, 0));
        mRightTextPadding = attr.getDimensionPixelOffset(R.styleable.BubbleGroupView_BubbleGroupView_rightTextPadding,
                DensityUtil.dip2px(context, 0));
        attr.recycle();
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint
                .FILTER_BITMAP_FLAG);
        setTextPadding(mRightTextPadding, mLeftTextPadding);
        mIsShowBorder = true;
    }


    private void initValues() {
        mDefaultCornerPadding = DensityUtil.dip2px(getContext(), 3);
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

    public void setShowBorder(boolean showBorder) {
        mIsShowBorder = showBorder;
    }

    public void setLoadingBackColor(int loadingBackColor) {
        mLoadingBackColor = getResources().getColor(loadingBackColor);
    }


    public void setBorderColor(int borderColor) {
        mBorderColor = getResources().getColor(borderColor);
        mBorderPaint.setColor(borderColor);
    }

    private void setTextPadding(int rightTextPadding, int leftTextPadding) {
        if (mIsRightPop) {
            setPadding(leftTextPadding, getPaddingTop(), rightTextPadding + mWidthDiff,
                    getPaddingBottom());
        } else {
            setPadding(leftTextPadding + mWidthDiff, getPaddingTop(), rightTextPadding, getPaddingBottom());
        }
    }


    public void updateView() {
        setTextPadding(mRightTextPadding, mLeftTextPadding);
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
    protected void dispatchDraw(Canvas canvas) {
        canvas.setDrawFilter(mPaintFlagsDrawFilter);
        int saveCount = canvas.saveLayerAlpha(0, 0, mWidth, mHeight, 255,
                Canvas.ALL_SAVE_FLAG);
        drawBackColor(canvas);
        super.dispatchDraw(canvas);

        mPaint.setXfermode(mPorterDuffXfermode);
        //绘制气泡部分，和 super.onDraw(canvas);绘制的画面利用xfermode做叠加计算
        canvas.drawBitmap(mBubbleBitmap, 0, 0, mPaint);
        if (mIsShowBorder && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //绘制气泡的四周边框
            canvas.drawPath(mSrcPath, mBorderPaint);
        }
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
    }

    private void drawBackColor(Canvas canvas) {
        if (mLoadingBackColor != 0) {
            canvas.drawColor(mLoadingBackColor);
        }
    }

    /**
     * 计算绘制气泡
     */
    private void drawBubblePath(Canvas canvas) {
        mSrcPath.reset();
        mCornerPath.reset();
        if (mIsRightPop) {
            mRoundRect.set(0, 0, mWidth - mWidthDiff, mHeight);
        } else {
            mRoundRect.set(mWidthDiff, 0, mWidth, mHeight);
        }

        mSrcPath.addRoundRect(mRoundRect, mRoundRadius, mRoundRadius, Path.Direction.CW);

        if (mIsRightPop) {
            //给path增加右侧的犄角，形成气泡效果
            mCornerPath.moveTo(mWidth - mWidthDiff, mRoundRadius);
            mCornerPath.quadTo(topControl.x, topControl.y, mWidth, mRoundRadius - mDefaultCornerPadding);
            mCornerPath.quadTo(bottomControl.x, bottomControl.y, mWidth - mWidthDiff,
                    mRoundRadius + mWidthDiff);
        } else {
            //给path增加右侧的犄角，形成气泡效果
            mCornerPath.moveTo(mWidthDiff, mRoundRadius);
            mCornerPath.quadTo(topControl.x, topControl.y, 0, mRoundRadius -mDefaultCornerPadding);
            mCornerPath.quadTo(bottomControl.x, bottomControl.y, mWidthDiff, mRoundRadius + mWidthDiff);
        }
        mCornerPath.close();
        //绘制path所形成的图形，清除形成透明效果，露出这一区域
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSrcPath.op(mCornerPath, Path.Op.UNION);
            canvas.drawPath(mSrcPath, mPaint);
            //绘制气泡的四周边框
            canvas.drawPath(mSrcPath, mBorderPaint);
        } else {
            mSrcPath.addPath(mCornerPath);
            canvas.drawPath(mCornerPath, mPaint);
        }
    }


}
