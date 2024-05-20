//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.alan.clients.util.shader;

import com.alan.clients.util.interfaces.InstanceAccess;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderUtil implements InstanceAccess {
    private static final IResourceManager RESOURCE_MANAGER;

    public ShaderUtil() {
    }

    public static int createShader(String fragmentResource, String vertexResource) {
        String fragmentSource = getShaderResource(fragmentResource);
        String vertexSource = getShaderResource(vertexResource);
        if (fragmentResource != null && vertexResource != null) {
            int fragmentId = GL20.glCreateShader(35632);
            int vertexId = GL20.glCreateShader(35633);
            GL20.glShaderSource(fragmentId, fragmentSource);
            GL20.glShaderSource(vertexId, vertexSource);
            GL20.glCompileShader(fragmentId);
            GL20.glCompileShader(vertexId);
            if (!compileShader(fragmentId)) {
                return -1;
            } else if (!compileShader(vertexId)) {
                return -1;
            } else {
                int programId = GL20.glCreateProgram();
                GL20.glAttachShader(programId, fragmentId);
                GL20.glAttachShader(programId, vertexId);
                GL20.glValidateProgram(programId);
                GL20.glLinkProgram(programId);
                GL20.glDeleteShader(fragmentId);
                GL20.glDeleteShader(vertexId);
                return programId;
            }
        } else {
            System.out.println("An error occurred whilst creating shader");
            System.out.println("Fragment: " + fragmentSource == null);
            System.out.println("Vertex: " + vertexSource == null);
            return -1;
        }
    }

    private static boolean compileShader(int shaderId) {
        boolean compiled = GL20.glGetShaderi(shaderId, 35713) == 1;
        if (compiled) {
            return true;
        } else {
            String shaderLog = GL20.glGetShaderInfoLog(shaderId, 8192);
            System.out.println("\nError while compiling shader: ");
            System.out.println("-------------------------------");
            System.out.println(shaderLog);
            return false;
        }
    }

    public static String getShaderResource(String resource) {
        try {
            InputStream inputStream = RESOURCE_MANAGER.getResource(new ResourceLocation("rebirth/shader/" + resource)).getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String source = "";

            String s;
            try {
                while((s = bufferedReader.readLine()) != null) {
                    source = source + s + System.lineSeparator();
                }
            } catch (IOException var6) {
            }

            return source;
        } catch (NullPointerException | IOException var7) {
            Exception e = var7;
            System.out.println("An error occurred while getting a shader resource");
            e.printStackTrace();
            return null;
        }
    }

    public static void drawQuads(ScaledResolution sr) {
        if (!Minecraft.getMinecraft().gameSettings.ofFastRender) {
            float width = (float)sr.getScaledWidth_double();
            float height = (float)sr.getScaledHeight_double();
            GL11.glBegin(7);
            GL11.glTexCoord2f(0.0F, 1.0F);
            GL11.glVertex2f(0.0F, 0.0F);
            GL11.glTexCoord2f(0.0F, 0.0F);
            GL11.glVertex2f(0.0F, height);
            GL11.glTexCoord2f(1.0F, 0.0F);
            GL11.glVertex2f(width, height);
            GL11.glTexCoord2f(1.0F, 1.0F);
            GL11.glVertex2f(width, 0.0F);
            GL11.glEnd();
        }
    }

    static {
        RESOURCE_MANAGER = mc.getResourceManager();
    }
}
