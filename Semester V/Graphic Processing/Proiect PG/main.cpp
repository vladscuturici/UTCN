#if defined (__APPLE__)
#define GLFW_INCLUDE_GLCOREARB
#define GL_SILENCE_DEPRECATION
#else
#define GLEW_STATIC
#include <GL/glew.h>
#endif

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

#include <GLFW/glfw3.h>

#include <glm/glm.hpp> //core glm functionality
#include <glm/gtc/matrix_transform.hpp> //glm extension for generating common transformation matrices
#include <glm/gtc/matrix_inverse.hpp> //glm extension for computing inverse matrices
#include <glm/gtc/type_ptr.hpp> //glm extension for accessing the internal data structure of glm types

#include "Window.h"
#include "Shader.hpp"
#include "Camera.hpp"
#include "Model3D.hpp"
#include "SkyBox.hpp"

#include <iostream>

enum class RenderMode { SOLID, WIREFRAME, POINT, POLYGONAL, SMOOTH };
RenderMode currentMode = RenderMode::SOLID;

enum class DroneMovement { FORWARD, BACKWARD, LEFT, RIGHT, UP, DOWN, ROTATE_LEFT, ROTATE_RIGHT, SCALE_UP, SCALE_DOWN };
bool droneMovement[10] = { false, false, false, false, false, false, false, false, false, false };

enum class AnimationState { MOVING_UP, ROTATING, MOVING_DOWN, MOVING_RIGHT, IDLE };
AnimationState currentAnimationState = AnimationState::IDLE;

enum class ShadingModel { PHONG, BLINN_PHONG };
ShadingModel currentShadingModel = ShadingModel::PHONG;


std::vector<const GLchar*> faces;
gps::SkyBox mySkyBox;
gps::Shader skyboxShader;
// matrices
glm::mat4 model;
glm::mat4 view;
glm::mat4 projection;
glm::mat3 normalMatrix;

// light parameters
glm::vec3 lightDir;
glm::vec3 lightColor;

// shader uniform locations
GLint modelLoc;
GLint viewLoc;
GLint projectionLoc;
GLint normalMatrixLoc;
GLint lightDirLoc;
GLint lightColorLoc;

// camera
gps::Camera myCamera(
    glm::vec3(0.0f, 1.0f, 3.0f),
    glm::vec3(0.0f, 0.0f, -10.0f),
    glm::vec3(0.0f, 1.0f, 0.0f));

GLfloat cameraSpeed = 0.1f;

GLboolean pressedKeys[1024];

// models
gps::Model3D teapot;
gps::Model3D bridge;
gps::Model3D drone;
gps::Model3D man;

glm::mat4 teapotModel;
glm::mat4 droneModel;
glm::mat4 manModel;

GLfloat angle;

// shaders
gps::Shader myBasicShader;

// window

float pitch = 0.0f;
float yaw = -90.0f;
gps::Window myWindow;
int glWindowHeight = 700, glWindowWidth = 900;
float sensitivity = 0.2f;
float lastX = glWindowWidth / 2;
float lastY = glWindowHeight / 2;

bool firstMouse = true;

bool fogEnabled = false;

bool isDirectionalLight = true;


GLint displayFog;

// Global variables for the animation
bool isCameraAnimationActive = false;
float cameraAnimationStartTime = 0.0f;
float cameraAnimationDuration = 10.0f; // Duration of the animation in seconds
float cameraRadius = 5.0f; // Radius of the circular path

float initialAngleOffset = 90.0f; // Angle in degrees

void startCameraAnimation() {
    isCameraAnimationActive = true;
    cameraAnimationStartTime = glfwGetTime();
}

enum class CameraAnimationStage { FLYING_UP, MOVING_FORWARD, LOOKING_DOWN, ROTATING, DONE, MOVING_RIGHT };
CameraAnimationStage currentStage = CameraAnimationStage::MOVING_RIGHT;


