package com.tc.bubblelayout;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Pair;

import com.facebook.soloader.SysUtil;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
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


    public void setInitSuccess(String moduleName, boolean initSuccess) {
        mInitModuleSOMap.put(moduleName, initSuccess);
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
                    LogUtil.i(TAG, "[copySo] Build.CPU_ABI supported api:" + Build.CPU_ABI + " second:" + Build
                            .CPU_ABI2);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LogUtil.i(TAG, "[copySo] supported api:" + Arrays.asList(Build.SUPPORTED_64_BIT_ABIS) +
                                " " + Arrays.asList(Build.SUPPORTED_32_BIT_ABIS));
                    }

                    boolean isArm64 = false;
                    // TODO: 2019/4/28 这里要从服务器动态获取armeabi 和armeabi-v7a、armabi-v8a等对应文件。测试代码暂时默认获取v7a
                    for (String supportedAbi : supportedAbis) {
                        LogUtil.i(TAG, "supportedAbi:" + supportedAbi);
                        if (Build.CPU_ABI.equals(supportedAbi)) {
                            if ("arm64-v8a".equals(Build.CPU_ABI)) {
                                isArm64 = true;
                            }
                        }
                    }
                    //用来区分abi拷贝记录，识别是否需要进行拷贝当前so文件到内部存储的系统目录里
                    String abiName;
                    if (isArm64) {
                        abiName = "arm64-v8a";
                        soFilePath = fromPath + "/arm64-v8a";
                    } else {
                        abiName = "armeabi-v7a";
                        soFilePath = fromPath + "/armeabi-v7a";
                    }
                    File newSOLibPath = context.getDir(LIBS_DIR_NAME + soModuleName, Context.MODE_PRIVATE);
                    LogUtil.i(TAG, "local cache SO lib source path:" + soFilePath + " ,  new SO lib source path:"
                            + newSOLibPath.getPath());
                    int result = copySOToSystemDir(soFilePath, newSOLibPath.getAbsolutePath(), context, abiName,
                            soModuleName);
                    if (result == -1) {
                        setInitSuccess(soModuleName, false);
                        subscriber.onNext(new Pair<>(soModuleName, false));
                        LogUtil.e(TAG, "init SO lib file fail:" + soModuleName);
                        subscriber.onCompleted();
                        return;
                    }
                    //反射把刚才的SO存放目录添加到SO加载目录列表里
                    TinkerLoadLibrary.installNativeLibraryPath(context.getClassLoader(), newSOLibPath);
                    //初始化SO目录完成，可以通知应用层加载对应模块数据
                    setInitSuccess(soModuleName, true);
                    subscriber.onNext(new Pair<>(soModuleName, true));
                    LogUtil.i(TAG, "init SO lib file success:" + soModuleName);
                } catch (Throwable e) {
                    setInitSuccess(soModuleName, false);
                    subscriber.onNext(new Pair<>(soModuleName, false));
                    LogUtil.e(TAG, "load SO lib file fail", e);
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
     * @param fromPath 指定的下载目录
     * @param toPath   应用的包路径,最终识别SO文件的目录
     */
    private static int copySOToSystemDir(String fromPath, String toPath, Context context, String abiName, String
            soModuleName) {
        //要复制的文件目录
        File root = new File(fromPath);
        //如同判断SD卡是否存在或者文件是否存在,如果不存在则 return出去
        if (!root.exists()) {
            LogUtil.e(TAG, "SO root file is not exist,fromPath:" + fromPath);
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        File[] currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toPath);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        LogUtil.i(TAG, String.format(Locale.ENGLISH, "abiName:%s  ,soModuleName:%s  ,SO root files:%d", abiName,
                soModuleName, currentFiles.length));
        if (currentFiles != null && currentFiles.length > 0) {
            //遍历要复制该目录下的全部文件
            for (File currentFile : currentFiles) {
                if (currentFile.isDirectory()) {
                    //如果当前项为子目录 进行递归
                    copySOToSystemDir(currentFile.getPath() + "/", toPath + currentFile.getName() + "/", context,
                            abiName, soModuleName);
                } else {
                    String copyFilePath = toPath + File.separator + currentFile.getName();

                    // TODO: 2019/4/26 根据版本号进行变更，动态替换
                    //如果当前项为文件则进行文件拷贝
                    if (currentFile.getName().contains(".so")) {
                        SharedTool instance = SharedTool.getInstance(context);
                        //上次更新so的时间
                        long updateSOTime = URegex.convertLong(instance.getString(currentFile.getPath() + abiName));
                        //最终拷贝到系统目录的so文件
                        File soCopyFile = new File(copyFilePath);
                        if (soCopyFile.exists() && updateSOTime == currentFile.lastModified()) {
                            LogUtil.w(TAG, String.format(Locale.ENGLISH, "abiName:%s  ,soModuleName:%s  ,had copy to " +
                                    "system Dir:%s", abiName, soModuleName, currentFile.getName()));
                        } else {
                            createOrExistsFile(copyFilePath);
                            boolean copyFileSuccess = copyFile(currentFile.getPath(), copyFilePath);
                            //记录下修改so的时间
                            instance.saveString(currentFile.getPath() + abiName, String.valueOf(currentFile
                                    .lastModified()));
                            LogUtil.i(TAG, String.format(Locale.ENGLISH, "current SO file time " +
                                            ":%d  ,record  updateSOTime :%d .start copy to system Dir:%s " +
                                            " ,copy file command result:%s  , abiName:%s , soModuleName:%s",
                                    currentFile.lastModified(), updateSOTime, currentFile.getName(), copyFileSuccess,
                                    abiName, soModuleName));
                        }

                    }
                }
            }
        }
        return 0;
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null) {
            return false;
        }
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(String filePath) {
        return TextUtils.isEmpty(filePath) ? null : new File(filePath);
    }

    /**
     * 判断文件或者文件夹是否存在
     *
     * @param dirPath 文件或文件夹绝对路径
     * @return
     */
    public static boolean isFileExists(String dirPath) {
        File file = new File(dirPath);
        return file.exists();
    }


    public static boolean copyFile(String oldPath, String newPath) {
        if (!isFileExists(oldPath)) {
            LogUtil.e(TAG, "this file is not exist:" + oldPath);
            return false;
        }
        if (!isFileExists(newPath)) {
            LogUtil.e(TAG, "this file is not exist:" + newPath);
            return false;
        }
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(oldPath);
            outputStream = new FileOutputStream(newPath);
            byte[] buffer = new byte[1024];
            while ((inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, buffer.length);
            }
            outputStream.flush();
            return true;
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, e);
        } catch (IOException e) {
            LogUtil.e(TAG, e);
        } finally {
            closeIO(inputStream, outputStream);
        }
        return false;
    }

    /**
     * 关流操作
     *
     * @param closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            Closeable[] var4 = closeables;
            int var3 = closeables.length;

            for (int var2 = 0; var2 < var3; ++var2) {
                Closeable cb = var4[var2];

                try {
                    if (cb != null) {
                        cb.close();
                    }
                } catch (IOException var6) {
                    LogUtil.e(TAG, var6);
                }
            }

        }
    }
}
