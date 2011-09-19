import processing.core.*; 
import processing.xml.*; 

import ddf.minim.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class spectrum extends PApplet {

class Ribbon
{
  int ribbonAmount;
  float randomness;
  int ribbonParticleAmount;         // length of the Particle Array (max number of points)
  int particlesAssigned = 0;        // current amount of particles currently in the Particle array                                
  float radiusMax = 8;              // maximum width of ribbon
  float radiusDivide = 10;          // distance between current and next point / this = radius for first half of the ribbon
  float gravity = .03f;              // gravity applied to each particle
  float friction = 1.1f;             // friction applied to the gravity of each particle
  int maxDistance = 40;             // if the distance between particles is larger than this the drag comes into effect
  float drag = 2;                   // if distance goes above maxDistance - the points begin to grag. high numbers = less drag
  float dragFlare = .008f;           // degree to which the drag makes the ribbon flare out
  RibbonParticle[] particles;       // particle array
  int ribbonColor;
  
  Ribbon(int ribbonParticleAmount, int ribbonColor, float randomness)
  {
    this.ribbonParticleAmount = ribbonParticleAmount;
    this.ribbonColor = ribbonColor;
    this.randomness = randomness;
    init();
  }
  
  public void init()
  {
    particles = new RibbonParticle[ribbonParticleAmount];
  }
  
  public void update(float randX, float randY)
  {
    addParticle(randX, randY);
    drawCurve();
  }
  
  public void addParticle(float randX, float randY)
  {
    if(particlesAssigned == ribbonParticleAmount)
    {
      for (int i = 1; i < ribbonParticleAmount; i++)
      {
        particles[i-1] = particles[i];
      }
      particles[ribbonParticleAmount - 1] = new RibbonParticle(randomness, this);
      particles[ribbonParticleAmount - 1].px = randX;
      particles[ribbonParticleAmount - 1].py = randY;
      return;
    }
    else
    {
      particles[particlesAssigned] = new RibbonParticle(randomness, this);
      particles[particlesAssigned].px = randX;
      particles[particlesAssigned].py = randY;
      ++particlesAssigned;
    }
    if (particlesAssigned > ribbonParticleAmount) ++particlesAssigned;
  }
  
  public void drawCurve()
  {
    smooth();
    for (int i = 1; i < particlesAssigned - 1; i++)
    {
      RibbonParticle p = particles[i];
      p.calculateParticles(particles[i-1], particles[i+1], ribbonParticleAmount, i);
    }

    fill(30);
    for (int i = particlesAssigned - 3; i > 1 - 1; i--)
    {
      RibbonParticle p = particles[i];
      RibbonParticle pm1 = particles[i-1];
      fill(ribbonColor, 255);
      if (i < particlesAssigned-3) 
      {
        noStroke();
        beginShape();
        vertex(p.lcx2, p.lcy2);
        bezierVertex(p.leftPX, p.leftPY, pm1.lcx2, pm1.lcy2, pm1.lcx2, pm1.lcy2);
        vertex(pm1.rcx2, pm1.rcy2);
        bezierVertex(p.rightPX, p.rightPY, p.rcx2, p.rcy2, p.rcx2, p.rcy2);
        vertex(p.lcx2, p.lcy2);
        endShape();
      }
    }
  }
}
class RibbonManager
{
  PImage img;
  int ribbonAmount;
  int ribbonParticleAmount;
  float randomness;
  String imgName;
  Ribbon[] ribbons;       // ribbon array
  
  RibbonManager(int ribbonAmount, int ribbonParticleAmount, float randomness, String imgName)
  {
    this.ribbonAmount = ribbonAmount;
    this.ribbonParticleAmount = ribbonParticleAmount;
    this.randomness = randomness;
    this.imgName = imgName;
    init();
  }
  
  public void init()
  {
    img = loadImage(imgName);
    addRibbon();
  }

  public void addRibbon()
  {
    ribbons = new Ribbon[ribbonAmount];
    for (int i = 0; i < ribbonAmount; i++)
    {
      int xpos = PApplet.parseInt(random(img.width));
      int ypos = PApplet.parseInt(random(img.height));
      int ribbonColor = img.get(xpos, ypos);
      ribbons[i] = new Ribbon(ribbonParticleAmount, ribbonColor, randomness);
    }
  }
  
  public void update(int currX, int currY) 
  {
    for (int i = 0; i < ribbonAmount; i++)
    {
      //float randX = currX + (randomness / 2) - random(randomness);
      //float randY = currY + (randomness / 2) - random(randomness);
      
      float randX = currX;
      float randY = currY;
      
      ribbons[i].update(randX, randY);
    }
  }
  
  public void setPalette(String pal)
  {
    img = loadImage(pal);
    
    for (int i = 0; i < ribbonAmount; i++) { 
      
      int xpos = PApplet.parseInt(random(img.width));
      int ypos = PApplet.parseInt(random(img.height));
      
      ribbons[i].ribbonColor = img.get(xpos, ypos);
    }
    
  }
  
  public void setRadiusMax(float value) { for (int i = 0; i < ribbonAmount; i++) { ribbons[i].radiusMax = value; } }
  public void setRadiusDivide(float value) { for (int i = 0; i < ribbonAmount; i++) { ribbons[i].radiusDivide = value; } }
  public void setGravity(float value) { for (int i = 0; i < ribbonAmount; i++) { ribbons[i].gravity = value; } }
  public void setFriction(float value) { for (int i = 0; i < ribbonAmount; i++) { ribbons[i].friction = value; } }
  public void setMaxDistance(int value) { for (int i = 0; i < ribbonAmount; i++) { ribbons[i].maxDistance = value; } }
  public void setDrag(float value) { for (int i = 0; i < ribbonAmount; i++) { ribbons[i].drag = value; } }
  public void setDragFlare(float value) { for (int i = 0; i < ribbonAmount; i++) { ribbons[i].dragFlare = value; } }
  
}
class RibbonParticle
{
  float px, py;                                       // x and y position of particle (this is the bexier point)
  float xSpeed, ySpeed = 0;                           // speed of the x and y positions
  float cx1, cy1, cx2, cy2;                           // the avarage x and y positions between px and py and the points of the surrounding Particles
  float leftPX, leftPY, rightPX, rightPY;             // the x and y points of that determine the thickness of this segment
  float lpx, lpy, rpx, rpy;                           // the x and y points of the outer bezier points
  float lcx1, lcy1, lcx2, lcy2;                       // the avarage x and y positions between leftPX and leftPX and the left points of the surrounding Particles
  float rcx1, rcy1, rcx2, rcy2;                       // the avarage x and y positions between rightPX and rightPX and the right points of the surrounding Particles
  float radius;                                       // thickness of current particle
  float randomness;
  Ribbon ribbon;

  RibbonParticle(float randomness, Ribbon ribbon)
  {
    this.randomness = randomness;
    this.ribbon = ribbon;
  }

  public void calculateParticles(RibbonParticle pMinus1, RibbonParticle pPlus1, int particleMax, int i)
  {

    float div = 2;
    cx1 = (pMinus1.px + px) / div;
    cy1 = (pMinus1.py + py) / div;
    cx2 = (pPlus1.px + px) / div;
    cy2 = (pPlus1.py + py) / div;

    // calculate radians (direction of next point)
    float dx = cx2 - cx1;
    float dy = cy2 - cy1;

    float pRadians = atan2(dy, dx);

    float distance = sqrt(dx*dx + dy*dy);

    if (distance > ribbon.maxDistance)   //  && i > 1 
    {
      float oldX = px;
      float oldY = py;
      px = px + ((ribbon.maxDistance/ribbon.drag) * cos(pRadians));
      py = py + ((ribbon.maxDistance/ribbon.drag) * sin(pRadians));
      xSpeed += (px - oldX) * ribbon.dragFlare;
      ySpeed += (py - oldY) * ribbon.dragFlare;
    }

    ySpeed += ribbon.gravity;
    xSpeed *= ribbon.friction;
    ySpeed *= ribbon.friction;
    px += xSpeed + random(.3f);
    py += ySpeed + random(.3f);

    float randX = ((randomness / 2) - random(randomness)) * distance;
    float randY = ((randomness / 2) - random(randomness)) * distance;
    px += randX;
    py += randY;

    //float radius = distance / 2;
    //if (radius > radiusMax) radius = ribbon.radiusMax;

    if (i > particleMax / 2) 
    {
      radius = distance / ribbon.radiusDivide;
    } 
    else 
    {
      radius = pPlus1.radius * .9f;
    }

    if (radius > ribbon.radiusMax) radius = ribbon.radiusMax;
    if (i == particleMax - 2 || i == 1) 
    {
      if (radius > 1) radius = 1;
    }

    // calculate the positions of the particles relating to thickness
    leftPX = px + cos(pRadians + (HALF_PI * 3)) * radius;
    leftPY = py + sin(pRadians + (HALF_PI * 3)) * radius;
    rightPX = px + cos(pRadians + HALF_PI) * radius;
    rightPY = py + sin(pRadians + HALF_PI) * radius;

    // left and right points of current particle
    lpx = (pMinus1.lpx + lpx) / div;
    lpy = (pMinus1.lpy + lpy) / div;
    rpx = (pPlus1.rpx + rpx) / div;
    rpy = (pPlus1.rpy + rpy) / div;

    // left and right points of previous particle
    lcx1 = (pMinus1.leftPX + leftPX) / div;
    lcy1 = (pMinus1.leftPY + leftPY) / div;
    rcx1 = (pMinus1.rightPX + rightPX) / div;
    rcy1 = (pMinus1.rightPY + rightPY) / div;

    // left and right points of next particle
    lcx2 = (pPlus1.leftPX + leftPX) / div;
    lcy2 = (pPlus1.leftPY + leftPY) / div;
    rcx2 = (pPlus1.rightPX + rightPX) / div;
    rcy2 = (pPlus1.rightPY + rightPY) / div;

    // scrolling
    px -= 15;
  }
}

class RibbonText
{

  
  int _blank[][] = { {0,0}, {0,0}, {0,0}, {0,0}, {0,0} };
  int _b[][] = { {0, -200}, {-15,0}, {0, -50}, {0, -50}, {0, -60}, {-15, -50}, {-30, 0}};
  int _n[][] = { {0,-50}, {-15,0}, {-30, 0}, {0,-50}, {0,0}, {0, 0}};
  int _a[][] = { {60, -45}, {60, -35}, {10, -35}, {0, -20}, {0, 0}, {0, -45}, {-10, 0}, {0, 0} };
  int _u[][] = {  {0, -50}, {-30, -50},  {-30, 0}, {-15, 0}, {0, 0},  {-30, -50}, {-30, -50}, {-60, 0}, {-30, 0} }; //{-15, -50}, {-15, -50}, {-15, 0}, {0, 0}, {0, 0}, {0, -50}, {0, -50}, {-30, 0}, {0, 0} };
  int _h[][] = { {15, -100}, {-15, -150}, {0, 0}, {-30, 0},  {-30, -50}, {-15, -50}, {0, -50},  {-30, 0}, {0, 0} };
  int _m[][] = { {0,-50}, {-15,0}, {-30, 0}, {0,-50}, {0, 0}, {0, -50},{0,0}, {0, 0}};
  int _g[][] = { {0,0}, {0,200}, {100, 100}, {200, 200}, {200, 0} };
  boolean isDrawing = false;
  RibbonManager rbm;
 
  int te[][][] = {_blank, _h, _u, _m, _a, _n, _blank};
  int letter = 0;
  
  int pos = 0;
  
  RibbonText(RibbonManager rbn)
  {
    rbm = rbn;
  }

  public void update(int x, int y)
  {
    if(isDrawing) 
    {
      if( pos >= te[letter].length) {
        pos = 0;
        letter++;
      }
     
      if( letter >= te.length ) {
        isDrawing = false;
        pos = 0;
        letter =0;
        
      } else {
        rbm.update(x+te[letter][pos][0], y+te[letter][pos][1]);
        pos++;
      } 
    }
  }
  
  public void drawText(String tex)
  {
    
    isDrawing = true;
  }
  
}


Minim minim;
AudioPlayer groove;
RibbonManager ribbonManager1;
RibbonText ribbonText; 

int ribbonAmount = 5;
int ribbonParticleAmount = 60;
float randomness = 0.11f;

boolean paused = false;

String[] palettes = { "color.jpg", "blue.jpg", "green.jpg", "rothko_01.jpg" };

int curPalette = 0;
  
public void setup()
{
  size(950, 385);
  frameRate(30);
  background(0);

  createribbonManager( palettes[curPalette] );

  // Create the text plotter
  ribbonText = new RibbonText(ribbonManager1);
  
  // Load the mp3
  minim = new Minim(this);
  groove = minim.loadFile("music.mp3", 2048);
  groove.play();
}

public void createribbonManager(String palette) 
{
  // Create the ribbon manager
  ribbonManager1 = new RibbonManager(ribbonAmount, ribbonParticleAmount, randomness, palette);
  ribbonManager1.setRadiusMax(8);                 // default = 8
  ribbonManager1.setRadiusDivide(10);              // default = 10
  ribbonManager1.setGravity(0);                   // default = .03
  ribbonManager1.setFriction(1.1f);                  // default = 1.1
  ribbonManager1.setMaxDistance(100);               // default = 40
  ribbonManager1.setDrag(2.5f);                      // default = 2
  ribbonManager1.setDragFlare(0);                 // default = .008
}

public void draw()
{
  background(0);

  // draw the waveforms and calculate the total
  stroke(255);
  float avg = 0;
  for(int i = 0; i < groove.bufferSize() - 1; i++)
  {
    float x1 = map(i, 0, groove.bufferSize(), 0, width);
    float x2 = map(i+1, 0, groove.bufferSize(), 0, width);
    line(x1, 200 + groove.mix.get(i)*60, x2, 200 + groove.mix.get(i+1)*60);
    avg += groove.mix.get(i);
  }

  // if we are drawing text, update the text else plot the total waveform
  if(ribbonText.isDrawing)
    ribbonText.update(900, 200);
  else
    ribbonManager1.update(900, 200+PApplet.parseInt(avg));
    
}

public void keyReleased() {
  if( key == 'p' || key == ' ' ) {// pause
    if( paused ) {
      loop();
      groove.play();
      paused = !paused;
    } else {
      groove.pause();
      noLoop();
      paused = !paused;
    }
  }
    
  if( !paused ) {
    if (key == CODED) {
      if (keyCode == UP || keyCode == DOWN) {      
        ribbonManager1.setPalette( palettes[curPalette] );
      } else if(keyCode == LEFT || keyCode == RIGHT) {
        curPalette = ++curPalette % palettes.length;
        ribbonManager1.setPalette( palettes[curPalette] );
        
      }
    }

  }
 
}

public void mouseReleased() {
  ribbonText.drawText("nol");
}

// Cleanup, minim must be explicitly closed
public void stop()
{
  if( !paused )
    groove.close();
    
  minim.stop();
  super.stop();
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "spectrum" });
  }
}
