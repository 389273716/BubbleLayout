package com.tc.bubblelayout.fresco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.tc.bubblelayout.fresco.config.ImagePipelineConfigFactory;
import com.tc.bubblelayout.fresco.controller.SingleImageControllerListener;
import com.tc.bubblelayout.fresco.listener.ILoadImageResult;
import com.tc.bubblelayout.fresco.listener.MyPostprocessor;
import com.tc.bubblelayout.fresco.utils.DensityUtil;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Executors;

/**
 * 图片加载、处理工具类
 * Created by htliu on 2016/11/15.
 */

public class FrescoUtil {

    private FrescoUtil() {
    }

    private static Context mContext;

    /**
     * 可以设置最大的内存缓存大小
     *
     * @param memoryCacheSize 内存缓存大小,单位MB
     */
    public static void setMaxMemoryCacheSize(int memoryCacheSize) {
        ImagePipelineConfigFactory.setMaxMemoryCacheSize(memoryCacheSize);
    }

    /**
     * 可以设置最大的磁盘缓存大小
     *
     * @param diskCacheSize 磁盘缓存大小,单位MB
     */
    public static void setMaxDiskCacheSize(int diskCacheSize) {
        ImagePipelineConfigFactory.setMaxDiskCacheSize(diskCacheSize);
    }


    /**
     * 初始化 Fresco
     */
    public static void init(Context context) {
        mContext = context;
        Fresco.initialize(mContext, ImagePipelineConfigFactory.getImagePipelineConfig(mContext));
    }

    /**
     * 销毁 Fresco
     */
    public static void onDestroy() {
        Fresco.shutDown();
        mContext = null;
    }

    public static Builder with(SimpleDraweeView simpleDraweeView) {
        return new Builder().build(simpleDraweeView);
    }

    public static class Builder {

        private SimpleDraweeView mSimpleDraweeView;
        private int mWidth;
        private int mHeight;
        private float mAspectRatio;

        private RoundingParams mRoundingParams;//圆角圆环设置
        private int fadeDuration = -1;//淡入淡出动画时间
        private Drawable mPlaceHolderImage;//占位图
        private ScalingUtils.ScaleType placeholderImageScaleType;//占位图缩放方式
        private Drawable mProgressBarImage;//loading图
        private ScalingUtils.ScaleType progressBarImageScaleType;//loading图缩放方式
        private int progressBarAutoRotateInterval = 2000; //动画旋转周期
        private Drawable mRetryImage;//重试图
        private ScalingUtils.ScaleType retryImageScaleType;//重试图缩放方式
        private Drawable mFailureImage;//失败图
        private ScalingUtils.ScaleType failureImageScaleType;//失败图缩放方式
        private Drawable mBackgroundImage;//背景图
        private ScalingUtils.ScaleType mActualImageScaleType = ScalingUtils.ScaleType.CENTER_CROP;//图片缩放类型

        private boolean asGif = false;//是否是gif图片
        private boolean isReload = false;//是否重新加载
        private MyPostprocessor postprocessor;

        /**
         * 设置显示控件
         */
        public Builder build(SimpleDraweeView simpleDraweeView) {
            this.mSimpleDraweeView = simpleDraweeView;
            return this;
        }

        public Builder setAsGif() {
            this.asGif = true;
            return this;
        }

        public Builder asReload() {
            this.isReload = true;
            return this;
        }

        /**
         * 设置显示控件的宽
         */
        public Builder setWidth(int reqWidth) {
            this.mWidth = reqWidth;
            return this;
        }

        /**
         * 设置显示控件的高
         */
        public Builder setHeight(int reqHeight) {
            this.mHeight = reqHeight;
            return this;
        }

        /**
         * 设置显示控件的比例
         */
        public Builder setAspectRatio(float aspectRatio) {
            mAspectRatio = aspectRatio;
            return this;
        }

        /**
         * 设置淡入淡出动画效果的时间
         */
        public Builder setFadeDuration(int duration) {
            fadeDuration = duration;
            return this;
        }

