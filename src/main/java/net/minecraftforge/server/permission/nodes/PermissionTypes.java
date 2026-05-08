/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.server.permission.nodes;

import net.minecraft.text.Text;
import net.minecraftforge.server.permission.nodes.PermissionType;
import org.jetbrains.annotations.Nullable;

public final class PermissionTypes {
    public static final PermissionType<Boolean> BOOLEAN = new PermissionType<Boolean>(Boolean.class, "boolean");
    public static final PermissionType<Integer> INTEGER = new PermissionType<Integer>(Integer.class, "integer");
    public static final PermissionType<String> STRING = new PermissionType<String>(String.class, "string");
    public static final PermissionType<Text> COMPONENT = new PermissionType<Text>(Text.class, "component");

    private PermissionTypes() {
    }

    @Nullable
    public static PermissionType<?> getTypeByName(String name) {
        return switch (name) {
            case "boolean" -> BOOLEAN;
            case "integer" -> INTEGER;
            case "string" -> STRING;
            case "component" -> COMPONENT;
            default -> null;
        };
    }
}

