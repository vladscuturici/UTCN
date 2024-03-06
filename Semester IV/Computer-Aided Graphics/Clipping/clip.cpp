#include "clip.h"

namespace egc {

	std::vector<int> computeCSCode(std::vector<vec3> clipWindow, const vec3 p) {
		std::vector<int> code;
		//TO DO - compute the code for the point given as argument
		int a, b, c, d;
		a = b = c = d = 0;
		int x_min, y_min, x_max, y_max;
		x_min = x_max = clipWindow.at(0).x;
		y_min = y_max = clipWindow.at(0).y;
		for (int i = 1; i < 4; i++) {
			if (clipWindow.at(i).x > x_max)
				x_max = clipWindow.at(i).x;
			if (clipWindow.at(i).x < x_min)
				x_min = clipWindow.at(i).x;
			if (clipWindow.at(i).y > y_max)
				y_max = clipWindow.at(i).y;
			if (clipWindow.at(i).y < y_min)
				y_min = clipWindow.at(i).y;
		}
		//std::cout << x_min << " " << x_max << " " << y_min << " " << y_max << " \n";
		if (p.y > y_max)
			b = 1;
		if (p.y < y_min)
			a = 1;
		if (p.x < x_min)
			d = 1;
		if (p.x > x_max)
			c = 1;
		code.push_back(a);
		code.push_back(b);
		code.push_back(c);
		code.push_back(d);
		return code;
	}

	bool simpleRejection(std::vector<int> cod1, std::vector<int> cod2) {
		//TO DO - write the code to determine if the two input codes represent 
		//points in the SIMPLE REJECTION case
		if (cod1.at(0) == 1 && cod2.at(0) == 1)
			return true;
		if (cod1.at(1) == 1 && cod2.at(1) == 1)
			return true;
		if (cod1.at(2) == 1 && cod2.at(2) == 1)
			return true;
		if (cod1.at(3) == 1 && cod2.at(3) == 1)
			return true;
		return false;
	}

	bool simpleAcceptance(std::vector<int> cod1, std::vector<int> cod2) {
		//TO DO - write the code to determine if the two input codes represent 
		//points in the SIMPLE ACCEPTANCE case
		if (cod1.at(0) == 0 && cod2.at(0) == 0 && cod1.at(1) == 0 && cod2.at(1) == 0 && cod1.at(2) == 0 && cod2.at(2) == 0 && cod1.at(3) == 0 && cod2.at(3) == 0)
			return true;
 		return false;
	}

	//function returns -1 if the line segment cannot be clipped
	int lineClip_CohenSutherland(std::vector<vec3> clipWindow, vec3& p1, vec3& p2) {
		//TO DO - implement the Cohen-Sutherland line clipping algorithm - consult the laboratory work
		bool finished = false;
		std::vector<int> cod1;
		std::vector<int> cod2;
		int x_min, y_min, x_max, y_max;
		x_min = x_max = clipWindow.at(0).x;
		y_min = y_max = clipWindow.at(0).y;
		for (int i = 1; i < 4; i++) {
			if (clipWindow.at(i).x > x_max)
				x_max = clipWindow.at(i).x;
			if (clipWindow.at(i).x < x_min)
				x_min = clipWindow.at(i).x;
			if (clipWindow.at(i).y > y_max)
				y_max = clipWindow.at(i).y;
			if (clipWindow.at(i).y < y_min)
				y_min = clipWindow.at(i).y;
		}
		while (finished == false) {
			cod1 = computeCSCode(clipWindow, p1);
			cod2 = computeCSCode(clipWindow, p2);
			if (simpleRejection(cod1, cod2)) {
				finished = true;
				return -1;
			}
			else {
				if (simpleAcceptance(cod1, cod2))
					finished = true;
				else {
					if (cod1.at(0) == 0 && cod1.at(1) == 0 && cod1.at(2) == 0 && cod1.at(3) == 0) {
						swap(cod1, cod2);
						vec3 t;
						t = p1;
						p1 = p2;
						p2 = t;
					}
					if (cod1.at(0) == 1 && p2.y != p1.y) {
						p1.x = p1.x + (p2.x - p1.x) * (y_min - p1.y) / (p2.y - p1.y);
						p1.y = y_min;
					}
					else if (cod1.at(1) == 1 && p2.y != p1.y) {
						p1.x = p1.x + (p2.x - p1.x) * (y_max - p1.y) / (p2.y - p1.y);
						p1.y = y_max;
					}
					else if (cod1.at(2) == 1 && p2.x != p1.x) {
						p1.y = p1.y + (p2.y - p1.y) * (x_max - p1.x) / (p2.x - p1.x);
						p1.x = x_max;
					}
					else if (cod1.at(3) == 1 && p2.x != p1.x) {
						p1.y = p1.y + (p2.y - p1.y) * (x_min - p1.x) / (p2.x - p1.x);
						p1.x = x_min;
					}
				}
			}
			
		}
		return 0;
	}
}

