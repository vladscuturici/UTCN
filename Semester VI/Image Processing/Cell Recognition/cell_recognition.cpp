// OpenCVApplication.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include "common.h"
#include <opencv2/core/utils/logger.hpp>

wchar_t* projectPath;

//culori background si foreground
#define FG 0
#define BG 255

//binarizare cu prag automat
//calculeaza un prag optim pentru a separa pixelii in alb si negru bazandu-se pe distributia intensitatilor
//pragul este ajustat pana cand diferenta dintre pragurile succesive este destul de mica, mai mica decat 'e'
Mat pragBinarizare(Mat src) {
	int M = src.rows * src.cols;
	int h[256] = { 0 };
	int hc[256] = { 0 };
	float p[256] = { 0 };
	for (int i = 0; i < src.rows; i++) {
		for (int j = 0; j < src.cols; j++) {
			uchar g = src.at<uchar>(i, j);
			h[g] ++;
		}
	}
	for (int g = 0; g < 256; g++) {
		p[g] = (float)h[g] / M;
	}
	hc[0] = h[0];
	for (int g = 1; g < 256; g++) {
		hc[g] = hc[g - 1] + h[g];
	}
	int imin, imax = 0, ginmin, ginmax;
	int g;
	for (g = 0; g < 256; g++)
		if (h[g] > 0)
			break;
	imin = g;
	float hmax = 0;
	for (int y = 1; y < 256; y++)
		if (h[y] > hmax) {
			hmax = h[y];
			imax = y;
		}

	float e = 0.5;
	float Told = 0;
	float T = (imin + imax) / 2;
	do {
		Told = T;
		float m1 = 0, m2 = 0;
		float sum_m1 = 0, sum_m2 = 0;
		for (g = 0; g < Told; g++) {
			m1 += g * p[g];
			sum_m1 += p[g];
		}
		for (g = Told; g < 256; g++) {
			m2 += g * p[g];
			sum_m2 += p[g];
		}
		if (sum_m1 > 0) m1 /= sum_m1;
		if (sum_m2 > 0) m2 /= sum_m2;
		T = (m1 + m2) / 2;
	} while (abs(T - Told) > e);

	Mat dst(src.rows, src.cols, CV_8UC1);
	for (int i = 0; i < dst.rows; i++) {
		for (int j = 0; j < dst.cols; j++) {
			uchar pixel = src.at<uchar>(i, j);
			if (pixel < T)
				dst.at<uchar>(i, j) = 0;
			else
				dst.at<uchar>(i, j) = 255;
		}
	}
	return dst;
}

//dilatare + eroziune
Mat inchidere(Mat src) {

	int di[8] = { 0, -1, -1, -1, 0, 1, 1, 1 };
	int dj[8] = { 1, 1, 0, -1, -1, -1, 0, 1 };

	Mat temp = src.clone();

	int height = src.rows;
	int width = src.cols;

	for (int i = 1; i < height - 1; i++)
		for (int j = 1; j < width - 1; j++)
			if (src.at<uchar>(i, j) == FG)
				for (int k = 0; k < 8; k++)
					temp.at<uchar>(i + di[k], j + dj[k]) = FG;

	Mat dst = temp.clone();
	for (int i = 1; i < height - 1; i++)
		for (int j = 1; j < width - 1; j++)
			if (temp.at<uchar>(i, j) == BG)
				for (int k = 0; k < 8; k++)
					dst.at<uchar>(i + di[k], j + dj[k]) = BG;
	return dst;
}

