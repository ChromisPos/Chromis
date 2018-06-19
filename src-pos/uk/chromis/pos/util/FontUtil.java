package uk.chromis.pos.util;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.UIManager;

public class FontUtil {
    private static String FONT_NAME1 = "Oswald Bold";
    private static String FONT_NAME2 = "Oswald Bold";
    private static Font defaultFont; 
    private static Font fontDinamic;
    private static String FONT_TYPE = "Oswald-Bold.ttf";
    private static String FONT_TYPE2 = "Lusitana-Regular.ttf";
    private static int STYLE_DEFAULT = Font.BOLD;
    public static int SIZE_DEFAULT = 12;
    public static int SIZE_CATALOG = 12;
    
     public static Font getFont( int style, int size){
        if (defaultFont!=null){
            defaultFont = fontDinamic.deriveFont(style, size);
            return defaultFont;
        }else{
            try{
                File file = new File ("fonts"+ java.io.File.separator + FONT_TYPE); 
                file.getAbsoluteFile();
                if (file.exists()){

                     FileInputStream in = new FileInputStream (file); 
                fontDinamic =
                        Font.createFont (Font.TRUETYPE_FONT, in);
                  defaultFont = fontDinamic.deriveFont (style,size) ;
                 }
                }catch(Exception e){
                    e.printStackTrace();
                }
        }
        
        return defaultFont;
    }
     
    public static Font getPOSFont(){
        return new java.awt.Font(FONT_NAME1, Font.BOLD,SIZE_DEFAULT);
    } 
     
    public static Font getPOSFont(int size){
        return new java.awt.Font(FONT_NAME1, Font.BOLD, size);
    } 
    
    public static Font getPOSFont(int type, int size){
        return new java.awt.Font(FONT_NAME1, type, size);
    } 
  
    public static Font getPOSFont2(){
        return new java.awt.Font(FONT_NAME2, Font.PLAIN, 14);
    }  
     
   public static Font getPOSFont2(int size){
        return new java.awt.Font(FONT_NAME2, Font.PLAIN, size);
    } 
    
    public static Font getPOSFont2(int type, int size){
        return new java.awt.Font(FONT_NAME2, type, size);
    } 
    
    
    public static Font getPOSFont(String name,int type, int size){
        return new java.awt.Font(name, type, size);
    } 
    
    
    public static Color getPOSColor(int r,int g, int b){
      return  new java.awt.Color(r,g, b);
    }

    public static Color getPOSColor(){
      return  new java.awt.Color(0,0, 0);
    }
    
    public static Color getPOSColor2(){
      return  new java.awt.Color(0,188, 243);
    }
    
     
     public static Font getFont(){
        if (defaultFont!=null){
            defaultFont = fontDinamic.deriveFont(STYLE_DEFAULT, SIZE_DEFAULT);
            return defaultFont;
        }else{
            try{
                File file = new File ("fonts"+ java.io.File.separator + FONT_TYPE); 
                file.getAbsoluteFile();
                if (file.exists()){

                     FileInputStream in = new FileInputStream (file); 
                fontDinamic =
                        Font.createFont (Font.TRUETYPE_FONT, in);
                  defaultFont = fontDinamic.deriveFont (STYLE_DEFAULT,SIZE_DEFAULT) ;
                 }
                }catch(Exception e){
                    e.printStackTrace();
                }
        }
        
        return defaultFont;
    }
    
    public static void setUIFont(String _fontName)
    {
        try{
        File file = new File ("fonts"+ java.io.File.separator + _fontName); 
        file.getAbsoluteFile();
        if (file.exists()){
                FileInputStream in = new FileInputStream (file); 
                Font dynamicFont =
                        Font.createFont (Font.TRUETYPE_FONT, in); 
                Font font = dynamicFont.deriveFont (12f) ;

               javax.swing.plaf.FontUIResource f= new javax.swing.plaf.FontUIResource(font);
                java.util.Enumeration keys = UIManager.getDefaults().keys();
                while (keys.hasMoreElements())
                {
                    Object key = keys.nextElement();
                    Object value = UIManager.get(key);
                    if (value instanceof javax.swing.plaf.FontUIResource)
                    {
                        UIManager.put(key, f);
                    }
                }
         }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    //Font for All Catalog
    public static Font getCatalogFont(){
        return new java.awt.Font(FONT_NAME1, Font.BOLD,SIZE_CATALOG);
    }
    
    //Color for All Catalog (set Null if not need color)
     public static Color getCatalogColor(){
//        return null;
        return getPOSColor();
    }
     
     
     
     //Font for Menu
    public static Font getMenuFont(){
        return getPOSFont();
    }
    
    //Color for All Catalog  (set Null if not need color)
     public static Color getMenuColor(){
//        return null;
          return getPOSColor2();
    }
     
     
     //Font for Table
    public static Font getTableHeaderFont(){
         return new java.awt.Font(FONT_NAME1, Font.BOLD,14);
    }
    
    public static Font getTableFont(){
        return getPOSFont();
    }
    
    //Color for All Catalog  (set Null if not need color)
    public static Color getTableHeaderColor(){
//        return null;
          return  new java.awt.Color(200,200, 200);
    }
    
     public static Color getTableColor(){
//        return null;
          return  new java.awt.Color(255,255, 255);
    }
     
     
     
}
