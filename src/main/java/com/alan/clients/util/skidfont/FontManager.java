package com.alan.clients.util.skidfont;

import java.awt.*;
import java.io.InputStream;

public class FontManager {
    public static FontDrawer Naven12 = new FontDrawer(getFont("Naven.ttf", 12), true, true);
    public static FontDrawer Naven13 = new FontDrawer(getFont("Naven.ttf", 13), true, true);
    public static FontDrawer Naven14 = new FontDrawer(getFont("Naven.ttf", 14), true, true);
    public static FontDrawer Naven15 = new FontDrawer(getFont("Naven.ttf", 15), true, true);
    public static FontDrawer Naven16 = new FontDrawer(getFont("Naven.ttf", 16), true, true);
    public static FontDrawer Naven17 = new FontDrawer(getFont("Naven.ttf", 17), true, true);
    public static FontDrawer Naven18 = new FontDrawer(getFont("Naven.ttf", 18), true, true);
    public static FontDrawer Naven20 = new FontDrawer(getFont("Naven.ttf", 20), true, true);
    public static FontDrawer Naven25 = new FontDrawer(getFont("Naven.ttf", 25), true, true);
    public static FontDrawer Naven34 = new FontDrawer(getFont("Naven.ttf", 34), true, true);

    public static FontDrawer GenShinGothic18 = new FontDrawer(getFont("x-GenShinGothic-Medium.ttf", 18), true, true);
    public static FontDrawer GenShinGothic20 = new FontDrawer(getFont("x-GenShinGothic-Medium.ttf", 20), true, true);
    public static FontDrawer GenShinGothic24 = new FontDrawer(getFont("x-GenShinGothic-Medium.ttf", 24), true, true);

    public static FontDrawer PingFang_bold12 = new FontDrawer(getFont("PingFang-bold.ttf", 12), true, true);
    public static FontDrawer PingFang_bold13 = new FontDrawer(getFont("PingFang-bold.ttf", 13), true, true);
    public static FontDrawer PingFang_bold14 = new FontDrawer(getFont("PingFang-bold.ttf", 14), true, true);
    public static FontDrawer PingFang_bold15 = new FontDrawer(getFont("PingFang-bold.ttf", 15), true, true);
    public static FontDrawer PingFang_bold16 = new FontDrawer(getFont("PingFang-bold.ttf", 16), true, true);
    public static FontDrawer PingFang_bold17 = new FontDrawer(getFont("PingFang-bold.ttf", 17), true, true);
    public static FontDrawer PingFang_bold18 = new FontDrawer(getFont("PingFang-bold.ttf", 18), true, true);
    public static FontDrawer PingFang_bold20 = new FontDrawer(getFont("PingFang-bold.ttf", 20), true, true);
    public static FontDrawer PingFang_bold25 = new FontDrawer(getFont("PingFang-bold.ttf", 25), true, true);
    public static FontDrawer PingFang_bold34 = new FontDrawer(getFont("PingFang-bold.ttf", 34), true, true);


    public static Font getFont(String name, int size) {
        Font font;
        try {
            InputStream is = FontManager.class.getResourceAsStream("/assets/minecraft/OnLooker/font/" + name);
            font = Font.createFont(0, is);
            font = font.deriveFont(Font.PLAIN, size);
            System.out.println("Loading " + name);
        } catch (Exception ex) {
            System.out.println("Error loading font " + name);
            font = new Font("Arial", Font.PLAIN, size);
        }
        return font;
    }
}