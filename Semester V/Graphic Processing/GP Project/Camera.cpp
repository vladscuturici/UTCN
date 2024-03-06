#include "Camera.hpp"

namespace gps {

    Camera::Camera(glm::vec3 cameraPosition, glm::vec3 cameraTarget, glm::vec3 cameraUp) {
        this->cameraFrontDirection = glm::normalize(cameraTarget - cameraPosition);
        this->cameraPosition = cameraPosition;
        this->cameraTarget = cameraTarget;
        this->cameraRightDirection = glm::normalize(glm::cross(cameraFrontDirection, cameraUp));
        this->cameraUpDirection = cameraUp;
    }

    glm::mat4 Camera::getViewMatrix() {
        return glm::lookAt(cameraPosition, cameraPosition + cameraFrontDirection, cameraUpDirection);
    }

    void Camera::move(MOVE_DIRECTION direction, float speed) {
        //if (direction == MOVE_FORWARD) {
        //    cameraPosition += cameraFrontDirection * speed;
        //}
        //else if (direction == MOVE_BACKWARD)
        switch (direction) {
            case MOVE_LEFT:
                cameraPosition -= cameraRightDirection * speed;
                break;
            case MOVE_RIGHT:
                cameraPosition += cameraRightDirection * speed;
                break;
            case MOVE_BACKWARD:
                cameraPosition -= cameraFrontDirection * speed;
                break;
            case MOVE_FORWARD:
                cameraPosition += cameraFrontDirection * speed;
                break;
            case MOVE_UP:
                cameraPosition += cameraUpDirection * speed;
                break;
            case MOVE_DOWN:
                cameraPosition -= cameraUpDirection * speed;
                break;

        }
    }

    void Camera::rotate(float pitch, float yaw) {
        yaw = glm::radians(yaw);
        pitch = glm::radians(pitch);
        

        //cameraFrontDirection = glm::normalize(cameraFrontDirection);
        cameraFrontDirection.x = cos(yaw) * cos(pitch);
        cameraFrontDirection.y = sin(pitch);
        cameraFrontDirection.z = sin(yaw) * cos(pitch);

        cameraFrontDirection = glm::normalize(cameraFrontDirection);

        glm::vec3 right = glm::vec3(0.0f, 1.0f, 0.0f);
        glm::vec3 rd = glm::cross(cameraFrontDirection, right);
        cameraRightDirection = glm::normalize(rd);
        glm::vec3 ud = glm::cross(cameraRightDirection, cameraFrontDirection);
        cameraUpDirection = glm::normalize(ud);
    }
    void Camera::setPosition(glm::vec3 position) {
        cameraPosition = position;
    }

    void Camera::setTarget(glm::vec3 target) {
        cameraFrontDirection = glm::normalize(target - cameraPosition);
        cameraRightDirection = glm::normalize(glm::cross(cameraFrontDirection, glm::vec3(0.0f, 1.0f, 0.0f)));
        cameraUpDirection = glm::normalize(glm::cross(cameraRightDirection, cameraFrontDirection));
    }

    glm::vec3 Camera::getPosition() {
        return cameraPosition;
    }

}
