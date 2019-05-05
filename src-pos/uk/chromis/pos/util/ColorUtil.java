
package uk.chromis.pos.util;

import java.awt.Color;

public class ColorUtil {
    
    
      public static Color getColorFromArray(String[] _arrColor){
         Color color=null;
         int r= Integer.parseInt(_arrColor[0].toString().trim());
         int g=Integer.parseInt(_arrColor[1].toString().trim());
         int b=Integer.parseInt(_arrColor[2].toString().trim());
         color = new Color(r,g,b);
         if (_arrColor.length==4){
            int a = Integer.parseInt(_arrColor[3].toString().trim());
            color = new Color(r,g,b,a);
         }
        return color;
    }
      
      
      public static Color setColorFromConfig(String _color){
         Color color=Color.WHITE;
         if (_color!=null){
             String[] _arrColor = _color.split(",");
             int r= Integer.parseInt(_arrColor[0].toString().trim());
             int g=Integer.parseInt(_arrColor[1].toString().trim());
             int b=Integer.parseInt(_arrColor[2].toString().trim());
             color = new Color(r,g,b);
             if (_arrColor.length==4){
                int a = Integer.parseInt(_arrColor[3].toString().trim());
                color = new Color(r,g,b,a);
             }
         }
        return color;
    }
      
      public static String getColorToRgb(Color _color){
         String result = _color.getRed()+","+_color.getGreen()+","+_color.getBlue();
        return result;
    }
      
}