        /**
         * 设置圆形图片
         */
        public Builder setAsCircle() {
            mRoundingParams = RoundingParams.asCircle();
            return this;
        }

        /**
         * 设置圆环图片
         *
         * @param ringColor 圆环颜色
         * @param ringWidth 圆环半径
         */
        public Builder setCircleRing(int ringColor, float ringWidth) {
            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setBorder(ringColor, ringWidth);
            roundingParams.setRoundAsCircle(true);
            mRoundingParams = roundingParams;
            return this;
        }

        /**
         * 设置圆角图片
         *
         * @param radius 圆角半径
         */
        public Builder setCornersRadius(float radius) {
            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setCornersRadius(radius);
            mRoundingParams = roundingParams;
            return this;
        }

        /**
         * 设置圆角图片
         *
         * @param radii 圆角集合
         */
        public Builder setCornersRadius(float[] radii) {
            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setCornersRadii(radii);
            mRoundingParams = roundingParams;
            return this;
        }

        /**
         * 设置圆角图片
         *
         * @param topLeft     左上圆角半径
         * @param topRight    右上圆角半径
         * @param bottomRight 右下圆角半径
         * @param bottomLeft  左下圆角半径
         */
        public Builder setCornersRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setCornersRadii(topLeft, topRight, bottomRight, bottomLeft);
            mRoundingParams = roundingParams;
            return this;
        }

        /**
         * 设置圆角环图片
         *
         * @param topLeft     左上圆角半径
         * @param topRight    右上圆角半径
         * @param bottomRight 右下圆角半径
         * @param bottomLeft  左下圆角半径
         * @param ringColor   圆环颜色
         * @param ringWidth   圆环半径
         */
        public Builder setCircleRadiusRing(float topLeft, float topRight, float bottomRight, float bottomLeft, int
                ringColor, float ringWidth) {
            RoundingParams roundingParams = new RoundingParams();
            roundingParams.setBorder(ringColor, ringWidth);
            roundingParams.setCornersRadii(topLeft, topRight, bottomRight, bottomLeft);
            mRoundingParams = roundingParams;
            return this;
        }

        public Builder setActualImageScaleType(ScalingUtils.ScaleType mActualImageScaleType) {
            this.mActualImageScaleType = mActualImageScaleType;
            return this;
        }

        public Builder setPlaceHolderImage(Drawable mPlaceHolderImage, ScalingUtils.ScaleType
                placeholderImageScaleType) {
            this.mPlaceHolderImage = mPlaceHolderImage;
            this.placeholderImageScaleType = placeholderImageScaleType;
            return this;
        }

        public Builder setProgressBarImage(Drawable mProgressBarImage, ScalingUtils.ScaleType
                progressBarImageScaleType, int progressBarAutoRotateInterval) {
            this.mProgressBarImage = mProgressBarImage;
            this.progressBarImageScaleType = progressBarImageScaleType;
            this.progressBarAutoRotateInterval = progressBarAutoRotateInterval;
            return this;
        }

        public Builder setRetryImage(Drawable mRetryImage, ScalingUtils.ScaleType retryImageScaleType) {
            this.mRetryImage = mRetryImage;
            this.retryImageScaleType = retryImageScaleType;
            return this;
        }

        public Builder setFailureImage(Drawable mFailureImage, ScalingUtils.ScaleType failureImageScaleType) {
            this.mFailureImage = mFailureImage;
            this.failureImageScaleType = failureImageScaleType;
            return this;
        }

        public Builder setBackgroundImage(Drawable mBackgroundImage) {
            this.mBackgroundImage = mBackgroundImage;
            return this;
        }

        public Builder setBackgroundImageColor(int colorId) {
            if (mSimpleDraweeView == null) {
                throw new NullPointerException("控件对象不能为空!");
            }
            Drawable color = ContextCompat.getDrawable(mSimpleDraweeView.getContext(), colorId);
            this.mBackgroundImage = color;
            return this;
        }

