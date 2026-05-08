/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.event.Event;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;

public class PlayerScriptData
implements IScriptHandler {
    private List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
    private String scriptLanguage = "ECMAScript";
    private PlayerEntity player;
    private IPlayer playerAPI;
    private long lastPlayerUpdate = 0L;
    public long lastInited = -1L;
    public boolean hadInteract = true;
    private boolean enabled = false;
    private static Map<Long, String> console = new TreeMap<Long, String>();
    private static List<Integer> errored = new ArrayList<Integer>();

    public PlayerScriptData(PlayerEntity player) {
        this.player = player;
    }

    public void clear() {
        console = new TreeMap<Long, String>();
        errored = new ArrayList<Integer>();
        this.scripts = new ArrayList<ScriptContainer>();
    }

    public void load(NbtCompound compound) {
        this.scripts = NBTTags.GetScript(compound.getList("Scripts", 10), this);
        this.scriptLanguage = compound.getString("ScriptLanguage");
        this.enabled = compound.getBoolean("ScriptEnabled");
        console = NBTTags.GetLongStringMap(compound.getList("ScriptConsole", 10));
    }

    public NbtCompound save(NbtCompound compound) {
        compound.put("Scripts", (NbtElement)NBTTags.NBTScript(this.scripts));
        compound.putString("ScriptLanguage", this.scriptLanguage);
        compound.putBoolean("ScriptEnabled", this.enabled);
        compound.put("ScriptConsole", (NbtElement)NBTTags.NBTLongStringMap(console));
        return compound;
    }

    @Override
    public void runScript(EnumScriptType type, Event event) {
        if (!this.isEnabled()) {
            return;
        }
        if (ScriptController.Instance.lastLoaded > this.lastInited || ScriptController.Instance.lastPlayerUpdate > this.lastPlayerUpdate) {
            this.lastInited = ScriptController.Instance.lastLoaded;
            errored.clear();
            if (this.player != null) {
                this.scripts.clear();
                for (ScriptContainer script : ScriptController.Instance.playerScripts.scripts) {
                    ScriptContainer s = new ScriptContainer(this);
                    s.load(script.save(new NbtCompound()));
                    this.scripts.add(s);
                }
            }
            this.lastPlayerUpdate = ScriptController.Instance.lastPlayerUpdate;
            if (type != EnumScriptType.INIT) {
                EventHooks.onPlayerInit(this);
            }
        }
        for (int i = 0; i < this.scripts.size(); ++i) {
            ScriptContainer script;
            script = this.scripts.get(i);
            if (errored.contains(i)) continue;
            script.run(type, event);
            if (script.errored) {
                errored.add(i);
            }
            for (Map.Entry<Long, String> entry : script.console.entrySet()) {
                if (console.containsKey(entry.getKey())) continue;
                console.put(entry.getKey(), " tab " + (i + 1) + ":\n" + entry.getValue());
            }
            script.console.clear();
        }
    }

    public boolean isEnabled() {
        return ScriptController.Instance.playerScripts.enabled && ScriptController.HasStart && (this.player == null || !this.player.getWorld().isClient);
    }

    @Override
    public boolean isClient() {
        return this.player.getWorld().isClient();
    }

    @Override
    public boolean getEnabled() {
        return ScriptController.Instance.playerScripts.enabled;
    }

    @Override
    public void setEnabled(boolean bo) {
        this.enabled = bo;
    }

    @Override
    public String getLanguage() {
        return ScriptController.Instance.playerScripts.scriptLanguage;
    }

    @Override
    public void setLanguage(String lang) {
        this.scriptLanguage = lang;
    }

    @Override
    public List<ScriptContainer> getScripts() {
        return this.scripts;
    }

    @Override
    public String noticeString() {
        if (this.player == null) {
            return "Global script";
        }
        BlockPos pos = this.player.getBlockPos();
        return MoreObjects.toStringHelper(this.player).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
    }

    public IPlayer getPlayer() {
        if (this.playerAPI == null) {
            this.playerAPI = (IPlayer)NpcAPI.Instance().getIEntity((Entity)this.player);
        }
        return this.playerAPI;
    }

    @Override
    public Map<Long, String> getConsoleText() {
        return console;
    }

    @Override
    public void clearConsole() {
        console.clear();
    }
}

