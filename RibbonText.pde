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

  void update(int x, int y)
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
  
  void drawText(String tex)
  {
    
    isDrawing = true;
  }
  
}