        public Builder setPostprocessor(String type) {
            postprocessor = new MyPostprocessor(type);
            return this;
        }

        /**
         * 构建图层
         */
        private GenericDraweeHierarchy getHierarchy() {
            if (mSimpleDraweeView == null) {
                throw new NullPointerException("控件对象不能为空!");
            }
            GenericDraweeHierarchy hierarchy = mSimpleDraweeView.getHierarchy();
            if (hierarchy == null) {
                hierarchy = GenericDraweeHierarchyBuilder.newInstance(mSimpleDraweeView.getResources()).build();
            }
            if (fadeDuration != -1) {
                hierarchy.setFadeDuration(fadeDuration);
            }
            hierarchy.setActualImageScaleType(mActualImageScaleType);
            if (mActualImageScaleType == ScalingUtils.ScaleType.FOCUS_CROP) {
                hierarchy.setActualImageFocusPoint(new PointF(0f, 0f));
            }
            if (mPlaceHolderImage != null) {
                hierarchy.setPlaceholderImage(mPlaceHolderImage, placeholderImageScaleType);
            }
            if (mProgressBarImage != null) {
                Drawable progressBarDrawable = new AutoRotateDrawable(mProgressBarImage, progressBarAutoRotateInterval);
                hierarchy.setProgressBarImage(progressBarDrawable, progressBarImageScaleType);
            }

            //设置重试图 同时就是设置支持加载视频时重试
            if (mRetryImage != null) {
                hierarchy.setRetryImage(mRetryImage, retryImageScaleType);
            }

            if (mFailureImage != null) {
                hierarchy.setFailureImage(mFailureImage, failureImageScaleType);
            }

            if (mBackgroundImage != null) {
                hierarchy.setBackgroundImage(mBackgroundImage);
            }
            if (mRoundingParams != null) {
                hierarchy.setRoundingParams(mRoundingParams);
            }

            return hierarchy;
        }

        /**
         * 构建图片加载控制
         *
         * @param uri     图片路径
         * @param isLocal 是否是本地
         * @return PipelineDraweeControllerBuilder 加载控制器
         */
        private PipelineDraweeControllerBuilder getControllerBuilder(Uri uri, boolean isLocal, boolean isResize) {
            ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
            requestBuilder.setCacheChoice(ImageRequest.CacheChoice.DEFAULT);
            if (mWidth > 0 && mHeight > 0) {
                requestBuilder.setResizeOptions(new ResizeOptions(mWidth, mHeight));
            }
            if (isLocal) {
                requestBuilder.setLocalThumbnailPreviewsEnabled(true);
            }
            if (postprocessor != null) {
                requestBuilder.setPostprocessor(postprocessor);
            }
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setOldController(mSimpleDraweeView.getController()).setImageRequest(requestBuilder.build());
            if (isResize) {
                controller.setControllerListener(new SingleImageControllerListener(mSimpleDraweeView));
            }
            if (mRetryImage != null) {
                controller.setTapToRetryEnabled(true);
            }
            if (asGif) {
                controller.setRetainImageOnFailure(true).setAutoPlayAnimations(true);
            }
            return controller;
        }


