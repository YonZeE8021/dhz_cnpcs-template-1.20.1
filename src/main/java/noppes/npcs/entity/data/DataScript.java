/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.entity.data;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.EventHooks;
import noppes.npcs.NBTTags;
import noppes.npcs.api.event.Event;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.EntityNPCInterface;

public class DataScript
implements IScriptHandler {
    private List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
    private String scriptLanguage = "ECMAScript";
    private EntityNPCInterface npc;
    private boolean enabled = false;
    public long lastInited = -1L;

    public DataScript(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public void load(NbtCompound compound) {
        this.scripts = NBTTags.GetScript(compound.getList("Scripts", 10), this);
        this.scriptLanguage = compound.getString("ScriptLanguage");
        this.enabled = compound.getBoolean("ScriptEnabled");
    }

    public NbtCompound save(NbtCompound compound) {
        compound.put("Scripts", (NbtElement)NBTTags.NBTScript(this.scripts));
        compound.putString("ScriptLanguage", this.scriptLanguage);
        compound.putBoolean("ScriptEnabled", this.enabled);
        return compound;
    }

    @Override
    public void runScript(EnumScriptType type, Event event) {
        if (!this.isEnabled()) {
            return;
        }
        if (ScriptController.Instance.lastLoaded > this.lastInited) {
            this.lastInited = ScriptController.Instance.lastLoaded;
            if (type != EnumScriptType.INIT) {
                EventHooks.onNPCInit(this.npc);
            }
        }
        for (ScriptContainer script : this.scripts) {
            script.run(type, event);
        }
    }

    public boolean isEnabled() {
        return this.enabled && ScriptController.HasStart && !this.npc.getWorld().isClient;
    }

    @Override
    public boolean isClient() {
        return this.npc.isClientSide();
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean bo) {
        this.enabled = bo;
    }

    @Override
    public String getLanguage() {
        return this.scriptLanguage;
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
        BlockPos pos = this.npc.getBlockPos();
        return MoreObjects.toStringHelper((this.npc)).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).toString();
    }

    @Override
    public Map<Long, String> getConsoleText() {
        TreeMap<Long, String> map = new TreeMap<Long, String>();
        int tab = 0;
        for (ScriptContainer script : this.getScripts()) {
            ++tab;
            for (Map.Entry<Long, String> entry : script.console.entrySet()) {
                map.put(entry.getKey(), " tab " + tab + ":\n" + entry.getValue());
            }
        }
        return map;
    }

    @Override
    public void clearConsole() {
        for (ScriptContainer script : this.getScripts()) {
            script.console.clear();
        }
    }
}