void updateCameraAnimation() {
    if (!isCameraAnimationActive) return;

    float currentTime = glfwGetTime();
    float elapsedTime = currentTime - cameraAnimationStartTime;

    switch (currentStage) {
    case CameraAnimationStage::MOVING_RIGHT:
        if (elapsedTime < 5.0f) {
            float rightMovement = elapsedTime / 10;
            myCamera.setPosition(glm::vec3(myCamera.getPosition().x + rightMovement, myCamera.getPosition().y, myCamera.getPosition().z));
        }
        else {
            currentStage = CameraAnimationStage::FLYING_UP;
            cameraAnimationStartTime = currentTime;
        }
        break;
    case CameraAnimationStage::FLYING_UP:
        if (elapsedTime < 2.0f) {
            float height = elapsedTime * 10;
            myCamera.setPosition(glm::vec3(myCamera.getPosition().x, height, myCamera.getPosition().z));
        }
        else {
            currentStage = CameraAnimationStage::LOOKING_DOWN;
            cameraAnimationStartTime = currentTime;
        }
        break;
    case CameraAnimationStage::LOOKING_DOWN:
        if (elapsedTime < 2.0f) {
            pitch = -15.0f * elapsedTime;
            myCamera.rotate(pitch, yaw);
        }
        else {
            currentStage = CameraAnimationStage::MOVING_FORWARD;
            cameraAnimationStartTime = currentTime;
        }
        break;
    case CameraAnimationStage::MOVING_FORWARD:
        if (elapsedTime < 5.0f) {
            float distance = elapsedTime / 5;
            myCamera.setPosition(glm::vec3(myCamera.getPosition().x, myCamera.getPosition().y, myCamera.getPosition().z - distance));
        }
        else {
            currentStage = CameraAnimationStage::ROTATING;
            cameraAnimationStartTime = currentTime;
        }
        break;

    case CameraAnimationStage::ROTATING:
        if (elapsedTime < 5.0f) {
            float rotationAngle = 45.0f * (elapsedTime / 100.0f);
            yaw += rotationAngle;
            myCamera.rotate(pitch, yaw);
            view = myCamera.getViewMatrix();
            myBasicShader.useShaderProgram();
            glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        }
        else {
            currentStage = CameraAnimationStage::DONE;
        }
        break;
    }

    // Update the view matrix
    view = myCamera.getViewMatrix();
    myBasicShader.useShaderProgram();
    glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
}


GLenum glCheckError_(const char* file, int line)
{
    GLenum errorCode;
    while ((errorCode = glGetError()) != GL_NO_ERROR) {
        std::string error;
        switch (errorCode) {
        case GL_INVALID_ENUM:
            error = "INVALID_ENUM";
            break;
        case GL_INVALID_VALUE:
            error = "INVALID_VALUE";
            break;
        case GL_INVALID_OPERATION:
            error = "INVALID_OPERATION";
            break;
        case GL_OUT_OF_MEMORY:
            error = "OUT_OF_MEMORY";
            break;
        case GL_INVALID_FRAMEBUFFER_OPERATION:
            error = "INVALID_FRAMEBUFFER_OPERATION";
            break;
        }
        std::cout << error << " | " << file << " (" << line << ")" << std::endl;
    }
    return errorCode;
}
#define glCheckError() glCheckError_(__FILE__, __LINE__)

void windowResizeCallback(GLFWwindow* window, int width, int height) {
    fprintf(stdout, "Window resized! New width: %d , and height: %d\n", width, height);

    glViewport(0, 0, width, height);
    float ratio = (float)width / (float)height;
    float angle = glm::radians(45.0f);
    float near = 0.1f;
    float far = 1000.0f;
    projection = glm::perspective(angle, ratio, near, far);

    myBasicShader.useShaderProgram();
    glUniformMatrix4fv(projectionLoc, 1, GL_FALSE, glm::value_ptr(projection));
}

bool isAnimationPlaying = false;
float animationStartTime;
glm::vec3 initialDronePosition;
std::vector<glm::vec3> animationPath;
int currentPathIndex = 0;

