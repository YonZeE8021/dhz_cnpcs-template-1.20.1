/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 */
package noppes.npcs.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;

public class MassBlockController {
    private static Queue<IMassBlock> queue;
    private static MassBlockController Instance;

    public MassBlockController() {
        queue = new LinkedList<IMassBlock>();
        Instance = this;
    }

    public static void Update() {
        if (queue.isEmpty()) {
            return;
        }
        IMassBlock imb = queue.remove();
        World level = imb.getNpc().getWorld();
        BlockPos pos = imb.getNpc().getBlockPos();
        int range = imb.getRange();
        ArrayList<BlockData> list = new ArrayList<BlockData>();
        for (int x = -range; x < range; ++x) {
            for (int z = -range; z < range; ++z) {
                if (!level.canSetBlock(new BlockPos(x + pos.getX(), 64, z + pos.getZ()))) continue;
                for (int y = 0; y < range; ++y) {
                    BlockPos blockPos = pos.add(x, y - range / 2, z);
                    list.add(new BlockData(blockPos, level.getBlockState(blockPos), null));
                }
            }
        }
        imb.processed(list);
    }

    public static void Queue(IMassBlock imb) {
        queue.add(imb);
    }

    public static interface IMassBlock {
        public EntityNPCInterface getNpc();

        public int getRange();

        public void processed(List<BlockData> var1);
    }
}

