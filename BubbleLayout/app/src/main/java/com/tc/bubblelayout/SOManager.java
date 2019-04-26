package com.tc.bubblelayout;

import android.content.Context;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public static final String LIBS_DIR_NAME = "libs";

    private SOManager() {
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

    public void setInitSuccess(boolean initSuccess) {
        mInitSuccess = initSuccess;
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
    public void copyAndInitSoFileToSystem(final Context context, final String soModuleName, Subscriber sub) {

        Observable<Object> objectObservable = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                final String fromPath = Environment.getExternalStorageDirectory().getPath() + "/test_so/" +
                        soModuleName;

                String soFilePath = "";
                try {
                    LogUtil.i(TAG, "copyAndInitSoFileToSystem");
//        String[] supportedAbis = SysUtil.getSupportedAbis();

//        boolean isArm64 = false;
//        for (String supportedAbi : supportedAbis) {
//            LogUtil.i(TAG, "supportedAbi:" + supportedAbi);
//            if ("arm64-v8a".equals(supportedAbi)) {
//                isArm64 = true;
//            }
//        }
//        if (isArm64) {
//            fromPath = fromPath + "/arm64-v8a";
//        } else {
                    soFilePath = fromPath + "/armeabi-v7a";
//        }
                    File dir = context.getDir(LIBS_DIR_NAME + soModuleName, Context.MODE_PRIVATE);
//            if (!isLoadSoFile(dir)) {
                    copy(soFilePath, dir.getAbsolutePath());
//            }
                    TinkerLoadLibrary.installNativeLibraryPath(context.getClassLoader(), dir);
                    LogUtil.i(TAG, "prependSoSource");
//            SoLoaderShim.setHandler(new SoLoaderShim.Handler() {
//                @Override
//                public void loadLibrary(String libraryName) {
//                    System.load(absolutePath + "/" + libraryName);
//                    LogUtil.i(TAG, "load libraryName:" + libraryName);
//                }
//            });
//            SoLoader.prependSoSource(new DirectorySoSource(file, 0));
                    mInitSuccess = true;
                    LogUtil.i(TAG, "init so file success.");
                } catch (Throwable e) {
                    LogUtil.e(TAG, "load so file fail", e);
                }

                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        if (sub != null) {
            objectObservable.subscribe(sub);
        } else {
            objectObservable.subscribe(new Subscriber<Object>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.e(TAG, e);
                }

                @Override
                public void onNext(Object o) {

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
        if (currentFiles != null && currentFiles.length > 0) {
            //遍历要复制该目录下的全部文件
            for (File currentFile : currentFiles) {
                if (currentFile.isDirectory()) {
                    //如果当前项为子目录 进行递归
                    copy(currentFile.getPath() + "/", toFile + currentFile.getName() + "/");
                } else {
                    String toPath = toFile + File.separator + currentFile.getName();
                    // TODO: 2019/4/26 根据版本号进行变更，动态替换 。&& !FileUtil.isFileExists(toPath)
                    //如果当前项为文件则进行文件拷贝
                    if (currentFile.getName().contains(".so")) {
                        LogUtil.i(TAG, "copy so file:" + currentFile.getName());
                        int id = copySdcardFile(currentFile.getPath(), toPath);
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
        } catch (Exception ex) {
            result = -1;
        } finally {
            // 关闭文件流
            try {
                if (baos != null) {
                    baos.close();
                }
                if (fosto != null) {
                    fosto.close();
                }
                if (fosfrom != null) {
                    fosfrom.close();
                }
            } catch (IOException e) {
                LogUtil.e(TAG, e);
            }

        }
        return result;
    }
}
