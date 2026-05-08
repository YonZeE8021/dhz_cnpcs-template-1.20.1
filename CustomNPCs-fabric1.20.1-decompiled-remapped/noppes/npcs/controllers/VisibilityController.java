/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;

public class VisibilityController {
    public static VisibilityController instance = new VisibilityController();
    private Map<Integer, EntityNPCInterface> trackedEntityHashTable = new TreeMap<Integer, EntityNPCInterface>();

    public void trackNpc(EntityNPCInterface npc) {
        if (npc.isClientSide()) {
            return;
        }
        boolean hasOptions = npc.display.availability.hasOptions();
        if ((hasOptions || npc.display.getVisible() != 0) && !this.trackedEntityHashTable.containsKey(npc.getId())) {
            this.trackedEntityHashTable.put(npc.getId(), npc);
        }
        if (!hasOptions && npc.display.getVisible() == 0 && this.trackedEntityHashTable.containsKey(npc.getId())) {
            this.trackedEntityHashTable.remove(npc.getId());
        }
    }

    public void remove(EntityNPCInterface npc) {
        if (npc.isClientSide()) {
            return;
        }
        this.trackedEntityHashTable.remove(npc.getId());
    }

    public void onUpdate(ServerPlayerEntity player) {
        if (!CustomNpcs.EnableInvisibleNpcs) {
            return;
        }
        for (Map.Entry<Integer, EntityNPCInterface> entry : this.trackedEntityHashTable.entrySet()) {
            VisibilityController.checkIsVisible(entry.getValue(), player);
        }
    }

    public static void checkIsVisible(EntityNPCInterface npc, ServerPlayerEntity playerMP) {
        if (!CustomNpcs.EnableInvisibleNpcs) {
            return;
        }
        if (npc.display.isVisibleTo(playerMP) || playerMP.isSpectator() || playerMP.getMainHandStack().getItem() == CustomItems.wand) {
            npc.setVisible(playerMP);
        } else {
            npc.setInvisible(playerMP);
        }
    }

    public static void addValue(HashMap<Integer, ArrayList<EntityNPCInterface>> map, int id, EntityNPCInterface npc) {
        ArrayList<EntityNPCInterface> npcs;
        if (!map.containsKey(id)) {
            map.put(id, new ArrayList());
        }
        if (!(npcs = map.get(id)).contains((Object)npc)) {
            npcs.add(npc);
            map.replace(id, npcs);
        }
    }
}

