package com.tc.bubblelayout;

import android.content.Context;
import android.os.Environment;
import android.util.Pair;

import com.facebook.soloader.SysUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * author：   tc
 * date：      2019/4/25 & 10:28
 * version    1.0
 * description  动态加载SO库
 * modify by
 */
public class SOManager {

    private static final String TAG = "SOManager";
    public static final String LIBS_DIR_NAME = "libs_";
    private final ConcurrentHashMap<String, Boolean> mInitModuleSOMap;

    private SOManager() {
        mInitModuleSOMap = new ConcurrentHashMap<>(10);
    }

    private static class SingleInstance {
        private static final SOManager INSTANCE = new SOManager();
    }

    public static SOManager getInstance() {
        return SingleInstance.INSTANCE;
    }

    private boolean mInitSuccess;

    public boolean isInitSuccess() {
        return mInitSuccess;
    }

    public void setInitSuccess(String moduleName, boolean initSuccess) {
        mInitSuccess = initSuccess;
        mInitModuleSOMap.put(moduleName, mInitSuccess);
    }

    /**
     * 加载 so 文件(直接指定你so下载的路径即可)
     *
     * @param context
     * @param soModuleName so对应的模块名
     */
    public void copyAndInitSoFileToSystem(final Context context, final String soModuleName) {
        copyAndInitSoFileToSystem(context, soModuleName, null);
    }

    /**
     * 加载 so 文件(直接指定你so下载的路径即可)
     *
     * @param context
     * @param soModuleName so对应的模块名
     */
    public void copyAndInitSoFileToSystem(final Context context, final String soModuleName, Subscriber<Pair> sub) {

        Observable<Pair> objectObservable = Observable.create(new Observable.OnSubscribe<Pair>() {
            @Override
            public void call(Subscriber<? super Pair> subscriber) {
                final String fromPath = Environment.getExternalStorageDirectory().getPath() + "/test_so/" +
                        soModuleName;

                String soFilePath = "";
                try {
                    LogUtil.i(TAG, "copyAndInitSoFileToSystem");
                    String[] supportedAbis = SysUtil.getSupportedAbis();

                    boolean isArm32 = false;
                    // TODO: 2019/4/28 这里要从服务器动态获取armeabi 和armeabi-v7a、armabi-v8a等对应文件。测试代码暂时默认获取v7a
                    for (String supportedAbi : supportedAbis) {
                        LogUtil.i(TAG, "supportedAbi:" + supportedAbi);
                        if ("armeabi-v7a".equals(supportedAbi)) {
                            isArm32 = true;
                        }
                    }
//                    if (!isArm32) {
//                        soFilePath = fromPath + "/arm64-v8a";
//                    } else {
                        soFilePath = fromPath + "/armeabi-v7a";
//                    }
                    LogUtil.i(TAG, "prependSoSource  soFilePath:" + soFilePath);
                    File dir = context.getDir(LIBS_DIR_NAME + soModuleName, Context.MODE_PRIVATE);
                    int result = copy(soFilePath, dir.getAbsolutePath());
                    if (result == -1) {
                        setInitSuccess(soModuleName, false);
                        subscriber.onNext(new Pair<>(soModuleName, false));
                        LogUtil.e(TAG, "init so file fail");
                        subscriber.onCompleted();
                        return;
                    }
                    TinkerLoadLibrary.installNativeLibraryPath(context.getClassLoader(), dir);

                    setInitSuccess(soModuleName, true);
                    subscriber.onNext(new Pair<>(soModuleName, true));
                    LogUtil.i(TAG, "init so file success.");
                } catch (Throwable e) {
                    LogUtil.e(TAG, "load so file fail", e);
                    subscriber.onNext(new Pair<>(soModuleName, false));
                }


                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        if (sub != null) {
            objectObservable.subscribe(sub);
        } else {
            objectObservable.subscribe(new Subscriber<Pair>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.e(TAG, e);
                }

                @Override
                public void onNext(Pair o) {

                }
            });
        }

    }


    /**
     * 判断 so 文件是否存在
     *
     * @param dir
     * @return
     */
    private static boolean isLoadSoFile(File dir, String soName) {
        File[] currentFiles;
        currentFiles = dir.listFiles();
        boolean hasSoLib = false;
        if (currentFiles == null) {
            return false;
        }
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].getName().contains(soName)) {
                hasSoLib = true;
            }
        }
        return hasSoLib;
    }

    /**
     * @param fromFile 指定的下载目录
     * @param toFile   应用的包路径
     * @return
     */
    private static int copy(String fromFile, String toFile) {
        //要复制的文件目录
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在,如果不存在则 return出去
        if (!root.exists()) {
            LogUtil.e(TAG, "so root file is not exist");
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        File[] currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        LogUtil.i(TAG, "so root files:" + currentFiles.length);
        if (currentFiles != null && currentFiles.length > 0) {
            //遍历要复制该目录下的全部文件
            for (File currentFile : currentFiles) {
                if (currentFile.isDirectory()) {
                    //如果当前项为子目录 进行递归
                    copy(currentFile.getPath() + "/", toFile + currentFile.getName() + "/");
                } else {
                    String toPath = toFile + File.separator + currentFile.getName();

                    // TODO: 2019/4/26 根据版本号进行变更，动态替换
                    //如果当前项为文件则进行文件拷贝
                    if (currentFile.getName().contains(".so")) {
                        //最终拷贝到系统目录的so文件
                        File soCopyFile = new File(toPath);
                        if (soCopyFile.exists() && currentFile.length() == soCopyFile.length() &&
                                currentFile.lastModified() == soCopyFile.lastModified()) {
                            LogUtil.w(TAG, "had copy so file:" + currentFile.getName());
                        } else {
                            int result = copySdcardFile(currentFile.getPath(), toPath);
                            LogUtil.i(TAG, "start copy so file:" + currentFile.getName() + "  command result:" +
                                    result);
                        }

                    }
                }
            }
        }
        return 0;
    }


    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    private static int copySdcardFile(String fromFile, String toFile) {
        FileInputStream fosfrom = null;
        FileOutputStream fosto = null;
        ByteArrayOutputStream baos = null;
        int result = -1;
        try {
            fosfrom = new FileInputStream(fromFile);
            fosto = new FileOutputStream(toFile);
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fosfrom.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            // 从内存到写入到具体文件
            fosto.write(baos.toByteArray());
            result = 0;
        } catch (Exception e) {
            result = -1;
            LogUtil.e(TAG, e);
        } finally {
            // 关闭文件流

            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, e);
                }
            }
            if (fosfrom != null) {
                try {
                    fosfrom.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, e);
                }
            }
            if (fosto != null) {
                try {
                    fosto.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, e);
                }
            }

        }


        LogUtil.i(TAG, String.format(Locale.ENGLISH, "fromFile %s ,toFile %s, command status:%d", fromFile, toFile,
                result));
        return result;
    }
}
