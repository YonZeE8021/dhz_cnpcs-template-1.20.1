/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtDouble
 *  net.minecraft.nbt.NbtFloat
 *  net.minecraft.nbt.NbtIntArray
 *  net.minecraft.nbt.NbtInt
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.api.wrapper;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.util.NBTJsonUtil;

public class NBTWrapper
implements INbt {
    private NbtCompound compound;

    public NBTWrapper(NbtCompound compound) {
        this.compound = compound;
    }

    @Override
    public void remove(String key) {
        this.compound.remove(key);
    }

    @Override
    public boolean has(String key) {
        return this.compound.contains(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return this.compound.getBoolean(key);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        this.compound.putBoolean(key, value);
    }

    @Override
    public short getShort(String key) {
        return this.compound.getShort(key);
    }

    @Override
    public void setShort(String key, short value) {
        this.compound.putShort(key, value);
    }

    @Override
    public int getInteger(String key) {
        return this.compound.getInt(key);
    }

    @Override
    public void setInteger(String key, int value) {
        this.compound.putInt(key, value);
    }

    @Override
    public byte getByte(String key) {
        return this.compound.getByte(key);
    }

    @Override
    public void setByte(String key, byte value) {
        this.compound.putByte(key, value);
    }

    @Override
    public long getLong(String key) {
        return this.compound.getLong(key);
    }

    @Override
    public void setLong(String key, long value) {
        this.compound.putLong(key, value);
    }

    @Override
    public double getDouble(String key) {
        return this.compound.getDouble(key);
    }

    @Override
    public void setDouble(String key, double value) {
        this.compound.putDouble(key, value);
    }

    @Override
    public float getFloat(String key) {
        return this.compound.getFloat(key);
    }

    @Override
    public void setFloat(String key, float value) {
        this.compound.putFloat(key, value);
    }

    @Override
    public String getString(String key) {
        return this.compound.getString(key);
    }

    @Override
    public void putString(String key, String value) {
        this.compound.putString(key, value);
    }

    @Override
    public byte[] getByteArray(String key) {
        return this.compound.getByteArray(key);
    }

    @Override
    public void setByteArray(String key, byte[] value) {
        this.compound.putByteArray(key, value);
    }

    @Override
    public int[] getIntegerArray(String key) {
        return this.compound.getIntArray(key);
    }

    @Override
    public void setIntegerArray(String key, int[] value) {
        this.compound.putIntArray(key, value);
    }

    @Override
    public Object[] getList(String key, int type) {
        NbtList list = this.compound.getList(key, type);
        Object[] nbts = new Object[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            if (list.getHeldType() == 10) {
                nbts[i] = NpcAPI.Instance().getINbt(list.getCompound(i));
                continue;
            }
            if (list.getHeldType() == 8) {
                nbts[i] = list.getString(i);
                continue;
            }
            if (list.getHeldType() == 6) {
                nbts[i] = list.getDouble(i);
                continue;
            }
            if (list.getHeldType() == 5) {
                nbts[i] = Float.valueOf(list.getFloat(i));
                continue;
            }
            if (list.getHeldType() == 3) {
                nbts[i] = list.getInt(i);
                continue;
            }
            if (list.getHeldType() != 11) continue;
            nbts[i] = list.getIntArray(i);
        }
        return nbts;
    }

    @Override
    public int getListType(String key) {
        NbtElement b = this.compound.get(key);
        if (b == null) {
            return 0;
        }
        if (b.getType() != 9) {
            throw new CustomNPCsException("NBT tag " + key + " isn't a list", new Object[0]);
        }
        return ((NbtList)b).getHeldType();
    }

    @Override
    public void setList(String key, Object[] value) {
        NbtList list = new NbtList();
        for (Object nbt : value) {
            if (nbt instanceof INbt) {
                list.add((Object)((INbt)nbt).getMCNBT());
                continue;
            }
            if (nbt instanceof String) {
                list.add((Object)NbtString.of((String)((String)nbt)));
                continue;
            }
            if (nbt instanceof Double) {
                list.add((Object)NbtDouble.of((double)((Double)nbt)));
                continue;
            }
            if (nbt instanceof Float) {
                list.add((Object)NbtFloat.of((float)((Float)nbt).floatValue()));
                continue;
            }
            if (nbt instanceof Integer) {
                list.add((Object)NbtInt.of((int)((Integer)nbt)));
                continue;
            }
            if (!(nbt instanceof int[])) continue;
            list.add((Object)new NbtIntArray((int[])nbt));
        }
        this.compound.put(key, (NbtElement)list);
    }

    @Override
    public INbt getCompound(String key) {
        return NpcAPI.Instance().getINbt(this.compound.getCompound(key));
    }

    @Override
    public void setCompound(String key, INbt value) {
        if (value == null) {
            throw new CustomNPCsException("Value cant be null", new Object[0]);
        }
        this.compound.put(key, (NbtElement)value.getMCNBT());
    }

    @Override
    public String[] getKeys() {
        return this.compound.getKeys().toArray(new String[this.compound.getKeys().size()]);
    }

    @Override
    public int getType(String key) {
        return this.compound.get(key).getType();
    }

    @Override
    public NbtCompound getMCNBT() {
        return this.compound;
    }

    @Override
    public String toJsonString() {
        return NBTJsonUtil.Convert(this.compound);
    }

    @Override
    public boolean isEqual(INbt nbt) {
        if (nbt == null) {
            return false;
        }
        return this.compound.equals((Object)nbt.getMCNBT());
    }

    @Override
    public void clear() {
        for (String name : this.compound.getKeys()) {
            this.compound.remove(name);
        }
    }

    @Override
    public boolean isEmpty() {
        return this.compound.isEmpty();
    }

    @Override
    public void merge(INbt nbt) {
        this.compound.copyFrom(nbt.getMCNBT());
    }

    @Override
    public void mcSetTag(String key, NbtElement base) {
        this.compound.put(key, base);
    }

    @Override
    public NbtElement mcGetTag(String key) {
        return this.compound.get(key);
    }
}

