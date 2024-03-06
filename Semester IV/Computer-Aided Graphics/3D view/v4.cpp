#include "vec4.h"
#include <cmath>

namespace egc {
    vec4& vec4::operator =(const vec4& srcVector) {
        this->x = srcVector.x;
        this->y = srcVector.y;
        this->z = srcVector.z;
        this->w = srcVector.w;

        return *this;
    }
    
    vec4 vec4::operator +(const vec4& srcVector) const {
        return vec4(this->x + srcVector.x, this->y + srcVector.y, this->z + srcVector.z, this->w + srcVector.w);
    }

    vec4& vec4::operator +=(const vec4& srcVector) {
        this->x += srcVector.x;
        this->y += srcVector.y;
        this->z += srcVector.z;
        this->w += srcVector.w;

        return *this;
    }

    vec4 vec4::operator *(float scalarValue) const {
        return vec4(this->x * scalarValue, this->y * scalarValue, this->z * scalarValue, this->w * scalarValue);
    }
    
    vec4 vec4::operator -(const vec4& srcVector) const {
        return vec4(this->x - srcVector.x, this->y - srcVector.y, this->z - srcVector.z, this->w - srcVector.w);
    }

    vec4& vec4::operator -=(const vec4& srcVector) {
        this->x -= srcVector.x;
        this->y -= srcVector.y;
        this->z -= srcVector.z;
        this->w -= srcVector.w;

        return *this;
    }

    vec4 vec4::operator -() const {
        return vec4(this->x * (-1), this->y * (-1), this->z * (-1), this->w * (-1));
    }

    float vec4 :: length() const {
        return sqrt(this->x * this->x + this->y * this->y + this->z * this->z + this->w * this->w);
    }
    
    vec4& vec4::normalize() {
        float l = length();
        this->x /= l;
        this->y /= l;
        this->z /= l;
        this->w /= 1;

        return *this;
    }
}