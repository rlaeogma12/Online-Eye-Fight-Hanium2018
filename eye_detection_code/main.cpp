#include <iostream>
#include "algorithms.h"

using namespace std;
using namespace cv;

struct eye_params {
	int length;
	int up_border;
	int down_border;
};

int beforeStart = 0;
int NotDetectTime = 0;
bool gameStart = false;
int beforeDetect1 = 0;
int beforeDetect2 = 0;
bool readyForDetect = false;
bool gameExit = false;
Mat grayframe1, grayframe2;
CascadeClassifier eyes_cascade;
vector<Rect> eyes1, eyes2;

bool face_is_found = false;
bool face_refreshed = false;
bool left_eye_is_found = false;
bool right_eye_is_found = false;
bool eyes_are_closed = false;

int false_eyes_count = 0;
int find_eyes_refresh_count = 0;
int max_false_eyes = 5;
int find_eyes_refresh_iterations = 3;

float cur_face_up_koeff = 0;
float cur_face_down_koeff = 0;
float cur_face_side_koeff = 0;
float expand_koeff = 2;

eye_params left_eye_params;
eye_params right_eye_params;

CvRect face;
CvRect left_eye_area;
CvRect right_eye_area;
CvRect left_eye;
CvRect right_eye;



IplImage *grey_frame;

void SaveCurFaceKoeffs() {
	CvPoint center = GetEyesCenter(left_eye, right_eye);

	float centers_dist = GetDistanceBetweenEyes(left_eye, right_eye);

	cur_face_up_koeff = abs(center.y - face.y) / centers_dist;
	cur_face_down_koeff = abs(face.y + face.height - center.y) / centers_dist;
	cur_face_side_koeff = abs(center.x - face.x) / centers_dist;
}