        /**
         * 加载文件图片
         *
         * @param url 网络url或图片文件路径
         */
        public void load(String url) {
            if (TextUtils.isEmpty(url) || mSimpleDraweeView == null) {
                return;
            }
            if (isReload) {
                evictFromCache(url);
            }
            Uri uri = Uri.parse(url);
            ViewGroup.LayoutParams layoutParams = mSimpleDraweeView.getLayoutParams();
            if (mWidth > 0 && mHeight > 0) {
                layoutParams.width = mWidth;
                layoutParams.height = mHeight;
                mSimpleDraweeView.requestLayout();
            } else if (mAspectRatio > 0) {
                mSimpleDraweeView.setAspectRatio(mAspectRatio);
                if (layoutParams.width > 0) {
                    mWidth = layoutParams.width;
                    mHeight = (int) (mWidth / mAspectRatio);
                } else if (layoutParams.height > 0) {
                    mHeight = layoutParams.height;
                    mWidth = (int) (mHeight * mAspectRatio);
                } else if (layoutParams.width == -1) {
                    mWidth = DensityUtil.getDisplayWidth(mSimpleDraweeView.getContext());
                    mHeight = (int) (mWidth / mAspectRatio);
                } else if (layoutParams.height == -1) {
                    mHeight = DensityUtil.getDisplayHeight(mSimpleDraweeView.getContext());
                    mWidth = (int) (mHeight * mAspectRatio);
                } else {
                    mWidth = 0;
                    mHeight = 0;
                }
            } else if (layoutParams.width > 0 && layoutParams.height > 0) {
                mWidth = layoutParams.width;
                mHeight = layoutParams.height;
            } else if (layoutParams.width > 0 && layoutParams.height == -1) {
                mWidth = layoutParams.width;
                mHeight = DensityUtil.getDisplayHeight(mSimpleDraweeView.getContext());

            } else if (layoutParams.width == -1 && layoutParams.height > 0) {
                mWidth = DensityUtil.getDisplayWidth(mSimpleDraweeView.getContext());
                mHeight = layoutParams.height;
            } else if (layoutParams.width == -1 && layoutParams.height == -1) {
                mWidth = DensityUtil.getDisplayWidth(mSimpleDraweeView.getContext());
                mHeight = DensityUtil.getDisplayHeight(mSimpleDraweeView.getContext());
            } else {
                mWidth = 0;
                mHeight = 0;
            }
            mSimpleDraweeView.setHierarchy(getHierarchy());
            if (mWidth > 0 && mHeight > 0) {
                if (UriUtil.isNetworkUri(uri)) {
                    mSimpleDraweeView.setController(getControllerBuilder(uri, false, false).build());
                } else {
                    uri = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(url).build();
                    mSimpleDraweeView.setController(getControllerBuilder(uri, true, false).build());
                }
            } else {
                if (UriUtil.isNetworkUri(uri)) {
                    mSimpleDraweeView.setController(getControllerBuilder(uri, false, true).build());
                } else {
                    uri = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(url).build();
                    mSimpleDraweeView.setController(getControllerBuilder(uri, true, true).build());
                }
            }
        }

        public void loadDrawable() {
            mSimpleDraweeView.setHierarchy(getHierarchy());
        }

        /**
         * 加载资源图片
         */
        public void load(int resId) {
            if (resId == 0 || mSimpleDraweeView == null) {
                return;
            }
            if (isReload) {
                evictFromCache(resId);
            }
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                    .path(String.valueOf(resId))
                    .build();
            ViewGroup.LayoutParams layoutParams = mSimpleDraweeView.getLayoutParams();
            if (mWidth > 0 && mHeight > 0) {
                layoutParams.width = mWidth;
                layoutParams.height = mHeight;
                mSimpleDraweeView.requestLayout();
            } else if (mAspectRatio > 0) {
                mSimpleDraweeView.setAspectRatio(mAspectRatio);
                if (layoutParams.width > 0) {
                    mWidth = layoutParams.width;
                    mHeight = (int) (mWidth / mAspectRatio);
                } else if (layoutParams.height > 0) {
                    mHeight = layoutParams.height;
                    mWidth = (int) (mHeight * mAspectRatio);
                } else if (layoutParams.width == -1) {
                    mWidth = DensityUtil.getDisplayWidth(mSimpleDraweeView.getContext());
                    mHeight = (int) (mWidth / mAspectRatio);
                } else if (layoutParams.height == -1) {
                    mHeight = DensityUtil.getDisplayHeight(mSimpleDraweeView.getContext());
                    mWidth = (int) (mHeight * mAspectRatio);
                } else {
                    mWidth = 0;
                    mHeight = 0;
                }
            } else if (layoutParams.width > 0 && layoutParams.height > 0) {
                mWidth = layoutParams.width;
                mHeight = layoutParams.height;
            } else if (layoutParams.width > 0 && layoutParams.height == -1) {
                mWidth = layoutParams.width;
                mHeight = DensityUtil.getDisplayHeight(mSimpleDraweeView.getContext());

            } else if (layoutParams.width == -1 && layoutParams.height > 0) {
                mWidth = DensityUtil.getDisplayWidth(mSimpleDraweeView.getContext());
                mHeight = layoutParams.height;
            } else if (layoutParams.width == -1 && layoutParams.height == -1) {
                mWidth = DensityUtil.getDisplayWidth(mSimpleDraweeView.getContext());
                mHeight = DensityUtil.getDisplayHeight(mSimpleDraweeView.getContext());
            } else {
                mWidth = 0;
                mHeight = 0;
            }
            final Drawable drawable = ContextCompat.getDrawable(mSimpleDraweeView.getContext(), resId);
            if (drawable instanceof BitmapDrawable) {
                mSimpleDraweeView.setHierarchy(getHierarchy());
                if (mWidth > 0 && mHeight > 0) {
                    mSimpleDraweeView.setController(getControllerBuilder(uri, true, false).build());
                } else {
                    mSimpleDraweeView.setController(getControllerBuilder(uri, true, true).build());
                }
            }
        }

