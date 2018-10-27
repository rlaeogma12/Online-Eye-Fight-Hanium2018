#ifndef ALGORITHMS_H
#define ALGORITHMS_H

#include <iostream>
#include <vector>

#include "opencv2/core/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv/cv.h"
#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <opencv2\opencv.hpp>

using namespace std;
using namespace cv;

extern CvHaarClassifierCascade *face_cascade;
extern CvHaarClassifierCascade *left_eye_cascade;
extern CvHaarClassifierCascade *right_eye_cascade;

enum OBJECT {
	FACE,
	LEFT_EYE,
	RIGHT_EYE
};

struct edge {
	int index;
	int square;

	edge(int i, int s) : index(i), square(s) { }

	bool operator<(const edge &str) const {
		return (square > str.square);
	}
};

void DrawRect(CvRect rect, int r, int g, int b, IplImage *&frame);

void DrawRhombus(int* params, int r, int g, int b, int height, int width, IplImage* &frame);

void InitCascades();

CvRect Find(OBJECT obj, IplImage *frame, bool &status, CvRect *area = 0, float resize_koeff = 1);

CvRect ClarifyArea(OBJECT obj, CvRect *area);

void ExpandArea(CvRect &area, float koeff);

bool EyesCorrect(CvRect left_eye, CvRect right_eye, IplImage *frame);

float DistanceToMonitor(CvRect left_eye, CvRect right_eye, int centers_coeff, IplImage *frame);

int CalcEdgeSquare(int **mass, int i, int j, int z);

int *GetEyeDist(IplImage *frame);

CvPoint GetEyesCenter(CvRect left_eye, CvRect right_eye);

float GetDistanceBetweenEyes(CvRect left_eye, CvRect right_eye);

CvRect BuildFace(float cur_face_up_koeff, float cur_face_down_koeff, float cur_face_side_koeff, CvRect left_eye,
	CvRect right_eye);

#endif