int main()
{
	VideoCapture capture(0);
	Mat frame, frameCopy, image;
	
	eyes_cascade.load("C:/opencv/sources/data/haarcascades/haarcascade_eye_tree_eyeglasses.xml");


	if (!capture.isOpened()) {
		cerr << "Could not open camera" << endl;
		return 0;
	}

	namedWindow("result", 1);

	InitCascades();

	if (1) {
		cout << "인식중..." << endl;
		while (!gameExit) {
			capture >> frame;

			rectangle(frame, Point2d(frame.cols * 3 / 15, frame.rows * 2 / 5 - frame.cols / 9), Point2d(frame.cols * 7 / 15, frame.rows * 2 / 5 + frame.cols / 9), cv::Scalar(255, 255, 255));
			rectangle(frame, Point2d(frame.cols * 8 / 15, frame.rows * 2 / 5 - frame.cols / 9), Point2d(frame.cols * 12 / 15, frame.rows * 2 / 5 + frame.cols / 9), cv::Scalar(255, 255, 255));
			

			if (readyForDetect == false) {
				Mat L_eye(frame, Rect(Point2d(frame.cols * 3 / 15, frame.rows * 2 / 5 - frame.cols / 9), Point2d(frame.cols * 7 / 15, frame.rows * 2 / 5 + frame.cols / 9)));
				Mat R_eye(frame, Rect(Point2d(frame.cols * 8 / 15, frame.rows * 2 / 5 - frame.cols / 9), Point2d(frame.cols * 12 / 15, frame.rows * 2 / 5 + frame.cols / 9)));

				cvtColor(L_eye, grayframe1, CV_BGR2GRAY);
				equalizeHist(grayframe1, grayframe1);

				cvtColor(R_eye, grayframe2, CV_BGR2GRAY);
				equalizeHist(grayframe2, grayframe2);

				eyes_cascade.detectMultiScale(grayframe1, eyes1, 1.1, 2, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));
				eyes_cascade.detectMultiScale(grayframe2, eyes2, 1.1, 2, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));

				if (eyes1.size() > 0 && eyes2.size() > 0) {
					rectangle(L_eye, Rect(Point(eyes1[0].x, eyes1[0].y), Point(eyes1[0].x + eyes1[0].width, eyes1[0].y + eyes1[0].height)), Scalar(0, 0, 255));
					rectangle(R_eye, Rect(Point(eyes2[0].x, eyes2[0].y), Point(eyes2[0].x + eyes2[0].width, eyes2[0].y + eyes2[0].height)), Scalar(0, 0, 255));
					beforeDetect2 = 0;
					beforeDetect1+=10;
				}
				else {
					beforeDetect1--;
					beforeDetect2--;
				}

				if (beforeDetect1 >= 100) {
					cout << "눈 인식을 시작합니다" << endl;
					readyForDetect = true;
				}
				else if(beforeDetect2 < -30){
					cout << "눈을 잘 가져다 대주세요. 환한데로 가주세요." << endl;
					beforeDetect2 = 0;
				}
			}
			

			//CvRect Left_eye_rect(frame.cols * 5 / 15, frame.rows * 2 / 5 - frame.cols / 13, frame.cols * 2 / 15, frame.cols * 2 / 11);
			//CvRect Right_eye_rect(frame.cols * 8 / 15, frame.rows * 2 / 5 - frame.cols / 13, frame.cols * 2 / 15, frame.cols * 2 / 11);

			IplImage *iplImg = &IplImage(frame);

			if (!frame.empty()) {

				if (iplImg->origin == IPL_ORIGIN_TL)
					frame.copyTo(frameCopy);
				else
					flip(frame, frameCopy, 0);


				if (false_eyes_count <= 0 && readyForDetect == true) {
					face_is_found = false;
					face = Find(FACE, iplImg, face_is_found, 0, 0.5);

					face_refreshed = true;
				}

				if (face_is_found) {
					if (find_eyes_refresh_count == 0) {
						CvRect temp;

						left_eye_area = left_eye;
						if (false_eyes_count <= 0) {
							left_eye_area = ClarifyArea(LEFT_EYE, &face);
						}
						else {
							ExpandArea(left_eye_area, expand_koeff);
						}
						temp = Find(LEFT_EYE, iplImg, left_eye_is_found, &left_eye_area);
						if (left_eye_is_found) {
							left_eye = temp;
						}

						right_eye_area = right_eye;
						if (false_eyes_count <= 0)
							right_eye_area = ClarifyArea(RIGHT_EYE, &face);
						else
							ExpandArea(right_eye_area, expand_koeff);
						temp = Find(RIGHT_EYE, iplImg, right_eye_is_found, &right_eye_area);
						if (right_eye_is_found)
							right_eye = temp;
					}

					find_eyes_refresh_count++;

					if ((left_eye_is_found && right_eye_is_found) && (!EyesCorrect(left_eye, right_eye, iplImg))) {
						left_eye_is_found = false;
						right_eye_is_found = false;
						false_eyes_count = 1;
					}

					if (left_eye_is_found && right_eye_is_found)
						false_eyes_count = max_false_eyes;
					else
						false_eyes_count--;
				}

				grey_frame = cvCreateImage(cvSize(iplImg->width, iplImg->height), iplImg->depth, 1);
				cvCvtColor(iplImg, grey_frame, CV_RGB2GRAY);

				if (left_eye_is_found && right_eye_is_found) {
					if (face_refreshed)
						SaveCurFaceKoeffs();
					else
						face = BuildFace(cur_face_up_koeff, cur_face_down_koeff, cur_face_side_koeff, left_eye,
							right_eye);

					//DrawRect(face, 150, 150, 150, iplImg);

					int *eye_params;

					cvSetImageROI(grey_frame, left_eye);
					eye_params = GetEyeDist(grey_frame);
					left_eye_params.length = eye_params[0];
					left_eye_params.down_border = eye_params[1];
					left_eye_params.up_border = eye_params[2];
					cvResetImageROI(grey_frame);

					cvSetImageROI(iplImg, left_eye);
					DrawRhombus(eye_params, 0, 255, 0, left_eye.height, left_eye.width, iplImg);
					delete[] eye_params;
					cvResetImageROI(iplImg);

					cvSetImageROI(grey_frame, right_eye);
					eye_params = GetEyeDist(grey_frame);
					right_eye_params.length = eye_params[0];
					right_eye_params.down_border = eye_params[1];
					right_eye_params.up_border = eye_params[2];
					cvResetImageROI(grey_frame);

					cvSetImageROI(iplImg, right_eye);
					DrawRhombus(eye_params, 0, 255, 0, right_eye.height, right_eye.width, iplImg);
					delete[] eye_params;
					cvResetImageROI(iplImg);

					///////////////////////////////////////////////////////////// BLINK DETECTION

					if ((left_eye_params.up_border >= left_eye.height / 2) ||
						(right_eye_params.up_border >= right_eye.height / 2)) {
						if (!eyes_are_closed && gameStart) {
							cout << "blink" << endl;
							gameExit = true;
						}
						eyes_are_closed = true;
						DrawRect(left_eye, 0, 0, 0, iplImg);
						DrawRect(right_eye, 0, 0, 0, iplImg);
						
					}
					else {
						eyes_are_closed = false;
						DrawRect(left_eye, 255, 255, 255, iplImg);
						DrawRect(right_eye, 255, 255, 255, iplImg);
		
					}

					beforeStart++;
					NotDetectTime = 0;
					if (beforeStart == 101) {
						gameStart = true;
						cout << "게임을 시작합니다" << endl;
					}

					/////////////////////////////////////////////////////////////
				}
				else {
					find_eyes_refresh_count = 0;
					NotDetectTime++;
					if (NotDetectTime > 20 && readyForDetect == true && gameStart == false) {
						cout << "눈을 제대로 맞춰주세요" << endl;
						NotDetectTime = 0;
					}
					if (beforeStart < 100) {
						beforeStart = 0;
					}
				}
				if (find_eyes_refresh_count > find_eyes_refresh_iterations) {
					find_eyes_refresh_count = 0;
				}

				face_refreshed = false;

				if (beforeStart > 0 && beforeStart <= 100 && beforeStart%10==0) 
					cout << "준비 : " << beforeStart << endl;

				cvShowImage("result", iplImg);

				if (waitKey(10) >= 0)
					break;
			}
		}
	}
	cout << "게임 종료" << endl;

	//cvReleaseCapture(&capture);
	cvDestroyWindow("result");

	return 0;
}