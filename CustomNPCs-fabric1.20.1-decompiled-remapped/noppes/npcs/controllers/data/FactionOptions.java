/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 */
package noppes.npcs.controllers.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerFactionData;

public class FactionOptions {
    public boolean decreaseFactionPoints = false;
    public boolean decreaseFaction2Points = false;
    public int factionId = -1;
    public int faction2Id = -1;
    public int factionPoints = 100;
    public int faction2Points = 100;

    public void load(NbtCompound compound) {
        this.factionId = compound.getInt("OptionFactions1");
        this.faction2Id = compound.getInt("OptionFactions2");
        this.decreaseFactionPoints = compound.getBoolean("DecreaseFaction1Points");
        this.decreaseFaction2Points = compound.getBoolean("DecreaseFaction2Points");
        this.factionPoints = compound.getInt("OptionFaction1Points");
        this.faction2Points = compound.getInt("OptionFaction2Points");
    }

    public NbtCompound save(NbtCompound par1CompoundTag) {
        par1CompoundTag.putInt("OptionFactions1", this.factionId);
        par1CompoundTag.putInt("OptionFactions2", this.faction2Id);
        par1CompoundTag.putInt("OptionFaction1Points", this.factionPoints);
        par1CompoundTag.putInt("OptionFaction2Points", this.faction2Points);
        par1CompoundTag.putBoolean("DecreaseFaction1Points", this.decreaseFactionPoints);
        par1CompoundTag.putBoolean("DecreaseFaction2Points", this.decreaseFaction2Points);
        return par1CompoundTag;
    }

    public boolean hasFaction(int id) {
        return this.factionId == id || this.faction2Id == id;
    }

    public void addPoints(PlayerEntity player) {
        if (this.factionId < 0 && this.faction2Id < 0) {
            return;
        }
        PlayerData playerdata = PlayerData.get(player);
        PlayerFactionData data = playerdata.factionData;
        if (this.factionId >= 0 && this.factionPoints > 0) {
            this.addPoints(player, data, this.factionId, this.decreaseFactionPoints, this.factionPoints);
        }
        if (this.faction2Id >= 0 && this.faction2Points > 0) {
            this.addPoints(player, data, this.faction2Id, this.decreaseFaction2Points, this.faction2Points);
        }
        playerdata.updateClient = true;
    }

    private void addPoints(PlayerEntity player, PlayerFactionData data, int factionId, boolean decrease, int points) {
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null) {
            return;
        }
        if (!faction.hideFaction) {
            String message = decrease ? "faction.decreasepoints" : "faction.increasepoints";
            player.sendMessage((Text)Text.translatable((String)message, (Object[])new Object[]{faction.name, points}));
        }
        data.increasePoints(player, factionId, decrease ? -points : points);
    }
}

