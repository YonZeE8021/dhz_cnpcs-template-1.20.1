/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.block.BlockState
 */
package noppes.npcs.blocks.tiles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.block.BlockState;
import noppes.npcs.CustomBlocks;
import noppes.npcs.blocks.tiles.TileNpcEntity;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.quests.QuestLocation;

public class TileWaypoint
extends TileNpcEntity {
    public String name = "";
    private int ticks = 10;
    private List<PlayerEntity> recentlyChecked = new ArrayList<PlayerEntity>();
    private List<PlayerEntity> toCheck;
    public int range = 10;

    public TileWaypoint(BlockPos pos, BlockState state) {
        super(CustomBlocks.tile_waypoint, pos, state);
    }

    public static void tick(World level, BlockPos pos, BlockState state, TileWaypoint tile) {
        if (level.isClient || tile.name.isEmpty()) {
            return;
        }
        --tile.ticks;
        if (tile.ticks > 0) {
            return;
        }
        tile.ticks = 10;
        tile.toCheck = tile.getPlayerList(tile.range, tile.range, tile.range);
        tile.toCheck.removeAll(tile.recentlyChecked);
        List<PlayerEntity> listMax = tile.getPlayerList(tile.range + 10, tile.range + 10, tile.range + 10);
        tile.recentlyChecked.retainAll(listMax);
        tile.recentlyChecked.addAll(tile.toCheck);
        if (tile.toCheck.isEmpty()) {
            return;
        }
        for (PlayerEntity player : tile.toCheck) {
            PlayerData pdata = PlayerData.get(player);
            PlayerQuestData playerdata = pdata.questData;
            for (QuestData data : playerdata.activeQuests.values()) {
                QuestLocation quest;
                if (data.quest.type != 3 || !(quest = (QuestLocation)data.quest.questInterface).setFound(data, tile.name)) continue;
                player.sendMessage((Text)Text.translatable((String)tile.name).append(" ").append((Text)Text.translatable((String)"quest.found")));
                playerdata.checkQuestCompletion(player, 3);
                pdata.updateClient = true;
            }
        }
    }

    private List<PlayerEntity> getPlayerList(int x, int y, int z) {
        return this.world.getNonSpectatingEntities(PlayerEntity.class, new Box(this.pos, this.pos.add(1, 1, 1)).expand((double)x, (double)y, (double)z));
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.name = compound.getString("LocationName");
        this.range = compound.getInt("LocationRange");
        if (this.range < 2) {
            this.range = 2;
        }
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        if (!this.name.isEmpty()) {
            compound.putString("LocationName", this.name);
        }
        compound.putInt("LocationRange", this.range);
        super.writeNbt(compound);
    }
}