//eroziune + dilatare
 Mat deschidere(Mat src) {

	int di[8] = { 0, -1, -1, -1, 0, 1, 1, 1 };
	int dj[8] = { 1, 1, 0, -1, -1, -1, 0, 1 };

	Mat temp = src.clone();

	int height = src.rows;
	int width = src.cols;

	for (int i = 1; i < height - 1; i++)
		for (int j = 1; j < width - 1; j++)
			if (src.at<uchar>(i, j) == BG)
				for (int k = 0; k < 8; k++)
					temp.at<uchar>(i + di[k], j + dj[k]) = BG;

	Mat dst = temp.clone();
	for (int i = 1; i < height - 1; i++)
		for (int j = 1; j < width - 1; j++)
			if (temp.at<uchar>(i, j) == FG)
				for (int k = 0; k < 8; k++)
					dst.at<uchar>(i + di[k], j + dj[k]) = FG;
	return dst;
}

Mat medianBlur(Mat src, int kernelSize) {
	int edge = kernelSize / 2;
	Mat dst = src.clone();

	//pentru fiecare pixel, se adauga vecinii din nucleu, se sorteaza, iar apoi se alege valoarea mediana
	for (int i = edge; i < src.rows - edge; i++) {
		for (int j = edge; j < src.cols - edge; j++) {
			std::vector<uchar> neighbors;
			for (int x = -edge; x <= edge; x++) {
				for (int y = -edge; y <= edge; y++) {
					neighbors.push_back(src.at<uchar>(i + x, j + y));
				}
			}
			std::sort(neighbors.begin(), neighbors.end());
			dst.at<uchar>(i, j) = neighbors[neighbors.size() / 2];
		}
	}
	return dst;
}

Mat removeNoiseFromImage(Mat& src, int kernel_size) {
	//aplicare filtru median prin median blur, deschidere, inchidere
	Mat filtered, opened, closed;
	filtered = medianBlur(src, kernel_size);
	opened = deschidere(filtered);
	closed = inchidere(opened);
	return closed;
}

Mat removeNoise(Mat src) {
	//aplicarea a 3 filtre mediene
	Mat temp = removeNoiseFromImage(src, 9);
	temp = removeNoiseFromImage(temp, 9);
	temp = removeNoiseFromImage(temp, 5);
	return temp;
}

void fillHoles(Mat& src) {
	//functie de umple a gaurilor
	int rows = src.rows;
	int cols = src.cols;
	Mat visited = Mat::zeros(src.size(), CV_8U); 

	int dx[] = { -1, 1, 0, 0, -1, -1, 1, 1 };
	int dy[] = { 0, 0, -1, 1, -1, 1, -1, 1 };

	//identificam pixelii de fundal de pe marginea imaginii si ii punem in coada
	std::queue<Point> q;
	for (int i = 0; i < rows; i++) {
		for (int j = 0; j < cols; j++) {
			if ((i == 0 || i == rows - 1 || j == 0 || j == cols - 1) && src.at<uchar>(i, j) == BG && visited.at<uchar>(i, j) == 0) {
				q.push(Point(j, i));
				visited.at<uchar>(i, j) = 1;
			}
		}
	}

	//pornind de la fiecare pixel de fundal de pe margine, se exploreaza toti pixelii de fundal prin BFS care nu au fost explorati
	//si se marcheaza ca vizitat
	while (!q.empty()) {
		Point p = q.front();
		q.pop();

		for (int k = 0; k < 8; k++) {
			int nx = p.x + dx[k];
			int ny = p.y + dy[k];

			if (nx >= 0 && nx < cols && ny >= 0 && ny < rows && src.at<uchar>(ny, nx) == BG && visited.at<uchar>(ny, nx) == 0) {
				q.push(Point(nx, ny));
				visited.at<uchar>(ny, nx) = 1; 
			}
		}
	}

	//dupa primii 2 pasi, singurii pixeli de fundal care nu au fost vizitati sunti golurile din celule
	//la acest pas umplem golurile cu culoarea FG
	for (int i = 0; i < rows; i++) {
		for (int j = 0; j < cols; j++) {
			if (src.at<uchar>(i, j) == BG && visited.at<uchar>(i, j) == 0) {
				src.at<uchar>(i, j) = FG;  
			}
		}
	}
}

