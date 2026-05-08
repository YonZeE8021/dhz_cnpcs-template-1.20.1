/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.text.MutableText
 */
package noppes.npcs.client;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.MutableText;
import noppes.npcs.TextBlock;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.shared.client.util.NoppesStringUtils;

public class TextBlockClient
extends TextBlock {
    public int color = 0xE0E0E0;
    private String name;
    private ServerCommandSource sender;

    public TextBlockClient(String name, String text, int lineWidth, int color, Object ... obs) {
        this(text, lineWidth, false, obs);
        this.color = color;
        this.name = name;
    }

    public TextBlockClient(ServerCommandSource sender, String text, int lineWidth, int color, Object ... obs) {
        this(text, lineWidth, false, obs);
        this.color = color;
        this.sender = sender;
    }

    public String getName() {
        if (this.sender != null) {
            return this.sender.getName();
        }
        return this.name;
    }

    public TextBlockClient(String text, int lineWidth, boolean mcFont, Object ... obs) {
        text = NoppesStringUtils.formatText(text, obs);
        Object line = "";
        text = text.replace("\n", " \n ");
        text = text.replace("\r", " \r ");
        String[] words = text.split(" ");
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        for (String word : words) {
            char c;
            if (word.isEmpty()) continue;
            if (word.length() == 1 && ((c = word.charAt(0)) == '\r' || c == '\n')) {
                this.addLine((String)line);
                line = "";
                continue;
            }
            Object newLine = ((String)line).isEmpty() ? word : (String)line + " " + word;
            if ((mcFont ? font.getWidth((String)newLine) : ClientProxy.Font.width((String)newLine)) > lineWidth) {
                this.addLine((String)line);
                line = word.trim();
                continue;
            }
            line = newLine;
        }
        if (!((String)line).isEmpty()) {
            this.addLine((String)line);
        }
    }

    private void addLine(String text) {
        MutableText line = Text.literal((String)text);
        this.lines.add(line);
    }
}

