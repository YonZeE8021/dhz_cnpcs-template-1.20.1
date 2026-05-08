/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.internal.runtime.linker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Supplier;
import javax.script.Bindings;
import jdk.dynalink.CallSiteDescriptor;
import jdk.dynalink.SecureLookupSupplier;
import jdk.dynalink.linker.ConversionComparator;
import jdk.dynalink.linker.GuardedInvocation;
import jdk.dynalink.linker.GuardingTypeConverterFactory;
import jdk.dynalink.linker.LinkRequest;
import jdk.dynalink.linker.LinkerServices;
import jdk.dynalink.linker.TypeBasedGuardingDynamicLinker;
import jdk.dynalink.linker.support.Guards;
import jdk.dynalink.linker.support.Lookup;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.openjdk.nashorn.api.scripting.ScriptUtils;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.objects.NativeArray;
import org.openjdk.nashorn.internal.runtime.AccessControlContextFactory;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.ListAdapter;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.Undefined;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;
import org.openjdk.nashorn.internal.runtime.linker.JavaAdapterFactory;
import org.openjdk.nashorn.internal.runtime.linker.JavaArgumentConverters;

final class NashornLinker
implements TypeBasedGuardingDynamicLinker,
GuardingTypeConverterFactory,
ConversionComparator {
    private static final AccessControlContext GET_LOOKUP_PERMISSION_CONTEXT = AccessControlContextFactory.createAccessControlContext("dynalink.getLookup");
    private static final ClassValue<MethodHandle> ARRAY_CONVERTERS = new ClassValue<MethodHandle>(){

        @Override
        protected MethodHandle computeValue(Class<?> type) {
            return NashornLinker.createArrayConverter(type);
        }
    };
    private static final MethodHandle IS_SCRIPT_OBJECT = Guards.isInstance(ScriptObject.class, org.openjdk.nashorn.internal.lookup.Lookup.MH.type(Boolean.TYPE, Object.class));
    private static final MethodHandle IS_FUNCTION = NashornLinker.findOwnMH("isFunction", Boolean.TYPE, Object.class);
    private static final MethodHandle IS_NATIVE_ARRAY = Guards.isOfClass(NativeArray.class, org.openjdk.nashorn.internal.lookup.Lookup.MH.type(Boolean.TYPE, Object.class));
    private static final MethodHandle IS_NASHORN_OR_UNDEFINED_TYPE = NashornLinker.findOwnMH("isNashornTypeOrUndefined", Boolean.TYPE, Object.class);
    private static final MethodHandle CREATE_MIRROR = NashornLinker.findOwnMH("createMirror", Object.class, Object.class);
    private static final MethodHandle TO_COLLECTION;
    private static final MethodHandle TO_DEQUE;
    private static final MethodHandle TO_LIST;
    private static final MethodHandle TO_QUEUE;

    NashornLinker() {
    }

    @Override
    public boolean canLinkType(Class<?> type) {
        return NashornLinker.canLinkTypeStatic(type);
    }

    static boolean canLinkTypeStatic(Class<?> type) {
        return ScriptObject.class.isAssignableFrom(type) || Undefined.class == type;
    }

    @Override
    public GuardedInvocation getGuardedInvocation(LinkRequest request, LinkerServices linkerServices) {
        CallSiteDescriptor desc = request.getCallSiteDescriptor();
        return Bootstrap.asTypeSafeReturn(NashornLinker.getGuardedInvocation(request, desc), linkerServices, desc);
    }

    private static GuardedInvocation getGuardedInvocation(LinkRequest request, CallSiteDescriptor desc) {
        GuardedInvocation inv;
        Object self = request.getReceiver();
        if (self instanceof ScriptObject) {
            inv = ((ScriptObject)self).lookup(desc, request);
        } else if (self instanceof Undefined) {
            inv = Undefined.lookup(desc);
        } else {
            throw new AssertionError((Object)self.getClass().getName());
        }
        return inv;
    }

    @Override
    public GuardedInvocation convertToType(Class<?> sourceType, Class<?> targetType, Supplier<MethodHandles.Lookup> lookupSupplier) throws Exception {
        GuardedInvocation gi = NashornLinker.convertToTypeNoCast(sourceType, targetType, lookupSupplier);
        if (gi == null) {
            gi = NashornLinker.getSamTypeConverter(sourceType, targetType, lookupSupplier);
        }
        return gi == null ? null : gi.asType(org.openjdk.nashorn.internal.lookup.Lookup.MH.type(targetType, sourceType));
    }

    private static GuardedInvocation convertToTypeNoCast(Class<?> sourceType, Class<?> targetType, Supplier<MethodHandles.Lookup> lookupSupplier) {
        MethodHandle mh = JavaArgumentConverters.getConverter(targetType);
        if (mh != null) {
            return new GuardedInvocation(mh, NashornLinker.canLinkTypeStatic(sourceType) ? null : IS_NASHORN_OR_UNDEFINED_TYPE);
        }
        GuardedInvocation arrayConverter = NashornLinker.getArrayConverter(sourceType, targetType, lookupSupplier);
        if (arrayConverter != null) {
            return arrayConverter;
        }
        return NashornLinker.getMirrorConverter(sourceType, targetType);
    }

    private static GuardedInvocation getSamTypeConverter(Class<?> sourceType, Class<?> targetType, Supplier<MethodHandles.Lookup> lookupSupplier) throws Exception {
        boolean isSourceTypeGeneric = sourceType.isAssignableFrom(ScriptObject.class);
        if ((isSourceTypeGeneric || ScriptFunction.class.isAssignableFrom(sourceType)) && NashornLinker.isAutoConvertibleFromFunction(targetType)) {
            Class paramType = isSourceTypeGeneric ? Object.class : ScriptFunction.class;
            MethodHandle ctor = JavaAdapterFactory.getConstructor(paramType, targetType, NashornLinker.getCurrentLookup(lookupSupplier));
            assert (ctor != null);
            return new GuardedInvocation(ctor, isSourceTypeGeneric ? IS_FUNCTION : null);
        }
        return null;
    }

    private static MethodHandles.Lookup getCurrentLookup(Supplier<MethodHandles.Lookup> lookupSupplier) {
        return AccessController.doPrivileged(lookupSupplier::get, GET_LOOKUP_PERMISSION_CONTEXT);
    }

    private static GuardedInvocation getArrayConverter(Class<?> sourceType, Class<?> targetType, Supplier<MethodHandles.Lookup> lookupSupplier) {
        boolean isSourceTypeGeneric;
        boolean isSourceTypeNativeArray = sourceType == NativeArray.class;
        boolean bl = isSourceTypeGeneric = !isSourceTypeNativeArray && sourceType.isAssignableFrom(NativeArray.class);
        if (isSourceTypeNativeArray || isSourceTypeGeneric) {
            MethodHandle guard;
            MethodHandle methodHandle = guard = isSourceTypeGeneric ? IS_NATIVE_ARRAY : null;
            if (targetType.isArray()) {
                MethodHandle mhWithLookup;
                MethodHandle mh = ARRAY_CONVERTERS.get(targetType);
                if (mh.type().parameterCount() == 2) {
                    assert (mh.type().parameterType(1) == SecureLookupSupplier.class);
                    mhWithLookup = org.openjdk.nashorn.internal.lookup.Lookup.MH.insertArguments(mh, 1, new SecureLookupSupplier(NashornLinker.getCurrentLookup(lookupSupplier)));
                } else {
                    mhWithLookup = mh;
                }
                return new GuardedInvocation(mhWithLookup, guard);
            }
            if (targetType == List.class) {
                return new GuardedInvocation(TO_LIST, guard);
            }
            if (targetType == Deque.class) {
                return new GuardedInvocation(TO_DEQUE, guard);
            }
            if (targetType == Queue.class) {
                return new GuardedInvocation(TO_QUEUE, guard);
            }
            if (targetType == Collection.class) {
                return new GuardedInvocation(TO_COLLECTION, guard);
            }
        }
        return null;
    }

    private static MethodHandle createArrayConverter(Class<?> type) {
        assert (type.isArray());
        Class<?> componentType = type.getComponentType();
        CompilerConstants.Call converterCall = NashornLinker.isComponentTypeAutoConvertibleFromFunction(componentType) ? JSType.TO_JAVA_ARRAY_WITH_LOOKUP : JSType.TO_JAVA_ARRAY;
        MethodHandle typeBoundConverter = org.openjdk.nashorn.internal.lookup.Lookup.MH.insertArguments(converterCall.methodHandle(), 1, componentType);
        return org.openjdk.nashorn.internal.lookup.Lookup.MH.asType(typeBoundConverter, typeBoundConverter.type().changeReturnType(type));
    }

    private static boolean isComponentTypeAutoConvertibleFromFunction(Class<?> targetType) {
        if (targetType.isArray()) {
            return NashornLinker.isComponentTypeAutoConvertibleFromFunction(targetType.getComponentType());
        }
        return NashornLinker.isAutoConvertibleFromFunction(targetType);
    }

    private static GuardedInvocation getMirrorConverter(Class<?> sourceType, Class<?> targetType) {
        if (targetType == Map.class || targetType == Bindings.class || targetType == JSObject.class || targetType == ScriptObjectMirror.class) {
            if (ScriptObject.class.isAssignableFrom(sourceType)) {
                return new GuardedInvocation(CREATE_MIRROR);
            }
            if (sourceType.isAssignableFrom(ScriptObject.class) || sourceType.isInterface()) {
                return new GuardedInvocation(CREATE_MIRROR, IS_SCRIPT_OBJECT);
            }
        }
        return null;
    }

    private static boolean isAutoConvertibleFromFunction(Class<?> clazz) {
        return NashornLinker.isAbstractClass(clazz) && !ScriptObject.class.isAssignableFrom(clazz) && JavaAdapterFactory.isAutoConvertibleFromFunction(clazz);
    }

    static boolean isAbstractClass(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers()) && !clazz.isArray();
    }

    @Override
    public ConversionComparator.Comparison compareConversion(Class<?> sourceType, Class<?> targetType1, Class<?> targetType2) {
        if (sourceType == NativeArray.class) {
            if (NashornLinker.isArrayPreferredTarget(targetType1)) {
                if (!NashornLinker.isArrayPreferredTarget(targetType2)) {
                    return ConversionComparator.Comparison.TYPE_1_BETTER;
                }
            } else if (NashornLinker.isArrayPreferredTarget(targetType2)) {
                return ConversionComparator.Comparison.TYPE_2_BETTER;
            }
            if (targetType1.isArray()) {
                if (!targetType2.isArray()) {
                    return ConversionComparator.Comparison.TYPE_1_BETTER;
                }
            } else if (targetType2.isArray()) {
                return ConversionComparator.Comparison.TYPE_2_BETTER;
            }
        }
        if (ScriptObject.class.isAssignableFrom(sourceType)) {
            if (targetType1.isInterface()) {
                if (!targetType2.isInterface()) {
                    return ConversionComparator.Comparison.TYPE_1_BETTER;
                }
            } else if (targetType2.isInterface()) {
                return ConversionComparator.Comparison.TYPE_2_BETTER;
            }
        }
        return ConversionComparator.Comparison.INDETERMINATE;
    }

    private static boolean isArrayPreferredTarget(Class<?> clazz) {
        return clazz == List.class || clazz == Collection.class || clazz == Queue.class || clazz == Deque.class;
    }

    private static MethodHandle asReturning(MethodHandle mh, Class<?> nrtype) {
        return mh.asType(mh.type().changeReturnType(nrtype));
    }

    private static boolean isNashornTypeOrUndefined(Object obj) {
        return obj instanceof ScriptObject || obj instanceof Undefined;
    }

    private static Object createMirror(Object obj) {
        return obj instanceof ScriptObject ? ScriptUtils.wrap(obj) : obj;
    }

    private static boolean isFunction(Object obj) {
        return obj instanceof ScriptFunction || obj instanceof ScriptObjectMirror && ((ScriptObjectMirror)obj).isFunction();
    }

    private static MethodHandle findOwnMH(String name, Class<?> rtype, Class<?> ... types) {
        return org.openjdk.nashorn.internal.lookup.Lookup.MH.findStatic(MethodHandles.lookup(), NashornLinker.class, name, org.openjdk.nashorn.internal.lookup.Lookup.MH.type(rtype, types));
    }

    static {
        MethodHandle listAdapterCreate = new Lookup(MethodHandles.lookup()).findStatic(ListAdapter.class, "create", MethodType.methodType(ListAdapter.class, Object.class));
        TO_COLLECTION = NashornLinker.asReturning(listAdapterCreate, Collection.class);
        TO_DEQUE = NashornLinker.asReturning(listAdapterCreate, Deque.class);
        TO_LIST = NashornLinker.asReturning(listAdapterCreate, List.class);
        TO_QUEUE = NashornLinker.asReturning(listAdapterCreate, Queue.class);
    }
}