//functie care determina toti pixelii componentei de culoare de fundal pornind de la punctul de start
int bfs(Mat& src, Mat& visited, Point start, std::vector<Point>& component) {
	std::queue<Point> q;
	q.push(start);
	visited.at<uchar>(start.y, start.x) = 1;
	int size = 0;

	while (!q.empty()) {
		Point p = q.front();
		q.pop();
		component.push_back(p);
		size++;

		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				int nx = p.x + dx;
				int ny = p.y + dy;
				if (nx >= 0 && nx < src.cols && ny >= 0 && ny < src.rows &&
					src.at<uchar>(ny, nx) == BG && visited.at<uchar>(ny, nx) == 0) {
					visited.at<uchar>(ny, nx) = 1;
					q.push(Point(nx, ny));
				}
			}
		}
	}
	return size;
}

//functie pentru a umple golurile pentru celulele partial in imagine
void fillHolesConnectedToEdges(Mat& src) {
	int rows = src.rows, cols = src.cols;
	Mat visited = Mat::zeros(src.size(), CV_8UC1);
	std::vector<std::vector<Point>> components;
	std::vector<int> sizes;

	//se identifica pixelii de fundal de pe primul si ultimul rand, iar apoi se determina componentele cu functia bfs
	for (int i = 0; i < rows; i++) {
		if (src.at<uchar>(i, 0) == BG && visited.at<uchar>(i, 0) == 0) {
			std::vector<Point> component;
			sizes.push_back(bfs(src, visited, Point(0, i), component));
			components.push_back(component);
		}
		if (src.at<uchar>(i, cols - 1) == BG && visited.at<uchar>(i, cols - 1) == 0) {
			std::vector<Point> component;
			sizes.push_back(bfs(src, visited, Point(cols - 1, i), component));
			components.push_back(component);
		}
	}

	//se identifica pixelii de fundal de pe prima si ultima coloana, iar apoi se determina componentele cu functia bfs
	for (int j = 0; j < cols; j++) {
		if (src.at<uchar>(0, j) == BG && visited.at<uchar>(0, j) == 0) {
			std::vector<Point> component;
			sizes.push_back(bfs(src, visited, Point(j, 0), component));
			components.push_back(component);
		}
		if (src.at<uchar>(rows - 1, j) == BG && visited.at<uchar>(rows - 1, j) == 0) {
			std::vector<Point> component;
			sizes.push_back(bfs(src, visited, Point(j, rows - 1), component));
			components.push_back(component);
		}
	}

	//se determina elementul cel mai mare, care este fundalul
	int max_index = max_element(sizes.begin(), sizes.end()) - sizes.begin();

	//umplem spatiile goale, adica restul componentelor 
	for (size_t i = 0; i < components.size(); i++) {
		if (i != max_index) {
			for (Point& p : components[i]) {
				src.at<uchar>(p.y, p.x) = FG; 
			}
		}
	}
}

//structura pentru celule
struct Cell {
	std::vector<Point2d> pointList;//lista de puncte care formeaza celula
	int area;
	double diameter;
	Point2d center;
	double elongation;//elongatia pentru a determina daca avem o celula intreaga si nesuprapusa
	bool isComplete;//variabila pentru a determina daca celulele sunt in totalitate in imagine
	int id;//numar de identificare
};

//functie care colecteaza punctele care formeaza o componenta conexa / celula in functie de un punct de start
void bfsCollect(Mat& src, Mat& visited, int x, int y, std::vector<Point2d>& points) {
	std::queue<Point> q;
	q.push(Point(x, y));
	visited.at<uchar>(y, x) = 1;

	while (!q.empty()) {
		Point p = q.front();
		q.pop();
		points.push_back(Point2d(p.x, p.y));

		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				int nx = p.x + dx;
				int ny = p.y + dy;
				if (nx >= 0 && nx < src.cols && ny >= 0 && ny < src.rows && src.at<uchar>(ny, nx) == FG && visited.at<uchar>(ny, nx) == 0) {
					visited.at<uchar>(ny, nx) = 1;
					q.push(Point(nx, ny));
				}
			}
		}
	}
}

