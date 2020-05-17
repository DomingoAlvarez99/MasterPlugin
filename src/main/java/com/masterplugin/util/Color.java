package com.masterplugin.util;

import java.lang.reflect.Field;

public class Color {
    public static final String BLACK = "&0";
    public static final String DARK_BLUE = "&1";
    public static final String DARK_GREEN = "&2";
    public static final String DARK_TURQUOISE = "&3";
    public static final String DARK_RED = "&4";
    public static final String PURPLE = "&5";
    public static final String DARK_YELLOW = "&6";
    public static final String LIGHT_GRAY = "&7";
    public static final String DARK_GRAY = "&8";
    public static final String LIGHT_BLUE = "&9";
    public static final String LIGHT_GREEN = "&a";
    public static final String LIGHT_TURQUOISE = "&b";
    public static final String LIGHT_RED = "&c";
    public static final String MAGENTA = "&d";
    public static final String LIGHT_YELLOW = "&e";
    public static final String WHITE = "&f";
    public static final String RAINBOW = "RAINBOW";
    public static final String BOLD = "&l";
    public static final String RANDOM = "&K";

    public static String stringToColor(String c) {
        String color = "";
        for (Field field : Color.class.getFields()) {
            if (field.getName().equalsIgnoreCase(c)) {
                try {
                    color = field.get(Color.class).toString();
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (c.equalsIgnoreCase("rainbow")) {
            color = "rainbow";
        }
        return color;
    }

    public static String[] getNameColors() {
        String[] colors = new String[Color.class.getFields().length];
        int cont = 0;
        for (Field field : Color.class.getFields()) {
            colors[cont] = field.getName();
            cont++;
        }
        return colors;
    }

    public static String[] getColors() {
        String[] colors = new String[Color.class.getFields().length];
        int cont = 0;
        for (Field field : Color.class.getFields()) {
            try {
                if (field.getName().equalsIgnoreCase("RAINBOW")) {
                    String rainbow = "";
                    String[] colorRGB = new String[]{Color.LIGHT_RED, Color.DARK_YELLOW, Color.LIGHT_YELLOW, Color.LIGHT_GREEN, Color.DARK_TURQUOISE, Color.LIGHT_TURQUOISE, Color.PURPLE};
                    for (int i = 0; i < field.getName().length(); i++) {
                        rainbow += colorRGB[i] + field.getName().charAt(i);
                    }
                    colors[cont] = rainbow;
                } else {
                    colors[cont] = field.get(Color.class) + field.getName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            cont++;
        }
        return colors;
    }
}
