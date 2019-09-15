package com.tc.bubblelayout.testrecylerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * author：   tc
 * date：      2019/9/15 & 11:12
 * version    1.0
 * description 动态生成带圆角的drawable
 * modify by
 */
public class GroupPressBackgroundDrawable extends Drawable {

    private Paint mPaint;
    private int backgroundColor = -1;
    private float[] corners;
    private RectF rectF;
    private Context mContext;
    private Path srcPath;

    public GroupPressBackgroundDrawable(int backgroundColor, float[] corners, View child, Context context) {
        this.backgroundColor = backgroundColor;
        this.corners = corners;
        mContext = context;
        srcPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        rectF = new RectF(0, 0, child.getWidth(), child.getHeight());
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (backgroundColor != -1) {
            mPaint.setColor(mContext.getResources().getColor(backgroundColor));
            //默认无圆角
            srcPath.addRoundRect(rectF, corners, Path.Direction.CCW);
            canvas.drawPath(srcPath, mPaint);
        }

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
