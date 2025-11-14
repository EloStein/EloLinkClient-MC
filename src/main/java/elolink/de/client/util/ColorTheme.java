package elolink.de.client.util;

import java.awt.*;



public class ColorTheme {
    public static final Color defaultBlue = new Color(100, 150, 255);
    public static final Color newChatBlue = new Color(145, 180, 250);

    public static final Color defaultMagenta = new Color(180, 40, 235);
    public static final Color newChatMagenta = new Color(203, 100, 255);

    public static final Color defaultGreen = new Color(32, 143, 65);
    public static final Color newChatGreen = new Color(57, 209, 103);

    public static final Color defaultGray = new Color(89, 90, 89);
    public static final Color newChatGray = new Color(127, 128, 127);


    private static Color setDefaultChatColor;
    private static Color setNewChatColor;

    public static void setDefaultChatColor(Color color){
        setDefaultChatColor = color;
    }

    public static void setDefaultChatColor(int index){
        switch (index){
            case 0 : setDefaultChatColor(ColorTheme.defaultBlue);
            case 1 : setDefaultChatColor(ColorTheme.defaultMagenta);
            case 2 : setDefaultChatColor(ColorTheme.defaultGreen);
        }
    }

    public static Color getDefaultChatColor(){
        return setDefaultChatColor;
    }

    public static void setNewChatColor(Color color){
        setNewChatColor = color;
    }

    public static void setNewChatColor(int index){
        switch (index){
            case 0 : setDefaultChatColor(ColorTheme.newChatBlue);
            case 1 : setDefaultChatColor(ColorTheme.newChatMagenta);
            case 2 : setDefaultChatColor(ColorTheme.newChatGreen);
        }
    }

    public static Color getNewChatColor(){
        return setNewChatColor;
    }

}
