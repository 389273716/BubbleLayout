package com.tc.bubblelayout;

import android.app.Activity;
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
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * author：   tc
 * date：      2018/3/14 & 10:04
 * version    1.0
 * description 透明气泡view，尖角在下方的气泡view，调用show方法会直接悬浮到界面decorview里的顶层
 * 注意内部如果有TextView，文本长度要短，不能超出一屏幕，不然在列表控件里可能部分机器显示无内容、空白。
 * 如果遇到这种场景，可以采用BubbleTextView的写法，用clipPath做气泡犄角，不过会导致裁剪边缘出现锯齿
 * modify by
 */
public class BubblePopGroupView extends FrameLayout {
    private static final String TAG = "BubbleBottomGroupView";
    private Path mSrcPath;
    private int mHeight;
    private int mWidth;
    private Paint mPaint;
    private RectF mRoundRect;

    /**
     * 左侧或右侧尖角下，留出的空余区域,尖角离左右边的距离，
     */
    private int mWidthDiff;
    /**
     * 左、右上角圆角的半径,影响气泡的起点
     */
    private int mRoundRadius;
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
    private int mBubbleHeight;
    private int mLocation;
    private boolean mIsDismiss;

    public BubblePopGroupView(Context context) {
        this(context, null);
    }

    public BubblePopGroupView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubblePopGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.BubbleView);
        mLoadingBackColor = attr.getColor(R.styleable.BubbleView_BubbleView_backgroundColor, 0);
        //左侧或右侧留出的空余区域
        mWidthDiff = attr.getDimensionPixelOffset(R.styleable.BubbleView_BubbleView_blank_space_width,
                DensityUtil.dip2px(getContext(), 28));
        //圆角的半径
        mRoundRadius = attr.getDimensionPixelOffset(R.styleable.BubbleView_BubbleView_roundRadius,
                DensityUtil.dip2px(context, 10));
        mLeftTextPadding = attr.getDimensionPixelOffset(R.styleable.BubbleView_BubbleView_leftTextPadding,
                DensityUtil.dip2px(context, 0));
        mRightTextPadding = attr.getDimensionPixelOffset(R.styleable.BubbleView_BubbleView_rightTextPadding,
                DensityUtil.dip2px(context, 0));
        attr.recycle();
        mBubbleCanvas = new Canvas();
        mSrcPath = new Path();
        mCornerPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStrokeWidth(0.5f);
        mBorderColor = getResources().getColor(R.color.color_999999);
        mBorderPaint.setColor(mBorderColor);
        mRoundRect = new RectF();
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint
                .FILTER_BITMAP_FLAG);
        mBubbleHeight = DensityUtil.dip2px(getContext(), 13);
        setTextPadding(mRightTextPadding, mLeftTextPadding);
        mIsShowBorder = true;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }


    public void setShowBorder(boolean showBorder) {
        mIsShowBorder = showBorder;
    }

    public void setLoadingBackColor(int loadingBackColor) {
        if (loadingBackColor <= 0) {
            mLoadingBackColor = 0;
            return;
        }
        mLoadingBackColor = getResources().getColor(loadingBackColor);
    }


    public void setBorderColor(int borderColor) {
        if (borderColor <= 0) {
            mBorderColor = 0;
            return;
        }
        mBorderColor = getResources().getColor(borderColor);
        mBorderPaint.setColor(borderColor);
    }

    private void setTextPadding(int rightTextPadding, int leftTextPadding) {
        setPadding(leftTextPadding, getPaddingTop(), rightTextPadding,
                getPaddingBottom() + mBubbleHeight);
    }


    public void updateView() {
        createBubbleLayout();
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
     * location，0,1,2分别为左侧、中间、右侧尖角
     */
    public void setLocation(int location) {
        mLocation = location;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;

    }

    private void createBubbleLayout() {
        if (mBubbleBitmap != null && !mBubbleBitmap.isRecycled()) {
            mBubbleBitmap.recycle();
        }
        if (mWidth == 0 || mHeight == 0) {
            LogUtil.w(TAG, "mWidth==0||mHeight==0");
            return;
        }
        mBubbleBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mBubbleCanvas.setBitmap(mBubbleBitmap);
        drawBubblePath(mBubbleCanvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //创建气泡布局
        createBubbleLayout();
        canvas.setDrawFilter(mPaintFlagsDrawFilter);
        int saveCount = canvas.saveLayerAlpha(0, 0, mWidth, mHeight, 255,
                Canvas.ALL_SAVE_FLAG);
        drawBackColor(canvas);
        super.dispatchDraw(canvas);

        mPaint.setXfermode(mPorterDuffXfermode);
        //绘制气泡部分，和 super.onDraw(canvas);绘制的画面利用xfermode做叠加计算
        canvas.drawBitmap(mBubbleBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
        if (mIsShowBorder && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mBorderColor != 0) {
            //绘制气泡的四周边框
            canvas.drawPath(mSrcPath, mBorderPaint);
        }
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
        mRoundRect.set(0, 0, mWidth, mHeight - mBubbleHeight);

        mSrcPath.addRoundRect(mRoundRect, mRoundRadius, mRoundRadius, Path.Direction.CW);
        //尖角的宽度
        int popWidth = DensityUtil.dip2px(getContext(), 26);
        if (mLocation == 2) {
            //给path增加右侧的犄角，形成气泡效果
            mCornerPath.moveTo(mWidth - mWidthDiff, mHeight - mBubbleHeight);
            mCornerPath.lineTo(mWidth - mWidthDiff - popWidth / 2, mHeight);
            mCornerPath.lineTo(mWidth - mWidthDiff - popWidth, mHeight - mBubbleHeight);
        } else if (mLocation == 1) {
            //给path增加中间的犄角，形成气泡效果
            int startPos = mWidth / 2 - popWidth / 2;
            mCornerPath.moveTo(startPos, mHeight - mBubbleHeight);
            mCornerPath.lineTo(startPos + popWidth / 2, mHeight);
            mCornerPath.lineTo(startPos + popWidth, mHeight - mBubbleHeight);
        } else {
            //给path增加左侧的犄角，形成气泡效果
            mCornerPath.moveTo(mWidthDiff, mHeight - mBubbleHeight);
            mCornerPath.lineTo(mWidthDiff + popWidth / 2, mHeight);
            mCornerPath.lineTo(mWidthDiff + popWidth, mHeight - mBubbleHeight);
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

    /**
     * 计算指定的anchor View 在屏幕中的坐标。
     */
    private RectF calcViewScreenLocation(View anchor) {
        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
        anchor.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + anchor.getWidth(),
                location[1] + anchor.getHeight());
    }

    public boolean isDismiss() {
        return mIsDismiss;
    }

    /**
     * 隐藏气泡
     */
    public void hide() {
        Activity context = (Activity) (getContext());
        FrameLayout decorView = (FrameLayout) context.getWindow().getDecorView();
        decorView.removeView(this);
        mIsDismiss = true;
    }


    /**
     * 显示气泡
     *
     * @param activity
     * @param anchor   以谁为参照物
     * @param width    气泡整体大小，宽度
     * @param height   气泡整体高度
     */
    public void show(final Activity activity, final View anchor, int width, int height) {
        hide();
        final View view = this;
        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();

        RectF rectF = calcViewScreenLocation(anchor);
        width = DensityUtil.dip2px(getContext(), width);
        height = DensityUtil.dip2px(getContext(), height);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        int marginTop = (int) (rectF.top - layoutParams.height);
        int marginLeft = (int) (rectF.left - (layoutParams.width - anchor.getWidth()) / 2);
        marginTop = marginTop <= 0 ? 0 : marginTop;
        int location = 0;
        int displayWidth = DensityUtil.getDisplayWidth(getContext());
        if (marginLeft < 0) {
            //如果锚点view的左间距不够显示气泡
            location = 0;
            marginLeft = DensityUtil.dip2px(getContext(), 14);
        } else if (displayWidth < width + marginLeft) {
            //如果屏幕宽度不够显示当前左间距下的气泡
            location = 2;
            marginLeft = displayWidth - DensityUtil.dip2px(getContext(), 14) - width;
        } else {
            //尖角显示在中间
            location = 1;
        }
        setLocation(location);
        layoutParams.setMargins(marginLeft, marginTop, layoutParams.rightMargin, layoutParams.bottomMargin);
        view.setLayoutParams(layoutParams);
        decorView.addView(view);
        mIsDismiss = false;
    }


}
