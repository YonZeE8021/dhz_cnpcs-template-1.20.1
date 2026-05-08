/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$Action
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.text.MutableText
 *  net.minecraft.client.network.ClientPlayerEntity
 */
package noppes.npcs.client;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.client.network.ClientPlayerEntity;

public class VersionChecker
extends Thread {
    @Override
    public void run() {
        ClientPlayerEntity player;
        String name = "\u00a72CustomNpcs\u00a7f";
        String link = "\u00a79\u00a7nClick here";
        String text = name + " installed. For more info " + link;
        try {
            player = MinecraftClient.getInstance().player;
        }
        catch (NoSuchMethodError e) {
            return;
        }
        while ((player = MinecraftClient.getInstance().player) == null) {
            try {
                Thread.sleep(2000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MutableText message = Text.translatable((String)text);
        message.setStyle(message.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.kodevelopment.nl/minecraft/customnpcs/")));
        player.sendMessage((Text)message);
    }
}

