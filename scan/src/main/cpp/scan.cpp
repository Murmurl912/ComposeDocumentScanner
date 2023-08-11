#include <jni.h>
#include <string>
#include <android/bitmap.h>
#include <opencv2/opencv.hpp>
#include "android_utils.h"
#include "Scanner.h"

using namespace std;
using namespace cv;

static struct {
    jclass jClassPoint;
    jmethodID jMethodInit;
    jfieldID jFieldIDX;
    jfieldID jFieldIDY;
} gPointInfo;

void init_point_class(JNIEnv *env) {
    gPointInfo.jClassPoint = reinterpret_cast<jclass>(env -> NewGlobalRef(env -> FindClass("android/graphics/Point")));
    gPointInfo.jMethodInit = env -> GetMethodID(gPointInfo.jClassPoint, "<init>", "(II)V");
    gPointInfo.jFieldIDX = env -> GetFieldID(gPointInfo.jClassPoint, "x", "I");
    gPointInfo.jFieldIDY = env -> GetFieldID(gPointInfo.jClassPoint, "y", "I");
}
static std::vector<cv::Point> pointsToNative(JNIEnv *env, jobjectArray points_) {
    int arrayLength = env->GetArrayLength(points_);
    std::vector<cv::Point> result;
    for(int i = 0; i < arrayLength; i++) {
        jobject point_ = env -> GetObjectArrayElement(points_, i);
        int pX = env -> GetIntField(point_, gPointInfo.jFieldIDX);
        int pY = env -> GetIntField(point_, gPointInfo.jFieldIDY);
        result.push_back(cv::Point(pX, pY));
    }
    return result;
}


cv::Mat resize(cv::Mat& src, int max_size, float& scale) {
    int width = src.cols;
    int height = src.rows;
    int size = width > height? width : height;
    if (size > max_size) {
        scale = 1.0f * size / max_size;
        width = static_cast<int>(width / scale);
        height = static_cast<int>(height / scale);
        cv::Size target_size = cv::Size (width, height);
        cv::Mat resizedBitmap(target_size, CV_8UC3);
        cv::resize(src, resizedBitmap, target_size);
        return resizedBitmap;
    }
    return src;
}

cv::Mat preprocesse_image(cv::Mat &image,
                          bool hist_equal,
                          int canny_value,
                          int blur_value) {
    cv::Mat grayMat;
    cvtColor(image, grayMat, cv::COLOR_BGR2GRAY);
    if (hist_equal){
        equalizeHist(grayMat, grayMat);
    }
    cv::Mat blurMat;
    GaussianBlur(grayMat, blurMat, cv::Size(blur_value, blur_value), 0);
    cv::Mat cannyMat;
    Canny(blurMat, cannyMat, 50, canny_value, 3);
    cv::Mat thresholdMat;
    threshold(cannyMat, thresholdMat, 0, 255, cv::THRESH_OTSU);
    return thresholdMat;
}

bool sortByArea(const vector<Point> &v1, const vector<Point> &v2) {
    double v1Area = fabs(contourArea(Mat(v1)));
    double v2Area = fabs(contourArea(Mat(v2)));
    return v1Area > v2Area;
}

//type代表左上，左下，右上，右下等方位
Point choosePoint(Point center, std::vector<cv::Point> &points, int type) {
    int index = -1;
    int minDis = 0;
    //四个堆都是选择距离中心点较远的点
    if (type == 0) {
        for (int i = 0; i < points.size(); i++) {
            if (points[i].x < center.x && points[i].y < center.y) {
                int dis = static_cast<int>(sqrt(pow((points[i].x - center.x), 2) + pow((points[i].y - center.y), 2)));
                if (dis > minDis){
                    index = i;
                    minDis = dis;
                }
            }
        }
    } else if (type == 1) {
        for (int i = 0; i < points.size(); i++) {
            if (points[i].x < center.x && points[i].y > center.y) {
                int dis = static_cast<int>(sqrt(pow((points[i].x - center.x), 2) + pow((points[i].y - center.y), 2)));
                if (dis > minDis){
                    index = i;
                    minDis = dis;
                }
            }
        }
    } else if (type == 2) {
        for (int i = 0; i < points.size(); i++) {
            if (points[i].x > center.x && points[i].y < center.y) {
                int dis = static_cast<int>(sqrt(pow((points[i].x - center.x), 2) + pow((points[i].y - center.y), 2)));
                if (dis > minDis){
                    index = i;
                    minDis = dis;
                }
            }
        }

    } else if (type == 3) {
        for (int i = 0; i < points.size(); i++) {
            if (points[i].x > center.x && points[i].y > center.y) {
                int dis = static_cast<int>(sqrt(pow((points[i].x - center.x), 2) + pow((points[i].y - center.y), 2)));
                if (dis > minDis){
                    index = i;
                    minDis = dis;
                }
            }
        }
    }

    if (index != -1){
        return Point(points[index].x, points[index].y);
    }
    return Point(0, 0);
}

