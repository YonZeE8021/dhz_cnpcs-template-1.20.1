/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.network.ServerPlayerEntity
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.server.permission.nodes;

import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.server.permission.nodes.PermissionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PermissionNodeCompat<T> {
    private final String nodeName;
    private final PermissionType<T> type;
    private final PermissionResolver<T> defaultResolver;
    @Nullable
    private Text readableName;
    @Nullable
    private Text description;

    public PermissionNodeCompat(Identifier nodeName, PermissionType<T> type, PermissionResolver<T> defaultResolver) {
        this(nodeName.getNamespace(), nodeName.getPath(), type, defaultResolver);
    }

    public PermissionNodeCompat(String modID, String nodeName, PermissionType<T> type, PermissionResolver<T> defaultResolver) {
        this(modID + "." + nodeName, type, defaultResolver);
    }

    private PermissionNodeCompat(String nodeName, PermissionType<T> type, PermissionResolver<T> defaultResolver) {
        this.nodeName = nodeName;
        this.type = type;
        this.defaultResolver = defaultResolver;
    }

    public PermissionNodeCompat setInformation(@NotNull Text readableName, @NotNull Text description) {
        Preconditions.checkNotNull(readableName, (String)"Readable name for PermissionNodes must not be null %s", this.nodeName);
        Preconditions.checkNotNull(description, (String)"Description for PermissionNodes must not be null %s", this.nodeName);
        this.readableName = readableName;
        this.description = description;
        return this;
    }

    public String getNodeName() {
        return this.nodeName;
    }

    public PermissionType<T> getType() {
        return this.type;
    }

    public PermissionResolver<T> getDefaultResolver() {
        return this.defaultResolver;
    }

    @Nullable
    public Text getReadableName() {
        return this.readableName;
    }

    @Nullable
    public Text getDescription() {
        return this.description;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionNodeCompat)) {
            return false;
        }
        PermissionNodeCompat otherNode = (PermissionNodeCompat)o;
        return this.nodeName.equals(otherNode.nodeName) && this.type.equals(otherNode.type);
    }

    public int hashCode() {
        return Objects.hash(this.nodeName, this.type);
    }

    @FunctionalInterface
    public static interface PermissionResolver<T> {
        public T resolve(@Nullable ServerPlayerEntity var1, UUID var2, Object ... var3);
    }
}

