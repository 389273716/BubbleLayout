package com.tc.bubblelayout;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri uri = Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536753048164&di=83b9c0277f5ca3df0f214becc465527c&imgtype=0&src=http%3A%2F%2Fpic150.nipic.com%2Ffile%2F20171222%2F21540071_162503708000_2.jpg");
        final SimpleDraweeView sdv2 = findViewById(R.id.sdv_img);
        loadGIFImg(uri,sdv2);
        sdv2.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
                final BubblePopGroupView bubblePopGroupView = (BubblePopGroupView) LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.include_pop_emoji_bubble, null);
                bubblePopGroupView.setLoadingBackColor(R.color.chat_emoji_back);
                bubblePopGroupView.setBorderColor(R.color.chat_emoji_bubble_border);
                bubblePopGroupView.setShowBorder(true);
                final RoundCornerSimpleDraweeView sdvPopImg = (RoundCornerSimpleDraweeView) bubblePopGroupView.findViewById(R.id.sdv_pop_img);
                sdvPopImg.setLoadingBackColor(R.color.chat_emoji_back);
                sdvPopImg.setRoundRadius(10);
                bubblePopGroupView.show(MainActivity.this, sdv2, 161, 161);
                final Uri uri = Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1538980461934&di=06bc2dc85608f9124869a640b3724332&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F20171_31_11%2Fa8debe8737775787542.gif");

                loadGIFImg(uri, sdvPopImg);
                bubblePopGroupView.updateView();
            }
        });

    }


    private void loadGIFImg(Uri path, SimpleDraweeView simpleDraweeView) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(path)
                .setProgressiveRenderingEnabled(true)
                .setAutoRotateEnabled(true)
                .build();
        GenericDraweeHierarchy hierarchy =
                new GenericDraweeHierarchyBuilder(getApplicationContext().getResources())
                        .setPlaceholderImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                        .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                        .build();
        simpleDraweeView.setHierarchy(hierarchy);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        simpleDraweeView.setController(controller);
    }


}
