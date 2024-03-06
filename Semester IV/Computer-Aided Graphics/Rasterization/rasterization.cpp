#include "rasterization.h"
#include "vec2.h"
namespace egc {
	float F_ab(const std::vector<egc::vec4> triangleVertices, int x, int y) {
		float xa = triangleVertices.at(0).x;
		float ya = triangleVertices.at(0).y;
		float xb = triangleVertices.at(1).x;
		float yb = triangleVertices.at(1).y;
		return (ya - yb) * x + (xb - xa) * y + xa * yb - xb * ya;
	}

	float F_bc(const std::vector<egc::vec4> triangleVertices, int x, int y) {
		float xb = triangleVertices.at(1).x;
		float yb = triangleVertices.at(1).y;
		float xc = triangleVertices.at(2).x;
		float yc = triangleVertices.at(2).y;
		return (yb - yc) * x + (xc - xb) * y + xb * yc - xc * yb;
	}

	float F_ca(const std::vector<egc::vec4> triangleVertices, int x, int y) {
		float xa = triangleVertices.at(0).x;
		float ya = triangleVertices.at(0).y;
		float xc = triangleVertices.at(2).x;
		float yc = triangleVertices.at(2).y;
		return (yc - ya) * x + (xa - xc) * y + xc * ya - xa * yc;
	}

	void computeAlphaBetaGamma(const std::vector<egc::vec4>& triangleVertices, vec2 pixel, float& alpha, float& beta, float& gamma)
	{
		alpha = F_bc(triangleVertices, pixel.x, pixel.y) / F_bc(triangleVertices, triangleVertices.at(0).x, triangleVertices.at(0).y);
		beta = F_ca(triangleVertices, pixel.x, pixel.y) / F_ca(triangleVertices, triangleVertices.at(1).x, triangleVertices.at(1).y);
		gamma = 1 - alpha - beta;
	}

	void rasterizeTriangle(SDL_Renderer* renderer, const std::vector<egc::vec4>& triangleVertices, const std::vector<egc::vec4>& triangleColors) {
		//TO DO - Implement the triangle rasterization algorithm
		float xmin = triangleVertices.at(0).x, ymin = triangleVertices.at(0).y, xmax = triangleVertices.at(0).x, ymax = triangleVertices.at(0).y;
		float alpha, gamma, beta;
		//std::cout << triangleVertices.at(0);
		for (int i = 1; i < 3; i++) {
			//std::cout << triangleVertices.at(i);
			if (xmin > triangleVertices.at(i).x)
				xmin = triangleVertices.at(i).x;
			if (ymin > triangleVertices.at(i).y)
				ymin = triangleVertices.at(i).y;
			if (xmax < triangleVertices.at(i).x)
				xmax = triangleVertices.at(i).x;
			if (ymax < triangleVertices.at(i).y)
				ymax = triangleVertices.at(i).y;
		}

		//std::cout << "\n" << xmin << " " << ymin << " \n" << xmax << " " << ymax;

		for (float x = xmin; x < xmax; x++) {
			for (float y = ymin; y < ymax; y++) {
				vec2 pixel;
				pixel.x = x;
				pixel.y = y;
				computeAlphaBetaGamma(triangleVertices, pixel, alpha, beta, gamma);
				vec4 color;
				float color2;
				if (0 < alpha && alpha < 1 && 0 < beta && beta < 1 && 0 < gamma && gamma < 1) {
					color = triangleColors.at(0) * alpha + triangleColors.at(1) * beta + triangleColors.at(2) * gamma;
					SDL_SetRenderDrawColor(renderer, color.x, color.y, color.z, 255);
					SDL_RenderDrawPoint(renderer, x, y);
				}
			}
		}
	}

}