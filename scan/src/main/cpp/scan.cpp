#include <jni.h>
#include <string>
#include <android/bitmap.h>
#include <opencv2/opencv.hpp>

void bitmap_to_mat(JNIEnv *env, jobject &srcBitmap, cv::Mat &srcMat) {
    void *srcPixels = 0;
    AndroidBitmapInfo srcBitmapInfo;
    try {
        AndroidBitmap_getInfo(env, srcBitmap, &srcBitmapInfo);
        AndroidBitmap_lockPixels(env, srcBitmap, &srcPixels);
        uint32_t srcHeight = srcBitmapInfo.height;
        uint32_t srcWidth = srcBitmapInfo.width;
        srcMat.create(srcHeight, srcWidth, CV_8UC4);
        if (srcBitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            cv::Mat tmp(srcHeight, srcWidth, CV_8UC4, srcPixels);
            tmp.copyTo(srcMat);
        } else {
            cv::Mat tmp = cv::Mat(srcHeight, srcWidth, CV_8UC2, srcPixels);
            cvtColor(tmp, srcMat, cv::COLOR_BGR5652RGBA);
        }
        AndroidBitmap_unlockPixels(env, srcBitmap);
        return;
    } catch (cv::Exception &e) {
        AndroidBitmap_unlockPixels(env, srcBitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env -> ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, srcBitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env -> ThrowNew(je, "unknown");
        return;
    }
}

void mat_to_bitmap(JNIEnv *env, cv::Mat &srcMat, jobject &dstBitmap) {
    void *dstPixels = 0;
    AndroidBitmapInfo dstBitmapInfo;
    try {
        AndroidBitmap_getInfo(env, dstBitmap, &dstBitmapInfo);
        AndroidBitmap_lockPixels(env, dstBitmap, &dstPixels);
        uint32_t dstHeight = dstBitmapInfo.height;
        uint32_t dstWidth = dstBitmapInfo.width;
        if (dstBitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
            cv::Mat tmp(dstHeight, dstWidth, CV_8UC4, dstPixels);
            if(srcMat.type() == CV_8UC1) {
                cvtColor(srcMat, tmp, cv::COLOR_GRAY2RGBA);
            } else if (srcMat.type() == CV_8UC3) {
                cvtColor(srcMat, tmp, cv::COLOR_RGB2RGBA);
            } else if (srcMat.type() == CV_8UC4) {
                srcMat.copyTo(tmp);
            }
        } else {
            cv::Mat tmp = cv::Mat(dstHeight, dstWidth, CV_8UC2, dstPixels);
            if(srcMat.type() == CV_8UC1) {
                cvtColor(srcMat, tmp, cv::COLOR_GRAY2BGR565);
            } else if (srcMat.type() == CV_8UC3) {
                cvtColor(srcMat, tmp, cv::COLOR_RGB2BGR565);
            } else if (srcMat.type() == CV_8UC4) {
                cvtColor(srcMat, tmp, cv::COLOR_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, dstBitmap);
    }catch (cv::Exception &e) {
        AndroidBitmap_unlockPixels(env, dstBitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env -> ThrowNew(je, e.what());
        return;
    } catch (...) {
        AndroidBitmap_unlockPixels(env, dstBitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env -> ThrowNew(je, "unknown");
        return;
    }
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_scan_NativeLib_decode(JNIEnv *env, jobject thiz, jint width, jint height, jobject buffer) {
    auto *dBuf = static_cast<jbyte *>(env->GetDirectBufferAddress(buffer));
    cv::Mat mat = cv::Mat(height, width, CV_8UC4, dBuf);
    auto java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    // 创建Bitmap
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jfieldID configField = env->GetStaticFieldID(bitmapConfigClass, "ARGB_8888", "Landroid/graphics/Bitmap$Config;");
    jobject config = env->GetStaticObjectField(bitmapConfigClass, configField);
    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMethod = env->GetStaticMethodID(bitmapClass, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jobject bitmap = env->CallStaticObjectMethod(bitmapClass, createBitmapMethod, width, height, config);

    // 将Mat数据复制到Bitmap中
    mat_to_bitmap(env, mat, bitmap);

    return bitmap;
}

