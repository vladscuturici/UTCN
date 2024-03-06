#include "mat4.h"
#include "mat3.h"
#include <cmath>

namespace egc {
    float& mat4::at(int i, int j) {
        return matrixData[i + j * 4];
    }

    const float& mat4::at(int i, int j) const {
        return matrixData[i + j * 4];
    }

    mat4& mat4::operator =(const mat4& srcMatrix) {
        for (int i = 0; i < 16; i++)
            this->matrixData[i] = srcMatrix.matrixData[i];
        return *this;
    }

    mat4 mat4::operator *(float scalarValue) const {
        mat4 result;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++)
                result.at(i, j) = this->at(i, j) * scalarValue;
        }
        return result;
    }

    mat4 mat4::operator +(const mat4& srcMatrix) const {
        mat4 result;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++)
                result.at(i, j) = this->at(i, j) + srcMatrix.at(i, j);
        }
        return result;
    }

    vec4 mat4::operator *(const vec4& srcVector) const {
        vec4 result;
        result.x = this->at(0, 0) * srcVector.x + this->at(0, 1) * srcVector.y + this->at(0, 2) * srcVector.z + this->at(0, 3) * srcVector.w;
        result.y = this->at(1, 0) * srcVector.x + this->at(1, 1) * srcVector.y + this->at(1, 2) * srcVector.z + this->at(1, 3) * srcVector.w;
        result.z = this->at(2, 0) * srcVector.x + this->at(2, 1) * srcVector.y + this->at(2, 2) * srcVector.z + this->at(2, 3) * srcVector.w;
        result.w = this->at(3, 0) * srcVector.x + this->at(3, 1) * srcVector.y + this->at(3, 2) * srcVector.z + this->at(3, 3) * srcVector.w;
       
        return result;
    }

    
    mat4 mat4::operator *(const mat4& srcMatrix) const {
        mat4 result;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                result.at(i, j) = this->at(i, 0) * srcMatrix.at(0, j) + this->at(i, 1) * srcMatrix.at(1, j) + this->at(i, 2) * srcMatrix.at(2, j) + this->at(i, 3) * srcMatrix.at(3, j);
            }
        return result;
    }

    float mat4::determinant() const {
        int num1, num2, det = 1, index, total = 1;
        int temp[5], s;
        mat4 t;
        for (int i = 0; i < 16; i++)
            t.matrixData[i] = this->matrixData[i];
        for (int i = 0; i < 4; i++)
        {
            index = i;
            while (index < 4 && t.at(index, i) == 0)
            {
                index++;
            }
            if (index == 4) 
            {
                continue;
            }
            if (index != i)
            {
                for (int j = 0; j < 4; j++)
                {
                    s = t.at(index, j);
                    t.at(index, j) = t.at(i, j);
                    t.at(i, j) = s;
                }
                det = det * pow(-1, index - i);
            }

            for (int j = 0; j < 4; j++)
            {
                temp[j] = t.at(i, j);
            }
            for (int j = i + 1; j < 4; j++)
            {
                num1 = temp[i];
                num2 = t.at(j,i);
                for (int k = 0; k < 4; k++)
                {
                        t.at(j, k) = (num1 * t.at(j,k) - (num2 * temp[k]));
                }
                total = total * num1;
            }
        }
        for (int i = 0; i < 4; i++)
        {
            det = det * t.at(i,i);
        }
        return (det / total); 
    }

    mat4 mat4::inverse() const {
        mat4 result;
        mat3 temp;
        float v[9]; 
        int cnt = 0;
        int semn[4][4] = { 0 };
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                semn[i][j] = 1;
        semn[0][1] = -1;
        semn[0][3] = -1;
        semn[1][0] = -1;
        semn[1][2] = -1;
        semn[2][1] = -1;
        semn[2][3] = -1;
        semn[3][0] = -1;
        semn[3][2] = -1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int ic = 0; ic < 4; ic++) {
                    for (int jc = 0; jc < 4; jc++) {
                        if (ic != i && jc != j) {
                            v[cnt] = this->at(ic, jc);
                            cnt++;
                        }
                    }
                }
                temp.at(0, 0) = v[0];
                temp.at(0, 1) = v[3];
                temp.at(0, 2) = v[6];

                temp.at(1, 0) = v[1];
                temp.at(1, 1) = v[4];
                temp.at(1, 2) = v[7];
                
                temp.at(2, 0) = v[2];
                temp.at(2, 1) = v[5];
                temp.at(2, 2) = v[8];
                temp = temp * semn[i][j];
                result.at(i, j) = temp.determinant();
                cnt = 0;
            }
        }
        result = result.transpose();
        result = result * (1/this->determinant());
        return result;
    }

    mat4 mat4::transpose() const {
        mat4 result;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                result.at(i, j) = this->at(j, i);
        return result;
    }
}