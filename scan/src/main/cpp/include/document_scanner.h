//
// Created by mur on 2023/8/11.
//

#ifndef COMPOSEDOCUMENTSCANNER_DOCUMENT_SCANNER_H
#define COMPOSEDOCUMENTSCANNER_DOCUMENT_SCANNER_H

#include <android/bitmap.h>
#include <opencv2/opencv.hpp>


namespace document_scanner {
    cv::Mat resize(cv::Mat& src, int max_size, float& scale);
}

#endif //COMPOSEDOCUMENTSCANNER_DOCUMENT_SCANNER_H
