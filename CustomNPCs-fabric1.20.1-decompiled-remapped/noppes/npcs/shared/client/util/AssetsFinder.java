/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.InvalidIdentifierException
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.resource.ResourceType
 */
package noppes.npcs.shared.client.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import noppes.npcs.shared.common.util.LogWriter;

public class AssetsFinder {
    private static List<Identifier> list = new ArrayList<Identifier>();
    private static String root;
    private static String type;

    public static List<Identifier> find(String root, String type) {
        AssetsFinder.root = root;
        AssetsFinder.type = type;
        list.clear();
        ArrayList<Identifier> resources = new ArrayList<Identifier>();
        MinecraftClient.getInstance().getResourceManager().streamResourcePacks().forEach(p -> {
            for (String s : p.getNamespaces(ResourceType.field_14188)) {
                try {
                    p.findResources(ResourceType.field_14188, s, root, (r, streamIoSupplier) -> {
                        if (r.toString().endsWith(type)) {
                            resources.add((Identifier)r);
                        }
                    });
                }
                catch (InvalidIdentifierException e) {
                    LogWriter.except(e);
                }
            }
        });
        return resources;
    }
}

