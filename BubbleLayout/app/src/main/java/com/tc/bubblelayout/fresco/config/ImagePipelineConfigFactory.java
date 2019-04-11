/*
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only.  Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tc.bubblelayout.fresco.config;


import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.tc.bubblelayout.LogUtil;

import java.io.File;
import java.util.Locale;

/**
 * Creates ImagePipeline configuration for the sample app
 */
public class ImagePipelineConfigFactory {
    private static final String TAG = "ImagePipelineConfigFactory";
    private static final int MAX_MEMORY_CACHE_SIZE = 80 * ByteConstants.MB;
    private static final int MAX_DISK_CACHE_SIZE = 500 * ByteConstants.MB;
    private static final int MAX_DISK_CACHE_SIZE_ON_LOW_DISK_SPACE = 60 * ByteConstants.MB;
    private static final int MAX_DISK_CACHE_SIZE_ON_VERY_LOW_DISK_SPACE = 30 * ByteConstants.MB;

    private static int mMaxMemoryCacheSize = MAX_MEMORY_CACHE_SIZE;
    private static int mMaxDiskCacheSize = MAX_MEMORY_CACHE_SIZE;

    /**
     * 可以设置最大的内存缓存大小
     *
     * @param memoryCacheSize 内存缓存大小,单位MB
     */
    public static void setMaxMemoryCacheSize(int memoryCacheSize) {
        mMaxMemoryCacheSize = memoryCacheSize * ByteConstants.MB;
    }

    /**
     * 可以设置最大的磁盘缓存大小
     *
     * @param diskCacheSize 磁盘缓存大小,单位MB
     */
    public static void setMaxDiskCacheSize(int diskCacheSize) {
        mMaxDiskCacheSize = diskCacheSize * ByteConstants.MB;
    }

    /**
     * 图片配置
     *
     * @param context 上下文
     * @return ImagePipelineConfig
     */
    public static ImagePipelineConfig getImagePipelineConfig(Context context) {

        if (mMaxDiskCacheSize <= 0) {
            mMaxDiskCacheSize = MAX_DISK_CACHE_SIZE;
        }
        if (mMaxMemoryCacheSize <= 0) {
            mMaxMemoryCacheSize = MAX_MEMORY_CACHE_SIZE;
        }

        //图片配置
        ImagePipelineConfig.Builder imagePipelineConfigBuilder = ImagePipelineConfig.newBuilder
                (context);

        imagePipelineConfigBuilder
                .setBitmapMemoryCacheParamsSupplier(
                        new Supplier<MemoryCacheParams>() {
                            @Override
                            public MemoryCacheParams get() {
                                int currentCount = (int) Runtime.getRuntime().maxMemory();
                                int currentMaxMemory = currentCount / 5;
                                if (currentMaxMemory > mMaxMemoryCacheSize) {
                                    LogUtil.w(TAG, "当前图片内存分配总大小,分配过大，减少内存缓存总大小,mMaxMemoryCacheSize:" +
                                            mMaxMemoryCacheSize);
                                    currentMaxMemory = mMaxMemoryCacheSize;
                                }

                                LogUtil.e(TAG, "当前图片内存分配总大小:" + String.valueOf(currentMaxMemory));
                                MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                                        // Max cache entry size
                                        currentMaxMemory,
                                        // Max total size of elements in the cache
                                        Integer.MAX_VALUE,
                                        // Max entries in the cache
                                        currentMaxMemory,
                                        // Max total size of elements in eviction queue
                                        Integer.MAX_VALUE,
                                        // Max length of eviction queue
                                        Integer.MAX_VALUE);
                                return bitmapCacheParams;
                            }
                        })

                //磁盘缓存配置
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(context)
                        .setBaseDirectoryPath(getExternalCacheDir(context))
                        .setBaseDirectoryName(context.getPackageName())
                        //默认缓存的最大大小。
                        .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                        // 缓存的最大大小,使用设备时低磁盘空间。
                        .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_SIZE_ON_LOW_DISK_SPACE)
                        // 缓存的最大大小,当设备极低磁盘空间
                        .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_SIZE_ON_VERY_LOW_DISK_SPACE)
                        .build());

        NoOpMemoryTrimmableRegistry.getInstance().registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();

                LogUtil.e(TAG, String.format(Locale.ENGLISH, "onCreate suggestedTrimRatio : %d", (int)
                        suggestedTrimRatio));
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() ==
                        suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground
                        .getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground
                        .getSuggestedTrimRatio() == suggestedTrimRatio
                        ) {
                    ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
                }
            }
        });

        //
//        imagePipelineConfigBuilder.setBitmapMemoryCacheParamsSupplier(bitmapCacheParamsSupplier);
//        imagePipelineConfigBuilder.setCacheKeyFactory(cacheKeyFactory);

//        imagePipelineConfigBuilder.setEncodedMemoryCacheParamsSupplier
// (encodedCacheParamsSupplier);
        //配置线程
//        imagePipelineConfigBuilder.setExecutorSupplier(executorSupplier);
        //配置统计跟踪器
//        imagePipelineConfigBuilder.setImageCacheStatsTracker(imageCacheStatsTracker);

        // 当builder.setResizeOptions(new ResizeOptions(width, height));时，  防止出现OOM，
        imagePipelineConfigBuilder.setDownsampleEnabled(true);
//        imagePipelineConfigBuilder.setMemoryTrimmableRegistry(memoryTrimmableRegistry);
//        imagePipelineConfigBuilder.setNetworkFetchProducer(networkFetchProducer);
//        imagePipelineConfigBuilder.setPoolFactory(poolFactory);
        imagePipelineConfigBuilder.setProgressiveJpegConfig(new SimpleProgressiveJpegConfig());
//        imagePipelineConfigBuilder.setRequestListeners(requestListeners);
//        imagePipelineConfigBuilder.setSmallImageDiskCacheConfig(smallImageDiskCacheConfig);
        return imagePipelineConfigBuilder.build();
    }

    static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= 8;
    }

    static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        return new File(folderPath, fileName);
    }

    static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        } else {
            String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
            return createFile(Environment.getExternalStorageDirectory().getPath() + cacheDir, "");
        }
    }


}
