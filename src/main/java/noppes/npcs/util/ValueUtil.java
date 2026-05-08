/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  net.minecraft.nbt.NbtByteArray
 *  net.minecraft.nbt.NbtByte
 *  net.minecraft.nbt.AbstractNbtList
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtDouble
 *  net.minecraft.nbt.NbtFloat
 *  net.minecraft.nbt.NbtIntArray
 *  net.minecraft.nbt.NbtInt
 *  net.minecraft.nbt.NbtLongArray
 *  net.minecraft.nbt.NbtLong
 *  net.minecraft.nbt.AbstractNbtNumber
 *  net.minecraft.nbt.NbtShort
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.util.Identifier
 *  net.minecraft.nbt.NbtType
 */
package noppes.npcs.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.NbtType;

public class ValueUtil {
    public static final UUID EMPTY_UUID = new UUID(0L, 0L);

    public static float correctFloat(float given, float min, float max) {
        if (given < min) {
            return min;
        }
        if (given > max) {
            return max;
        }
        return given;
    }

    public static int CorrectInt(int given, int min, int max) {
        if (given < min) {
            return min;
        }
        if (given > max) {
            return max;
        }
        return given;
    }

    public static String nbtToJson(NbtCompound nbt) {
        return new Gson().toJson(ValueUtil.getJsonValue((NbtElement)nbt));
    }

    private static JsonElement getJsonValue(NbtElement value) {
        if (value.getNbtType() == NbtCompound.TYPE) {
            NbtCompound nbt = (NbtCompound)value;
            JsonObject root = new JsonObject();
            for (String key : nbt.getKeys()) {
                NbtElement n = nbt.get(key);
                JsonElement ele = ValueUtil.getJsonValue(n);
                if (ele == null) continue;
                JsonObject ob = new JsonObject();
                ob.addProperty("type", n.getNbtType().getCrashReportName());
                ob.addProperty("type_id", (Number)n.getType());
                ob.addProperty("pretty_type", n.getNbtType().getCommandFeedbackName());
                ob.add("value", ele);
                root.add(key, (JsonElement)ob);
            }
            return root;
        }
        if (value == NbtString.TYPE) {
            return new JsonPrimitive(value.asString());
        }
        if (value instanceof AbstractNbtNumber) {
            return new JsonPrimitive(((AbstractNbtNumber)value).numberValue());
        }
        if (value instanceof AbstractNbtList) {
            JsonArray jsonValue = new JsonArray();
            for (NbtElement n : (AbstractNbtList<NbtElement>)(AbstractNbtList<?>)value) {
                jsonValue.add(ValueUtil.getJsonValue(n));
            }
            return jsonValue;
        }
        return null;
    }

    public static NbtCompound jsonToNbt(String json) {
        JsonObject ob = (JsonObject)new Gson().fromJson(json, JsonObject.class);
        return ValueUtil.toNbt(ob);
    }

    private static NbtCompound toNbt(JsonObject json) {
        NbtCompound nbt = new NbtCompound();
        for (Map.Entry entry : json.entrySet()) {
            JsonArray array;
            String key = (String)entry.getKey();
            JsonObject ele = (JsonObject)entry.getValue();
            NbtType<? extends NbtElement> type = ValueUtil.stringToType(ele.get("type").getAsString());
            if (type == NbtString.TYPE) {
                nbt.putString(key, ele.get("value").getAsString());
            }
            if (type == NbtInt.TYPE) {
                nbt.putInt(key, ele.get("value").getAsInt());
            }
            if (type == NbtByte.TYPE) {
                nbt.putByte(key, ele.get("value").getAsByte());
            }
            if (type == NbtLong.TYPE) {
                nbt.putLong(key, ele.get("value").getAsLong());
            }
            if (type == NbtFloat.TYPE) {
                nbt.putFloat(key, ele.get("value").getAsFloat());
            }
            if (type == NbtDouble.TYPE) {
                nbt.putDouble(key, ele.get("value").getAsDouble());
            }
            if (type == NbtShort.TYPE) {
                nbt.putShort(key, ele.get("value").getAsShort());
            }
            if (type == NbtCompound.TYPE) {
                nbt.put(key, (NbtElement)ValueUtil.toNbt((JsonObject)ele.get("value")));
            }
            if (type == NbtIntArray.TYPE) {
                array = (JsonArray)ele.get("value");
                nbt.put(key, (NbtElement)new NbtIntArray(StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsInt).collect(Collectors.toList())));
            }
            if (type == NbtByteArray.TYPE) {
                array = (JsonArray)ele.get("value");
                nbt.put(key, (NbtElement)new NbtByteArray(StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsByte).collect(Collectors.toList())));
            }
            if (type != NbtLongArray.TYPE) continue;
            array = (JsonArray)ele.get("value");
            nbt.put(key, (NbtElement)new NbtLongArray(StreamSupport.stream(array.spliterator(), false).map(JsonElement::getAsLong).collect(Collectors.toList())));
        }
        return nbt;
    }

    private static NbtType<? extends NbtElement> stringToType(String type) {
        if (type.equals(NbtInt.TYPE.getCrashReportName())) {
            return NbtInt.TYPE;
        }
        if (type.equals(NbtByte.TYPE.getCrashReportName())) {
            return NbtByte.TYPE;
        }
        if (type.equals(NbtFloat.TYPE.getCrashReportName())) {
            return NbtFloat.TYPE;
        }
        if (type.equals(NbtLong.TYPE.getCrashReportName())) {
            return NbtLong.TYPE;
        }
        if (type.equals(NbtDouble.TYPE.getCrashReportName())) {
            return NbtDouble.TYPE;
        }
        if (type.equals(NbtShort.TYPE.getCrashReportName())) {
            return NbtShort.TYPE;
        }
        if (type.equals(NbtCompound.TYPE.getCrashReportName())) {
            return NbtCompound.TYPE;
        }
        if (type.equals(NbtIntArray.TYPE.getCrashReportName())) {
            return NbtIntArray.TYPE;
        }
        if (type.equals(NbtByteArray.TYPE.getCrashReportName())) {
            return NbtByteArray.TYPE;
        }
        if (type.equals(NbtLongArray.TYPE.getCrashReportName())) {
            return NbtLongArray.TYPE;
        }
        return NbtString.TYPE;
    }

    public static boolean isValidPath(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (Identifier.isPathCharacterValid((char)s.charAt(i))) continue;
            return false;
        }
        return true;
    }
}

