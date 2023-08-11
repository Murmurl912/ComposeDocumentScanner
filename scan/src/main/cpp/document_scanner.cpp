//
// Created by mur on 2023/8/11.
//

#include "include/document_scanner.h"

using namespace cv;
using namespace std;



Mat document_scanner::resize(Mat& src, int max_size, float& scale) {
    int width = src.cols;
    int height = src.rows;
    int size = width > height? width : height;
    if (size > max_size) {
        scale = 1.0f * size / max_size;
        width = static_cast<int>(width / scale);
        height = static_cast<int>(height / scale);
        Size target_size = Size (width, height);
        Mat resizedBitmap(target_size, CV_8UC3);
        resize(src, resizedBitmap, target_size);
        return resizedBitmap;
    }
    return src;
}

vector<Point> detect_document(Mat& src) {
    Mat morphology;
    Mat kernel = getStructuringElement(0, Size(5, 5));
    morphologyEx(src, morphology, MORPH_CLOSE, kernel, Point(-1, -1), 3);
    Mat mask(morphology.size(), CV_8UC1, Scalar(0, 0, 0));
    Rect rect(20, 20, morphology.cols - 20, morphology.rows - 20);
    Mat bgdModel, fgdModel;
    grabCut(morphology, mask, rect, bgdModel, fgdModel, 5, GC_INIT_WITH_RECT);
    mask = (mask == GC_BGD) | (mask == GC_PR_BGD);
    Mat image = mask * morphology;
    cvtColor(image, image, COLOR_BGR2GRAY);
    GaussianBlur(image, image, Size(11, 11), 0);
    Canny(image, image, 0, 200);
    dilate(image, image, getStructuringElement(MORPH_ELLIPSE, Size(5, 5)));
    vector<vector<Point>> contours;
    vector<Vec4i> hierarchy;
    //提取边框
    findContours(image, contours, hierarchy, RETR_LIST, CHAIN_APPROX_NONE);
    std::sort(contours.begin(), contours.end(),
              [](const std::vector<cv::Point>& contour1, const std::vector<cv::Point>& contour2) {
        double area1 = cv::contourArea(contour1);
        double area2 = cv::contourArea(contour2);
        return area1 > area2;
    });
    // 找出四边形
    vector<vector<Point>> polygons;
    for (const auto & contour : contours) {
        size_t size = contour.size();

        if (size >= 4) {
            double epsilon = 0.02 * arcLength(contour, true);
            std::vector<cv::Point> polygon;
            approxPolyDP(contour, polygon, epsilon, true);
            if (polygon.size() == 4) {
                polygons.push_back(polygon);
            }
        }
    }
}