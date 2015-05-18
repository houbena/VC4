import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.awt.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ue_2_benamor extends PApplet {



int screenW = 500;
int screenH = 500;
int screenNumber = 0;

boolean getMs = false;
PrintWriter dataFile;
String fileName = "dataFile.txt";
BufferedReader reader;
String line;
int sumResults;
int numOfLine = 0;

boolean mouseClicked = false;
boolean mouseReleased;

int randX = (int) random(0, 7);
int randY = (int) random(0, 7);
int randShape = (int) random(0,2);
int in = 100;

int[][] xs = new int[7][7];
int[][] ys = new int[7][7];

boolean circleFound = false;

DescScreen descScreen1 = new DescScreen();
String desc1 = "Focus on the screen,\nwhen you tap, an image with squares and one circle will be displayed shortly,\n\nTap to start test 1";
TestScreen testScreen1 = new TestScreen();
WhiteScreen whiteScreen1 = new WhiteScreen();
String wDesc1 = "If you\u2019ve noticed the circle press LMB on its location,\n\nif not, press RMB to skip";

DescScreen descScreen2 = new DescScreen();
String desc2 = "An image with different shapes and only one circle will be displayed,\nmove the cursor and press LMB on its location as fast as possible\n\nTap to start test 2";
TestScreen testScreen2 = new TestScreen();
WhiteScreen whiteScreen2 = new WhiteScreen();
String wDesc2 = "Tap to restart the test";


public void saveData(String toAddData){
  String[] loadLines = loadStrings(fileName);
  String[] lines = append(loadLines, toAddData);
  saveStrings(fileName, lines);
}

public void nextScreen(){
  screenNumber += 1;
  mouseClicked = false;
}

class ScreenWDesc{
  String desc;
  int c;
  int startTime;
  int screenTime;
  int ms;
  public void displayDesc(){
    fill(c);
    textSize(24);
    rectMode(CENTER);
    textAlign(CENTER,CENTER);
    text(desc, screenW/2, screenH/2, screenW, screenH);
  }
}

class DescScreen extends ScreenWDesc{  
  public void touchEvent(){
    if (mousePressed){
      screenNumber += 1;
    }
  }
}

class TestScreen{
  int screeningTime;
  int screenTime;
  int ms;
  int ellipseX = 0;
  int ellipseY = 0;
}

class WhiteScreen extends ScreenWDesc{
  int startTime;
  int screenTime;
  public void checkTouch(){};
}

public void setup(){
  size(screenW, screenH);
  //dataFile = createWriter(fileName);
  randomizeXY();
  reader = createReader(fileName);
}

public void draw(){
  background(0);
  
  if (screenNumber == 0){ ///// DESCRIPTION screen 1
    descScreen1.desc = desc1;
    descScreen1.displayDesc();
    descScreen1.touchEvent();
    descScreen1.c = color(255);

  }else if (screenNumber == 1){ ////// TEST screen 1
    testScreen1.screeningTime = 250;
    
    if (getMs == false) {
      testScreen1.ms = millis();
      getMs = true;
    }
        // println(ms);
      println((millis()-testScreen1.ms) + " " + getMs);

    if ((millis() - testScreen1.ms) < testScreen1.screeningTime){
      println(millis()-testScreen1.ms);

      // display randomized squares //
//     if (mouseClicked == true){
//         randomizeXY();
//      }
      displayXYtest1();


    }else{   nextScreen();}
    
  }else if (screenNumber == 2){ ////// WHITE screen 1
    whiteScreen1.desc = wDesc1;
    whiteScreen1.c = color(100);
    whiteScreen1.displayDesc();
    whiteScreen1.screenTime = millis() - whiteScreen1.startTime;

    if (mouseClicked == true && whiteScreen1.screenTime > 500){
      mouseClicked = false;
      mouseReleased = false;
      screenNumber += 1;

      if (mouseButton == LEFT){
        if (abs(mouseX - testScreen1.ellipseX) < in && abs(mouseY - testScreen1.ellipseY) < in){
          circleFound = true;
        }else{ circleFound = false; }
      }else{ circleFound = false; }
    }else mouseClicked = false;
    drawCursor();

  }else if (screenNumber == 3){ ////// DESCRIPTION screen 2
    descScreen2.desc = desc2;
    descScreen2.displayDesc();
    descScreen2.touchEvent();
    descScreen2.c = color(255);
    getMs = false;
  }else if (screenNumber == 4){ ///// TEST screen 2
    
    if (getMs == false) {
      testScreen2.ms = millis();
      getMs = true;
    }

      displayXYtest2();
      drawCursor();
      testScreen2.screenTime = millis() - testScreen2.ms;
      if (mouseClicked == true && testScreen2.screenTime > 500){
        mouseClicked = false;
        getMs = false;
        screenNumber += 1;

        if (abs(mouseX - testScreen2.ellipseX) < in && abs(mouseY - testScreen2.ellipseY) < in){
          saveData(str(testScreen2.screenTime));
        }else{ testScreen2.screenTime = 0; }
        
      }else mouseClicked = false;
      
    //whiteScreen2.startTime = millis();
  }else if (screenNumber == 5){ ////// WHITE screen 2
  int avrW;
  int resW;
    if (getMs == false) {
      println(">>> RESULTS SCREEN 2 ");
      whiteScreen2.ms = millis();
      getMs = true;
    }
    whiteScreen2.desc = wDesc2;
    whiteScreen2.screenTime = millis() - whiteScreen2.ms;

    rectMode(CENTER);
    textAlign(CENTER,CENTER);
    textSize(24);
    //Result of first test
    text("Preattentivity-test: ", screenW/2, 2*screenH/8, screenW, screenH);
    String firstTestRes = "";
    int textC;
    if (circleFound == true){
       firstTestRes = "Passed (you found the first circle)";
       textC = color(100,200,100);
    }else{
      firstTestRes = "Failed (you couldn't find the first circle)";
      textC = color(200,100,100);
  }
      textSize(16);
      fill(textC);
      text(firstTestRes, screenW/2, 5*screenH/16, screenW, screenH);
    
    
    //Result of second test
    if (testScreen2.screenTime <= readFileAndCalcAverage()){
      avrW = 6*screenW/8;
      resW = testScreen2.screenTime * avrW / readFileAndCalcAverage();
    } else{
      resW = 6*screenW/8;
      avrW = readFileAndCalcAverage() * resW / testScreen2.screenTime;
    }
    textSize(24);
    fill(255);
    text("Timed-test with various distractors: ", screenW/2, 4*screenH/8, screenW, screenH);
    fill(100,100,100);
    rectMode(CORNER);
    noStroke();
    rect(screenW/8, 19*screenH/32, avrW, 40);
    fill(100,200,100);
    rect(screenW/8, 23*screenH/32, resW, 40);
    
    fill(255);
    rectMode(CENTER);
    textSize(16);
    text("Average of all tests: " + nfc(readFileAndCalcAverage()/1000.0f,1), screenW/2, 5*screenH/8, screenW, screenH);
    text("Your result: " + nfc(testScreen2.screenTime/1000.0f,1), screenW/2, 6*screenH/8, screenW, screenH);

    // Tap to restart the test
//    textSize(16);
//    text(whiteScreen2.desc, screenW/2, 15*screenH/16, screenW, screenH);

  }
  
}

public void mouseClicked(){
  mouseClicked = true;
}

public void randomizeXY(){
 for (int i=0; i<7; i++)
    for (int j=0; j<7; j++){
      xs[i][j] = (int) random(0,45);
      ys[i][j] = (int) random(0,45);
      
      mouseClicked = false;
    }
  randX = (int) random(0, 7);
  randY = (int) random(0, 7);
  randShape = (int) random(2,6);
}

public void displayXYtest1(){
  println(">>> display TEST 1");
  for (int i=0; i<7; i++)
  for (int j=0; j<7; j++){  
    if (i == randX && j == randY){
      testScreen1.ellipseX = i*screenW/7 + xs[i][j];
      testScreen1.ellipseY = j*screenH/7 + ys[i][j];
      ellipse(testScreen1.ellipseX, testScreen1.ellipseY, 25, 25);
    }else{
      rect(i*screenW/7 + xs[i][j], j*screenH/7 + ys[i][j], 20, 20);
    }  
  }
}

public void displayXYtest2(){
  println(">>> display TEST 2");
  ellipseMode(CORNER);
  noStroke();
  fill(255);
  for (int i=0; i<7; i++)
  for (int j=0; j<7; j++){  
    if (i == randX && j == randY){
      testScreen2.ellipseX = i*screenW/7 + xs[i][j];
      testScreen2.ellipseY = j*screenH/7 + ys[i][j];
      ellipse(testScreen2.ellipseX, testScreen2.ellipseY, 25, 25);
    }else if((i*j)%randShape == 2){
      rect(i*screenW/7 + xs[i][j], j*screenH/7 + ys[i][j], i+15, i+15);
    }else if((i*j)%randShape == 0){
      ellipse(i*screenW/7 + xs[i][j], j*screenH/7 + ys[i][j], 30, 20);

    }else{
      ellipse(i*screenW/7 + xs[i][j], j*screenH/7 + ys[i][j], 20, 30);
    }
  } 
}

public void drawCursor(){
  noFill();
  stroke(255);
  rect(mouseX, mouseY, in*2, in*2);
}

public int readFileAndCalcAverage(){
  int average = 0;
  try{
    line = reader.readLine();
  }catch (IOException e){
    line = null;
  }

  println(sumResults);

  if (line == null){
    noLoop();
  }else {
    sumResults += PApplet.parseInt(line);
    numOfLine += 1;
  }
  average = (sumResults/numOfLine);
  println ("lines: "+numOfLine);
  return average;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ue_2_benamor" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