int cellId = 0;

//creeaza variabile de tip Cell si le adauga intr un vector
std::vector<Cell> cellListDetector(Mat& src) {
	std::vector<Cell> cells;
	Mat visited = Mat::zeros(src.size(), CV_8UC1);
	for (int i = 0; i < src.rows; i++) {
		for (int j = 0; j < src.cols; j++) {
			if (src.at<uchar>(i, j) == FG && visited.at<uchar>(i, j) == 0) {
				std::vector<Point2d> pointList;
				bfsCollect(src, visited, j, i, pointList);
				if (!pointList.empty()) {
					Cell cell;
					cell.pointList = pointList;
					cell.id = ++cellId;
					//std::printf("found\n");
					cells.push_back(cell);
				}
			}
		}
	}
	return cells;
}

//calculeaza aria
int calculateArea(std::vector<Point2d> pointList) {
	return pointList.size();
}

//calculeaza centrul
Point2d calculateCenter(const std::vector<Point2d>& pointList) {
	Point2d center;
	center.x = 0;
	center.y = 0;
	double sumX = 0.0;
	double sumY = 0.0;

	for (Point2d point: pointList) {
		sumX+=point.x;
		sumY+=point.y;
	}
	//cout << sumX << " " << sumY << "\n";

	int n = pointList.size();
	if (n > 0) {
		center.x = sumX / n; center.y = sumY / n;
	}
	return center;
}

//calculeaza elongatia
double calculateElongation(const std::vector<Point2d>& pointList) {

	std::vector<Point> points;
	for (Point pt : pointList) {
		points.push_back(Point(pt.x, pt.y));
	}
	//creeaza o elipsa pe baza punctelor
	RotatedRect ellipse = fitEllipse(points);

	double majorAxis, minorAxis;

	if (ellipse.size.width > ellipse.size.height) {
		majorAxis = ellipse.size.width;
		minorAxis = ellipse.size.height;
	}
	else {
		majorAxis = ellipse.size.height;
		minorAxis = ellipse.size.width;
	}
	double elongation = majorAxis / minorAxis;

	return elongation;
}

void calculateElongations(std::vector<Cell>& cells) {
	for (Cell& cell : cells) {
		cell.elongation = calculateElongation(cell.pointList);
	}
}

void calculateProperties(std::vector<Cell> &cells) {

	for (Cell& cell :cells) {

		cell.area = calculateArea(cell.pointList);
		cell.center = calculateCenter(cell.pointList);
		cell.diameter = sqrt(4 * cell.area / 3.141527);
	}
}

void displayProperties(Mat &src, std::vector<Cell> cells) {

	for (const Cell& cell : cells) {

		std::cout << "Cell " << cell.id << " Properties:\n";
		std::cout << " Area: " << cell.area << '\n';
		std::cout << " Diameter: " << cell.diameter << '\n';
		std::cout << " Center: (x = " << cell.center.x << ", y = " << cell.center.y << ")\n";
	
		src.at<uchar>(cell.center.y , cell.center.x) = 0;
		
		Scalar color;
		if (cell.isComplete && cell.elongation < 1.3)
			color = Scalar(0, 0, 0);
		else
			color = Scalar(255, 255, 255);
		circle(src, Point(cell.center.x, cell.center.y), 2, color, -1);
		putText(src, std::to_string(cell.id), Point(cell.center.x, cell.center.y),
			FONT_HERSHEY_SIMPLEX, 0.3, Scalar(255, 255, 255), 1);
	}
}

//verifica daca celula e complet in imagine
void verifyComplete(Cell& cell, int height, int width) {
	bool isComplete = true;
	for (const Point& point : cell.pointList) {
		if (point.x == 0 || point.y == 0 || point.x == width - 1 || point.y == height - 1) {
			isComplete = false;
		}
	}
	cell.isComplete = isComplete;
}

