/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import org.openjdk.nashorn.internal.objects.NativeArray;
import org.openjdk.nashorn.internal.objects.NativeArray$Prototype;
import org.openjdk.nashorn.internal.runtime.AccessorProperty;
import org.openjdk.nashorn.internal.runtime.Property;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.PrototypeObject;
import org.openjdk.nashorn.internal.runtime.ScriptFunction;
import org.openjdk.nashorn.internal.runtime.Specialization;

final class NativeArray$Constructor
extends ScriptFunction {
    private Object isArray;
    private static final PropertyMap $nasgenmap$;

    public Object G$isArray() {
        return this.isArray;
    }

    public void S$isArray(Object object) {
        this.isArray = object;
    }

    static {
        ArrayList<Property> arrayList = new ArrayList<Property>(1);
        arrayList.add(AccessorProperty.create("isArray", 2, cfr_ldc_0(), cfr_ldc_1()));
        $nasgenmap$ = PropertyMap.newMap(arrayList);
    }

    NativeArray$Constructor() {
        super("Array", cfr_ldc_2(), $nasgenmap$, new Specialization[]{new Specialization(cfr_ldc_3(), false, false), new Specialization(cfr_ldc_4(), false, false), new Specialization(cfr_ldc_5(), false, false), new Specialization(cfr_ldc_6(), false, false), new Specialization(cfr_ldc_7(), false, false)});
        ScriptFunction scriptFunction = ScriptFunction.createBuiltin("isArray", cfr_ldc_8());
        scriptFunction.setDocumentationKey("Array.isArray");
        this.isArray = scriptFunction;
        NativeArray$Prototype nativeArray$Prototype = new NativeArray$Prototype();
        PrototypeObject.setConstructor(nativeArray$Prototype, this);
        this.setPrototype(nativeArray$Prototype);
        this.setArity(1);
        this.setDocumentationKey("Array");
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_0() {
        try {
            return MethodHandles.lookup().findVirtual(NativeArray$Constructor.class, "G$isArray", MethodType.fromMethodDescriptorString("()Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_1() {
        try {
            return MethodHandles.lookup().findVirtual(NativeArray$Constructor.class, "S$isArray", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;)V", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_2() {
        try {
            return MethodHandles.lookup().findStatic(NativeArray.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;[Ljava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeArray;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_3() {
        try {
            return MethodHandles.lookup().findStatic(NativeArray.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;)Lorg/openjdk/nashorn/internal/objects/NativeArray;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_4() {
        try {
            return MethodHandles.lookup().findStatic(NativeArray.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;Z)Ljava/lang/Object;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_5() {
        try {
            return MethodHandles.lookup().findStatic(NativeArray.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;I)Lorg/openjdk/nashorn/internal/objects/NativeArray;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_6() {
        try {
            return MethodHandles.lookup().findStatic(NativeArray.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;J)Lorg/openjdk/nashorn/internal/objects/NativeArray;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_7() {
        try {
            return MethodHandles.lookup().findStatic(NativeArray.class, "construct", MethodType.fromMethodDescriptorString("(ZLjava/lang/Object;D)Lorg/openjdk/nashorn/internal/objects/NativeArray;", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }

    /*
     * Works around MethodHandle LDC.
     */
    static MethodHandle cfr_ldc_8() {
        try {
            return MethodHandles.lookup().findStatic(NativeArray.class, "isArray", MethodType.fromMethodDescriptorString("(Ljava/lang/Object;Ljava/lang/Object;)Z", null));
        }
        catch (NoSuchMethodException | IllegalAccessException except) {
            throw new IllegalArgumentException(except);
        }
    }
}

