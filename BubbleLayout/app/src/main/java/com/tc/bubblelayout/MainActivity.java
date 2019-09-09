package com.tc.bubblelayout;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
//        testLoadSO();
//        testFresco();

//        setMode(ENTRANCE_CHAT_LOCATION | ENTRANCE_MEDIA);
//        printcontainModes();
//        Log.i(TAG, "onCreate: mRightEntranceMode == ENTRANCE_NONE" + (mRightEntranceMode == ENTRANCE_NONE));
//        setMode(ENTRANCE_PHOTO);
//        printcontainModes();
//        Log.i(TAG, "onCreate: mRightEntranceMode == ENTRANCE_MORE" + (mRightEntranceMode == ENTRANCE_MORE));
//        testErrorEvent();
//        testErrorEvent2();
//        testErrorEvent3();
    }

    private void testErrorEvent3() {

        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1);
                subscriber.onNext(2);
                if (true) {
//                                    subscriber.onError(new Throwable("jjjjjj"));
//                    throw new NullPointerException("null3333");
                }
                subscriber.onNext(3);
                subscriber.onCompleted();
            }
        })
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(final Integer o) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                if (o.equals(2)) {
//                                    subscriber.onError(new Throwable("jjjjjj"));
                                    throw new NullPointerException("null3333");
                                }
                                subscriber.onNext("" + o);
                                subscriber.onCompleted();
                            }
                        });
                    }
                })


                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: 333");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(String o) {
                        Log.i(TAG, "onNext: " + o);
                    }
                });
    }

    private void testErrorEvent() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onCompleted();
            }
        })
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(final Integer o) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                if (o.equals(2)) {
//                                    subscriber.onError(new Throwable("jjjjjj"));
                                    throw new NullPointerException("null");
                                }
                                subscriber.onNext("" + o);
                                subscriber.onCompleted();
                            }
                        }).onErrorResumeNext(new Func1<Throwable, Observable<? extends String>>() {
                            @Override
                            public Observable<? extends String> call(Throwable throwable) {
                                return Observable.create(new Observable.OnSubscribe<String>() {
                                    @Override
                                    public void call(Subscriber<? super String> subscriber) {
                                        subscriber.onNext("on error next event");
                                        subscriber.onCompleted();
                                    }
                                });
                            }
                        });
                    }
                })


                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: 222");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(String o) {
                        Log.i(TAG, "onNext: " + o);
                    }
                });
    }

    private void testErrorEvent2() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onCompleted();
            }
        })
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override
                    public Observable<String> call(final Integer o) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                if (o.equals(2)) {
//                                    subscriber.onError(new Throwable("jjjjjj"));
                                    throw new NullPointerException("null");
                                }
                                subscriber.onNext("" + o);
                                subscriber.onCompleted();
                            }
                        }).onErrorReturn(new Func1<Throwable, String>() {
                            @Override
                            public String call(Throwable throwable) {
                                return throwable.getMessage();
                            }
                        });
                    }
                })


                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: 222");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onNext(String o) {
                        Log.i(TAG, "onNext: " + o);
                    }
                });
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
//        loadGIFImg(uri,sdv2);

        sdv2.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
                final BubblePopGroupView bubblePopGroupView = (BubblePopGroupView) LayoutInflater.from(MainActivity
                        .this)
                        .inflate(R.layout.include_pop_emoji_bubble, null);
                bubblePopGroupView.setLoadingBackColor(R.color.chat_emoji_back);
                bubblePopGroupView.setBorderColor(R.color.chat_emoji_bubble_border);
                bubblePopGroupView.setShowBorder(true);
                final RoundCornerSimpleDraweeView sdvPopImg = (RoundCornerSimpleDraweeView) bubblePopGroupView
                        .findViewById(R.id.sdv_pop_img);
                sdvPopImg.setLoadingBackColor(R.color.chat_emoji_back);
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

    public void setMode(int mode) {
        this.mRightEntranceMode |= mode;
    }

    public boolean containMode(int mode) {
        return (this.mRightEntranceMode & mode) != 0;
    }

    public void clearMode(int mode) {
        this.mRightEntranceMode &= ~mode;
    }

    public void resetMode() {
        this.mRightEntranceMode &= ENTRANCE_NONE;
    }

    public void printcontainModes() {
        List<String> list = new ArrayList<>();
        if (containMode(ENTRANCE_CHAT_LOCATION)) {
            list.add("ENTRANCE_CHAT_LOCATION");
        }
        if (containMode(ENTRANCE_PHOTO)) {
            list.add("ENTRANCE_PHOTO");
        }
        if (containMode(ENTRANCE_MEDIA)) {
            list.add("ENTRANCE_MEDIA");
        }

        System.out.println(list);
    }


    static class NumTest {
        public int i;

        public synchronized void update() {
            for (int j = 0; j < 10; j++) {
                try {
                    Thread.sleep(100);
                    i = 10;
                    Log.i(TAG, "j--" + j + "run2: " + i);
                } catch (InterruptedException e) {
                    Log.e(TAG, "refresh2: ", e);
                }
            }

        }

        public void refresh() {
            synchronized (this) {
                for (int j = 0; j < 10; j++) {
                    try {
                        Thread.sleep(100);
                        i = 100;
                        Log.i(TAG, "j--" + j + " run1: " + i);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "refresh1: ", e);
                    }
                }
            }
        }

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
