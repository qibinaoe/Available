#include <jni.h>
#include <string>
#include <iostream>
#include <vector>
#include <utility>
#include <string>
#include <android/native_window_jni.h>

#include "opencv2/opencv.hpp"
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>
#include "opencv2/imgcodecs.hpp"

ANativeWindow *window = 0;
using namespace cv;
using namespace std;

DetectionBasedTracker *tracker = 0;
class CascadeDetectorAdapter: public DetectionBasedTracker::IDetector
{
public:
    CascadeDetectorAdapter(cv::Ptr<cv::CascadeClassifier> detector):
            IDetector(),
            Detector(detector)
    {
    }
    void detect(const cv::Mat &Image, std::vector<cv::Rect> &objects)
    {
        Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize, maxObjSize);
    }

    virtual ~CascadeDetectorAdapter()
    {
    }

private:
    CascadeDetectorAdapter();
    cv::Ptr<cv::CascadeClassifier> Detector;
};

cv::Mat countPaperGetImg(cv::Mat img){

    Mat src = img;
    if (src.empty()) {
        return src;
    }

    //transform source img to gray if not
    Mat gray;
    if (src.channels() == 3) {
        cvtColor(src, gray, COLOR_BGR2GRAY);
    }
    else {
        gray = src;
    }

    // Apply adaptiveThreshold at the bitwise_not of gray, notice the ~ symbol
    Mat bw;
    adaptiveThreshold(~gray, bw, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 15, -2);
    //show binary img

    Mat blurImg;
    GaussianBlur(bw, blurImg, Size(3, 3), 0);
    //threshold(gray, blurImg, 0, 255, THRESH_OTSU);


    // Create the images that will use to extract the horizontal and vertical lines
    Mat vertical = blurImg.clone();
    // Specify size on vertical axis
    int vertical_size = vertical.rows / 50;
    // Create structure element for extracting vertical lines through morphology operations
    Mat verticalStructure = getStructuringElement(MORPH_RECT, Size(1, vertical_size));
    // Apply morphology operations
    erode(vertical, vertical, verticalStructure, Point(-1, -1));
    dilate(vertical, vertical, verticalStructure, Point(-1, -1));
    // Show extracted vertical lines

    //增强效果
    Mat enhanceImg;
    threshold(vertical, enhanceImg, 0, 255, THRESH_BINARY | THRESH_OTSU);
    imwrite("enhance.jpg", enhanceImg);
    //计算条数

    //1.先分成多切片
    int height = enhanceImg.rows;
    int width = enhanceImg.cols;

    int scaleLowHeight = (int)height * 0.3;
    int scaleHighHeight = (int)height * 0.7;
    int scaleUserfulHeight = scaleHighHeight - scaleLowHeight;

    //根据图片大小进行分割
    int piecesLengthUnit = 25;
    int buffLengthUnit = (int)scaleUserfulHeight / 10;
    if (buffLengthUnit <= piecesLengthUnit) {
        buffLengthUnit = 30;
    }
    // 5个单位长度作缓冲区
    //切片的份数
    int piecesNumberUnit = (int)scaleUserfulHeight / buffLengthUnit;
    //每份的长度
    int heightUnit = (int)scaleUserfulHeight / piecesNumberUnit;

    //根据份数进行算法选择
    vector<Mat> pieces;
    vector<pair<int, int>> heightMarkList;
    int tempLowHeight = scaleLowHeight;

    if (piecesNumberUnit >= 10) {
        for (int i = 0; i < 10; i++) {
            Mat tempPiece = enhanceImg(Range(tempLowHeight, tempLowHeight + piecesLengthUnit), Range::all());
            pieces.push_back(tempPiece);
            int tempLowHighHeight = tempLowHeight + piecesLengthUnit;
            heightMarkList.push_back(make_pair(tempLowHeight, tempLowHighHeight));
            tempLowHeight += heightUnit;
        }
    }
    else if (piecesNumberUnit > 0) {
        for (int i = 0; i < piecesNumberUnit; i++) {
            Mat tempPiece = enhanceImg(Range(tempLowHeight, tempLowHeight + piecesLengthUnit), Range::all());
            pieces.push_back(tempPiece);
            int tempLowHighHeight = tempLowHeight + piecesLengthUnit;
            heightMarkList.push_back(make_pair(tempLowHeight, tempLowHighHeight));
            tempLowHeight += heightUnit;
        }
    }
    else {
        Mat tempPiece = enhanceImg(Range(scaleLowHeight, scaleHighHeight), Range::all());
        pieces.push_back(tempPiece);
        heightMarkList.push_back(make_pair(scaleLowHeight, scaleHighHeight));
    }

    //计数
    // 达到threshHigh判定为线段
    // markHeight标记图片的位置
    vector<vector<int>> piecesTargetColList;
    vector<int> piecesCountNumber;
    //遍历前面分片得到的图片，计算每个图片的数目
    size_t piecesNumber = pieces.size();
    for (size_t i = 0; i < piecesNumber; i++) {
        Mat apiece = pieces[i];
        int pWidth = apiece.cols;
        Mat Vdist = Mat::zeros(1, pWidth, CV_32FC1);
        reduce(apiece, Vdist, 0, REDUCE_AVG, CV_32FC1);

        // 选出最大值和最小值
        double minVal, maxVal;
        int minIdx[2] = {};
        int maxIdx[2] = {};
        minMaxIdx(Vdist, &minVal, &maxVal, minIdx, maxIdx);
        double threshHigh = ((minVal + maxVal) / 4);
        double threshLow = ((minVal + maxVal) / 16);
        int countNumber = 0;
        bool belowThresh = true;
        int curCol = 0;
        vector<int> targetColList;

        //比较每一横坐标与阈值判断是否为线段
        size_t VdistLength = Vdist.cols;
        for (size_t j = 0; j < VdistLength; j++) {
            double proVal = Vdist.at<float>(Point(j, 0));
            if (proVal >= threshHigh) {
                if (belowThresh) {
                    countNumber++;
                    targetColList.push_back(curCol);
                    belowThresh = false;
                }
            }
            else if (proVal <= threshLow) {
                if (belowThresh == false) {
                    belowThresh = true;
                }
            }
            curCol += 1;
        }
        piecesTargetColList.push_back(targetColList);
        piecesCountNumber.push_back(countNumber);
    }

    //piecesTargetColList存放piecesCountNumber中每一个点对应的坐标

    //计算众数得到唯一结果
    //求众数及其索引
    int candidate = -1;
    int numCount = 0;
    int numPos = 0;
    for (int i = 0; i < piecesCountNumber.size(); i++) {
        if (numCount == 0) {
            candidate = piecesCountNumber[i];
            numPos = i;
            numCount++;
        }
        else {
            numCount += (candidate == piecesCountNumber[i] ? 1 : -1);
        }
    }
    // candidate为众数 numPos为该众数的index
    // 至此每一个点及其坐标已经获得
    // candidate 为数量 piecesTargetColList[numPos]为每一点的横坐标 heightMarkList[numPos]为该区间的纵坐标范围
    // return  TODO

    // 绘制到图片上
    int rowMark = heightMarkList[numPos].first;
    vector<int> colMarkList = piecesTargetColList[numPos];
    if (height > 2000) {
        cout << height << endl;
        for (int i = 0; i < colMarkList.size(); i++) {
            circle(src, Point(colMarkList[i], rowMark), 5, Scalar(0, 255, 0),-1);
            putText(src, to_string(i + 1), Point(colMarkList[i], rowMark - 5), FONT_HERSHEY_PLAIN, 2.0, Scalar(222, 86, 69), 2);
        }
        putText(src, "total num: "+to_string(candidate), Point(int(width*0.2), int(height*0.8)), FONT_HERSHEY_COMPLEX, 2.0, Scalar(222, 86, 69), 2);
    }
    else if (height > 1000) {
        cout << height << endl;
        for (int i = 0; i < colMarkList.size(); i++) {
            circle(src, Point(colMarkList[i], rowMark), 3, Scalar(0, 255, 0), -1);
            putText(src, to_string(i + 1), Point(colMarkList[i], rowMark- 5), FONT_HERSHEY_PLAIN, 1.7, Scalar(222, 86, 69), 1);
        }
        putText(src, "total num: " + to_string(candidate), Point(int(width * 0.2), int(height * 0.8)), FONT_HERSHEY_COMPLEX, 1.0, Scalar(222, 86, 69), 1);
    }
    else {
        cout << height << endl;
        for (int i = 0; i < colMarkList.size(); i++) {
            circle(src, Point(colMarkList[i], rowMark), 1, Scalar(0, 255, 0), -1);
            if (i % 2 == 0) {
                putText(src, to_string(i + 1), Point(colMarkList[i], rowMark+10), FONT_HERSHEY_PLAIN, 0.5, Scalar(222, 86, 69), 1);
            }
            else {
                putText(src, to_string(i + 1), Point(colMarkList[i], rowMark-5), FONT_HERSHEY_PLAIN, 0.5, Scalar(222, 86, 69), 1);

            }
        }
        putText(src, "total num: " + to_string(candidate), Point(int(width * 0.2), int(height * 0.8)), FONT_HERSHEY_COMPLEX, 1.0, Scalar(222, 86, 69), 1);
    }
    return src;
//    imwrite("finalresult.jpg", src);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_scuavailable_available_OpencvJni_postData(JNIEnv *env, jobject instance, jbyteArray data_,
                                                   jint width, jint height, jint cameraId) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // 数据的行数也就是数据高度，因为数据类型是NV21，所以为Y+U+V的高度, 也就是height + height/4 + height/4
    Mat src(height*3/2, width, CV_8UC1, data);

    // 转RGB
    cvtColor(src, src, COLOR_YUV2RGBA_NV21);
    if (cameraId == 1) {// 前置摄像头
        //逆时针旋转90度
        rotate(src, src, ROTATE_90_COUNTERCLOCKWISE);
        //1：水平翻转   0：垂直翻转
        flip(src, src, 1);
    } else {
        //顺时针旋转90度
        rotate(src, src, ROTATE_90_CLOCKWISE);

    }
    Mat gray;
    //灰度化
    cvtColor(src, gray, COLOR_RGBA2GRAY);
    //二值化
    equalizeHist(gray, gray);

    std::vector<Rect> faces;
    //检测图片
    tracker->process(gray);
    //获取CascadeDetectorAdapter中的检测结果
    tracker->getObjects(faces);
    //画出矩形
    for (Rect face : faces) {
        rectangle(src, face, Scalar(255, 0, 0));
    }

    //显示到serface
    if (window) {
        ANativeWindow_setBuffersGeometry(window, src.cols, src.rows, WINDOW_FORMAT_RGBA_8888);
        ANativeWindow_Buffer window_buffer;
        do {
            //lock失败 直接brek出去
            if (ANativeWindow_lock(window, &window_buffer, 0)) {
                ANativeWindow_release(window);
                window = 0;
                break;
            }

            uint8_t *dst_data = static_cast<uint8_t *>(window_buffer.bits);
            //stride : 一行多少个数据
            //（RGBA） * 4
            int dst_linesize = window_buffer.stride * 4;

            //一行一行拷贝，src.data是图片的RGBA数据，要拷贝到dst_data中，也就是window的缓冲区里
            for (int i = 0; i < window_buffer.height; ++i) {
                memcpy(dst_data + i * dst_linesize, src.data + i * src.cols * 4, dst_linesize);
            }
            //提交刷新
            ANativeWindow_unlockAndPost(window);
        } while (0);


    }
    src.release();
    gray.release();
    env->ReleaseByteArrayElements(data_, data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_scuavailable_available_OpencvJni_init(JNIEnv *env, jobject instance, jstring path_) {
    const char *path = env->GetStringUTFChars(path_, 0);


    //创建检测器  Ptr是智能指针，不需要关心释放
    Ptr<CascadeClassifier> mainClassifier = makePtr<CascadeClassifier>(path);
    Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(mainClassifier);

    //创建跟踪器
    Ptr<CascadeClassifier> trackClassifier = makePtr<CascadeClassifier>(path);
    Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(trackClassifier);

    //开始识别，结果在CascadeDetectorAdapter中返回
    DetectionBasedTracker::Parameters DetectorParams;
    tracker= new DetectionBasedTracker(mainDetector, trackingDetector, DetectorParams);
    tracker->run();

    env->ReleaseStringUTFChars(path_, path);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_scuavailable_available_OpencvJni_setSurface(JNIEnv *env, jobject instance, jobject surface) {
    if (window) {
        ANativeWindow_release(window);
        window = 0;
    }
    window = ANativeWindow_fromSurface(env, surface);
}