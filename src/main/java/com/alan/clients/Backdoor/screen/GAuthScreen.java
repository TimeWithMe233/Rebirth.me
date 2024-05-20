package com.alan.clients.Backdoor.screen;

import com.alan.clients.Backdoor.Protection;
import com.alan.clients.Backdoor.Requester;
import com.alan.clients.util.TimeHelper;
import com.alan.clients.util.font.Font;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;

/**
 * @author Haze
 * @since 9/9/2015
 */
public class GAuthScreen extends GuiScreen {
    private boolean isSetup;
    private Minecraft mc;
    private GuiTextField authCodeTextField;
    private GuiButton submitButton, copyButton, confirmSecretButton;
    private String status;
    private TimeHelper timer = TimeHelper.getTimer();
    private File qrLoc;
    private boolean hasCopied = false;
    private String secret = "";

    public GAuthScreen(Minecraft mc) {
        this.mc = mc;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            System.out.println(Protection.getCryptedHWID());
            System.out.println(Protection.getCryptedHWID());
            JsonElement element = Requester.newRequester().username(Protection.getCryptedHWID()).authcode(this.authCodeTextField.getText()).requestType(Requester.RequestType.AUTHENTICATION).build().request();
            JsonPrimitive prim = element.getAsJsonPrimitive();
            if (prim.getAsString().isEmpty()) {
                status = "Invalid Username! Are you an actual Rapture User!?";
                return;
            }
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> myMap = new GsonBuilder().setPrettyPrinting().create().fromJson(prim.getAsString(), type);
            boolean isTrue = myMap.get("success").equalsIgnoreCase("true");
            if (isTrue) {
                Protection.isValidGAUTH = true;
                mc.displayGuiScreen(new GuiMainMenu());
            } else {
                status = String.format("Error - %s. Please send your hwid to rudy, thanks.", myMap.get("error"));
            }
            this.timer.reset();
        } else if (button.id == 1) {
            StringSelection stringSelection = new StringSelection(Protection.getCryptedHWID());
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
            status = "Copied HWID to clipboard.";
        } else if (button.id == 2) {
            if (hasCopied)
                confirmClicked(true, 0);
            else {
                StringSelection stringSelection = new StringSelection(secret);
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);
                this.confirmSecretButton.displayString = "Confirm";
                hasCopied = true;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.authCodeTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawDefaultBackground();
        if (isSetup) {
            if (timer.hasDelayRun(5000)) {
                if (!status.equals("Waiting..."))
                    status = "Waiting...";
                timer.reset();
            }
            fontRendererObj.drawCenteredString("Please enter your current Google Authentication code below.", this.width / 2, this.height / 2 - 30, 0xFFeeeeee);
            fontRendererObj.drawCenteredString(status, this.width / 2, this.height / 2 - 40, 0xFFeeeeee);
            this.submitButton.drawButton(mc, mouseX, mouseY);
            this.copyButton.drawButton(mc, mouseX, mouseY);
            this.authCodeTextField.drawTextBox();
        } else {
            try {
                fontRendererObj.drawCenteredString(secret, this.width / 2, this.height / 2 + 80, 0xFFeeeeee);
                this.confirmSecretButton.drawButton(mc, mouseX, mouseY);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.authCodeTextField = new GuiTextField(0, fontRendererObj, this.width / 2 - 100, this.height / 2 - 10, 200, 20);
        this.authCodeTextField.setFocused(true);
        this.authCodeTextField.setMaxStringLength(6);
        this.submitButton = new GuiButton(0, this.width / 2 - 100, (this.height / 6) * 5, "Submit");
        this.copyButton = new GuiButton(1, this.width / 2 - 100, (this.height / 8) * 5, "Copy HWID");
        this.submitButton.enabled = false;
        this.confirmSecretButton = new GuiButton(2, this.width / 2 - 100, (this.height / 8) * 7, "Copy Secret");
        this.buttonList.add(this.submitButton);
        this.buttonList.add(this.copyButton);
        this.buttonList.add(this.confirmSecretButton);
        this.status = "Awaiting Google Authentication code...";
        try {
            String chart = String.format("otpauth://totp/%s?secret=%s", "Rapture", secret);
            qrLoc = File.createTempFile("qrCode", ".png");
            URL website = new URL(String.format("https://chart.googleapis.com/chart?chs=200x200&chld=M|0&cht=qr&chl=%s", chart));
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(qrLoc);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            JsonElement element = Requester.newRequester().username(Protection.getCryptedHWID()).requestType(Requester.RequestType.GET_SECRET).build().request();
            JsonPrimitive prim = element.getAsJsonPrimitive();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> myMap = new GsonBuilder().setPrettyPrinting().create().fromJson(prim.getAsString(), type);
            secret = myMap.get("secret");
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        this.isSetup = result;
        mc.displayGuiScreen(this);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (Character.isDigit(typedChar) || !Character.isAlphabetic(typedChar))
            this.authCodeTextField.textboxKeyTyped(typedChar, keyCode);
        if (keyCode == 28 || keyCode == 156) {
            if (this.submitButton.enabled)
                this.actionPerformed((GuiButton) this.buttonList.get(0));
        }
        ((GuiButton) this.buttonList.get(0)).enabled = this.authCodeTextField.getText().length() == 6;
    }

    @Override
    public void updateScreen() {
        this.authCodeTextField.updateCursorCounter();
    }
}
