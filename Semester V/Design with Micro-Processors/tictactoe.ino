#include <Keypad.h>
#include <TimerOne.h>

int latchPin = 13;
int clockPin = 12;
int dataPin = 11;

int led4R = A0;
int led4V = A1;
int led8R = A2;
int led8V = A3;

const byte ROWS = 4; 
const byte COLS = 4; 

char keys[ROWS][COLS] = {
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};

byte rowPins[ROWS] = {2, 3, 4, 5}; 
byte colPins[COLS] = {6, 7, 8, 9}; 

Keypad keypad = Keypad( makeKeymap(keys), rowPins, colPins, ROWS, COLS );


char game[3][3];
volatile int led[3][3];

bool gameOver = false;

char turn = 'x';

int x, y;
int position;

bool checkWin(int player) {
    for(int i=0; i<=2; i++) {
        //for lines
        if(game[i][0] == game[i][1] && game[i][1] == game[i][2] && game[i][2] == player) 
            return true;

        //for columns
        if(game[0][i] == game[1][i] && game[1][i] == game[2][i] && game[2][i] == player) 
            return true;
    }

    //for diagonals
    if(game[0][0] == game[1][1] && game[1][1] == game[2][2] && game[2][2] == player)
        return true;
        
    if(game[0][2] == game[1][1] && game[1][1] == game[2][0] && game[2][0] == player)
        return true; 
    return false;
}

bool checkDraw() {
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            if (game[i][j] != 'x' && game[i][j] != '0') {
                return false; 
            }
        }
    }
    return true; 
}


void gameFinished() {
    Serial.print("Game over");
    gameOver = true;
}

void printMatrix() {
    Serial.print('\n');
    for(int i=0; i<3; i++){
        for(int j=0; j<3; j++)
            Serial.print(game[i][j]);
        Serial.print('\n');
    }
}

void printLEDs() {
    Serial.print('\n');
    for(int i=0; i<3; i++){
        for(int j=0; j<3; j++)
            Serial.print(led[i][j]);
        Serial.print('\n');
    }
}

void reset() {
    for(int i=0; i<3; i++)
        for(int j=0; j<3; j++)
            game[i][j] = NULL;
    Serial.print("Game reseted");
    gameOver = false;
    turn = 'x';
    printMatrix();
}

void setup(void) {
    Serial.begin(9600);
    pinMode(latchPin, OUTPUT);
    pinMode(clockPin, OUTPUT);
    pinMode(dataPin, OUTPUT);
    pinMode(led4R, OUTPUT);
    pinMode(led4V, OUTPUT);
    pinMode(led8R, OUTPUT);
    pinMode(led8V, OUTPUT);

    Timer1.initialize(500000);
    Timer1.attachInterrupt(timerIsr); 
}

void loop() {
    char key = keypad.getKey();

    if (key != NO_KEY) {
        if(key == '0')
            reset();

        if (key >= '1' && key <= '9') {
            position = key - '1';
            x = position / 3;
            y = position % 3;
            if(game[x][y] != 'x' && game[x][y] != '0' && !gameOver)
            {
                game[x][y] = turn;
                
                if(checkWin(turn)) {
                    Serial.print(turn);
                    Serial.println(" won!");
                    gameFinished();
                }else if(checkDraw()) {
                    Serial.println("Draw");
                    gameFinished();
                }

                if(turn == '0')
                    turn = 'x';
                else
                    turn = '0';
                printMatrix();
            }
        }
    }

    //display on LEDs

    for(int i=0; i<3; i++){
        for(int j=0; j<3; j++)
            if(game[i][j] == 'x')
                led[i][j] = 2;
            else if (game[i][j] == '0')
                led[i][j] = 1;
            else 
                led[i][j] = 0;
    }

}

void print8bitBinary(int value) {
    for (int i = 7; i >= 0; i--) {
        if (value & (1 << i)) {
            Serial.print("1");
        } else {
            Serial.print("0");
        }
    }
    Serial.println(); 
}

void timerIsr() {
    
    int concatenatedValue2 = (led[1][0] & 3) << 6 |  
                          (led[0][2] & 3) << 4 | 
                          (led[0][1] & 3) << 2 |  
                          (led[0][0] & 3);  

    int concatenatedValue = (led[2][1] & 1) << 7 |        
                         (led[2][0] & 3) << 5 |       
                         (led[1][2] & 3) << 3 |        
                         (led[1][1] & 3) << 1 |       
                         ((led[1][0] >> 1) & 1);       
    
    digitalWrite(latchPin, LOW);
    shiftOut(dataPin, clockPin, MSBFIRST, concatenatedValue);
    shiftOut(dataPin, clockPin, MSBFIRST, concatenatedValue2);

    if(led[1][0] == 0) {
        digitalWrite(led4R, LOW);
        digitalWrite(led4V, LOW);
    } else if(led[1][0] == 'x') {
        digitalWrite(led4R, HIGH);
        digitalWrite(led4V, LOW);
    } else {
        digitalWrite(led4R, LOW);
        digitalWrite(led4V, HIGH);
    }

    if(led[2][1] == 0) {
        digitalWrite(led4R, LOW);
        digitalWrite(led4V, LOW);
    } else if(led[1][0] == 'x') {
        digitalWrite(led4R, HIGH);
        digitalWrite(led4V, LOW);
    } else {
        digitalWrite(led4R, LOW);
        digitalWrite(led4V, HIGH);
    }
    digitalWrite(latchPin, HIGH);
}
