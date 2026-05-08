/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.internal.runtime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SwitchPoint;
import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.NamedOperation;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.LinkRequest;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.codegen.ObjectClassGenerator;
import org.openjdk.nashorn.internal.codegen.types.Type;
import org.openjdk.nashorn.internal.lookup.Lookup;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.FindProperty;
import org.openjdk.nashorn.internal.runtime.GlobalConstants;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyAccess;
import org.openjdk.nashorn.internal.runtime.PropertyDescriptor;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptRuntime;
import org.openjdk.nashorn.internal.runtime.SetMethodCreator;
import org.openjdk.nashorn.internal.runtime.SpillProperty;
import org.openjdk.nashorn.internal.runtime.StructureLoader;
import org.openjdk.nashorn.internal.runtime.Symbol;
import org.openjdk.nashorn.internal.runtime.UnwarrantedOptimismException;
import org.openjdk.nashorn.internal.runtime.UserAccessorProperty;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayData;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayIndex;
import org.openjdk.nashorn.internal.runtime.linker.NashornCallSiteDescriptor;
import org.openjdk.nashorn.internal.runtime.linker.NashornGuards;

public abstract class ScriptObject
implements PropertyAccess,
Cloneable {
    public static final String PROTO_PROPERTY_NAME = "__proto__";
    public static final String NO_SUCH_METHOD_NAME = "__noSuchMethod__";
    public static final String NO_SUCH_PROPERTY_NAME = "__noSuchProperty__";
    public static final int IS_ARRAY = 1;
    public static final int IS_ARGUMENTS = 2;
    public static final int IS_LENGTH_NOT_WRITABLE = 4;
    public static final int IS_BUILTIN = 8;
    public static final int IS_INTERNAL = 16;
    public static final int SPILL_RATE = 8;
    private PropertyMap map;
    private ScriptObject proto;
    private int flags;
    protected long[] primitiveSpill;
    protected Object[] objectSpill;
    private ArrayData arrayData;
    public static final MethodHandle GETPROTO = ScriptObject.findOwnMH_V("getProto", ScriptObject.class, new Class[0]);
    static final MethodHandle MEGAMORPHIC_GET = ScriptObject.findOwnMH_V("megamorphicGet", Object.class, String.class, Boolean.TYPE, Boolean.TYPE);
    static final MethodHandle GLOBALFILTER = ScriptObject.findOwnMH_S("globalFilter", Object.class, Object.class);
    static final MethodHandle DECLARE_AND_SET = ScriptObject.findOwnMH_V("declareAndSet", Void.TYPE, String.class, Object.class);
    private static final MethodHandle TRUNCATINGFILTER = ScriptObject.findOwnMH_S("truncatingFilter", Object[].class, Integer.TYPE, Object[].class);
    private static final MethodHandle KNOWNFUNCPROPGUARDSELF = ScriptObject.findOwnMH_S("knownFunctionPropertyGuardSelf", Boolean.TYPE, Object.class, PropertyMap.class, MethodHandle.class, ScriptFunction.class);
    private static final MethodHandle KNOWNFUNCPROPGUARDPROTO = ScriptObject.findOwnMH_S("knownFunctionPropertyGuardProto", Boolean.TYPE, Object.class, PropertyMap.class, MethodHandle.class, Integer.TYPE, ScriptFunction.class);
    private static final ArrayList<MethodHandle> PROTO_FILTERS = new ArrayList();
    public static final CompilerConstants.Call GET_ARRAY = CompilerConstants.virtualCall(MethodHandles.lookup(), ScriptObject.class, "getArray", ArrayData.class, new Class[0]);
    public static final CompilerConstants.Call GET_ARGUMENT = CompilerConstants.virtualCall(MethodHandles.lookup(), ScriptObject.class, "getArgument", Object.class, Integer.TYPE);
    public static final CompilerConstants.Call SET_ARGUMENT = CompilerConstants.virtualCall(MethodHandles.lookup(), ScriptObject.class, "setArgument", Void.TYPE, Integer.TYPE, Object.class);
    public static final CompilerConstants.Call GET_PROTO = CompilerConstants.virtualCallNoLookup(ScriptObject.class, "getProto", ScriptObject.class, new Class[0]);
    public static final CompilerConstants.Call GET_PROTO_DEPTH = CompilerConstants.virtualCallNoLookup(ScriptObject.class, "getProto", ScriptObject.class, Integer.TYPE);
    public static final CompilerConstants.Call SET_GLOBAL_OBJECT_PROTO = CompilerConstants.staticCallNoLookup(ScriptObject.class, "setGlobalObjectProto", Void.TYPE, ScriptObject.class);
    public static final CompilerConstants.Call SET_PROTO_FROM_LITERAL = CompilerConstants.virtualCallNoLookup(ScriptObject.class, "setProtoFromLiteral", Void.TYPE, Object.class);
    public static final CompilerConstants.Call SET_USER_ACCESSORS = CompilerConstants.virtualCallNoLookup(ScriptObject.class, "setUserAccessors", Void.TYPE, Object.class, ScriptFunction.class, ScriptFunction.class);
    public static final CompilerConstants.Call GENERIC_SET = CompilerConstants.virtualCallNoLookup(ScriptObject.class, "set", Void.TYPE, Object.class, Object.class, Integer.TYPE);
    public static final CompilerConstants.Call DELETE = CompilerConstants.virtualCall(MethodHandles.lookup(), ScriptObject.class, "delete", Boolean.TYPE, Object.class, Boolean.TYPE);
    static final MethodHandle[] SET_SLOW = new MethodHandle[]{ScriptObject.findOwnMH_V("set", Void.TYPE, Object.class, Integer.TYPE, Integer.TYPE), ScriptObject.findOwnMH_V("set", Void.TYPE, Object.class, Double.TYPE, Integer.TYPE), ScriptObject.findOwnMH_V("set", Void.TYPE, Object.class, Object.class, Integer.TYPE)};
    public static final CompilerConstants.Call SET_MAP = CompilerConstants.virtualCallNoLookup(ScriptObject.class, "setMap", Void.TYPE, PropertyMap.class);
    static final MethodHandle CAS_MAP = ScriptObject.findOwnMH_V("compareAndSetMap", Boolean.TYPE, PropertyMap.class, PropertyMap.class);
    static final MethodHandle EXTENSION_CHECK = ScriptObject.findOwnMH_V("extensionCheck", Boolean.TYPE, Boolean.TYPE, String.class);
    static final MethodHandle ENSURE_SPILL_SIZE = ScriptObject.findOwnMH_V("ensureSpillSize", Object.class, Integer.TYPE);
    private static final GuardedInvocation DELETE_GUARDED = new GuardedInvocation(Lookup.MH.insertArguments(DELETE.methodHandle(), 2, false), NashornGuards.getScriptObjectGuard());
    private static final GuardedInvocation DELETE_GUARDED_STRICT = new GuardedInvocation(Lookup.MH.insertArguments(DELETE.methodHandle(), 2, true), NashornGuards.getScriptObjectGuard());
    private static LongAdder count;

    public ScriptObject() {
        this(null);
    }

    public ScriptObject(PropertyMap map) {
        if (Context.DEBUG) {
            count.increment();
        }
        this.arrayData = ArrayData.EMPTY_ARRAY;
        this.setMap(map == null ? PropertyMap.newMap() : map);
    }

    protected ScriptObject(ScriptObject proto, PropertyMap map) {
        this(map);
        this.proto = proto;
    }

    public ScriptObject(PropertyMap map, long[] primitiveSpill, Object[] objectSpill) {
        this(map);
        this.primitiveSpill = primitiveSpill;
        this.objectSpill = objectSpill;
        assert (primitiveSpill == null || primitiveSpill.length == objectSpill.length) : " primitive spill pool size is not the same length as object spill pool size";
    }

    protected boolean isGlobal() {
        return false;
    }

    private static int alignUp(int size, int alignment) {
        return size + alignment - 1 & -alignment;
    }

    public static int spillAllocationLength(int nProperties) {
        return ScriptObject.alignUp(nProperties, 8);
    }

    public void addBoundProperties(ScriptObject source) {
        this.addBoundProperties(source, source.getMap().getProperties());
    }

    public void addBoundProperties(ScriptObject source, Property[] properties) {
        PropertyMap newMap = this.getMap();
        boolean extensible = newMap.isExtensible();
        for (Property property : properties) {
            newMap = this.addBoundProperty(newMap, source, property, extensible);
        }
        this.setMap(newMap);
    }

    protected PropertyMap addBoundProperty(PropertyMap propMap, ScriptObject source, Property property, boolean extensible) {
        PropertyMap newMap = propMap;
        Object key = property.getKey();
        Property oldProp = newMap.findProperty(key);
        if (oldProp == null) {
            if (!extensible) {
                throw ECMAErrors.typeError("object.non.extensible", key.toString(), ScriptRuntime.safeToString(this));
            }
            if (property instanceof UserAccessorProperty) {
                UserAccessorProperty prop = this.newUserAccessors(key, property.getFlags(), property.getGetterFunction(source), property.getSetterFunction(source));
                newMap = newMap.addPropertyNoHistory(prop);
            } else {
                newMap = newMap.addPropertyBind((AccessorProperty)property, source);
            }
        } else if (!(!property.isFunctionDeclaration() || oldProp.isConfigurable() || !(oldProp instanceof UserAccessorProperty) && oldProp.isWritable() && oldProp.isEnumerable())) {
            throw ECMAErrors.typeError("cant.redefine.property", key.toString(), ScriptRuntime.safeToString(this));
        }
        return newMap;
    }

    public void addBoundProperties(Object source, AccessorProperty[] properties) {
        PropertyMap newMap = this.getMap();
        boolean extensible = newMap.isExtensible();
        for (AccessorProperty property : properties) {
            Object key = property.getKey();
            if (newMap.findProperty(key) != null) continue;
            if (!extensible) {
                throw ECMAErrors.typeError("object.non.extensible", key.toString(), ScriptRuntime.safeToString(this));
            }
            newMap = newMap.addPropertyBind(property, source);
        }
        this.setMap(newMap);
    }

    static MethodHandle bindTo(MethodHandle methodHandle, Object receiver) {
        return Lookup.MH.dropArguments(Lookup.MH.bindTo(methodHandle, receiver), 0, new Class[]{methodHandle.type().parameterType(0)});
    }

    public Iterator<String> propertyIterator() {
        return new KeyIterator(this);
    }

    public Iterator<Object> valueIterator() {
        return new ValueIterator(this);
    }

    public final boolean isAccessorDescriptor() {
        return this.has("get") || this.has("set");
    }

    public final boolean isDataDescriptor() {
        return this.has("value") || this.has("writable");
    }

    public final PropertyDescriptor toPropertyDescriptor() {
        PropertyDescriptor desc;
        Global global = Context.getGlobal();
        if (this.isDataDescriptor()) {
            if (this.has("set") || this.has("get")) {
                throw ECMAErrors.typeError(global, "inconsistent.property.descriptor", new String[0]);
            }
            desc = global.newDataDescriptor(ScriptRuntime.UNDEFINED, false, false, false);
        } else if (this.isAccessorDescriptor()) {
            if (this.has("value") || this.has("writable")) {
                throw ECMAErrors.typeError(global, "inconsistent.property.descriptor", new String[0]);
            }
            desc = global.newAccessorDescriptor(ScriptRuntime.UNDEFINED, ScriptRuntime.UNDEFINED, false, false);
        } else {
            desc = global.newGenericDescriptor(false, false);
        }
        return desc.fillFrom(this);
    }

    public static PropertyDescriptor toPropertyDescriptor(Global global, Object obj) {
        if (obj instanceof ScriptObject) {
            return ((ScriptObject)obj).toPropertyDescriptor();
        }
        throw ECMAErrors.typeError(global, "not.an.object", ScriptRuntime.safeToString(obj));
    }

    public Object getOwnPropertyDescriptor(Object key) {
        Property property = this.getMap().findProperty(key);
        Global global = Context.getGlobal();
        if (property != null) {
            ScriptFunction get = property.getGetterFunction(this);
            ScriptFunction set = property.getSetterFunction(this);
            boolean configurable = property.isConfigurable();
            boolean enumerable = property.isEnumerable();
            boolean writable = property.isWritable();
            if (property.isAccessorProperty()) {
                return global.newAccessorDescriptor(get != null ? get : ScriptRuntime.UNDEFINED, set != null ? set : ScriptRuntime.UNDEFINED, configurable, enumerable);
            }
            return global.newDataDescriptor(this.getWithProperty(property), configurable, enumerable, writable);
        }
        int index = ArrayIndex.getArrayIndex(key);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            return array.getDescriptor(global, index);
        }
        return ScriptRuntime.UNDEFINED;
    }

    public Object getPropertyDescriptor(String key) {
        Object res = this.getOwnPropertyDescriptor(key);
        if (res != ScriptRuntime.UNDEFINED) {
            return res;
        }
        if (this.getProto() != null) {
            return this.getProto().getOwnPropertyDescriptor(key);
        }
        return ScriptRuntime.UNDEFINED;
    }

    protected void invalidateGlobalConstant(Object key) {
        GlobalConstants globalConstants = this.getGlobalConstants();
        if (globalConstants != null) {
            globalConstants.delete(key);
        }
    }

    public boolean defineOwnProperty(Object key, Object propertyDesc, boolean reject) {
        Global global = Context.getGlobal();
        PropertyDescriptor desc = ScriptObject.toPropertyDescriptor(global, propertyDesc);
        Object current = this.getOwnPropertyDescriptor(key);
        this.invalidateGlobalConstant(key);
        if (current == ScriptRuntime.UNDEFINED) {
            if (this.isExtensible()) {
                this.addOwnProperty(key, desc);
                return true;
            }
            if (reject) {
                throw ECMAErrors.typeError(global, "object.non.extensible", key.toString(), ScriptRuntime.safeToString(this));
            }
            return false;
        }
        PropertyDescriptor currentDesc = (PropertyDescriptor)current;
        if (desc.type() == 0 && !desc.has("configurable") && !desc.has("enumerable")) {
            return true;
        }
        if (desc.hasAndEquals(currentDesc)) {
            return true;
        }
        if (!currentDesc.isConfigurable()) {
            if (desc.has("configurable") && desc.isConfigurable()) {
                if (reject) {
                    throw ECMAErrors.typeError(global, "cant.redefine.property", key.toString(), ScriptRuntime.safeToString(this));
                }
                return false;
            }
            if (desc.has("enumerable") && currentDesc.isEnumerable() != desc.isEnumerable()) {
                if (reject) {
                    throw ECMAErrors.typeError(global, "cant.redefine.property", key.toString(), ScriptRuntime.safeToString(this));
                }
                return false;
            }
        }
        int propFlags = Property.mergeFlags(currentDesc, desc);
        Property property = this.getMap().findProperty(key);
        if (currentDesc.type() == 1 && (desc.type() == 1 || desc.type() == 0)) {
            Object value;
            if (!currentDesc.isConfigurable() && !currentDesc.isWritable() && (desc.has("writable") && desc.isWritable() || desc.has("value") && !ScriptRuntime.sameValue(currentDesc.getValue(), desc.getValue()))) {
                if (reject) {
                    throw ECMAErrors.typeError(global, "cant.redefine.property", key.toString(), ScriptRuntime.safeToString(this));
                }
                return false;
            }
            boolean newValue = desc.has("value");
            Object object = value = newValue ? desc.getValue() : currentDesc.getValue();
            if (newValue && property != null) {
                this.modifyOwnProperty(property, 0);
                this.set(key, value, 0);
                property = this.getMap().findProperty(key);
            }
            if (property == null) {
                this.addOwnProperty(key, propFlags, value);
                this.checkIntegerKey(key);
            } else {
                this.modifyOwnProperty(property, propFlags);
            }
        } else if (currentDesc.type() == 2 && (desc.type() == 2 || desc.type() == 0)) {
            if (!currentDesc.isConfigurable() && (desc.has("get") && !ScriptRuntime.sameValue(currentDesc.getGetter(), desc.getGetter()) || desc.has("set") && !ScriptRuntime.sameValue(currentDesc.getSetter(), desc.getSetter()))) {
                if (reject) {
                    throw ECMAErrors.typeError(global, "cant.redefine.property", key.toString(), ScriptRuntime.safeToString(this));
                }
                return false;
            }
            this.modifyOwnProperty(property, propFlags, desc.has("get") ? desc.getGetter() : currentDesc.getGetter(), desc.has("set") ? desc.getSetter() : currentDesc.getSetter());
        } else {
            int type;
            boolean value;
            if (!currentDesc.isConfigurable()) {
                if (reject) {
                    throw ECMAErrors.typeError(global, "cant.redefine.property", key.toString(), ScriptRuntime.safeToString(this));
                }
                return false;
            }
            propFlags = 0;
            boolean bl = value = desc.has("configurable") ? desc.isConfigurable() : currentDesc.isConfigurable();
            if (!value) {
                propFlags |= 4;
            }
            boolean bl2 = value = desc.has("enumerable") ? desc.isEnumerable() : currentDesc.isEnumerable();
            if (!value) {
                propFlags |= 2;
            }
            if ((type = desc.type()) == 1) {
                boolean bl3 = value = desc.has("writable") && desc.isWritable();
                if (!value) {
                    propFlags |= 1;
                }
                this.deleteOwnProperty(property);
                this.addOwnProperty(key, propFlags, desc.getValue());
            } else if (type == 2) {
                if (property == null) {
                    this.addOwnProperty(key, propFlags, desc.has("get") ? desc.getGetter() : null, desc.has("set") ? desc.getSetter() : null);
                } else {
                    this.modifyOwnProperty(property, propFlags, desc.has("get") ? desc.getGetter() : null, desc.has("set") ? desc.getSetter() : null);
                }
            }
        }
        this.checkIntegerKey(key);
        return true;
    }

    public void defineOwnProperty(int index, Object value) {
        long oldLength;
        assert (ArrayIndex.isValidArrayIndex(index)) : "invalid array index";
        long longIndex = ArrayIndex.toLongIndex(index);
        if (longIndex >= (oldLength = this.getArray().length())) {
            this.setArray(this.getArray().ensure(longIndex).safeDelete(oldLength, longIndex - 1L, false));
        }
        this.setArray(this.getArray().set(index, value, false));
    }

    private void checkIntegerKey(Object key) {
        ArrayData data;
        int index = ArrayIndex.getArrayIndex(key);
        if (ArrayIndex.isValidArrayIndex(index) && (data = this.getArray()).has(index)) {
            this.setArray(data.delete(index));
        }
    }

    public final void addOwnProperty(Object key, PropertyDescriptor propertyDesc) {
        int type;
        PropertyDescriptor pdesc = propertyDesc;
        int propFlags = Property.toFlags(pdesc);
        if (pdesc.type() == 0) {
            Global global = Context.getGlobal();
            PropertyDescriptor dDesc = global.newDataDescriptor(ScriptRuntime.UNDEFINED, false, false, false);
            dDesc.fillFrom((ScriptObject)((Object)pdesc));
            pdesc = dDesc;
        }
        if ((type = pdesc.type()) == 1) {
            this.addOwnProperty(key, propFlags, pdesc.getValue());
        } else if (type == 2) {
            this.addOwnProperty(key, propFlags, pdesc.has("get") ? pdesc.getGetter() : null, pdesc.has("set") ? pdesc.getSetter() : null);
        }
        this.checkIntegerKey(key);
    }

    public final FindProperty findProperty(Object key, boolean deep) {
        return this.findProperty(key, deep, false, this);
    }

    protected FindProperty findProperty(Object key, boolean deep, boolean isScope, ScriptObject start) {
        PropertyMap selfMap = this.getMap();
        Property property = selfMap.findProperty(key);
        if (property != null) {
            return new FindProperty(start, this, property);
        }
        if (deep) {
            ScriptObject myProto = this.getProto();
            FindProperty find = myProto == null ? null : myProto.findProperty(key, true, isScope, start);
            this.checkSharedProtoMap();
            return find;
        }
        return null;
    }

    boolean hasProperty(Object key, boolean deep) {
        ScriptObject myProto;
        if (this.getMap().findProperty(key) != null) {
            return true;
        }
        if (deep && (myProto = this.getProto()) != null) {
            return myProto.hasProperty(key, true);
        }
        return false;
    }

    private SwitchPoint findBuiltinSwitchPoint(Object key) {
        for (ScriptObject myProto = this.getProto(); myProto != null; myProto = myProto.getProto()) {
            SwitchPoint sp;
            Property prop = myProto.getMap().findProperty(key);
            if (prop == null || (sp = prop.getBuiltinSwitchPoint()) == null || sp.hasBeenInvalidated()) continue;
            return sp;
        }
        return null;
    }

    public final Property addOwnProperty(Object key, int propertyFlags, ScriptFunction getter, ScriptFunction setter) {
        return this.addOwnProperty(this.newUserAccessors(key, propertyFlags, getter, setter));
    }

    public final Property addOwnProperty(Object key, int propertyFlags, Object value) {
        return this.addSpillProperty(key, propertyFlags, value, true);
    }

    public final Property addOwnProperty(Property newProperty) {
        PropertyMap newMap;
        PropertyMap oldMap = this.getMap();
        while (!this.compareAndSetMap(oldMap, newMap = oldMap.addProperty(newProperty))) {
            oldMap = this.getMap();
            Property oldProperty = oldMap.findProperty(newProperty.getKey());
            if (oldProperty == null) continue;
            return oldProperty;
        }
        return newProperty;
    }

    private void erasePropertyValue(Property property) {
        if (property != null && !property.isAccessorProperty()) {
            property.setValue(this, this, ScriptRuntime.UNDEFINED, false);
        }
    }

    public final boolean deleteOwnProperty(Property property) {
        this.erasePropertyValue(property);
        PropertyMap oldMap = this.getMap();
        while (true) {
            PropertyMap newMap;
            if ((newMap = oldMap.deleteProperty(property)) == null) {
                return false;
            }
            if (this.compareAndSetMap(oldMap, newMap)) break;
            oldMap = this.getMap();
        }
        if (property instanceof UserAccessorProperty) {
            ((UserAccessorProperty)property).setAccessors(this, this.getMap(), null);
        }
        this.invalidateGlobalConstant(property.getKey());
        return true;
    }

    protected final void initUserAccessors(String key, ScriptFunction getter, ScriptFunction setter) {
        PropertyMap map = this.getMap();
        Property property = map.findProperty(key);
        assert (property instanceof UserAccessorProperty);
        this.ensureSpillSize(property.getSlot());
        this.objectSpill[property.getSlot()] = new UserAccessorProperty.Accessors(getter, setter);
    }

    public final Property modifyOwnProperty(Property oldProperty, int propertyFlags, ScriptFunction getter, ScriptFunction setter) {
        UserAccessorProperty newProperty;
        if (oldProperty instanceof UserAccessorProperty) {
            UserAccessorProperty uc = (UserAccessorProperty)oldProperty;
            int slot = uc.getSlot();
            assert (uc.getLocalType() == Object.class);
            UserAccessorProperty.Accessors gs = uc.getAccessors(this);
            assert (gs != null);
            gs.set(getter, setter);
            if (uc.getFlags() == (propertyFlags | 0x1000)) {
                return oldProperty;
            }
            newProperty = new UserAccessorProperty(uc.getKey(), propertyFlags, slot);
        } else {
            this.erasePropertyValue(oldProperty);
            newProperty = this.newUserAccessors(oldProperty.getKey(), propertyFlags, getter, setter);
        }
        return this.modifyOwnProperty(oldProperty, newProperty);
    }

    public final Property modifyOwnProperty(Property oldProperty, int propertyFlags) {
        return this.modifyOwnProperty(oldProperty, oldProperty.setFlags(propertyFlags));
    }

    private Property modifyOwnProperty(Property oldProperty, Property newProperty) {
        PropertyMap newMap;
        if (oldProperty == newProperty) {
            return newProperty;
        }
        assert (newProperty.getKey().equals(oldProperty.getKey())) : "replacing property with different key";
        PropertyMap oldMap = this.getMap();
        while (!this.compareAndSetMap(oldMap, newMap = oldMap.replaceProperty(oldProperty, newProperty))) {
            oldMap = this.getMap();
            Property oldPropertyLookup = oldMap.findProperty(oldProperty.getKey());
            if (oldPropertyLookup == null || !oldPropertyLookup.equals(newProperty)) continue;
            return oldPropertyLookup;
        }
        return newProperty;
    }

    public final void setUserAccessors(Object key, ScriptFunction getter, ScriptFunction setter) {
        Object realKey = JSType.toPropertyKey(key);
        Property oldProperty = this.getMap().findProperty(realKey);
        if (oldProperty instanceof UserAccessorProperty) {
            this.modifyOwnProperty(oldProperty, oldProperty.getFlags(), getter, setter);
        } else {
            this.addOwnProperty(this.newUserAccessors(realKey, oldProperty != null ? oldProperty.getFlags() : 0, getter, setter));
        }
    }

    private static int getIntValue(FindProperty find, int programPoint) {
        MethodHandle getter = find.getGetter(Integer.TYPE, programPoint, null);
        if (getter != null) {
            try {
                return getter.invokeExact(find.getGetterReceiver());
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    private static double getDoubleValue(FindProperty find, int programPoint) {
        MethodHandle getter = find.getGetter(Double.TYPE, programPoint, null);
        if (getter != null) {
            try {
                return getter.invokeExact(find.getGetterReceiver());
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return Double.NaN;
    }

    protected static MethodHandle getCallMethodHandle(FindProperty find, MethodType type, String bindName) {
        return ScriptObject.getCallMethodHandle(find.getObjectValue(), type, bindName);
    }

    private static MethodHandle getCallMethodHandle(Object value, MethodType type, String bindName) {
        return value instanceof ScriptFunction ? ((ScriptFunction)value).getCallMethodHandle(type, bindName) : null;
    }

    public final Object getWithProperty(Property property) {
        return new FindProperty(this, this, property).getObjectValue();
    }

    public final Property getProperty(String key) {
        return this.getMap().findProperty(key);
    }

    public Object getArgument(int key) {
        return this.get(key);
    }

    public void setArgument(int key, Object value) {
        this.set(key, value, 0);
    }

    protected Context getContext() {
        return Context.fromClass(this.getClass());
    }

    public final PropertyMap getMap() {
        return this.map;
    }

    public final void setMap(PropertyMap map) {
        this.map = map;
    }

    protected final boolean compareAndSetMap(PropertyMap oldMap, PropertyMap newMap) {
        if (oldMap == this.map) {
            this.map = newMap;
            return true;
        }
        return false;
    }

    public final ScriptObject getProto() {
        return this.proto;
    }

    public final ScriptObject getProto(int n) {
        ScriptObject p = this;
        for (int i = n; i > 0; --i) {
            p = p.getProto();
        }
        return p;
    }

    public final void setProto(ScriptObject newProto) {
        ScriptObject oldProto = this.proto;
        if (oldProto != newProto) {
            this.proto = newProto;
            this.getMap().protoChanged();
            this.setMap(this.getMap().changeProto(newProto));
        }
    }

    public void setInitialProto(ScriptObject initialProto) {
        this.proto = initialProto;
    }

    public static void setGlobalObjectProto(ScriptObject obj) {
        obj.setInitialProto(Global.objectPrototype());
    }

    public final void setPrototypeOf(Object newProto) {
        if (newProto == null || newProto instanceof ScriptObject) {
            if (!this.isExtensible()) {
                if (newProto == this.getProto()) {
                    return;
                }
                throw ECMAErrors.typeError("__proto__.set.non.extensible", ScriptRuntime.safeToString(this));
            }
            for (ScriptObject p = (ScriptObject)newProto; p != null; p = p.getProto()) {
                if (p != this) continue;
                throw ECMAErrors.typeError("circular.__proto__.set", ScriptRuntime.safeToString(this));
            }
        } else {
            throw ECMAErrors.typeError("cant.set.proto.to.non.object", ScriptRuntime.safeToString(this), ScriptRuntime.safeToString(newProto));
        }
        this.setProto((ScriptObject)newProto);
    }

    public final void setProtoFromLiteral(Object newProto) {
        if (newProto == null || newProto instanceof ScriptObject) {
            this.setPrototypeOf(newProto);
        } else {
            this.setPrototypeOf(Global.objectPrototype());
        }
    }

    public String[] getAllKeys() {
        HashSet<String> keys = new HashSet<String>();
        HashSet nonEnumerable = new HashSet();
        for (ScriptObject self = this; self != null; self = self.getProto()) {
            keys.addAll(Arrays.asList(self.getOwnKeys(String.class, true, nonEnumerable)));
        }
        return keys.toArray(new String[0]);
    }

    public final String[] getOwnKeys(boolean all) {
        return this.getOwnKeys(String.class, all, null);
    }

    public final Symbol[] getOwnSymbols(boolean all) {
        return this.getOwnKeys(Symbol.class, all, null);
    }

    protected <T> T[] getOwnKeys(Class<T> type, boolean all, Set<T> nonEnumerable) {
        ArrayList<Object> keys = new ArrayList<Object>();
        PropertyMap selfMap = this.getMap();
        ArrayData array = this.getArray();
        if (type == String.class) {
            Iterator<Long> iter = array.indexIterator();
            while (iter.hasNext()) {
                keys.add(JSType.toString(iter.next().longValue()));
            }
        }
        for (Property property : selfMap.getProperties()) {
            boolean enumerable = property.isEnumerable();
            Object key = property.getKey();
            if (!type.isInstance(key)) continue;
            if (all) {
                keys.add(key);
                continue;
            }
            if (enumerable) {
                if (nonEnumerable != null && nonEnumerable.contains(key)) continue;
                keys.add(key);
                continue;
            }
            if (nonEnumerable == null) continue;
            nonEnumerable.add(key);
        }
        return keys.toArray((Object[])Array.newInstance(type, keys.size()));
    }

    public boolean hasArrayEntries() {
        return this.getArray().length() > 0L || this.getMap().containsArrayKeys();
    }

    public String getClassName() {
        return "Object";
    }

    public Object getLength() {
        return this.get("length");
    }

    public String safeToString() {
        return "[object " + this.getClassName() + "]";
    }

    public Object getDefaultValue(Class<?> typeHint) {
        return Context.getGlobal().getDefaultValue(this, typeHint);
    }

    public boolean isInstance(ScriptObject instance) {
        return false;
    }

    public ScriptObject preventExtensions() {
        PropertyMap oldMap = this.getMap();
        while (!this.compareAndSetMap(oldMap, this.getMap().preventExtensions())) {
            oldMap = this.getMap();
        }
        ArrayData array = this.getArray();
        assert (array != null);
        this.setArray(ArrayData.preventExtension(array));
        return this;
    }

    public static boolean isArray(Object obj) {
        return obj instanceof ScriptObject && ((ScriptObject)obj).isArray();
    }

    public final boolean isArray() {
        return (this.flags & 1) != 0;
    }

    public final void setIsArray() {
        this.flags |= 1;
    }

    public final boolean isArguments() {
        return (this.flags & 2) != 0;
    }

    public final void setIsArguments() {
        this.flags |= 2;
    }

    public boolean isLengthNotWritable() {
        return (this.flags & 4) != 0;
    }

    public void setIsLengthNotWritable() {
        this.flags |= 4;
    }

    public final ArrayData getArray(Class<?> elementType) {
        if (elementType == null) {
            return this.arrayData;
        }
        ArrayData newArrayData = this.arrayData.convert(elementType);
        if (newArrayData != this.arrayData) {
            this.arrayData = newArrayData;
        }
        return newArrayData;
    }

    public final ArrayData getArray() {
        return this.arrayData;
    }

    public final void setArray(ArrayData arrayData) {
        this.arrayData = arrayData;
    }

    public boolean isExtensible() {
        return this.getMap().isExtensible();
    }

    public ScriptObject seal() {
        PropertyMap newMap;
        PropertyMap oldMap = this.getMap();
        while (!this.compareAndSetMap(oldMap, newMap = this.getMap().seal())) {
            oldMap = this.getMap();
        }
        this.setArray(ArrayData.seal(this.getArray()));
        return this;
    }

    public boolean isSealed() {
        return this.getMap().isSealed();
    }

    public ScriptObject freeze() {
        PropertyMap newMap;
        PropertyMap oldMap = this.getMap();
        while (!this.compareAndSetMap(oldMap, newMap = this.getMap().freeze())) {
            oldMap = this.getMap();
        }
        this.setArray(ArrayData.freeze(this.getArray()));
        return this;
    }

    public boolean isFrozen() {
        return this.getMap().isFrozen();
    }

    public boolean isScope() {
        return false;
    }

    public final void setIsBuiltin() {
        this.flags |= 8;
    }

    public final boolean isBuiltin() {
        return (this.flags & 8) != 0;
    }

    public final void setIsInternal() {
        this.flags |= 0x10;
    }

    public final boolean isInternal() {
        return (this.flags & 0x10) != 0;
    }

    public void clear(boolean strict) {
        Iterator<String> iter = this.propertyIterator();
        while (iter.hasNext()) {
            this.delete(iter.next(), strict);
        }
    }

    public boolean containsKey(Object key) {
        return this.has(key);
    }

    public boolean containsValue(Object value) {
        Iterator<Object> iter = this.valueIterator();
        while (iter.hasNext()) {
            if (!iter.next().equals(value)) continue;
            return true;
        }
        return false;
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        Iterator<String> iter = this.propertyIterator();
        HashSet<AbstractMap.SimpleImmutableEntry<String, Object>> entries = new HashSet<AbstractMap.SimpleImmutableEntry<String, Object>>();
        while (iter.hasNext()) {
            String key = iter.next();
            entries.add(new AbstractMap.SimpleImmutableEntry<String, Object>(key, this.get(key)));
        }
        return Collections.unmodifiableSet(entries);
    }

    public boolean isEmpty() {
        return !this.propertyIterator().hasNext();
    }

    public Set<Object> keySet() {
        Iterator<String> iter = this.propertyIterator();
        HashSet<String> keySet = new HashSet<String>();
        while (iter.hasNext()) {
            keySet.add(iter.next());
        }
        return Collections.unmodifiableSet(keySet);
    }

    public Object put(Object key, Object value, boolean strict) {
        Object oldValue = this.get(key);
        int scriptObjectFlags = strict ? 32 : 0;
        this.set(key, value, scriptObjectFlags);
        return oldValue;
    }

    public void putAll(Map<?, ?> otherMap, boolean strict) {
        int scriptObjectFlags = strict ? 32 : 0;
        for (Map.Entry<?, ?> entry : otherMap.entrySet()) {
            this.set(entry.getKey(), entry.getValue(), scriptObjectFlags);
        }
    }

    public Object remove(Object key, boolean strict) {
        Object oldValue = this.get(key);
        this.delete(key, strict);
        return oldValue;
    }

    public int size() {
        int n = 0;
        Iterator<String> iter = this.propertyIterator();
        while (iter.hasNext()) {
            ++n;
            iter.next();
        }
        return n;
    }

    public Collection<Object> values() {
        ArrayList<Object> values = new ArrayList<Object>(this.size());
        Iterator<Object> iter = this.valueIterator();
        while (iter.hasNext()) {
            values.add(iter.next());
        }
        return Collections.unmodifiableList(values);
    }

    public GuardedInvocation lookup(CallSiteDescriptor desc, LinkRequest request) {
        switch (NashornCallSiteDescriptor.getStandardOperation(desc)) {
            case GET: {
                return desc.getOperation() instanceof NamedOperation ? this.findGetMethod(desc, request) : this.findGetIndexMethod(desc, request);
            }
            case SET: {
                return desc.getOperation() instanceof NamedOperation ? this.findSetMethod(desc, request) : this.findSetIndexMethod(desc, request);
            }
            case REMOVE: {
                GuardedInvocation inv = NashornCallSiteDescriptor.isStrict(desc) ? DELETE_GUARDED_STRICT : DELETE_GUARDED;
                Object name = NamedOperation.getName(desc.getOperation());
                if (name != null) {
                    return inv.replaceMethods(Lookup.MH.insertArguments(inv.getInvocation(), 1, name), inv.getGuard());
                }
                return inv;
            }
            case CALL: {
                return this.findCallMethod(desc, request);
            }
            case NEW: {
                return this.findNewMethod(desc, request);
            }
        }
        return null;
    }

    protected GuardedInvocation findNewMethod(CallSiteDescriptor desc, LinkRequest request) {
        return this.notAFunction(desc);
    }

    protected GuardedInvocation findCallMethod(CallSiteDescriptor desc, LinkRequest request) {
        return this.notAFunction(desc);
    }

    private GuardedInvocation notAFunction(CallSiteDescriptor desc) {
        throw ECMAErrors.typeError("not.a.function", NashornCallSiteDescriptor.getFunctionErrorMessage(desc, this));
    }

    boolean hasWithScope() {
        return false;
    }

    static MethodHandle addProtoFilter(MethodHandle methodHandle, int depth) {
        MethodHandle filter;
        if (depth == 0) {
            return methodHandle;
        }
        int listIndex = depth - 1;
        MethodHandle methodHandle2 = filter = listIndex < PROTO_FILTERS.size() ? PROTO_FILTERS.get(listIndex) : null;
        if (filter == null) {
            filter = ScriptObject.addProtoFilter(GETPROTO, depth - 1);
            PROTO_FILTERS.add(null);
            PROTO_FILTERS.set(listIndex, filter);
        }
        return Lookup.MH.filterArguments(methodHandle, 0, filter.asType(filter.type().changeReturnType((Class<?>)methodHandle.type().parameterType(0))));
    }

    protected GuardedInvocation findGetMethod(CallSiteDescriptor desc, LinkRequest request) {
        SwitchPoint[] protoSwitchPoints;
        Class<ClassCastException> exception;
        GuardedInvocation cinv;
        boolean explicitInstanceOfCheck = NashornGuards.explicitInstanceOfCheck(desc, request);
        String name = NashornCallSiteDescriptor.getOperand(desc);
        if (NashornCallSiteDescriptor.isApplyToCall(desc) && Global.isBuiltinFunctionPrototypeApply()) {
            name = "call";
        }
        if (request.isCallSiteUnstable() || this.hasWithScope()) {
            return ScriptObject.findMegaMorphicGetMethod(desc, name, NashornCallSiteDescriptor.isMethodFirstOperation(desc));
        }
        FindProperty find = this.findProperty(name, true, NashornCallSiteDescriptor.isScope(desc), this);
        if (find == null) {
            if (!NashornCallSiteDescriptor.isMethodFirstOperation(desc)) {
                return this.noSuchProperty(desc, request);
            }
            return this.noSuchMethod(desc, request);
        }
        GlobalConstants globalConstants = this.getGlobalConstants();
        if (globalConstants != null && (cinv = globalConstants.findGetMethod(find, this, desc)) != null) {
            return cinv;
        }
        TypeDescriptor.OfField returnType = desc.getMethodType().returnType();
        Property property = find.getProperty();
        int programPoint = NashornCallSiteDescriptor.isOptimistic(desc) ? NashornCallSiteDescriptor.getProgramPoint(desc) : -1;
        MethodHandle mh = find.getGetter((Class<?>)returnType, programPoint, request);
        MethodHandle guard = NashornGuards.getGuard(this, property, desc, explicitInstanceOfCheck);
        ScriptObject owner = find.getOwner();
        Class<ClassCastException> clazz = exception = explicitInstanceOfCheck ? null : ClassCastException.class;
        if (mh == null) {
            mh = Lookup.emptyGetter(returnType);
            protoSwitchPoints = this.getProtoSwitchPoints(name, owner);
        } else if (!find.isSelf()) {
            assert (mh.type().returnType().equals(returnType)) : "return type mismatch for getter " + (Class)mh.type().returnType() + " != " + (Class)returnType;
            if (!property.isAccessorProperty()) {
                mh = ScriptObject.addProtoFilter(mh, find.getProtoChainLength());
            }
            protoSwitchPoints = this.getProtoSwitchPoints(name, owner);
        } else {
            protoSwitchPoints = null;
        }
        GuardedInvocation inv = new GuardedInvocation(mh, guard, protoSwitchPoints, exception);
        return inv.addSwitchPoint(this.findBuiltinSwitchPoint(name));
    }

    private static GuardedInvocation findMegaMorphicGetMethod(CallSiteDescriptor desc, String name, boolean isMethod) {
        Context.getContextTrusted().getLogger(ObjectClassGenerator.class).warning("Megamorphic getter: ", desc, " ", name + " ", isMethod);
        MethodHandle invoker = Lookup.MH.insertArguments(MEGAMORPHIC_GET, 1, name, isMethod, NashornCallSiteDescriptor.isScope(desc));
        MethodHandle guard = ScriptObject.getScriptObjectGuard(desc.getMethodType(), true);
        return new GuardedInvocation(invoker, guard);
    }

    private Object megamorphicGet(String key, boolean isMethod, boolean isScope) {
        FindProperty find = this.findProperty(key, true, isScope, this);
        if (find != null) {
            Object value = find.getObjectValue();
            if (isMethod && value instanceof ScriptFunction && find.getSelf() != this && !find.getSelf().isInternal()) {
                return ((ScriptFunction)value).createBound(find.getSelf(), ScriptRuntime.EMPTY_ARRAY);
            }
            return value;
        }
        return isMethod ? this.getNoSuchMethod(key, isScope, -1) : this.invokeNoSuchProperty(key, isScope, -1);
    }

    private void declareAndSet(String key, Object value) {
        this.declareAndSet(this.findProperty(key, false), value);
    }

    private void declareAndSet(FindProperty find, Object value) {
        PropertyMap oldMap = this.getMap();
        assert (find != null);
        Property property = find.getProperty();
        assert (property != null);
        assert (property.needsDeclaration());
        PropertyMap newMap = oldMap.replaceProperty(property, property.removeFlags(512));
        this.setMap(newMap);
        this.set(property.getKey(), value, 512);
    }

    protected GuardedInvocation findGetIndexMethod(CallSiteDescriptor desc, LinkRequest request) {
        Object name;
        MethodType callType = desc.getMethodType();
        TypeDescriptor.OfField returnType = callType.returnType();
        Object returnClass = ((Class)returnType).isPrimitive() ? returnType : Object.class;
        TypeDescriptor.OfField keyClass = callType.parameterType(1);
        boolean explicitInstanceOfCheck = NashornGuards.explicitInstanceOfCheck(desc, request);
        if (((Class)returnClass).isPrimitive()) {
            String returnTypeName = ((Class)returnClass).getName();
            name = "get" + Character.toUpperCase(returnTypeName.charAt(0)) + returnTypeName.substring(1);
        } else {
            name = "get";
        }
        MethodHandle mh = ScriptObject.findGetIndexMethodHandle(returnClass, (String)name, keyClass, desc);
        return new GuardedInvocation(mh, ScriptObject.getScriptObjectGuard(callType, explicitInstanceOfCheck), (SwitchPoint)null, explicitInstanceOfCheck ? null : ClassCastException.class);
    }

    private static MethodHandle getScriptObjectGuard(MethodType type, boolean explicitInstanceOfCheck) {
        return ScriptObject.class.isAssignableFrom((Class<?>)type.parameterType(0)) ? null : NashornGuards.getScriptObjectGuard(explicitInstanceOfCheck);
    }

    private static MethodHandle findGetIndexMethodHandle(Class<?> returnType, String name, Class<?> elementType, CallSiteDescriptor desc) {
        if (!returnType.isPrimitive()) {
            return ScriptObject.findOwnMH_V(name, returnType, elementType);
        }
        return Lookup.MH.insertArguments(ScriptObject.findOwnMH_V(name, returnType, elementType, Integer.TYPE), 2, NashornCallSiteDescriptor.isOptimistic(desc) ? NashornCallSiteDescriptor.getProgramPoint(desc) : -1);
    }

    public final SwitchPoint[] getProtoSwitchPoints(String name, ScriptObject owner) {
        ScriptObject obj;
        if (owner == this || this.getProto() == null) {
            return null;
        }
        HashSet<SwitchPoint> switchPoints = new HashSet<SwitchPoint>();
        SwitchPoint switchPoint = this.getProto().getMap().getSwitchPoint(name);
        if (switchPoint == null) {
            switchPoint = new SwitchPoint();
            for (obj = this; obj != owner && obj.getProto() != null; obj = obj.getProto()) {
                obj.getProto().getMap().addSwitchPoint(name, switchPoint);
            }
        }
        switchPoints.add(switchPoint);
        for (obj = this; obj != owner && obj.getProto() != null; obj = obj.getProto()) {
            SwitchPoint sharedProtoSwitchPoint = obj.getProto().getMap().getSharedProtoSwitchPoint();
            if (sharedProtoSwitchPoint == null || sharedProtoSwitchPoint.hasBeenInvalidated()) continue;
            switchPoints.add(sharedProtoSwitchPoint);
        }
        return switchPoints.toArray(new SwitchPoint[0]);
    }

    final SwitchPoint getProtoSwitchPoint(String name) {
        if (this.getProto() == null) {
            return null;
        }
        SwitchPoint switchPoint = this.getProto().getMap().getSwitchPoint(name);
        if (switchPoint == null) {
            switchPoint = new SwitchPoint();
            ScriptObject obj = this;
            while (obj.getProto() != null) {
                obj.getProto().getMap().addSwitchPoint(name, switchPoint);
                obj = obj.getProto();
            }
        }
        return switchPoint;
    }

    private void checkSharedProtoMap() {
        if (this.getMap().isInvalidSharedMapFor(this.getProto())) {
            this.setMap(this.getMap().makeUnsharedCopy());
        }
    }

    protected GuardedInvocation findSetMethod(CallSiteDescriptor desc, LinkRequest request) {
        GuardedInvocation cinv;
        String name = NashornCallSiteDescriptor.getOperand(desc);
        if (request.isCallSiteUnstable() || this.hasWithScope()) {
            return ScriptObject.findMegaMorphicSetMethod(desc, name);
        }
        boolean explicitInstanceOfCheck = NashornGuards.explicitInstanceOfCheck(desc, request);
        FindProperty find = this.findProperty(name, true, NashornCallSiteDescriptor.isScope(desc), this);
        if (find != null && find.isInheritedOrdinaryProperty()) {
            if (this.isExtensible() && !find.getProperty().isWritable()) {
                return this.createEmptySetMethod(desc, explicitInstanceOfCheck, "property.not.writable", true);
            }
            if (!NashornCallSiteDescriptor.isScope(desc) || !find.getOwner().isScope()) {
                find = null;
            }
        }
        if (find != null) {
            if (!find.getProperty().isWritable() && !NashornCallSiteDescriptor.isDeclaration(desc)) {
                if (NashornCallSiteDescriptor.isScope(desc) && find.getProperty().isLexicalBinding()) {
                    throw ECMAErrors.typeError("assign.constant", name);
                }
                return this.createEmptySetMethod(desc, explicitInstanceOfCheck, "property.not.writable", true);
            }
            if (!find.getProperty().hasNativeSetter()) {
                return this.createEmptySetMethod(desc, explicitInstanceOfCheck, "property.has.no.setter", true);
            }
        } else if (!this.isExtensible()) {
            return this.createEmptySetMethod(desc, explicitInstanceOfCheck, "object.non.extensible", false);
        }
        GuardedInvocation inv = new SetMethodCreator(this, find, desc, request).createGuardedInvocation(this.findBuiltinSwitchPoint(name));
        GlobalConstants globalConstants = this.getGlobalConstants();
        if (globalConstants != null && (cinv = globalConstants.findSetMethod(find, this, inv, desc, request)) != null) {
            return cinv;
        }
        return inv;
    }

    private GlobalConstants getGlobalConstants() {
        return !this.isGlobal() ? null : this.getContext().getGlobalConstants();
    }

    private GuardedInvocation createEmptySetMethod(CallSiteDescriptor desc, boolean explicitInstanceOfCheck, String strictErrorMessage, boolean canBeFastScope) {
        String name = NashornCallSiteDescriptor.getOperand(desc);
        if (NashornCallSiteDescriptor.isStrict(desc)) {
            throw ECMAErrors.typeError(strictErrorMessage, name, ScriptRuntime.safeToString(this));
        }
        assert (canBeFastScope || !NashornCallSiteDescriptor.isFastScope(desc));
        return new GuardedInvocation(Lookup.EMPTY_SETTER, NashornGuards.getMapGuard(this.getMap(), explicitInstanceOfCheck), this.getProtoSwitchPoints(name, null), explicitInstanceOfCheck ? null : ClassCastException.class);
    }

    private boolean extensionCheck(boolean isStrict, String name) {
        if (this.isExtensible()) {
            return true;
        }
        if (isStrict) {
            throw ECMAErrors.typeError("object.non.extensible", name, ScriptRuntime.safeToString(this));
        }
        return false;
    }

    private static GuardedInvocation findMegaMorphicSetMethod(CallSiteDescriptor desc, String name) {
        Context.getContextTrusted().getLogger(ObjectClassGenerator.class).warning("Megamorphic setter: ", desc, " ", name);
        MethodType type = desc.getMethodType().insertParameterTypes(1, Object.class);
        GuardedInvocation inv = ScriptObject.findSetIndexMethod(desc, false, type);
        return inv.replaceMethods(Lookup.MH.insertArguments(inv.getInvocation(), 1, name), inv.getGuard());
    }

    private static Object globalFilter(Object object) {
        ScriptObject sobj;
        for (sobj = (ScriptObject)object; sobj != null && !(sobj instanceof Global); sobj = sobj.getProto()) {
        }
        return sobj;
    }

    protected GuardedInvocation findSetIndexMethod(CallSiteDescriptor desc, LinkRequest request) {
        return ScriptObject.findSetIndexMethod(desc, NashornGuards.explicitInstanceOfCheck(desc, request), desc.getMethodType());
    }

    private static GuardedInvocation findSetIndexMethod(CallSiteDescriptor desc, boolean explicitInstanceOfCheck, MethodType callType) {
        assert (callType.parameterCount() == 3);
        TypeDescriptor.OfField keyClass = callType.parameterType(1);
        TypeDescriptor.OfField valueClass = callType.parameterType(2);
        MethodHandle methodHandle = ScriptObject.findOwnMH_V("set", Void.TYPE, new Class[]{keyClass, valueClass, Integer.TYPE});
        methodHandle = Lookup.MH.insertArguments(methodHandle, 3, NashornCallSiteDescriptor.getFlags(desc));
        return new GuardedInvocation(methodHandle, ScriptObject.getScriptObjectGuard(callType, explicitInstanceOfCheck), (SwitchPoint)null, explicitInstanceOfCheck ? null : ClassCastException.class);
    }

    public GuardedInvocation noSuchMethod(CallSiteDescriptor desc, LinkRequest request) {
        boolean scopeCall;
        String name = NashornCallSiteDescriptor.getOperand(desc);
        FindProperty find = this.findProperty(NO_SUCH_METHOD_NAME, true);
        boolean bl = scopeCall = this.isScope() && NashornCallSiteDescriptor.isScope(desc);
        if (find == null) {
            return this.noSuchProperty(desc, request).addSwitchPoint(this.getProtoSwitchPoint(NO_SUCH_METHOD_NAME));
        }
        boolean explicitInstanceOfCheck = NashornGuards.explicitInstanceOfCheck(desc, request);
        Object value = find.getObjectValue();
        if (!(value instanceof ScriptFunction)) {
            return this.createEmptyGetter(desc, explicitInstanceOfCheck, name);
        }
        ScriptFunction func = (ScriptFunction)value;
        ScriptObject thiz = scopeCall && func.isStrict() ? ScriptRuntime.UNDEFINED : this;
        return new GuardedInvocation(Lookup.MH.dropArguments(Lookup.MH.constant(ScriptFunction.class, func.createBound(thiz, new Object[]{name})), 0, Object.class), NashornGuards.combineGuards(NashornGuards.getIdentityGuard(this), NashornGuards.getMapGuard(this.getMap(), true))).addSwitchPoint(this.getProtoSwitchPoint(name));
    }

    public GuardedInvocation noSuchProperty(CallSiteDescriptor desc, LinkRequest request) {
        boolean scopeAccess;
        String name = NashornCallSiteDescriptor.getOperand(desc);
        FindProperty find = this.findProperty(NO_SUCH_PROPERTY_NAME, true);
        boolean bl = scopeAccess = this.isScope() && NashornCallSiteDescriptor.isScope(desc);
        if (find != null) {
            Object value = find.getObjectValue();
            ScriptFunction func = null;
            MethodHandle mh = null;
            if (value instanceof ScriptFunction) {
                func = (ScriptFunction)value;
                mh = ScriptObject.getCallMethodHandle(func, desc.getMethodType(), name);
            }
            if (mh != null) {
                assert (func != null);
                if (scopeAccess && func.isStrict()) {
                    mh = ScriptObject.bindTo(mh, ScriptRuntime.UNDEFINED);
                }
                return new GuardedInvocation(mh, find.isSelf() ? ScriptObject.getKnownFunctionPropertyGuardSelf(this.getMap(), find.getGetter(Object.class, -1, request), func) : ScriptObject.getKnownFunctionPropertyGuardProto(this.getMap(), find.getGetter(Object.class, -1, request), find.getProtoChainLength(), func), this.getProtoSwitchPoints(NO_SUCH_PROPERTY_NAME, find.getOwner()), null).addSwitchPoint(this.getProtoSwitchPoint(name));
            }
        }
        if (scopeAccess) {
            throw ECMAErrors.referenceError("not.defined", name);
        }
        return this.createEmptyGetter(desc, NashornGuards.explicitInstanceOfCheck(desc, request), name);
    }

    protected Object invokeNoSuchProperty(Object key, boolean isScope, int programPoint) {
        FindProperty find = this.findProperty(NO_SUCH_PROPERTY_NAME, true);
        Object func = find != null ? find.getObjectValue() : null;
        Object ret = ScriptRuntime.UNDEFINED;
        if (func instanceof ScriptFunction) {
            ScriptFunction sfunc = (ScriptFunction)func;
            ScriptObject self = isScope && sfunc.isStrict() ? ScriptRuntime.UNDEFINED : this;
            ret = ScriptRuntime.apply(sfunc, self, key);
        } else if (isScope) {
            throw ECMAErrors.referenceError("not.defined", key.toString());
        }
        if (UnwarrantedOptimismException.isValid(programPoint)) {
            throw new UnwarrantedOptimismException(ret, programPoint);
        }
        return ret;
    }

    private Object getNoSuchMethod(String name, boolean isScope, int programPoint) {
        FindProperty find = this.findProperty(NO_SUCH_METHOD_NAME, true);
        if (find == null) {
            return this.invokeNoSuchProperty(name, isScope, programPoint);
        }
        Object value = find.getObjectValue();
        if (!(value instanceof ScriptFunction)) {
            if (isScope) {
                throw ECMAErrors.referenceError("not.defined", name);
            }
            return ScriptRuntime.UNDEFINED;
        }
        ScriptFunction func = (ScriptFunction)value;
        ScriptObject self = isScope && func.isStrict() ? ScriptRuntime.UNDEFINED : this;
        return func.createBound(self, new Object[]{name});
    }

    private GuardedInvocation createEmptyGetter(CallSiteDescriptor desc, boolean explicitInstanceOfCheck, String name) {
        if (NashornCallSiteDescriptor.isOptimistic(desc)) {
            throw new UnwarrantedOptimismException(ScriptRuntime.UNDEFINED, NashornCallSiteDescriptor.getProgramPoint(desc), Type.OBJECT);
        }
        return new GuardedInvocation(Lookup.emptyGetter(desc.getMethodType().returnType()), NashornGuards.getMapGuard(this.getMap(), explicitInstanceOfCheck), this.getProtoSwitchPoints(name, null), explicitInstanceOfCheck ? null : ClassCastException.class);
    }

    private Property addSpillProperty(Object key, int flags, Object value, boolean hasInitialValue) {
        Property property;
        PropertyMap propertyMap = this.getMap();
        int fieldSlot = propertyMap.getFreeFieldSlot();
        int propertyFlags = flags | (this.useDualFields() ? 2048 : 0);
        if (fieldSlot > -1) {
            property = hasInitialValue ? new AccessorProperty(key, propertyFlags, fieldSlot, this, value) : new AccessorProperty(key, propertyFlags, this.getClass(), fieldSlot);
            property = this.addOwnProperty(property);
        } else {
            int spillSlot = propertyMap.getFreeSpillSlot();
            property = hasInitialValue ? new SpillProperty(key, propertyFlags, spillSlot, this, value) : new SpillProperty(key, propertyFlags, spillSlot);
            property = this.addOwnProperty(property);
            this.ensureSpillSize(property.getSlot());
        }
        return property;
    }

    MethodHandle addSpill(Class<?> type, String key) {
        return this.addSpillProperty(key, 0, null, false).getSetter(type, this.getMap());
    }

    protected static MethodHandle pairArguments(MethodHandle methodHandle, MethodType callType) {
        return ScriptObject.pairArguments(methodHandle, callType, null);
    }

    public static MethodHandle pairArguments(MethodHandle methodHandle, MethodType callType, Boolean callerVarArg) {
        boolean isCallerVarArg;
        boolean isCalleeVarArg;
        MethodType methodType = methodHandle.type();
        if (methodType.equals((Object)callType.changeReturnType((Class<?>)methodType.returnType()))) {
            return methodHandle;
        }
        int parameterCount = methodType.parameterCount();
        int callCount = callType.parameterCount();
        boolean bl = isCalleeVarArg = parameterCount > 0 && ((Class)methodType.parameterType(parameterCount - 1)).isArray();
        boolean bl2 = callerVarArg != null ? callerVarArg : (isCallerVarArg = callCount > 0 && ((Class)callType.parameterType(callCount - 1)).isArray());
        if (isCalleeVarArg) {
            return isCallerVarArg ? methodHandle : Lookup.MH.asCollector(methodHandle, Object[].class, callCount - parameterCount + 1);
        }
        if (isCallerVarArg) {
            return ScriptObject.adaptHandleToVarArgCallSite(methodHandle, callCount);
        }
        if (callCount < parameterCount) {
            int missingArgs = parameterCount - callCount;
            Object[] fillers = new Object[missingArgs];
            Arrays.fill(fillers, ScriptRuntime.UNDEFINED);
            return Lookup.MH.insertArguments(methodHandle, parameterCount - missingArgs, fillers);
        }
        if (callCount > parameterCount) {
            int discardedArgs = callCount - parameterCount;
            Object[] discards = new Class[discardedArgs];
            Arrays.fill(discards, Object.class);
            return Lookup.MH.dropArguments(methodHandle, callCount - discardedArgs, (Class<?>[])discards);
        }
        return methodHandle;
    }

    static MethodHandle adaptHandleToVarArgCallSite(MethodHandle mh, int callSiteParamCount) {
        int spreadArgs = mh.type().parameterCount() - callSiteParamCount + 1;
        return Lookup.MH.filterArguments(Lookup.MH.asSpreader(mh, Object[].class, spreadArgs), callSiteParamCount - 1, Lookup.MH.insertArguments(TRUNCATINGFILTER, 0, spreadArgs));
    }

    private static Object[] truncatingFilter(int n, Object[] array) {
        int length;
        int n2 = length = array == null ? 0 : array.length;
        if (n == length) {
            return array == null ? ScriptRuntime.EMPTY_ARRAY : array;
        }
        Object[] newArray = new Object[n];
        if (array != null) {
            System.arraycopy(array, 0, newArray, 0, Math.min(n, length));
        }
        if (length < n) {
            Arrays.fill(newArray, length, n, ScriptRuntime.UNDEFINED);
        }
        return newArray;
    }

    public final void setLength(long newLength) {
        ArrayData data = this.getArray();
        long arrayLength = data.length();
        if (newLength == arrayLength) {
            return;
        }
        if (newLength > arrayLength) {
            this.setArray(data.ensure(newLength - 1L).safeDelete(arrayLength, newLength - 1L, false));
            return;
        }
        long actualLength = newLength;
        if (this.getMap().containsArrayKeys()) {
            for (long l = arrayLength - 1L; l >= newLength; --l) {
                FindProperty find = this.findProperty(JSType.toString(l), false);
                if (find == null) continue;
                if (find.getProperty().isConfigurable()) {
                    this.deleteOwnProperty(find.getProperty());
                    continue;
                }
                actualLength = l + 1L;
                break;
            }
        }
        this.setArray(data.shrink(actualLength));
        data.setLength(actualLength);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private int getInt(int index, Object key, int programPoint) {
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData array;
            ScriptObject object = this;
            do {
                FindProperty find;
                if (object.getMap().containsArrayKeys() && (find = object.findProperty(key, false)) != null) {
                    return ScriptObject.getIntValue(find, programPoint);
                }
                if ((object = object.getProto()) == null) return JSType.toInt32(this.invokeNoSuchProperty(key, false, programPoint));
            } while (!(array = object.getArray()).has(index));
            return UnwarrantedOptimismException.isValid(programPoint) ? array.getIntOptimistic(index, programPoint) : array.getInt(index);
        }
        FindProperty find = this.findProperty(key, true);
        if (find == null) return JSType.toInt32(this.invokeNoSuchProperty(key, false, programPoint));
        return ScriptObject.getIntValue(find, programPoint);
    }

    @Override
    public int getInt(Object key, int programPoint) {
        Object primitiveKey = JSType.toPrimitive(key, String.class);
        int index = ArrayIndex.getArrayIndex(primitiveKey);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            return UnwarrantedOptimismException.isValid(programPoint) ? array.getIntOptimistic(index, programPoint) : array.getInt(index);
        }
        return this.getInt(index, JSType.toPropertyKey(primitiveKey), programPoint);
    }

    @Override
    public int getInt(double key, int programPoint) {
        int index = ArrayIndex.getArrayIndex(key);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            return UnwarrantedOptimismException.isValid(programPoint) ? array.getIntOptimistic(index, programPoint) : array.getInt(index);
        }
        return this.getInt(index, JSType.toString(key), programPoint);
    }

    @Override
    public int getInt(int key, int programPoint) {
        int index = ArrayIndex.getArrayIndex(key);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            return UnwarrantedOptimismException.isValid(programPoint) ? array.getIntOptimistic(key, programPoint) : array.getInt(key);
        }
        return this.getInt(index, JSType.toString(key), programPoint);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private double getDouble(int index, Object key, int programPoint) {
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData array;
            ScriptObject object = this;
            do {
                FindProperty find;
                if (object.getMap().containsArrayKeys() && (find = object.findProperty(key, false)) != null) {
                    return ScriptObject.getDoubleValue(find, programPoint);
                }
                if ((object = object.getProto()) == null) return JSType.toNumber(this.invokeNoSuchProperty(key, false, -1));
            } while (!(array = object.getArray()).has(index));
            return UnwarrantedOptimismException.isValid(programPoint) ? array.getDoubleOptimistic(index, programPoint) : array.getDouble(index);
        }
        FindProperty find = this.findProperty(key, true);
        if (find == null) return JSType.toNumber(this.invokeNoSuchProperty(key, false, -1));
        return ScriptObject.getDoubleValue(find, programPoint);
    }

    @Override
    public double getDouble(Object key, int programPoint) {
        Object primitiveKey = JSType.toPrimitive(key, String.class);
        int index = ArrayIndex.getArrayIndex(primitiveKey);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            return UnwarrantedOptimismException.isValid(programPoint) ? array.getDoubleOptimistic(index, programPoint) : array.getDouble(index);
        }
        return this.getDouble(index, JSType.toPropertyKey(primitiveKey), programPoint);
    }

    @Override
    public double getDouble(double key, int programPoint) {
        int index = ArrayIndex.getArrayIndex(key);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            return UnwarrantedOptimismException.isValid(programPoint) ? array.getDoubleOptimistic(index, programPoint) : array.getDouble(index);
        }
        return this.getDouble(index, JSType.toString(key), programPoint);
    }

    @Override
    public double getDouble(int key, int programPoint) {
        int index = ArrayIndex.getArrayIndex(key);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            return UnwarrantedOptimismException.isValid(programPoint) ? array.getDoubleOptimistic(key, programPoint) : array.getDouble(key);
        }
        return this.getDouble(index, JSType.toString(key), programPoint);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Object get(int index, Object key) {
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData array;
            ScriptObject object = this;
            do {
                FindProperty find;
                if (object.getMap().containsArrayKeys() && (find = object.findProperty(key, false)) != null) {
                    return find.getObjectValue();
                }
                if ((object = object.getProto()) == null) return this.invokeNoSuchProperty(key, false, -1);
            } while (!(array = object.getArray()).has(index));
            return array.getObject(index);
        }
        FindProperty find = this.findProperty(key, true);
        if (find == null) return this.invokeNoSuchProperty(key, false, -1);
        return find.getObjectValue();
    }

    @Override
    public Object get(Object key) {
        Object primitiveKey = JSType.toPrimitive(key, String.class);
        int index = ArrayIndex.getArrayIndex(primitiveKey);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            return array.getObject(index);
        }
        return this.get(index, JSType.toPropertyKey(primitiveKey));
    }

    @Override
    public Object get(double key) {
        int index = ArrayIndex.getArrayIndex(key);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            return array.getObject(index);
        }
        return this.get(index, JSType.toString(key));
    }

    @Override
    public Object get(int key) {
        int index = ArrayIndex.getArrayIndex(key);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            return array.getObject(index);
        }
        return this.get(index, JSType.toString(key));
    }

    private boolean doesNotHaveCheckArrayKeys(long longIndex, int value, int callSiteFlags) {
        String key;
        FindProperty find;
        if (this.hasDefinedArrayProperties() && (find = this.findProperty(key = JSType.toString(longIndex), true)) != null) {
            this.setObject(find, callSiteFlags, key, value);
            return true;
        }
        return false;
    }

    private boolean doesNotHaveCheckArrayKeys(long longIndex, double value, int callSiteFlags) {
        String key;
        FindProperty find;
        if (this.hasDefinedArrayProperties() && (find = this.findProperty(key = JSType.toString(longIndex), true)) != null) {
            this.setObject(find, callSiteFlags, key, value);
            return true;
        }
        return false;
    }

    private boolean doesNotHaveCheckArrayKeys(long longIndex, Object value, int callSiteFlags) {
        String key;
        FindProperty find;
        if (this.hasDefinedArrayProperties() && (find = this.findProperty(key = JSType.toString(longIndex), true)) != null) {
            this.setObject(find, callSiteFlags, key, value);
            return true;
        }
        return false;
    }

    private boolean hasDefinedArrayProperties() {
        for (ScriptObject obj = this; obj != null; obj = obj.getProto()) {
            if (!obj.getMap().containsArrayKeys()) continue;
            return true;
        }
        return false;
    }

    private boolean doesNotHaveEnsureLength(long longIndex, long oldLength, int callSiteFlags) {
        if (longIndex >= oldLength) {
            if (!this.isExtensible()) {
                if (NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)) {
                    throw ECMAErrors.typeError("object.non.extensible", JSType.toString(longIndex), ScriptRuntime.safeToString(this));
                }
                return true;
            }
            this.setArray(this.getArray().ensure(longIndex));
        }
        return false;
    }

    private void doesNotHave(int index, int value, int callSiteFlags) {
        long oldLength = this.getArray().length();
        long longIndex = ArrayIndex.toLongIndex(index);
        if (!this.doesNotHaveCheckArrayKeys(longIndex, value, callSiteFlags) && !this.doesNotHaveEnsureLength(longIndex, oldLength, callSiteFlags)) {
            boolean strict = NashornCallSiteDescriptor.isStrictFlag(callSiteFlags);
            this.setArray(this.getArray().set(index, value, strict).safeDelete(oldLength, longIndex - 1L, strict));
        }
    }

    private void doesNotHave(int index, double value, int callSiteFlags) {
        long oldLength = this.getArray().length();
        long longIndex = ArrayIndex.toLongIndex(index);
        if (!this.doesNotHaveCheckArrayKeys(longIndex, value, callSiteFlags) && !this.doesNotHaveEnsureLength(longIndex, oldLength, callSiteFlags)) {
            boolean strict = NashornCallSiteDescriptor.isStrictFlag(callSiteFlags);
            this.setArray(this.getArray().set(index, value, strict).safeDelete(oldLength, longIndex - 1L, strict));
        }
    }

    private void doesNotHave(int index, Object value, int callSiteFlags) {
        long oldLength = this.getArray().length();
        long longIndex = ArrayIndex.toLongIndex(index);
        if (!this.doesNotHaveCheckArrayKeys(longIndex, value, callSiteFlags) && !this.doesNotHaveEnsureLength(longIndex, oldLength, callSiteFlags)) {
            boolean strict = NashornCallSiteDescriptor.isStrictFlag(callSiteFlags);
            this.setArray(this.getArray().set(index, value, strict).safeDelete(oldLength, longIndex - 1L, strict));
        }
    }

    public final void setObject(FindProperty find, int callSiteFlags, Object key, Object value) {
        FindProperty f = find;
        this.invalidateGlobalConstant(key);
        if (f != null && f.isInheritedOrdinaryProperty()) {
            boolean isScope = NashornCallSiteDescriptor.isScopeFlag(callSiteFlags);
            if (isScope && f.getSelf() != this) {
                f.getSelf().setObject(null, 0, key, value);
                return;
            }
            if (!isScope || !f.getOwner().isScope()) {
                f = null;
            }
        }
        if (f != null) {
            if (!f.getProperty().isWritable() && !NashornCallSiteDescriptor.isDeclaration(callSiteFlags) || !f.getProperty().hasNativeSetter()) {
                if (NashornCallSiteDescriptor.isScopeFlag(callSiteFlags) && f.getProperty().isLexicalBinding()) {
                    throw ECMAErrors.typeError("assign.constant", key.toString());
                }
                if (NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)) {
                    throw ECMAErrors.typeError(f.getProperty().isAccessorProperty() ? "property.has.no.setter" : "property.not.writable", key.toString(), ScriptRuntime.safeToString(this));
                }
                return;
            }
            if (NashornCallSiteDescriptor.isDeclaration(callSiteFlags) && f.getProperty().needsDeclaration()) {
                f.getOwner().declareAndSet(f, value);
                return;
            }
            f.setValue(value, NashornCallSiteDescriptor.isStrictFlag(callSiteFlags));
        } else if (!this.isExtensible()) {
            if (NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)) {
                throw ECMAErrors.typeError("object.non.extensible", key.toString(), ScriptRuntime.safeToString(this));
            }
        } else {
            ScriptObject sobj;
            if (this.isScope()) {
                for (sobj = this; sobj != null && !(sobj instanceof Global); sobj = sobj.getProto()) {
                }
                assert (sobj != null) : "no parent global object in scope";
            }
            sobj.addSpillProperty(key, 0, value, true);
        }
    }

    @Override
    public void set(Object key, int value, int callSiteFlags) {
        Object primitiveKey = JSType.toPrimitive(key, String.class);
        int index = ArrayIndex.getArrayIndex(primitiveKey);
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData data = this.getArray();
            if (data.has(index)) {
                this.setArray(data.set(index, value, NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)));
            } else {
                this.doesNotHave(index, value, callSiteFlags);
            }
            return;
        }
        Object propName = JSType.toPropertyKey(primitiveKey);
        this.setObject(this.findProperty(propName, true), callSiteFlags, propName, JSType.toObject(value));
    }

    @Override
    public void set(Object key, double value, int callSiteFlags) {
        Object primitiveKey = JSType.toPrimitive(key, String.class);
        int index = ArrayIndex.getArrayIndex(primitiveKey);
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData data = this.getArray();
            if (data.has(index)) {
                this.setArray(data.set(index, value, NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)));
            } else {
                this.doesNotHave(index, value, callSiteFlags);
            }
            return;
        }
        Object propName = JSType.toPropertyKey(primitiveKey);
        this.setObject(this.findProperty(propName, true), callSiteFlags, propName, JSType.toObject(value));
    }

    @Override
    public void set(Object key, Object value, int callSiteFlags) {
        Object primitiveKey = JSType.toPrimitive(key, String.class);
        int index = ArrayIndex.getArrayIndex(primitiveKey);
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData data = this.getArray();
            if (data.has(index)) {
                this.setArray(data.set(index, value, NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)));
            } else {
                this.doesNotHave(index, value, callSiteFlags);
            }
            return;
        }
        Object propName = JSType.toPropertyKey(primitiveKey);
        this.setObject(this.findProperty(propName, true), callSiteFlags, propName, value);
    }

    @Override
    public void set(double key, int value, int callSiteFlags) {
        int index = ArrayIndex.getArrayIndex(key);
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData data = this.getArray();
            if (data.has(index)) {
                this.setArray(data.set(index, value, NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)));
            } else {
                this.doesNotHave(index, value, callSiteFlags);
            }
            return;
        }
        String propName = JSType.toString(key);
        this.setObject(this.findProperty(propName, true), callSiteFlags, propName, JSType.toObject(value));
    }

    @Override
    public void set(double key, double value, int callSiteFlags) {
        int index = ArrayIndex.getArrayIndex(key);
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData data = this.getArray();
            if (data.has(index)) {
                this.setArray(data.set(index, value, NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)));
            } else {
                this.doesNotHave(index, value, callSiteFlags);
            }
            return;
        }
        String propName = JSType.toString(key);
        this.setObject(this.findProperty(propName, true), callSiteFlags, propName, JSType.toObject(value));
    }

    @Override
    public void set(double key, Object value, int callSiteFlags) {
        int index = ArrayIndex.getArrayIndex(key);
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData data = this.getArray();
            if (data.has(index)) {
                this.setArray(data.set(index, value, NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)));
            } else {
                this.doesNotHave(index, value, callSiteFlags);
            }
            return;
        }
        String propName = JSType.toString(key);
        this.setObject(this.findProperty(propName, true), callSiteFlags, propName, value);
    }

    @Override
    public void set(int key, int value, int callSiteFlags) {
        int index = ArrayIndex.getArrayIndex(key);
        if (ArrayIndex.isValidArrayIndex(index)) {
            if (this.getArray().has(index)) {
                ArrayData data = this.getArray();
                this.setArray(data.set(index, value, NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)));
            } else {
                this.doesNotHave(index, value, callSiteFlags);
            }
            return;
        }
        String propName = JSType.toString(key);
        this.setObject(this.findProperty(propName, true), callSiteFlags, propName, JSType.toObject(value));
    }

    @Override
    public void set(int key, double value, int callSiteFlags) {
        int index = ArrayIndex.getArrayIndex(key);
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData data = this.getArray();
            if (data.has(index)) {
                this.setArray(data.set(index, value, NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)));
            } else {
                this.doesNotHave(index, value, callSiteFlags);
            }
            return;
        }
        String propName = JSType.toString(key);
        this.setObject(this.findProperty(propName, true), callSiteFlags, propName, JSType.toObject(value));
    }

    @Override
    public void set(int key, Object value, int callSiteFlags) {
        int index = ArrayIndex.getArrayIndex(key);
        if (ArrayIndex.isValidArrayIndex(index)) {
            ArrayData data = this.getArray();
            if (data.has(index)) {
                this.setArray(data.set(index, value, NashornCallSiteDescriptor.isStrictFlag(callSiteFlags)));
            } else {
                this.doesNotHave(index, value, callSiteFlags);
            }
            return;
        }
        String propName = JSType.toString(key);
        this.setObject(this.findProperty(propName, true), callSiteFlags, propName, value);
    }

    @Override
    public boolean has(Object key) {
        Object primitiveKey = JSType.toPrimitive(key);
        int index = ArrayIndex.getArrayIndex(primitiveKey);
        return ArrayIndex.isValidArrayIndex(index) ? this.hasArrayProperty(index) : this.hasProperty(JSType.toPropertyKey(primitiveKey), true);
    }

    @Override
    public boolean has(double key) {
        int index = ArrayIndex.getArrayIndex(key);
        return ArrayIndex.isValidArrayIndex(index) ? this.hasArrayProperty(index) : this.hasProperty(JSType.toString(key), true);
    }

    @Override
    public boolean has(int key) {
        int index = ArrayIndex.getArrayIndex(key);
        return ArrayIndex.isValidArrayIndex(index) ? this.hasArrayProperty(index) : this.hasProperty(JSType.toString(key), true);
    }

    private boolean hasArrayProperty(int index) {
        boolean hasArrayKeys = false;
        for (ScriptObject self = this; self != null; self = self.getProto()) {
            if (self.getArray().has(index)) {
                return true;
            }
            hasArrayKeys = hasArrayKeys || self.getMap().containsArrayKeys();
        }
        return hasArrayKeys && this.hasProperty(ArrayIndex.toKey(index), true);
    }

    @Override
    public boolean hasOwnProperty(Object key) {
        Object primitiveKey = JSType.toPrimitive(key, String.class);
        int index = ArrayIndex.getArrayIndex(primitiveKey);
        return ArrayIndex.isValidArrayIndex(index) ? this.hasOwnArrayProperty(index) : this.hasProperty(JSType.toPropertyKey(primitiveKey), false);
    }

    @Override
    public boolean hasOwnProperty(int key) {
        int index = ArrayIndex.getArrayIndex(key);
        return ArrayIndex.isValidArrayIndex(index) ? this.hasOwnArrayProperty(index) : this.hasProperty(JSType.toString(key), false);
    }

    @Override
    public boolean hasOwnProperty(double key) {
        int index = ArrayIndex.getArrayIndex(key);
        return ArrayIndex.isValidArrayIndex(index) ? this.hasOwnArrayProperty(index) : this.hasProperty(JSType.toString(key), false);
    }

    private boolean hasOwnArrayProperty(int index) {
        return this.getArray().has(index) || this.getMap().containsArrayKeys() && this.hasProperty(ArrayIndex.toKey(index), false);
    }

    @Override
    public boolean delete(int key, boolean strict) {
        int index = ArrayIndex.getArrayIndex(key);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            if (array.canDelete(index, strict)) {
                this.setArray(array.delete(index));
                return true;
            }
            return false;
        }
        return this.deleteObject(JSType.toObject(key), strict);
    }

    @Override
    public boolean delete(double key, boolean strict) {
        int index = ArrayIndex.getArrayIndex(key);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            if (array.canDelete(index, strict)) {
                this.setArray(array.delete(index));
                return true;
            }
            return false;
        }
        return this.deleteObject(JSType.toObject(key), strict);
    }

    @Override
    public boolean delete(Object key, boolean strict) {
        Object primitiveKey = JSType.toPrimitive(key, String.class);
        int index = ArrayIndex.getArrayIndex(primitiveKey);
        ArrayData array = this.getArray();
        if (array.has(index)) {
            if (array.canDelete(index, strict)) {
                this.setArray(array.delete(index));
                return true;
            }
            return false;
        }
        return this.deleteObject(primitiveKey, strict);
    }

    private boolean deleteObject(Object key, boolean strict) {
        Object propName = JSType.toPropertyKey(key);
        FindProperty find = this.findProperty(propName, false);
        if (find == null) {
            return true;
        }
        if (!find.getProperty().isConfigurable()) {
            if (strict) {
                throw ECMAErrors.typeError("cant.delete.property", propName.toString(), ScriptRuntime.safeToString(this));
            }
            return false;
        }
        Property prop = find.getProperty();
        this.deleteOwnProperty(prop);
        return true;
    }

    public final ScriptObject copy() {
        try {
            return this.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    protected ScriptObject clone() throws CloneNotSupportedException {
        ScriptObject clone = (ScriptObject)super.clone();
        if (this.objectSpill != null) {
            clone.objectSpill = (Object[])this.objectSpill.clone();
            if (this.primitiveSpill != null) {
                clone.primitiveSpill = (long[])this.primitiveSpill.clone();
            }
        }
        clone.arrayData = this.arrayData.copy();
        return clone;
    }

    protected final UserAccessorProperty newUserAccessors(Object key, int propertyFlags, ScriptFunction getter, ScriptFunction setter) {
        UserAccessorProperty uc = this.getMap().newUserAccessors(key, propertyFlags);
        uc.setAccessors(this, this.getMap(), new UserAccessorProperty.Accessors(getter, setter));
        return uc;
    }

    protected boolean useDualFields() {
        return !StructureLoader.isSingleFieldStructure(this.getClass().getName());
    }

    Object ensureSpillSize(int slot) {
        long[] newPrimitiveSpill;
        int oldLength;
        int n = oldLength = this.objectSpill == null ? 0 : this.objectSpill.length;
        if (slot < oldLength) {
            return this;
        }
        int newLength = ScriptObject.alignUp(slot + 1, 8);
        Object[] newObjectSpill = new Object[newLength];
        long[] lArray = newPrimitiveSpill = this.useDualFields() ? new long[newLength] : null;
        if (this.objectSpill != null) {
            System.arraycopy(this.objectSpill, 0, newObjectSpill, 0, oldLength);
            if (this.primitiveSpill != null && newPrimitiveSpill != null) {
                System.arraycopy(this.primitiveSpill, 0, newPrimitiveSpill, 0, oldLength);
            }
        }
        this.primitiveSpill = newPrimitiveSpill;
        this.objectSpill = newObjectSpill;
        return this;
    }

    private static MethodHandle findOwnMH_V(String name, Class<?> rtype, Class<?> ... types) {
        return Lookup.MH.findVirtual(MethodHandles.lookup(), ScriptObject.class, name, Lookup.MH.type(rtype, types));
    }

    private static MethodHandle findOwnMH_S(String name, Class<?> rtype, Class<?> ... types) {
        return Lookup.MH.findStatic(MethodHandles.lookup(), ScriptObject.class, name, Lookup.MH.type(rtype, types));
    }

    private static MethodHandle getKnownFunctionPropertyGuardSelf(PropertyMap map, MethodHandle getter, ScriptFunction func) {
        return Lookup.MH.insertArguments(KNOWNFUNCPROPGUARDSELF, 1, map, getter, func);
    }

    private static boolean knownFunctionPropertyGuardSelf(Object self, PropertyMap map, MethodHandle getter, ScriptFunction func) {
        if (self instanceof ScriptObject && ((ScriptObject)self).getMap() == map) {
            try {
                return getter.invokeExact(self) == func;
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return false;
    }

    private static MethodHandle getKnownFunctionPropertyGuardProto(PropertyMap map, MethodHandle getter, int depth, ScriptFunction func) {
        return Lookup.MH.insertArguments(KNOWNFUNCPROPGUARDPROTO, 1, map, getter, depth, func);
    }

    private static ScriptObject getProto(ScriptObject self, int depth) {
        ScriptObject proto = self;
        for (int d = 0; d < depth; ++d) {
            if ((proto = proto.getProto()) != null) continue;
            return null;
        }
        return proto;
    }

    private static boolean knownFunctionPropertyGuardProto(Object self, PropertyMap map, MethodHandle getter, int depth, ScriptFunction func) {
        if (self instanceof ScriptObject && ((ScriptObject)self).getMap() == map) {
            ScriptObject proto = ScriptObject.getProto((ScriptObject)self, depth);
            if (proto == null) {
                return false;
            }
            try {
                return getter.invokeExact(proto) == func;
            }
            catch (Error | RuntimeException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return false;
    }

    public static long getCount() {
        return count.longValue();
    }

    static {
        if (Context.DEBUG) {
            count = new LongAdder();
        }
    }

    private static class ValueIterator
    extends ScriptObjectIterator<Object> {
        ValueIterator(ScriptObject object) {
            super(object);
        }

        @Override
        protected void init() {
            ArrayList<Object> valueList = new ArrayList<Object>();
            HashSet nonEnumerable = new HashSet();
            for (ScriptObject self = this.object; self != null; self = self.getProto()) {
                for (String key : self.getOwnKeys(String.class, false, nonEnumerable)) {
                    valueList.add(self.get(key));
                }
            }
            this.values = valueList.toArray(new Object[0]);
        }
    }

    private static class KeyIterator
    extends ScriptObjectIterator<String> {
        KeyIterator(ScriptObject object) {
            super(object);
        }

        @Override
        protected void init() {
            LinkedHashSet<String> keys = new LinkedHashSet<String>();
            HashSet nonEnumerable = new HashSet();
            for (ScriptObject self = this.object; self != null; self = self.getProto()) {
                keys.addAll(Arrays.asList(self.getOwnKeys(String.class, false, nonEnumerable)));
            }
            this.values = keys.toArray(new String[0]);
        }
    }

    private static abstract class ScriptObjectIterator<T>
    implements Iterator<T> {
        protected T[] values;
        protected final ScriptObject object;
        private int index;

        ScriptObjectIterator(ScriptObject object) {
            this.object = object;
        }

        protected abstract void init();

        @Override
        public boolean hasNext() {
            if (this.values == null) {
                this.init();
            }
            return this.index < this.values.length;
        }

        @Override
        public T next() {
            if (this.values == null) {
                this.init();
            }
            return this.values[this.index++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}