        /**
         * 加载没有指定view宽高的资源文件
         *
         * @param resId
         */
        @Deprecated
        public void loadResize(int resId) {
            if (resId == 0 || mSimpleDraweeView == null) {
                return;
            }
            Uri uri = new Uri.Builder().scheme(UriUtil.LOCAL_RESOURCE_SCHEME).path(String.valueOf(resId)).build();
            mSimpleDraweeView.setHierarchy(getHierarchy());
            mSimpleDraweeView.setController(getControllerBuilder(uri, true, true).build());
        }

        /**
         * 加载没有指定view宽高的网络url或文件路径
         */
        @Deprecated
        public void loadResize(String url) {
            if (TextUtils.isEmpty(url) || mSimpleDraweeView == null) {
                return;
            }
            Uri uri = Uri.parse(url);
            mSimpleDraweeView.setHierarchy(getHierarchy());
            if (UriUtil.isNetworkUri(uri)) {
                mSimpleDraweeView.setController(getControllerBuilder(uri, false, true).build());
            } else {
                uri = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(String.valueOf(url)).build();
                mSimpleDraweeView.setController(getControllerBuilder(uri, true, true).build());
            }
        }
    }

    /**
     * 下载网络图片，且将图片保存在指定路径下,非ui线程下调用
     *
     * @param url             网络url
     * @param loadImageResult ILoadImageResult
     */
    public static void loadOriginalBigImage(Context context, String url, final ILoadImageResult loadImageResult) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        // 获取未解码的图片数据
        DataSource<CloseableReference<PooledByteBuffer>> dataSource = imagePipeline.fetchEncodedImage
                (imageRequestBuilder.build(), context);
        DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<PooledByteBuffer>>() {
            @Override
            public void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                if (!dataSource.isFinished() || loadImageResult == null) {
                    return;
                }

                CloseableReference<PooledByteBuffer> imageReference = dataSource.getResult();
                if (imageReference != null) {
                    final CloseableReference<PooledByteBuffer> closeableReference = imageReference.clone();
                    try {
                        PooledByteBuffer pooledByteBuffer = closeableReference.get();
                        InputStream inputStream = new PooledByteBufferInputStream(pooledByteBuffer);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        loadImageResult.onResult(bitmap);
                    } catch (Exception e) {
                        loadImageResult.onError(e.getCause().toString());
                    } finally {
                        imageReference.close();
                        closeableReference.close();
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Throwable throwable = dataSource.getFailureCause();
                if (throwable != null) {
                    loadImageResult.onError(throwable.toString());
                }
            }
        };
        dataSource.subscribe(dataSubscriber, Executors.newSingleThreadExecutor());
    }

    /**
     * 加载网络原图,图片小于100k时调用
     * * @param url             图片URL
     *
     * @param loadImageResult ILoadImageResult
     *                        的取值有以下三个：
     *                        UiThreadImmediateExecutorService.getInstance() 在回调中进行任何UI操作
     *                        CallerThreadExecutor.getInstance() 在回调里面做的事情比较少，并且不涉及UI
     *                        Executors.newSingleThreadExecutor()
     *                        你需要做一些比较复杂、耗时的操作，并且不涉及UI（如数据库读写、文件IO），你就不能用上面两个Executor。
     *                        你需要开启一个后台Executor，可以参考DefaultExecutorSupplier.forBackgroundTasks。
     */
    public static void loadOriginalSmallImage(Context mContext, String url, final ILoadImageResult loadImageResult) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        ImageRequest imageRequest = builder.build();
        // 获取已解码的图片，返回的是Bitmap
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest,
                mContext);
        DataSubscriber dataSubscriber = new BaseDataSubscriber<CloseableReference<CloseableBitmap>>() {
            @Override
            public void onNewResultImpl(DataSource<CloseableReference<CloseableBitmap>> dataSource) {
                if (!dataSource.isFinished() || loadImageResult == null) {
                    return;
                }

                CloseableReference<CloseableBitmap> imageReference = dataSource.getResult();
                if (imageReference != null) {
                    final CloseableReference<CloseableBitmap> closeableReference = imageReference.clone();
                    try {
                        CloseableBitmap closeableBitmap = closeableReference.get();
                        Bitmap bitmap = closeableBitmap.getUnderlyingBitmap();
                        if (bitmap != null && !bitmap.isRecycled()) {
                            // https://github.com/facebook/fresco/issues/648
                            final Bitmap tempBitmap = bitmap.copy(bitmap.getConfig(), false);
                            loadImageResult.onResult(tempBitmap);
                        }
                    } catch (Exception e) {
                        loadImageResult.onError(e.getCause().toString());
                    } finally {
                        imageReference.close();
                        closeableReference.close();
                    }
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                Throwable throwable = dataSource.getFailureCause();
                if (throwable != null) {
                    loadImageResult.onError(throwable.toString());
                }
            }
        };
        dataSource.subscribe(dataSubscriber, Executors.newSingleThreadExecutor());
    }

    /**
     * 通过资源url获取缓存文件
     *
     * @param url 资源url
     */
    public static File getCacheFile(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Uri uri = Uri.parse(url);
        ImageRequest imageRequest = ImageRequest.fromUri(uri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, "xtc");
        File file = null;
        if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
            file = ((FileBinaryResource) resource).getFile();
        } else if (ImagePipelineFactory.getInstance().getSmallImageFileCache().hasKey(cacheKey)) {
            BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageFileCache().getResource(cacheKey);
            file = ((FileBinaryResource) resource).getFile();
        }
        return file;
    }

    /**
     * 从内存缓存中移除指定图片的缓存
     *
     * @param url 网络url或者本地文件路径
     */
    public static void evictFromMemoryCache(String url) {
        Uri uri = Uri.parse(url);
        if (!UriUtil.isNetworkUri(uri)) {
            uri = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(url).build();
        }
        Fresco.getImagePipeline().evictFromMemoryCache(uri);
    }

    /**
     * 从内存缓存中移除指定图片的缓存
     *
     * @param resId 资源文件
     */
    public static void evictFromMemoryCache(int resId) {
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resId))
                .build();
        Fresco.getImagePipeline().evictFromMemoryCache(uri);
    }

    /**
     * 从磁盘缓存中移除指定图片的缓存
     *
     * @param url 网络url或者本地文件路径
     */
    public static void evictFromDiskCache(String url) {
        Uri uri = Uri.parse(url);
        if (!UriUtil.isNetworkUri(uri)) {
            uri = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(url).build();
        }
        Fresco.getImagePipeline().evictFromDiskCache(uri);
    }

    /**
     * 从磁盘缓存中移除指定图片的缓存
     *
     * @param resId 资源文件
     */
    public static void evictFromDiskCache(int resId) {
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resId))
                .build();
        Fresco.getImagePipeline().evictFromDiskCache(uri);
    }

    /**
     * 移除指定图片的所有缓存（包括内存+磁盘）
     *
     * @param url 网络url或者本地文件路径
     */
    public static void evictFromCache(String url) {
        evictFromMemoryCache(url);
        evictFromDiskCache(url);
    }

    /**
     * 移除指定图片的所有缓存（包括内存+磁盘）
     *
     * @param resId 资源文件
     */
    public static void evictFromCache(int resId) {
        evictFromMemoryCache(resId);
        evictFromDiskCache(resId);
    }

    /**
     * 清空所有内存缓存
     */
    public static void clearMemoryCaches() {
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    /**
     * 清空所有磁盘缓存，若你配置有两个磁盘缓存，则两个都会清除
     */
    public static void clearDiskCaches() {
        Fresco.getImagePipeline().clearDiskCaches();
    }

    /**
     * 清除所有缓存（包括内存+磁盘）
     */
    public static void clearCaches() {
        clearMemoryCaches();
        clearDiskCaches();
    }

    /**
     * 查找一张图片在已解码的缓存中是否存在
     *
     * @param url 网络url或者本地文件路径
     * @return
     */
    public static boolean isInBitmapMemoryCache(String url) {
        Uri uri = Uri.parse(url);
        if (!UriUtil.isNetworkUri(uri)) {
            uri = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(url).build();
        }
        return Fresco.getImagePipeline().isInBitmapMemoryCache(uri);
    }

    /**
     * 图片是否已经存在了
     */
    private static boolean isInDiskCache(Uri uri) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<Boolean> dataSource = imagePipeline.isInDiskCache(uri);
        if (dataSource == null) {
            return false;
        }
        ImageRequest imageRequest = ImageRequest.fromUri(uri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                .getEncodedCacheKey(imageRequest, "xtc");
        BinaryResource resource = ImagePipelineFactory.getInstance()
                .getMainFileCache().getResource(cacheKey);
        return resource != null && dataSource.getResult() != null && dataSource.getResult();
    }

    /**
     * 查找一张图片在磁盘缓存中是否存在，若配有两个磁盘缓存，则只要其中一个存在，就会返回true
     *
     * @param url 网络url或者本地文件路径
     * @return
     */
    public static boolean isInDiskCache(String url) {
        Uri uri = Uri.parse(url);
        if (!UriUtil.isNetworkUri(uri)) {
            uri = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(url).build();
        }
        return isInDiskCache(uri);
    }

    /**
     * 查找一张图片在磁盘缓存中是否存在，若配有两个磁盘缓存，则只要其中一个存在，就会返回true
     *
     * @param url 网络url或者本地文件路径
     * @return
     */
    @Deprecated   //该方法为异步方法，调用时会出现缓存清理不了的情况，建议调用isInDiskCache(url)
    public static boolean isInDiskCacheSync(String url) {
        Uri uri = Uri.parse(url);
        if (!UriUtil.isNetworkUri(uri)) {
            uri = new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(url).build();
        }
        return Fresco.getImagePipeline().isInDiskCacheSync(uri);
    }

    /**
     * 需要暂停网络请求时调用
     */
    public static void pause() {
        Fresco.getImagePipeline().pause();
    }

    /**
     * 需要恢复网络请求时调用
     */
    public static void resume() {
        Fresco.getImagePipeline().resume();
    }

    /**
     * 当前网络请求是否处于暂停状态
     *
     * @return
     */
    public static boolean isPaused() {
        return Fresco.getImagePipeline().isPaused();
    }
}
