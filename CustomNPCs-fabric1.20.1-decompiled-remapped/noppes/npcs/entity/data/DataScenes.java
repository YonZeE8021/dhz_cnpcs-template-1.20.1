/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.pathing.Path
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.Registries
 *  net.minecraft.server.MinecraftServer
 */
package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.AnimationType;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.shared.common.CommonUtil;
import noppes.npcs.util.ValueUtil;

public class DataScenes {
    private EntityNPCInterface npc;
    public List<SceneContainer> scenes = new ArrayList<SceneContainer>();
    public static Map<String, SceneState> StartedScenes = new HashMap<String, SceneState>();
    public static List<SceneContainer> ScenesToRun = new ArrayList<SceneContainer>();
    private LivingEntity owner = null;
    private String ownerScene = null;

    public DataScenes(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public NbtCompound save(NbtCompound compound) {
        NbtList list = new NbtList();
        for (SceneContainer scene : this.scenes) {
            list.add((Object)scene.save(new NbtCompound()));
        }
        compound.put("Scenes", (NbtElement)list);
        return compound;
    }

    public void load(NbtCompound compound) {
        NbtList list = compound.getList("Scenes", 10);
        ArrayList<SceneContainer> scenes = new ArrayList<SceneContainer>();
        for (int i = 0; i < list.size(); ++i) {
            SceneContainer scene = new SceneContainer();
            scene.load(list.getCompound(i));
            scenes.add(scene);
        }
        this.scenes = scenes;
    }

    public LivingEntity getOwner() {
        return this.owner;
    }

    public static void Toggle(MinecraftServer server, String id) {
        SceneState state = StartedScenes.get(id.toLowerCase());
        if (state == null || state.paused) {
            DataScenes.Start(server, id);
        } else {
            state.paused = true;
            CommonUtil.NotifyOPs(server, "Paused scene %s at %s", id, state.ticks);
        }
    }

    public static void Start(MinecraftServer server, String id) {
        SceneState state = StartedScenes.get(id.toLowerCase());
        if (state == null) {
            CommonUtil.NotifyOPs(server, "Started scene %s", id);
            StartedScenes.put(id.toLowerCase(), new SceneState());
        } else if (state.paused) {
            state.paused = false;
            CommonUtil.NotifyOPs(server, "Started scene %s from %s", id, state.ticks);
        }
    }

    public static void Pause(ServerCommandSource sender, String id) {
        if (id == null) {
            for (SceneState state : StartedScenes.values()) {
                state.paused = true;
            }
            CommonUtil.NotifyOPs(sender.getServer(), "Paused all scenes", new Object[0]);
        } else {
            SceneState state = StartedScenes.get(id.toLowerCase());
            if (state == null) {
                sender.sendFeedback(() -> Text.translatable((String)"Unknown scene %s ", (Object[])new Object[]{id}), false);
            } else {
                state.paused = true;
                CommonUtil.NotifyOPs(sender.getServer(), "Paused scene %s at %s", id, state.ticks);
            }
        }
    }

    public static void Reset(ServerCommandSource sender, String id) {
        if (id == null) {
            if (StartedScenes.isEmpty()) {
                return;
            }
            StartedScenes = new HashMap<String, SceneState>();
            CommonUtil.NotifyOPs(sender.getServer(), "Reset all scene", new Object[0]);
        } else if (StartedScenes.remove(id.toLowerCase()) == null) {
            sender.sendFeedback(() -> Text.translatable((String)"Unknown scene %s ", (Object[])new Object[]{id}), false);
        } else {
            CommonUtil.NotifyOPs(sender.getServer(), "Reset scene %s", id);
        }
    }

    public void update() {
        for (SceneContainer scene : this.scenes) {
            if (!scene.validState()) continue;
            ScenesToRun.add(scene);
        }
        if (this.owner != null && !StartedScenes.containsKey(this.ownerScene.toLowerCase())) {
            this.owner = null;
            this.ownerScene = null;
        }
    }

    public void addScene(String name) {
        if (name.isEmpty()) {
            return;
        }
        SceneContainer scene = new SceneContainer();
        scene.name = name;
        this.scenes.add(scene);
    }

    public class SceneContainer {
        public int btn = 0;
        public String name = "";
        public String lines = "";
        public boolean enabled = false;
        public int ticks = -1;
        private SceneState state = null;
        private List<SceneEvent> events = new ArrayList<SceneEvent>();

        public NbtCompound save(NbtCompound compound) {
            compound.putBoolean("Enabled", this.enabled);
            compound.putString("Name", this.name);
            compound.putString("Lines", this.lines);
            compound.putInt("Button", this.btn);
            compound.putInt("Ticks", this.ticks);
            return compound;
        }

        public boolean validState() {
            if (!this.enabled) {
                return false;
            }
            if (this.state != null) {
                if (StartedScenes.containsValue(this.state)) {
                    return !this.state.paused;
                }
                this.state = null;
            }
            this.state = StartedScenes.get(this.name.toLowerCase());
            if (this.state == null) {
                this.state = StartedScenes.get(this.btn + "btn");
            }
            if (this.state != null) {
                return !this.state.paused;
            }
            return false;
        }

        public void load(NbtCompound compound) {
            this.enabled = compound.getBoolean("Enabled");
            this.name = compound.getString("Name");
            this.lines = compound.getString("Lines");
            this.btn = compound.getInt("Button");
            this.ticks = compound.getInt("Ticks");
            ArrayList<SceneEvent> events = new ArrayList<SceneEvent>();
            for (String line : this.lines.split("\r\n|\r|\n")) {
                SceneEvent event = SceneEvent.parse(line);
                if (event == null) continue;
                events.add(event);
            }
            Collections.sort(events);
            this.events = events;
        }

        public void update() {
            if (!this.enabled || this.events.isEmpty() || this.state == null) {
                return;
            }
            for (SceneEvent event : this.events) {
                if (event.ticks > this.state.ticks) break;
                if (event.ticks != this.state.ticks) continue;
                try {
                    this.handle(event);
                }
                catch (Exception exception) {}
            }
            this.ticks = this.state.ticks;
        }

        private LivingEntity getEntity(String name) {
            UUID uuid = null;
            try {
                uuid = UUID.fromString(name);
            }
            catch (Exception exception) {
                // empty catch block
            }
            for (Entity entity : DataScenes.this.npc.getEntityWorld().getEntityLookup().iterate()) {
                if (!(entity instanceof LivingEntity)) continue;
                if (uuid != null && entity.getUuid() == uuid) {
                    return (LivingEntity)entity;
                }
                if (!name.equalsIgnoreCase(entity.getName().getString())) continue;
                return (LivingEntity)entity;
            }
            return null;
        }

        private BlockPos parseBlockPos(BlockPos blockpos, String[] args, int startIndex, boolean centerBlock) throws Exception {
            return new BlockPos((int)this.parseDouble(blockpos.getX(), args[startIndex], -30000000, 30000000, centerBlock), (int)this.parseDouble(blockpos.getY(), args[startIndex + 1], -64, 319, false), (int)this.parseDouble(blockpos.getZ(), args[startIndex + 2], -30000000, 30000000, centerBlock));
        }

        private double parseDouble(double base, String input, int min, int max, boolean centerBlock) throws Exception {
            double d0;
            boolean flag = input.startsWith("~");
            if (flag && Double.isNaN(base)) {
                throw new Exception("invalid number");
            }
            double d = d0 = flag ? base : 0.0;
            if (!flag || input.length() > 1) {
                boolean flag1 = input.contains(".");
                if (flag) {
                    input = input.substring(1);
                }
                d0 += Double.parseDouble(input);
                if (!flag1 && !flag && centerBlock) {
                    d0 += 0.5;
                }
            }
            if (min != 0 || max != 0) {
                if (d0 < (double)min) {
                    throw new Exception("number too small");
                }
                if (d0 > (double)max) {
                    throw new Exception("number too big");
                }
            }
            return d0;
        }

        private void handle(SceneEvent event) throws Exception {
            block64: {
                if (event.type == SceneType.MOVE) {
                    String[] param = event.param.split(" ");
                    while (param.length > 1) {
                        boolean move = false;
                        if (param[0].startsWith("to")) {
                            move = true;
                        } else if (!param[0].startsWith("tp")) break;
                        BlockPos pos = null;
                        if (param[0].startsWith("@")) {
                            LivingEntity entitylivingbase = this.getEntity(param[0]);
                            if (entitylivingbase != null) {
                                pos = entitylivingbase.getBlockPos();
                            }
                            param = Arrays.copyOfRange(param, 2, param.length);
                        } else {
                            if (param.length < 4) {
                                return;
                            }
                            pos = this.parseBlockPos(DataScenes.this.npc.getBlockPos(), param, 1, false);
                            param = Arrays.copyOfRange(param, 4, param.length);
                        }
                        if (pos == null) continue;
                        DataScenes.this.npc.ais.setStartPos(pos);
                        DataScenes.this.npc.getNavigation().stop();
                        if (move) {
                            Path pathentity = DataScenes.this.npc.getNavigation().findPathTo(pos, 0);
                            DataScenes.this.npc.getNavigation().startMovingAlong(pathentity, 1.0);
                            continue;
                        }
                        if (DataScenes.this.npc.isInRange((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, 2.0)) continue;
                        DataScenes.this.npc.setPosition((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5);
                    }
                } else if (event.type == SceneType.SAY) {
                    DataScenes.this.npc.saySurrounding(new Line(event.param));
                } else if (event.type == SceneType.ROTATE) {
                    if (event.param.startsWith("@")) {
                        LivingEntity entitylivingbase = this.getEntity(event.param);
                        DataScenes.this.npc.lookAi.rotate((Entity)DataScenes.this.npc.getWorld().getClosestPlayer((Entity)entitylivingbase, 30.0));
                    } else if (event.param.equals("clear")) {
                        DataScenes.this.npc.lookAi.stop();
                    } else {
                        DataScenes.this.npc.lookAi.rotate(Integer.parseInt(event.param));
                    }
                } else if (event.type == SceneType.EQUIP) {
                    String[] args = event.param.split(" ");
                    if (args.length < 2) {
                        return;
                    }
                    IItemStack itemstack = null;
                    if (!args[1].equalsIgnoreCase("none")) {
                        Identifier resourcelocation = new Identifier(args[1]);
                        Item item = (Item)Registries.ITEM.get(resourcelocation);
                        int i = args.length >= 3 ? ValueUtil.CorrectInt(Integer.parseInt(args[2]), 1, 64) : 1;
                        itemstack = NpcAPI.Instance().getIItemStack(new ItemStack((ItemConvertible)item, i));
                    }
                    if (args[0].equalsIgnoreCase("main")) {
                        DataScenes.this.npc.inventory.weapons.put(0, itemstack);
                    } else if (args[0].equalsIgnoreCase("off")) {
                        DataScenes.this.npc.inventory.weapons.put(2, itemstack);
                    } else if (args[0].equalsIgnoreCase("proj")) {
                        DataScenes.this.npc.inventory.weapons.put(1, itemstack);
                    } else if (args[0].equalsIgnoreCase("head")) {
                        DataScenes.this.npc.inventory.armor.put(0, itemstack);
                    } else if (args[0].equalsIgnoreCase("body")) {
                        DataScenes.this.npc.inventory.armor.put(1, itemstack);
                    } else if (args[0].equalsIgnoreCase("legs")) {
                        DataScenes.this.npc.inventory.armor.put(2, itemstack);
                    } else if (args[0].equalsIgnoreCase("boots")) {
                        DataScenes.this.npc.inventory.armor.put(3, itemstack);
                    }
                } else if (event.type == SceneType.ATTACK) {
                    if (event.param.equals("none")) {
                        DataScenes.this.npc.setTarget(null);
                    } else {
                        LivingEntity entity = this.getEntity(event.param);
                        if (entity != null) {
                            DataScenes.this.npc.setTarget(entity);
                        }
                    }
                } else if (event.type == SceneType.THROW) {
                    String[] args = event.param.split(" ");
                    LivingEntity entity = this.getEntity(args[0]);
                    if (entity == null) {
                        return;
                    }
                    float damage = Float.parseFloat(args[1]);
                    if (damage <= 0.0f) {
                        damage = 0.01f;
                    }
                    ItemStack stack = ItemStackWrapper.MCItem(DataScenes.this.npc.inventory.getProjectile());
                    if (args.length > 2) {
                        Identifier resourcelocation = new Identifier(args[2]);
                        Item item = (Item)Registries.ITEM.get(resourcelocation);
                        stack = new ItemStack((ItemConvertible)item, 1);
                    }
                    EntityProjectile projectile = DataScenes.this.npc.shoot(entity, 100, stack, false);
                    projectile.damage = damage;
                } else if (event.type == SceneType.ANIMATE) {
                    DataScenes.this.npc.animateAi.temp = AnimationType.valueOf(event.param);
                } else if (event.type == SceneType.COMMAND) {
                    NoppesUtilServer.runCommand((Entity)DataScenes.this.npc, DataScenes.this.npc.getName().getString(), event.param, null);
                } else if (event.type == SceneType.STATS) {
                    int i = event.param.indexOf(" ");
                    if (i <= 0) {
                        return;
                    }
                    String type = event.param.substring(0, i).toLowerCase();
                    String value = event.param.substring(i).trim();
                    try {
                        if (type.equals("walking_speed")) {
                            DataScenes.this.npc.ais.setWalkingSpeed(ValueUtil.CorrectInt(Integer.parseInt(value), 0, 10));
                            break block64;
                        }
                        if (type.equals("size")) {
                            DataScenes.this.npc.display.setSize(ValueUtil.CorrectInt(Integer.parseInt(value), 1, 30));
                            break block64;
                        }
                        CommonUtil.NotifyOPs(DataScenes.this.npc.getWorld().getServer(), "Unknown scene stat: " + type, new Object[0]);
                    }
                    catch (NumberFormatException e) {
                        CommonUtil.NotifyOPs(DataScenes.this.npc.getWorld().getServer(), "Unknown scene stat " + type + " value: " + value, new Object[0]);
                    }
                } else if (event.type == SceneType.FACTION) {
                    DataScenes.this.npc.setFaction(Integer.parseInt(event.param));
                } else if (event.type == SceneType.FOLLOW) {
                    if (event.param.equalsIgnoreCase("none")) {
                        DataScenes.this.owner = null;
                        DataScenes.this.ownerScene = null;
                    } else {
                        LivingEntity entity = this.getEntity(event.param);
                        if (entity == null) {
                            return;
                        }
                        DataScenes.this.owner = entity;
                        DataScenes.this.ownerScene = this.name;
                    }
                }
            }
        }
    }

    public static class SceneState {
        public boolean paused = false;
        public int ticks = -1;
    }

    public static enum SceneType {
        ANIMATE,
        MOVE,
        FACTION,
        COMMAND,
        EQUIP,
        THROW,
        ATTACK,
        FOLLOW,
        SAY,
        ROTATE,
        STATS;

    }

    public static class SceneEvent
    implements Comparable<SceneEvent> {
        public int ticks = 0;
        public SceneType type;
        public String param = "";

        public String toString() {
            return this.ticks + " " + this.type.name() + " " + this.param;
        }

        public static SceneEvent parse(String str) {
            SceneEvent event = new SceneEvent();
            int i = str.indexOf(" ");
            if (i <= 0) {
                return null;
            }
            try {
                event.ticks = Integer.parseInt(str.substring(0, i));
                str = str.substring(i + 1);
            }
            catch (NumberFormatException ex) {
                return null;
            }
            i = str.indexOf(" ");
            if (i <= 0) {
                return null;
            }
            String name = str.substring(0, i);
            for (SceneType type : SceneType.values()) {
                if (!name.equalsIgnoreCase(type.name())) continue;
                event.type = type;
            }
            if (event.type == null) {
                return null;
            }
            event.param = str.substring(i + 1);
            return event;
        }

        @Override
        public int compareTo(SceneEvent o) {
            return this.ticks - o.ticks;
        }
    }
}

