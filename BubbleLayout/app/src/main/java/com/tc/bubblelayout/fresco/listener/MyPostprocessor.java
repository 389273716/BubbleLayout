package com.tc.bubblelayout.fresco.listener;

import android.graphics.Bitmap;

import com.facebook.imagepipeline.request.BasePostprocessor;
import com.tc.bubblelayout.fresco.blur.BitmapBlurHelper;

/**
 * Created by 3020 on 2016/12/5.
 */

public class MyPostprocessor extends BasePostprocessor {
    public final static String BLUR = "blur";
    public final static String RED_MESH = "red_mesh";
    private String type;
    public MyPostprocessor(String type){
        this.type = type;
    }

    @Override
    public void process(Bitmap bitmap) {
        if(type.equals(BLUR)){
            BitmapBlurHelper.blur(bitmap, 35);
        }else if(type.equals(RED_MESH)){
            int width = bitmap.getWidth();         //获取位图的宽
            int height = bitmap.getHeight();       //获取位图的高
            int []pixels = new int[width * height]; //通过位图的大小创建像素点数组
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            int alpha = 0xFF << 24;
            for(int i = 0; i < height; i++)  {
                for(int j = 0; j < width; j++) {
                    int grey = pixels[width * i + j];
                    int red = ((grey  & 0x00FF0000 ) >> 16);
                    int green = ((grey & 0x0000FF00) >> 8);
                    int blue = (grey & 0x000000FF);
                    grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);
                    grey = alpha | (grey << 16) | (grey << 8) | grey;
                    pixels[width * i + j] = grey;
                }
            }
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        }
    }
}
