/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.internal.runtime.regexp.joni;

import org.openjdk.nashorn.internal.runtime.regexp.joni.ByteCodeMachine;
import org.openjdk.nashorn.internal.runtime.regexp.joni.Matcher;
import org.openjdk.nashorn.internal.runtime.regexp.joni.Regex;

public abstract class MatcherFactory {
    static final MatcherFactory DEFAULT = new MatcherFactory(){

        @Override
        public Matcher create(Regex regex, char[] chars, int p, int end) {
            return new ByteCodeMachine(regex, chars, p, end);
        }
    };

    public abstract Matcher create(Regex var1, char[] var2, int var3, int var4);
}