vector<Point> selectPoints(vector<Point> points) {
    if (points.size() > 4) {
        Point &p = points[0];
        int minX = p.x;
        int maxX = p.x;
        int minY = p.y;
        int maxY = p.y;
        //得到一个矩形去包住所有点
        for (int i = 1; i < points.size(); i++) {
            if (points[i].x < minX) {
                minX = points[i].x;
            }
            if (points[i].x > maxX) {
                maxX = points[i].x;
            }
            if (points[i].y < minY) {
                minY = points[i].y;
            }
            if (points[i].y > maxY) {
                maxY = points[i].y;
            }
        }
        //矩形中心点
        Point center = Point((minX + maxX) / 2, (minY + maxY) / 2);
        //分别得出左上，左下，右上，右下四堆中的结果点
        Point p0 = choosePoint(center, points, 0);
        Point p1 = choosePoint(center, points, 1);
        Point p2 = choosePoint(center, points, 2);
        Point p3 = choosePoint(center, points, 3);
        points.clear();
        //如果得到的点不是０，即是得到的结果点
        if (!(p0.x == 0 && p0.y == 0)){
            points.push_back(p0);
        }
        if (!(p1.x == 0 && p1.y == 0)){
            points.push_back(p1);
        }
        if (!(p2.x == 0 && p2.y == 0)){
            points.push_back(p2);
        }
        if (!(p3.x == 0 && p3.y == 0)){
            points.push_back(p3);
        }
    }
    return points;
}

long long pointSideLine(Point &lineP1, Point &lineP2, Point &point) {
    long x1 = lineP1.x;
    long y1 = lineP1.y;
    long x2 = lineP2.x;
    long y2 = lineP2.y;
    long x = point.x;
    long y = point.y;
    return (x - x1)*(y2 - y1) - (y - y1)*(x2 - x1);
}



vector<Point> sortPointClockwise(vector<Point> points) {
    if (points.size() != 4) {
        return points;
    }

    Point unFoundPoint;
    vector<Point> result = {unFoundPoint, unFoundPoint, unFoundPoint, unFoundPoint};

    long minDistance = -1;
    for(Point &point : points) {
        long distance = point.x * point.x + point.y * point.y;
        if(minDistance == -1 || distance < minDistance) {
            result[0] = point;
            minDistance = distance;
        }
    }
    if (result[0] != unFoundPoint) {
        Point &leftTop = result[0];
        points.erase(std::remove(points.begin(), points.end(), leftTop));
        if ((pointSideLine(leftTop, points[0], points[1]) * pointSideLine(leftTop, points[0], points[2])) < 0) {
            result[2] = points[0];
        } else if ((pointSideLine(leftTop, points[1], points[0]) * pointSideLine(leftTop, points[1], points[2])) < 0) {
            result[2] = points[1];
        } else if ((pointSideLine(leftTop, points[2], points[0]) * pointSideLine(leftTop, points[2], points[1])) < 0) {
            result[2] = points[2];
        }
    }
    if (result[0] != unFoundPoint && result[2] != unFoundPoint) {
        Point &leftTop = result[0];
        Point &rightBottom = result[2];
        points.erase(std::remove(points.begin(), points.end(), rightBottom));
        if (pointSideLine(leftTop, rightBottom, points[0]) > 0) {
            result[1] = points[0];
            result[3] = points[1];
        } else {
            result[1] = points[1];
            result[3] = points[0];
        }
    }

    if (result[0] != unFoundPoint && result[1] != unFoundPoint && result[2] != unFoundPoint && result[3] != unFoundPoint) {
        return result;
    }

    return points;
}