void initAnimationPath() {
    animationPath.clear();
    glm::vec3 startPosition = glm::vec3(droneModel[3]);
    glm::vec3 currentPosition = startPosition;

    for (int i = 0; i < 2; ++i) {
        currentPosition.y += 1.0f;
        animationPath.push_back(currentPosition);
    }

    currentPosition.z -= 2.0f;
    animationPath.push_back(currentPosition);
}

void startAnimation() {
    if (!isAnimationPlaying) {
        isAnimationPlaying = true;
        currentAnimationState = AnimationState::MOVING_UP;
        animationStartTime = glfwGetTime();
        initialDronePosition = droneModel[3];
        currentPathIndex = 0;
    }
}


void startRotation() {
    animationStartTime = glfwGetTime();
}

void updateRotation() {
    float currentTime = glfwGetTime();
    float timeSinceStart = currentTime - animationStartTime;

    if (timeSinceStart < 2.0) {
        float rotationAngle = 90.0f * timeSinceStart;
        droneModel = glm::rotate(droneModel, glm::radians(rotationAngle), glm::vec3(0.0f, 1.0f, 0.0f));
    }
    else {
        isAnimationPlaying = false;
    }
}

void updateAnimation() {
    if (!isAnimationPlaying) return;

    float currentTime = glfwGetTime();
    float timeSinceStart = currentTime - animationStartTime;

    switch (currentAnimationState) {
    case AnimationState::MOVING_UP:
    case AnimationState::MOVING_RIGHT:
    case AnimationState::MOVING_DOWN:
        if (currentPathIndex < animationPath.size()) {
            glm::vec3 newPosition = glm::mix(initialDronePosition, animationPath[currentPathIndex], timeSinceStart);
            droneModel = glm::translate(glm::mat4(1.0f), newPosition);

            if (timeSinceStart >= 1.0) {
                initialDronePosition = newPosition;
                animationStartTime = currentTime;
                currentPathIndex++;

                if (currentPathIndex == 2) {
                    currentAnimationState = AnimationState::ROTATING;
                    startRotation();
                }
                else if (currentPathIndex >= animationPath.size()) {
                    isAnimationPlaying = false;
                    currentAnimationState = AnimationState::IDLE;
                }
                else {
                    currentAnimationState = AnimationState::MOVING_RIGHT;
                }
            }
        }
        break;

    case AnimationState::ROTATING:
        if (timeSinceStart < 4.0) {
            float rotationAngle = 180.0f * timeSinceStart;
            droneModel = glm::rotate(droneModel, glm::radians(rotationAngle), glm::vec3(0.0f, 1.0f, 0.0f));
        }
        else {
            currentAnimationState = AnimationState::MOVING_DOWN;
            initialDronePosition = droneModel[3];
            animationStartTime = currentTime;
        }
        break;

    case AnimationState::IDLE:
        break;
    }
}

void resetDronePosition() {
    droneModel = glm::mat4(1.0f);
    droneModel = glm::translate(droneModel, glm::vec3(0.0f, 0.0f, 0.0f));
}

