//package com.alan.clients.util.font.dreamfont;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.util.ResourceLocation;
//
//import java.awt.*;
//
//public class FontLoaders {
//    //鸿蒙字体
//    public static FontRender Harmony12;
//    public static FontRender Harmony14;
//    public static FontRender Harmony16;
//    public static FontRender Harmony18;
//    public static FontRender Harmony20;
//    public static FontRender Harmony22;
//    public static FontRender Harmony24;
//    public static FontRender Harmony26;
//    public static FontRender Harmony28;
//    public static FontRender Harmony30;
//    public static FontRender HarmonyBold12;
//    public static FontRender HarmonyBold14;
//    public static FontRender HarmonyBold16;
//    public static FontRender HarmonyBold18;
//    public static FontRender HarmonyBold20;
//    public static FontRender HarmonyBold22;
//    public static FontRender HarmonyBold24;
//    public static FontRender HarmonyBold26;
//    public static FontRender HarmonyBold28;
//    public static FontRender HarmonyBold30;
//
//    public static void init() throws Throwable {
//        //鸿蒙字体
//        Harmony12 = createByResource("harmony.ttf",12f,true,true);
//        Harmony14 = createByResource("harmony.ttf",14f,true,true);
//        Harmony16 = createByResource("harmony.ttf",16f,true,true);
//        Harmony18 = createByResource("harmony.ttf",18f,true,true);
//        Harmony20 = createByResource("harmony.ttf",20f,true,true);
//        Harmony22 = createByResource("harmony.ttf",22f,true,true);
//        Harmony24 = createByResource("harmony.ttf",24f,true,true);
//        Harmony26 = createByResource("harmony.ttf",26f,true,true);
//        Harmony28 = createByResource("harmony.ttf",28f,true,true);
//        Harmony30 = createByResource("harmony.ttf",30f,true,true);
//        HarmonyBold12 = createByResource("harmony_bold.ttf",12f,true,true);
//        HarmonyBold14 = createByResource("harmony_bold.ttf",14f,true,true);
//        HarmonyBold16 = createByResource("harmony_bold.ttf",16f,true,true);
//        HarmonyBold18 = createByResource("harmony_bold.ttf",18f,true,true);
//        HarmonyBold20 = createByResource("harmony_bold.ttf",20f,true,true);
//        HarmonyBold22 = createByResource("harmony_bold.ttf",22f,true,true);
//        HarmonyBold24 = createByResource("harmony_bold.ttf",24f,true,true);
//        HarmonyBold26 = createByResource("harmony_bold.ttf",26f,true,true);
//        HarmonyBold28 = createByResource("harmony_bold.ttf",28f,true,true);
//        HarmonyBold30 = createByResource("harmony_bold.ttf",30f,true,true);
//    }
//
//    public static FontRender createByResource(String resourceName, float size, boolean antiAliasing, boolean fractionalMetrics) throws Throwable {
//        int scaleFactor = 1;
//        return new FontRender(
//                Font.createFont(
//                        Font.TRUETYPE_FONT,
//                        Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("Client/Fonts/"+resourceName)).getInputStream()
//                ).deriveFont(Font.PLAIN, size), antiAliasing,fractionalMetrics
//        );
//    }
//}
