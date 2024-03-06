//
//  projection.cpp
//  Lab8_TODO
//
//  Copyright © 2016 CGIS. All rights reserved.
//

#include "projection.h"

namespace egc {
    //define the viewport transformation matrix
    //see section 3 from the laboratory work
    mat4 defineViewTransformMatrix(int startX, int startY, int width, int height)
    {
		mat4 viewTransformMatrix, smt;
        smt.at(0, 0) = width / 2.0F;
        smt.at(0, 3) = width / 2.0F;
        smt.at(1, 1) = -height / 2.0F;
        smt.at(1, 3) = height / 2.0F;
        mat4 temp = translate((float)startX, (float)startY, 0.0f);
        viewTransformMatrix = temp * smt;
        //std::cout << smt << std::endl;
        return viewTransformMatrix; 
    }
    
    //define the camera transformation matrix
    //see section 4 from the laboratory work
    mat4 defineCameraMatrix(Camera mc)
    {
        mat4 cameraMatrix;
        vec3 u, w, v;
        vec3 g = mc.cameraTarget - mc.cameraPosition;
        w = - g.normalize();
        u = crossProduct(mc.cameraUp, w).normalize();
        v = crossProduct(w, u);
        mat4 t, p;
        t.at(0, 0) = u.x;
        t.at(0, 1) = u.y;
        t.at(0, 2) = u.z;
        t.at(1, 0) = v.x;
        t.at(1, 1) = v.y;
        t.at(1, 2) = v.z;
        t.at(2, 0) = w.x;
        t.at(2, 1) = w.y;
        t.at(2, 2) = w.z;
        p.at(0, 3) = - mc.cameraPosition.x;
        p.at(1, 3) = - mc.cameraPosition.y;
        p.at(2, 3) = - mc.cameraPosition.z;
        cameraMatrix = t * p;
        //std::cout <<"cameraMatrix in functie\n" << cameraMatrix << "\n";
        //std::cout << "\nt: ";
        //std::cout << t << "\np:\n";
        //std::cout << p << "\n";
        return cameraMatrix;
    }
    
    //define the projection transformation matrix
    //see section 5 from the laboratory work
    mat4 definePerspectiveProjectionMatrix(float fov, float aspect, float zNear, float zFar)
    {
        mat4 perspectiveProjectionMatrix;
        double n = tan(fov / 2);
        perspectiveProjectionMatrix.at(0, 0) = (double)( - 1 / (aspect * n));
        perspectiveProjectionMatrix.at(1, 1) = (double)( - 1 / n);
        perspectiveProjectionMatrix.at(2, 2) = (double)((zFar + zNear) / (zNear - zFar));
        perspectiveProjectionMatrix.at(2, 3) = (double)(2 * zFar * zNear / (zFar - zNear));
        perspectiveProjectionMatrix.at(3, 2) = 1;
        perspectiveProjectionMatrix.at(3, 3) = 0;
        return perspectiveProjectionMatrix;
    }
    
    //define the perspective divide operation
    //see section 5 from the laboratory work
    void perspectiveDivide(vec4& iv) {
        if (iv.w) {
            iv.x /= iv.w;
            iv.y /= iv.w;
            iv.z /= iv.w;
            iv.w = 1;
        }
    }

    //check if a point should be clipped
    //see section 9 from the laboratory work
    bool clipPointInHomogeneousCoordinate(const egc::vec4 &vertex)
    {
        return false;
    }

    //check if a triangle should be clipped
    //clip only those triangles for which all vertices are outside the viewing volume
    bool clipTriangleInHomegeneousCoordinates(const std::vector<egc::vec4> &triangle)
    {
        return false;
    }

    //compute the normal vector to a triangle
    //see section 7 from the laboratory work
    egc::vec3 findNormalVectorToTriangle(const std::vector<egc::vec4> &triangle)
    {
        egc::vec3 n;
        
        return n;
    }

    //compute the coordinates of the triangle's center
    //(we will use this point to display the normal vector)
    egc::vec4 findCenterPointOfTriangle(const std::vector<egc::vec4> &triangle)
    {
        egc::vec4 triangleCenter;
        
        return triangleCenter;
    }

    //check if the triangle is visible (front facing)
    //see section 8 from the laboratory work
    bool isTriangleVisible(const std::vector<egc::vec4> &triangle, const egc::vec3 &normalVector)
    {
        return true;
    }

    //display the normal vector of a triangle
    //see section 7 from the laboratory work
    //use the SDL_RenderDrawLine to draw the normal vector
	void displayNormalVectors(egc::vec3 &normalVector, egc::vec4 &triangleCenter, SDL_Renderer *renderer, egc::mat4 viewTransformMatrix, egc::mat4 perspectiveMatrix)
    {
        
    }
}