void keyboardCallback(GLFWwindow* window, int key, int scancode, int action, int mode) {
    if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
        glfwSetWindowShouldClose(window, GL_TRUE);
    }

    if (key >= 0 && key < 1024) {
        if (action == GLFW_PRESS) {
            pressedKeys[key] = true;
        }
        else if (action == GLFW_RELEASE) {
            pressedKeys[key] = false;
        }
    }

    if (key == GLFW_KEY_RIGHT_SHIFT && action == GLFW_PRESS) {
        startCameraAnimation();
    }

    if (key == GLFW_KEY_F && action == GLFW_PRESS) {
        fogEnabled = !fogEnabled;
    }

    if (key == GLFW_KEY_N && action == GLFW_PRESS) {
        resetDronePosition();
    }

    if (key == GLFW_KEY_L && action == GLFW_PRESS) {
        isDirectionalLight = !isDirectionalLight;
    }

    if (key == GLFW_KEY_TAB && action == GLFW_PRESS) {
        if (currentMode == RenderMode::SOLID) {
            currentMode = RenderMode::WIREFRAME;
        }
        else if (currentMode == RenderMode::WIREFRAME) {
            currentMode = RenderMode::POINT;
        }
        else if (currentMode == RenderMode::POINT) {
            currentMode = RenderMode::SOLID;
        }
    }

    if (action == GLFW_PRESS || action == GLFW_RELEASE) {
        switch (key) {
        case GLFW_KEY_UP:
            droneMovement[static_cast<int>(DroneMovement::FORWARD)] = (action == GLFW_PRESS);
            break;
        case GLFW_KEY_DOWN:
            droneMovement[static_cast<int>(DroneMovement::BACKWARD)] = (action == GLFW_PRESS);
            break;
        case GLFW_KEY_LEFT:
            droneMovement[static_cast<int>(DroneMovement::LEFT)] = (action == GLFW_PRESS);
            break;
        case GLFW_KEY_RIGHT:
            droneMovement[static_cast<int>(DroneMovement::RIGHT)] = (action == GLFW_PRESS);
            break;
        case GLFW_KEY_1:
            droneMovement[static_cast<int>(DroneMovement::UP)] = (action == GLFW_PRESS);
            break;
        case GLFW_KEY_2:
            droneMovement[static_cast<int>(DroneMovement::DOWN)] = (action == GLFW_PRESS);
            break;
        case GLFW_KEY_LEFT_BRACKET:
            droneMovement[static_cast<int>(DroneMovement::ROTATE_LEFT)] = (action == GLFW_PRESS);
            break;
        case GLFW_KEY_RIGHT_BRACKET:
            droneMovement[static_cast<int>(DroneMovement::ROTATE_RIGHT)] = (action == GLFW_PRESS);
            break;
        case GLFW_KEY_O:
            droneMovement[static_cast<int>(DroneMovement::SCALE_UP)] = (action == GLFW_PRESS);
            break;
        case GLFW_KEY_P:
            droneMovement[static_cast<int>(DroneMovement::SCALE_DOWN)] = (action == GLFW_PRESS);
            break;
        }

        if (key == GLFW_KEY_M && action == GLFW_PRESS) {
            startAnimation();
        }

        if (key == GLFW_KEY_0 && action == GLFW_PRESS) {
            if (currentShadingModel == ShadingModel::PHONG) {
                currentShadingModel = ShadingModel::BLINN_PHONG;
            }
            else {
                currentShadingModel = ShadingModel::PHONG;
            }
        }
    }
}

void mouseCallback(GLFWwindow* window, double xpos, double ypos) {

    if (firstMouse) {
        firstMouse = false;
        lastX = xpos;
        lastY = ypos;
    }

    float x_off = -lastX + xpos;
    float y_off = lastY - ypos;

    x_off *= sensitivity;
    y_off *= sensitivity;

    yaw += x_off;
    pitch += y_off;

    lastX = xpos;
    lastY = ypos;

    if (pitch <= -88.0f)
        pitch = -88.0f;
    if (pitch >= 88.0f)
        pitch = 88.0f;

    myCamera.rotate(pitch, yaw);
    view = myCamera.getViewMatrix();
    myBasicShader.useShaderProgram();
    glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
}

