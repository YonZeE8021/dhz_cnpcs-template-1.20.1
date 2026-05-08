/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.entity.data.IEntityPersistentData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketMarkData;

public class MarkData {
    private static final String NBTKEY = "cnpcmarkdata";
    private LivingEntity entity;
    public List<Mark> marks = new ArrayList<Mark>();
    private static Map<Integer, MarkData> dataMap = new HashMap<Integer, MarkData>();

    public void setNBT(NbtCompound compound) {
        ArrayList<Mark> marks = new ArrayList<Mark>();
        NbtList list = compound.getList("marks", 10);
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound c = list.getCompound(i);
            Mark m = new Mark();
            m.type = c.getInt("type");
            m.color = c.getInt("color");
            m.availability.load(c.getCompound("availability"));
            marks.add(m);
        }
        this.marks = marks;
    }

    public NbtCompound getNBT() {
        NbtCompound compound = new NbtCompound();
        NbtList list = new NbtList();
        for (Mark m : this.marks) {
            NbtCompound c = new NbtCompound();
            c.putInt("type", m.type);
            c.putInt("color", m.color);
            c.put("availability", (NbtElement)m.availability.save(new NbtCompound()));
            list.add(c);
        }
        compound.put("marks", (NbtElement)list);
        return compound;
    }

    public void save() {
        ((IEntityPersistentData)this.entity).getPersistentData().put(NBTKEY, (NbtElement)this.getNBT());
    }

    public IMark addMark(int type) {
        Mark m = new Mark();
        m.type = type;
        this.marks.add(m);
        if (!this.entity.getWorld().isClient) {
            this.syncClients();
        }
        return m;
    }

    public IMark addMark(int type, int color) {
        Mark m = new Mark();
        m.type = type;
        m.color = color;
        this.marks.add(m);
        if (!this.entity.getWorld().isClient) {
            this.syncClients();
        }
        return m;
    }

    public static MarkData get(LivingEntity entity) {
        MarkData data = dataMap.computeIfAbsent(entity.getId(), i -> new MarkData());
        if (data.entity == null) {
            data.entity = entity;
            data.setNBT(((IEntityPersistentData)entity).getPersistentData().getCompound(NBTKEY));
        }
        return data;
    }

    public void syncClients() {
        Packets.sendAll(new PacketMarkData(this.entity.getId(), this.getNBT()));
    }

    public class Mark
    implements IMark {
        public int type = 0;
        public Availability availability = new Availability();
        public int color = 16772433;

        @Override
        public IAvailability getAvailability() {
            return this.availability;
        }

        @Override
        public int getColor() {
            return this.color;
        }

        @Override
        public void setColor(int color) {
            this.color = color;
        }

        @Override
        public int getType() {
            return this.type;
        }

        @Override
        public void setType(int type) {
            this.type = type;
        }

        @Override
        public void update() {
            MarkData.this.syncClients();
        }
    }
}

