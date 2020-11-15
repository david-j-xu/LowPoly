#include <iostream>
//#include <opencv2/highgui.hpp>
//#include <opencv2/imgcodecs.hpp>
//#include <opencv2/imgproc.hpp>
//#include <opencv2/core.hpp>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

void LaplacianFilter(Mat input, Mat output) {
	Mat kernel = (Mat_<double>(3, 3) << 0, -1, 0, -1, 4, -1, 0, -1, 0);
	cout << kernel;
	filter2D(input, output, -1, kernel);
}

int main() {

    Mat original;        // input image
    Mat grayscale; // grayscale of input image
    Mat gaussian;            // Gaussian image
    Mat laplacian;           // Laplacian image
	Mat canny;            // Canny edge image

    original = imread("lena.png");            // open image

    if (original.empty()) {                             
        cout << "error: image not read from file\n\n";
        return(0);                                         
    }

    cvtColor(original, grayscale, COLOR_BGRA2GRAY);        // convert to grayscale
	
	Mat laplacianKernel = (Mat_<double>(3, 3) << 0, -1, 0, -1, 4, -1, 0, -1, 0);
	filter2D(grayscale, laplacian, -1, laplacianKernel);
	
	// std::cout << laplacian;
    
	GaussianBlur(grayscale,            // input image
        gaussian,                            // output image
        Size(3, 3),                        // smoothing window width and height in pixels
        1.5);                                // sigma value, determines how much the image will be blurred

    Canny(gaussian,            // input image
        canny,                    // output image
        20,                        // low threshold
        50);                        // high threshold
 
    namedWindow("Original", WINDOW_AUTOSIZE);
	namedWindow("Gaussian", WINDOW_AUTOSIZE);
	namedWindow("Laplacian", WINDOW_AUTOSIZE);
    namedWindow("Canny", WINDOW_AUTOSIZE);

    //Show windows
    imshow("Original", original);
    imshow("Gaussian", gaussian);
	imshow("Laplacian", laplacian);
 	imshow("Canny", canny);

	waitKey(0);                    // hold windows open until user presses a key
    return 0;
}
