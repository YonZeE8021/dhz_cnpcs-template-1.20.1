/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.dimension.DimensionType
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.api.wrapper;

import net.minecraft.world.dimension.DimensionType;
import net.minecraft.util.Identifier;
import noppes.npcs.api.IDimension;

public class DimensionWrapper
implements IDimension {
    private Identifier id;
    private DimensionType type;

    public DimensionWrapper(Identifier id, DimensionType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public String getId() {
        return this.id.toString();
    }
}

