//
//  main.cpp
//  SDL_Lab1
//
//  Created by CGIS on 16/02/16.
//  Copyright © 2016 CGIS. All rights reserved.
//

#include <iostream>
#include "include\SDL.h"
#include "transform.h"
#include <vector>
using namespace egc;

SDL_Renderer *windowRenderer;
// define rectangle vertices
vec3 P1(100, 100, 1), P2(400, 100, 1);
vec3 origine;
std::vector<egc::vec3> lista_de_puncte;

//define window dimensions
const int WINDOW_WIDTH = 640;
const int WINDOW_HEIGHT = 480;

SDL_Window *window = NULL;
SDL_Event currentEvent;

SDL_Rect rectangleCoordinates = {100, 100, 200, 200};

bool quit = false;

int mouseX, mouseY;

bool initWindow()
{
    bool success = true;
    
    //Try to initialize SDL
    if(SDL_Init(SDL_INIT_VIDEO) < 0)
    {
        std::cout << "SDL initialization failed" << std::endl;
        success = false;
    }
    else{
        //Try to create the window
        window = SDL_CreateWindow("SDL Hello World Example", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, WINDOW_WIDTH, WINDOW_HEIGHT, SDL_WINDOW_SHOWN | SDL_WINDOW_ALLOW_HIGHDPI);
        
        if(window == NULL)
        {
            std::cout << "Failed to create window: " << SDL_GetError() << std::endl;
            success = false;
        }
        else
        {
			// Create a renderer for the current window
			windowRenderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED);

			if(windowRenderer == NULL)
            {
                std::cout << "Failed to create the renderer: " << SDL_GetError() << std::endl;
                success = false;
            }
            else
            {
                //Set background color
				SDL_SetRenderDrawColor(windowRenderer, 255, 255, 255, 255);
			
                //Apply background color
                SDL_RenderClear(windowRenderer);
            }

        }
    }
    
    return success;
}

void destroyWindow()
{
    //Destroy window
    SDL_DestroyWindow(window);
    window = NULL;
    
    //Quit SDL
    SDL_Quit();
}

int main(int argc, char * argv[]) {
    if(!initWindow())
    {
        std::cout << "Failed to initialize" << std::endl;
        return -1;
    }
    
    while (!quit) {
        //Handle events on queue
        if(SDL_WaitEvent(&currentEvent) != 0)
        {
            //User requests quit
            if(currentEvent.type == SDL_QUIT)
            {
                quit = true;
            }
            
            //Mouse event -> pressed button
            if(currentEvent.type == SDL_MOUSEBUTTONDOWN)
            {
                if(currentEvent.button.button == SDL_BUTTON_LEFT)
                {
                    SDL_GetMouseState(&mouseX, &mouseY);
                    std::cout << "Mouse left click => " << "x: " << mouseX << ", y: " << mouseY << std::endl;
                    lista_de_puncte.push_back(egc::vec3(mouseX, mouseY, 1));
                }

                if (currentEvent.button.button == SDL_BUTTON_RIGHT)
                {
                    SDL_GetMouseState(&mouseX, &mouseY);
                    std::cout << "Mouse right click => " << "x: " << mouseX << ", y: " << mouseY << std::endl;
                    origine.x = mouseX;
                    origine.y = mouseY;
                    //schimba originea
                }
            }
            
            //Mouse event -> mouse movement
            if(currentEvent.type == SDL_MOUSEMOTION)
            {
                if(currentEvent.button.button == SDL_BUTTON_LEFT)
                {
                    SDL_GetMouseState(&mouseX, &mouseY);
                    std::cout << "Mouse move => " << "x: " << mouseX << ", y: " << mouseY << std::endl;
                }
            }
            mat3 m;
            //Keyboard event
            if(currentEvent.type == SDL_KEYDOWN)
            {
                switch(currentEvent.key.keysym.sym)
                {
                    case SDLK_UP:  
                        m = translate(0, -2);
                        break;

                    case SDLK_DOWN:
                        m = translate(0, 2);
                        break;

                    case SDLK_LEFT:
                        m = translate(-2, 0);
                        break;

                    case SDLK_RIGHT:
                        m = translate(2, 0);
                        break;

                    case SDLK_q:
                        m = rotate(2);
                        break;
                    
                    case SDLK_e:
                        m = rotate(-2);
                        break;

                    case SDLK_n:
                        std::cout << origine << std::endl;
                        m = translate(origine.x, origine.y) * scale(1.2, 1.2) * translate(-origine.x, -origine.y);
                        //m = scale(1.2, 1.2);
                        break;

                    case SDLK_m:
                        m = scale(1/1.2, 1/1.2);
                        break;

                    default:                        
                        break;
                }
                for (int i = 0; i < lista_de_puncte.size(); i++)
                    lista_de_puncte.at(i) = m * lista_de_puncte.at(i);
            }
            SDL_SetRenderDrawColor(windowRenderer, 255, 255, 255, 255);
            SDL_RenderClear(windowRenderer);

            SDL_SetRenderDrawColor(windowRenderer, 0, 0, 255, 255);

            int x1, y1, x2, y2;

            if (lista_de_puncte.size() > 1) {
                for (int i = 0; i < lista_de_puncte.size()-1; i++) {
                    x1 = lista_de_puncte.at(i).x;
                    y1 = lista_de_puncte.at(i).y;
                    x2 = lista_de_puncte.at(i + 1).x;
                    y2 = lista_de_puncte.at(i + 1).y;
                    SDL_RenderDrawLine(windowRenderer, x1, y1, x2, y2);
                }
                x1 = lista_de_puncte.at(lista_de_puncte.size()-1).x;
                y1 = lista_de_puncte.at(lista_de_puncte.size()-1).y;
                x2 = lista_de_puncte.at(0).x;
                y2 = lista_de_puncte.at(0).y;
                SDL_RenderDrawLine(windowRenderer, x1, y1, x2, y2);
                SDL_RenderPresent(windowRenderer);
            }
        }
    }
    
    destroyWindow();
    return 0;
}
