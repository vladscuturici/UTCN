#include "vec2.h"

namespace egc {
	vec3& vec3::operator =(const vec3& srcVector) {
		this->x = srcVector.x;
		this->y = srcVector.y;
		this->z = srcVector.z;

		return *this;
	}


	vec3 vec3::operator +(const vec3& srcVector) const {
		return vec3(this->x + srcVector.x, this->y + srcVector.y, this->z + srcVector.z);
	}


	vec3& vec3::operator +=(const vec3& srcVector) {
		this->x += srcVector.x;
		this->y += srcVector.y;
		this->z += srcVector.z;

		return *this;
	}

	vec3 vec3::operator *(float scalarValue) const {
		return vec3(this->x * scalarValue, this->y * scalarValue, this->z * scalarValue);
	}

	vec3 vec3::operator -(const vec3& srcVector) const {
		return vec3(this->x - srcVector.x, this->y - srcVector.y, this->z - srcVector.z);
	}

	vec3& vec3::operator -=(const vec3& srcVector) {
		this->x -= srcVector.x;
		this->y -= srcVector.y;
		this->z -= srcVector.z;

		return *this;
	}
	vec3 vec3::operator -() const {
		return vec3(this->x * (-1), this->y * (-1), this->z * (-1));
	}

	float vec3::length() const {
		return sqrt(this->x * this->x + this->y * this->y + this->z * this->z);
	}

	vec3& vec3::normalize() {
		float l = length();
		this->x /= l;
		this->y /= l;
		this->z /= l;

		return *this;
	}

	float dotProduct(const vec3& v1, const vec3& v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	vec3 crossProduct(const vec3& v1, const vec3& v2) {
		vec3 result;
		result.x = v1.y * v2.z - v1.z * v2.y;
		result.y = v1.z * v2.x - v1.x * v2.z;
		result.z = v1.x * v2.y - v1.y * v2.x;
		return result;
	}
}