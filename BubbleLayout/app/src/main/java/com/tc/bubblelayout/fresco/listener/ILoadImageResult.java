package com.tc.bubblelayout.fresco.listener;

import android.graphics.Bitmap;

/**
 * 异步加载图片
 * Created by htliu on 2017/3/23.
 */

public interface ILoadImageResult {
    public void onResult(Bitmap bitmap);
    public void onError(String msg);
}