std::vector<cv::Point > scan_points(cv::Mat& image, bool hist_equal) {
    std::vector<cv::Point> result;
    int max_size = 500;
    float scale = 1;
    int canny_values[] = {100, 150, 300};
    int blur_values[] = {3, 7, 11, 15};
    //缩小图片尺寸
    cv::Mat resized = resize(image, max_size, scale);
    for (int i : canny_values){
        for (int j : blur_values){
            //预处理图片
            cv::Mat scanImage = preprocesse_image(image, hist_equal, i, j);
            vector<vector<Point>> contours;
            //提取边框
            findContours(scanImage, contours, RETR_EXTERNAL, CHAIN_APPROX_NONE);
            //按面积排序
            std::sort(contours.begin(), contours.end(), sortByArea);
            if (!contours.empty()) {
                vector<Point> contour = contours[0];
                double arc = arcLength(contour, true);
                vector<Point> outDP;
                //多变形逼近
                approxPolyDP(Mat(contour), outDP, 0.01 * arc, true);
                //筛选去除相近的点
                vector<Point> selectedPoints = selectPoints(outDP);
                if (selectedPoints.size() != 4) {
                    //如果筛选出来之后不是四边形
                    continue;
                } else {
                    int widthMin = selectedPoints[0].x;
                    int widthMax = selectedPoints[0].x;
                    int heightMin = selectedPoints[0].y;
                    int heightMax = selectedPoints[0].y;
                    for (int k = 0; k < 4; k++) {
                        if (selectedPoints[k].x < widthMin) {
                            widthMin = selectedPoints[k].x;
                        }
                        if (selectedPoints[k].x > widthMax) {
                            widthMax = selectedPoints[k].x;
                        }
                        if (selectedPoints[k].y < heightMin) {
                            heightMin = selectedPoints[k].y;
                        }
                        if (selectedPoints[k].y > heightMax) {
                            heightMax = selectedPoints[k].y;
                        }
                    }
                    //选择区域外围矩形面积
                    int selectArea = (widthMax - widthMin) * (heightMax - heightMin);
                    int imageArea = scanImage.cols * scanImage.rows;
                    if (selectArea < (imageArea / 20)) {
                        result.clear();
                        //筛选出来的区域太小
                        continue;
                    } else {
                        result = selectedPoints;
                        if (result.size() != 4) {
                            Point2f p[4];
                            p[0] = Point2f(0, 0);
                            p[1] = Point2f(image.cols, 0);
                            p[2] = Point2f(image.cols, image.rows);
                            p[3] = Point2f(0, image.rows);
                            result.push_back(p[0]);
                            result.push_back(p[1]);
                            result.push_back(p[2]);
                            result.push_back(p[3]);
                        }
                        for (Point &p : result) {
                            p.x *= scale;
                            p.y *= scale;
                        }
                        // 按左上，右上，右下，左下排序
                        return sortPointClockwise(result);
                    }
                }
            }
        }
    }
    //当没选出所需要区域时，如果还没做过直方图均衡化则尝试使用均衡化，但该操作只执行一次，若还无效，则判定为图片不能裁出有效区域，返回整张图
    if (!hist_equal){
        hist_equal = true;
        return scan_points(image, hist_equal);
    }
    if (result.size() != 4) {
        Point2f p[4];
        p[0] = Point2f(0, 0);
        p[1] = Point2f(image.cols, 0);
        p[2] = Point2f(image.cols, image.rows);
        p[3] = Point2f(0, image.rows);
        result.push_back(p[0]);
        result.push_back(p[1]);
        result.push_back(p[2]);
        result.push_back(p[3]);
    }
    for (Point &p : result) {
        p.x *= scale;
        p.y *= scale;
    }
    // 按左上，右上，右下，左下排序
    return sortPointClockwise(result);
}

static jobject createJavaPoint(JNIEnv *env, Point point_) {
    return env -> NewObject(gPointInfo.jClassPoint, gPointInfo.jMethodInit, point_.x, point_.y);
}


extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_scan_NativeScanner_decode(JNIEnv *env, jobject thiz, jint width, jint height,
                                           jobject buffer) {
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

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_scan_NativeScanner_scan(JNIEnv *env, jobject thiz, jobject image_buffer,
                                         jint width, jint height, jboolean draw_points,
                                         jobject out) {

    auto *dBuf = static_cast<jbyte *>(env->GetDirectBufferAddress(image_buffer));
    cv::Mat image = cv::Mat(height, width, CV_8UC4, dBuf);
    cv::Mat rotate;
    cv::rotate(image, rotate, ROTATE_90_CLOCKWISE);
    image = rotate;
    vector<Point> points = scan_points(image, false);

    init_point_class(env);
    jobjectArray point_array = env ->NewObjectArray(points.size(), gPointInfo.jClassPoint, NULL);
    for (int i = 0; i < points.size(); i++) {
        env -> SetObjectArrayElement(point_array, i, createJavaPoint(env, points[i]));
    }
    polylines(image, points, true, Scalar(255, 0, 0), 2);
    // now decode bitmap
    mat_to_bitmap(env, image, out);

    return point_array;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_scan_NativeScanner_smartScan(
        JNIEnv *env,
        jobject thiz,
        jobject buffer,
        jobject out, jint width, jint height,
        jboolean overlay_point) {
    auto *dBuf = static_cast<jbyte *>(env->GetDirectBufferAddress(buffer));
    cv::Mat image = cv::Mat(height, width, CV_8UC4, dBuf);
    cv::Mat rotate;
    cv::rotate(image, rotate, ROTATE_90_CLOCKWISE);
    image = rotate;

    cv::Mat bgr;
    cvtColor(image, bgr, COLOR_RGBA2BGR);
    scanner::Scanner docScanner(bgr, true);
    std::vector<Point> points = docScanner.scanPoint();
    init_point_class(env);
    jobjectArray point_array = env ->NewObjectArray(
            points.size(),
            gPointInfo.jClassPoint,
            nullptr
            );
    for (int i = 0; i < points.size(); i++) {
        env -> SetObjectArrayElement(point_array, i, createJavaPoint(env, points[i]));
    }
    polylines(image, points, true, Scalar(255, 0, 0), 2);
    mat_to_bitmap(env, image, out);
    return point_array;
}