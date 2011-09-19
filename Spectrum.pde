import ddf.minim.*;

Minim minim;
AudioPlayer groove;
RibbonManager ribbonManager1;
RibbonText ribbonText; 

int ribbonAmount = 5;
int ribbonParticleAmount = 60;
float randomness = 0.11;

boolean paused = false;

String[] palettes = { "color.jpg", "blue.jpg", "green.jpg", "rothko_01.jpg" };

int curPalette = 0;
  
void setup()
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

void createribbonManager(String palette) 
{
  // Create the ribbon manager
  ribbonManager1 = new RibbonManager(ribbonAmount, ribbonParticleAmount, randomness, palette);
  ribbonManager1.setRadiusMax(8);                 // default = 8
  ribbonManager1.setRadiusDivide(10);              // default = 10
  ribbonManager1.setGravity(0);                   // default = .03
  ribbonManager1.setFriction(1.1);                  // default = 1.1
  ribbonManager1.setMaxDistance(100);               // default = 40
  ribbonManager1.setDrag(2.5);                      // default = 2
  ribbonManager1.setDragFlare(0);                 // default = .008
}

void draw()
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
    ribbonManager1.update(900, 200+int(avg));
    
}

void keyReleased() {
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

void mouseReleased() {
  ribbonText.drawText("nol");
}

// Cleanup, minim must be explicitly closed
void stop()
{
  if( !paused )
    groove.close();
    
  minim.stop();
  super.stop();
}