//separa o componenta alcatuita din 2 celule in 2 variabile de tip cell
void separateCells(Cell& cell, Cell& cell1, Cell& cell2) {

	std::vector<cv::Point> points;
	for (Point2d& pt : cell.pointList) {
		points.push_back(Point(pt.x, pt.y));
	}
	//creeaza o elipsa pe baza punctelor
	RotatedRect ellipse = fitEllipse(points);
	
	Point2f center = ellipse.center;
	float angle = ellipse.angle; 
	//convertim unghiul elipsei in radiani si calcula 
	double angle_rad = angle * CV_PI / 180.0;
	double cos_a = cos(angle_rad), sin_a = sin(angle_rad);

	//pentru fiecare punct determinam de ce parte se afla relativ cu centrul
	for (Point2d& pt : cell.pointList) {
		//rotim punctul pt cu unghiul elipsei
		double x_shifted = pt.x - center.x;
		double y_shifted = pt.y - center.y;

		double x_rot = x_shifted * cos_a + y_shifted * sin_a;
		double y_rot = -x_shifted * sin_a + y_shifted * cos_a;

		//verificam y al punctului rotit este in imagine
		if (y_rot > 0) {
			cell1.pointList.push_back(pt);
		}
		else {
			cell2.pointList.push_back(pt);
		}
	}
	cell1.id = cell.id;
	cell2.id = ++cellId;
}

//separam celulele care se suprapun
void separateOverlappingCells(std::vector<Cell> &cells, int height, int width) {
	for (Cell &cell : cells) {
		verifyComplete(cell, height, width);
		if (cell.elongation > 1.3) { //daca celula "este departe de a fi" cerc
			if (cell.isComplete) { //daca celula este completa
				Cell cell1, cell2;
				separateCells(cell, cell1, cell2);
				cell = cell1;
				cells.push_back(cell2);
			}
		}
	}
}

//generator de culori ranodm
cv::Scalar randomColor() {
	return Scalar(std::rand() % 256, std::rand() % 256, std::rand() % 256);
}

//coloreaza fiecare celula cu o culoare diferita si returneaza rezultatul intr-o imagine colora
Mat colorComponents(std::vector<Cell> cells, Mat src) {
	Mat colorImage = Mat::zeros(src.size(), CV_8UC3);

	for (Cell& cell : cells) {
		Scalar color = randomColor();
		for (Point2d& point : cell.pointList) {
			colorImage.at<Vec3b>(Point(point.x, point.y)) = Vec3b((uchar)color[0], (uchar)color[1], (uchar)color[2]);
		}
	}

	return colorImage;
}

void detectCells(Mat &src) {
	Mat temp;
	temp = removeNoise(src);
	imshow("Reducere zgomot", temp);
	temp = pragBinarizare(temp);
	imshow("Binarizare", temp);
	fillHoles(temp);
	fillHolesConnectedToEdges(temp);
	imshow("Umplere obiecte", temp);
	std::vector<Cell> cells;
	cells = cellListDetector(temp);
	calculateElongations(cells);
	separateOverlappingCells(cells, src.rows, src.cols);
	calculateProperties(cells);
	displayProperties(src, cells);
	imshow("Centre de masa", src);
	std::srand((unsigned)std::time(0));
	Mat colorImage = colorComponents(cells, src);
	imshow("Componente vizulizate", colorImage);
}

void testDetectCells() {
	char fname[] = "C:/Users/User/Documents/PI/Proiect/OpenCVApplication-VS2022_OCV490_basic/blood1.bmp";
	Mat src = imread(fname, IMREAD_GRAYSCALE);
	imshow("Original", src);
	detectCells(src);
	waitKey(0);
}

int main() 
{
	cv::utils::logging::setLogLevel(cv::utils::logging::LOG_LEVEL_FATAL);
    projectPath = _wgetcwd(0, 0);

	testDetectCells();
	return 0;
}