void processMovement() {
    if (pressedKeys[GLFW_KEY_W]) {
        myCamera.move(gps::MOVE_FORWARD, cameraSpeed);
        //update view matrix
        view = myCamera.getViewMatrix();
        myBasicShader.useShaderProgram();
        glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        // compute normal matrix for teapot
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    if (pressedKeys[GLFW_KEY_S]) {
        myCamera.move(gps::MOVE_BACKWARD, cameraSpeed);
        //update view matrix
        view = myCamera.getViewMatrix();
        myBasicShader.useShaderProgram();
        glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        // compute normal matrix for teapot
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    if (pressedKeys[GLFW_KEY_A]) {
        myCamera.move(gps::MOVE_LEFT, cameraSpeed);
        //update view matrix
        view = myCamera.getViewMatrix();
        myBasicShader.useShaderProgram();
        glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        // compute normal matrix for teapot
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    if (pressedKeys[GLFW_KEY_D]) {
        myCamera.move(gps::MOVE_RIGHT, cameraSpeed);
        //update view matrix
        view = myCamera.getViewMatrix();
        myBasicShader.useShaderProgram();
        glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        // compute normal matrix for teapot
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    if (pressedKeys[GLFW_KEY_Q]) {
        angle -= 1.0f;
        // update model matrix for teapot
        model = glm::rotate(glm::mat4(1.0f), glm::radians(angle), glm::vec3(0, 1, 0));
        // update normal matrix for teapot
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    if (pressedKeys[GLFW_KEY_E]) {
        angle += 1.0f;
        // update model matrix for teapot
        model = glm::rotate(glm::mat4(1.0f), glm::radians(angle), glm::vec3(0, 1, 0));
        // update normal matrix for teapot
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    if (pressedKeys[GLFW_KEY_SPACE]) {
        myCamera.move(gps::MOVE_UP, cameraSpeed);
        view = myCamera.getViewMatrix();
        myBasicShader.useShaderProgram();
        glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    if (pressedKeys[GLFW_KEY_LEFT_SHIFT]) {
        myCamera.move(gps::MOVE_DOWN, cameraSpeed);
        view = myCamera.getViewMatrix();
        myBasicShader.useShaderProgram();
        glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));
        normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    }

    glm::vec3 droneMovementDirection = glm::vec3(0.0f);

    if (droneMovement[static_cast<int>(DroneMovement::FORWARD)])
        droneMovementDirection.z -= 1.0f;
    if (droneMovement[static_cast<int>(DroneMovement::BACKWARD)])
        droneMovementDirection.z += 1.0f;
    if (droneMovement[static_cast<int>(DroneMovement::LEFT)])
        droneMovementDirection.x -= 1.0f;
    if (droneMovement[static_cast<int>(DroneMovement::RIGHT)])
        droneMovementDirection.x += 1.0f;
    if (droneMovement[static_cast<int>(DroneMovement::UP)])
        droneModel = glm::translate(droneModel, glm::vec3(0.0f, 0.05f, 0.0f));
    if (droneMovement[static_cast<int>(DroneMovement::DOWN)])
        droneModel = glm::translate(droneModel, glm::vec3(0.0f, -0.05f, 0.0f));

    if (glm::length(droneMovementDirection) > 0) {
        droneMovementDirection = glm::normalize(droneMovementDirection);
        float droneSpeed = 0.05f;
        droneModel = glm::translate(droneModel, droneMovementDirection * droneSpeed);
    }

    float rotationSpeed = 1.0f;
    if (droneMovement[static_cast<int>(DroneMovement::ROTATE_LEFT)])
        droneModel = glm::rotate(droneModel, glm::radians(rotationSpeed), glm::vec3(0.0f, 1.0f, 0.0f));

    if (droneMovement[static_cast<int>(DroneMovement::ROTATE_RIGHT)])
        droneModel = glm::rotate(droneModel, glm::radians(-rotationSpeed), glm::vec3(0.0f, 1.0f, 0.0f));

    float scaleSpeed = 0.05f;

    if (droneMovement[static_cast<int>(DroneMovement::SCALE_UP)])
        droneModel = glm::scale(droneModel, glm::vec3(1.0f + scaleSpeed));

    if (droneMovement[static_cast<int>(DroneMovement::SCALE_DOWN)]) {
        if (glm::length(glm::vec3(droneModel[0])) > scaleSpeed)
            droneModel = glm::scale(droneModel, glm::vec3(1.0f - scaleSpeed));
    }

}

void initOpenGLWindow() {
    myWindow.Create(1024, 768, "Proiect PG");
}

void setWindowCallbacks() {
    glfwSetWindowSizeCallback(myWindow.getWindow(), windowResizeCallback);
    glfwSetKeyCallback(myWindow.getWindow(), keyboardCallback);
    glfwSetCursorPosCallback(myWindow.getWindow(), mouseCallback);
}

void initOpenGLState() {
    glClearColor(0.7f, 0.7f, 0.7f, 1.0f);
    glViewport(0, 0, myWindow.getWindowDimensions().width, myWindow.getWindowDimensions().height);
    glEnable(GL_FRAMEBUFFER_SRGB);
    glEnable(GL_DEPTH_TEST); // enable depth-testing
    glDepthFunc(GL_LESS); // depth-testing interprets a smaller value as "closer"
    glEnable(GL_CULL_FACE); // cull face
    glCullFace(GL_BACK); // cull back face
    glFrontFace(GL_CCW); // GL_CCW for counter clock-wise
}

void initModels() {
    //teapot.LoadModel("models/teapot/teapot20segUT.obj");
    drone.LoadModel("models/drone/drone.obj");
    bridge.LoadModel("models/scene/city.obj");
    mySkyBox.Load(faces);
}

void loadSkyBox() {
    faces.push_back("skybox/nou/posx.jpg");
    faces.push_back("skybox/nou/negx.jpg");
    faces.push_back("skybox/nou/posy.jpg");
    faces.push_back("skybox/nou/negy.jpg");
    faces.push_back("skybox/nou/posz.jpg");
    faces.push_back("skybox/nou/negz.jpg");
}

void initShaders() {
    myBasicShader.loadShader(
        "shaders/basic.vert",
        "shaders/basic.frag");
    skyboxShader.loadShader("shaders/skyboxShader.vert", "shaders/skyboxShader.frag");
}

void initUniforms() {
    myBasicShader.useShaderProgram();

    projection = glm::perspective(glm::radians(45.0f),
        (float)myWindow.getWindowDimensions().width / (float)myWindow.getWindowDimensions().height,
        0.1f, 1000.0f);

    droneModel = glm::translate(glm::mat4(1.0f), glm::vec3(0.0f, 0.0f, -10.0f));
    model = glm::rotate(glm::mat4(1.0f), glm::radians(angle), glm::vec3(0.0f, 1.0f, 0.0f));
    modelLoc = glGetUniformLocation(myBasicShader.shaderProgram, "model");

    // get view matrix for current camera
    view = myCamera.getViewMatrix();
    viewLoc = glGetUniformLocation(myBasicShader.shaderProgram, "view");
    // send view matrix to shader
    glUniformMatrix4fv(viewLoc, 1, GL_FALSE, glm::value_ptr(view));

    // compute normal matrix for teapot
    normalMatrix = glm::mat3(glm::inverseTranspose(view * model));
    normalMatrixLoc = glGetUniformLocation(myBasicShader.shaderProgram, "normalMatrix");

    // create projection matrix
    projection = glm::perspective(glm::radians(45.0f),
        (float)myWindow.getWindowDimensions().width / (float)myWindow.getWindowDimensions().height,
        0.1f, 1000.0f);
    projectionLoc = glGetUniformLocation(myBasicShader.shaderProgram, "projection");
    // send projection matrix to shader
    glUniformMatrix4fv(projectionLoc, 1, GL_FALSE, glm::value_ptr(projection));

    //set the light direction (direction towards the light)
    lightDir = glm::vec3(0.0f, 1.0f, 1.0f);
    lightDirLoc = glGetUniformLocation(myBasicShader.shaderProgram, "lightDir");
    // send light dir to shader
    glUniform3fv(lightDirLoc, 1, glm::value_ptr(lightDir));

    //set light color
    lightColor = glm::vec3(1.0f, 1.0f, 1.0f); //white light
    lightColorLoc = glGetUniformLocation(myBasicShader.shaderProgram, "lightColor");
    // send light color to shader
    glUniform3fv(lightColorLoc, 1, glm::value_ptr(lightColor));

    teapotModel = glm::mat4(1.0f);
}

void renderTeapot(gps::Shader shader) {
    shader.useShaderProgram();

    // Send teapot model matrix data to shader
    glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));

    // Send teapot normal matrix data to shader
    glUniformMatrix3fv(normalMatrixLoc, 1, GL_FALSE, glm::value_ptr(normalMatrix));

    // Draw teapot
    teapot.Draw(shader);
}

void renderBridge(gps::Shader shader) {
    shader.useShaderProgram();

    glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(model));

    glUniformMatrix3fv(normalMatrixLoc, 1, GL_FALSE, glm::value_ptr(normalMatrix));

    bridge.Draw(shader);
}

void renderDrone(gps::Shader shader) {
    shader.useShaderProgram();

    glUniformMatrix4fv(modelLoc, 1, GL_FALSE, glm::value_ptr(droneModel));
    glUniformMatrix3fv(normalMatrixLoc, 1, GL_FALSE, glm::value_ptr(normalMatrix));

    drone.Draw(shader);
}

void renderObjects() {
    //renderTeapot(myBasicShader);
    renderBridge(myBasicShader);
    renderDrone(myBasicShader);
}

void renderSkyBox() {
    view = myCamera.getViewMatrix();
    skyboxShader.useShaderProgram();
    glUniformMatrix4fv(glGetUniformLocation(skyboxShader.shaderProgram, "view"), 1, GL_FALSE, glm::value_ptr(view));

    projection = glm::perspective(glm::radians(45.0f), (float)glWindowWidth / (float)glWindowHeight, 0.1f, 1000.0f);
    glUniformMatrix4fv(glGetUniformLocation(skyboxShader.shaderProgram, "projection"), 1, GL_FALSE, glm::value_ptr(projection));
}

glm::vec3 getDronePosition() {
    glm::vec3 position = glm::vec3(droneModel[3]);
    return position;
}

void renderScene() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    if (currentMode == RenderMode::SOLID) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }
    else if (currentMode == RenderMode::WIREFRAME) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }
    else if (currentMode == RenderMode::POINT) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
    }
    renderObjects();
    renderSkyBox();

    myBasicShader.useShaderProgram();

    GLint pointLightPosLoc = glGetUniformLocation(myBasicShader.shaderProgram, "pointLightPos");
    GLint constantLoc = glGetUniformLocation(myBasicShader.shaderProgram, "constant");
    GLint linearLoc = glGetUniformLocation(myBasicShader.shaderProgram, "linear");
    GLint quadraticLoc = glGetUniformLocation(myBasicShader.shaderProgram, "quadratic");

    glm::vec3 pointLightPosition = glm::vec3(getDronePosition().x, getDronePosition().y, getDronePosition().z);
    float constant = 1.0f;
    float linear = 0.01f;
    float quadratic = 0.007f;

    glUniform3fv(pointLightPosLoc, 1, glm::value_ptr(pointLightPosition));
    glUniform1f(constantLoc, constant);
    glUniform1f(linearLoc, linear);
    glUniform1f(quadraticLoc, quadratic);

    glUniform1i(glGetUniformLocation(myBasicShader.shaderProgram, "fogEnabled"), fogEnabled);
    glUniform1i(glGetUniformLocation(myBasicShader.shaderProgram, "isDirectionalLight"), isDirectionalLight ? 1 : 0);

    mySkyBox.Draw(skyboxShader, view, projection);
}

void cleanup() {
    myWindow.Delete();
    //cleanup code for your own data
}

int main(int argc, const char* argv[]) {
    initAnimationPath();
    try {
        initOpenGLWindow();
    }
    catch (const std::exception& e) {
        std::cerr << e.what() << std::endl;
        return EXIT_FAILURE;
    }

    initOpenGLState();
    loadSkyBox();
    initModels();
    initShaders();
    initUniforms();
    setWindowCallbacks();

    glCheckError();
    // application loop
    while (!glfwWindowShouldClose(myWindow.getWindow())) {
        processMovement();
        updateAnimation();
        updateCameraAnimation();
        renderScene();

        glfwPollEvents();
        glfwSwapBuffers(myWindow.getWindow());

        glCheckError();
    }

    cleanup();

    return EXIT_SUCCESS;
}