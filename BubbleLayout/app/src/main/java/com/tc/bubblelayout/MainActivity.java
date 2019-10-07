package com.tc.bubblelayout;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
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
import com.facebook.soloader.SysUtil;

import rx.Subscriber;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int mRightEntranceMode;
    private final int ENTRANCE_CHAT_LOCATION = 0x001;
    private final int ENTRANCE_MEDIA = 0x010;
    private final int ENTRANCE_PHOTO = 0x100;
    /**
     * 入口全部需要显示
     */
    private final int ENTRANCE_MORE = 0x111;
    private final int ENTRANCE_NONE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] supportedAbis = SysUtil.getSupportedAbis();
        for (String supportedAbi : supportedAbis) {
            LogUtil.i(TAG, "supportedAbi:" + supportedAbi);
        }
        LogUtil.d(TAG, "cpu is :" + CpuUtil.getArchType());
        //测试动态下载so文件，用来研究so文件动态下载拷贝，减轻apk大小，对应的目录文件需要提前拷贝到手机，模拟下载完成后的场景
//        testLoadSO();
        testFresco();


    }



    private void testLoadSO() {
        //把测试so的文件拷贝到对应目录
        SOManager.getInstance().copyAndInitSoFileToSystem(getApplicationContext(), "fresco", new Subscriber<Pair>() {
            @Override
            public void onCompleted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i(TAG, "testFresco");
                        testFresco();
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, e);
            }

            @Override
            public void onNext(Pair pair) {

            }
        });
        SOManager.getInstance().copyAndInitSoFileToSystem(getApplicationContext(), "shortvideo", new Subscriber<Pair>
                () {
            @Override
            public void onCompleted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i(TAG, "shortvideo");
                        System.loadLibrary("pldroid_amix");
//                        ReLinker.loadLibrary(getApplicationContext(),"pldroid_amix");
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, e);
            }

            @Override
            public void onNext(Pair pair) {

            }
        });


    }


    private void testFresco() {
        Uri uri = Uri.parse("https://timgsa.baidu" +
                ".com/timg?image&quality=80&size=b9999_10000&sec=1536753048164&di=83b9c0277f5ca3df0f214becc465527c" +
                "&imgtype=0&src=http%3A%2F%2Fpic150.nipic.com%2Ffile%2F20171222%2F21540071_162503708000_2.jpg");
        final SimpleDraweeView sdv2 = findViewById(R.id.sdv_img);
        loadGIFImg(uri,sdv2);

        sdv2.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
                final BubblePopGroupView bubblePopGroupView = (BubblePopGroupView) LayoutInflater.from(MainActivity
                        .this)
                        .inflate(R.layout.include_pop_emoji_bubble, null);
                bubblePopGroupView.setLoadingBackColor(R.color.color_e6ffffff);
                bubblePopGroupView.setBorderColor(R.color.color_e6cbcbcb);
                bubblePopGroupView.setShowBorder(true);
                final RoundCornerSimpleDraweeView sdvPopImg = (RoundCornerSimpleDraweeView) bubblePopGroupView
                        .findViewById(R.id.sdv_pop_img);
                sdvPopImg.setLoadingBackColor(R.color.color_e6ffffff);
                sdvPopImg.setRoundRadius(10);
                bubblePopGroupView.show(MainActivity.this, sdv2, 161, 161);
                final Uri uri = Uri.parse("https://timgsa.baidu" +
                        ".com/timg?image&quality=80&size=b9999_10000&sec=1538980461934&di" +
                        "=06bc2dc85608f9124869a640b3724332&imgtype=0&src=http%3A%2F%2Fs9.rr.itc" +
                        ".cn%2Fr%2FwapChange%2F20171_31_11%2Fa8debe8737775787542.gif");

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
