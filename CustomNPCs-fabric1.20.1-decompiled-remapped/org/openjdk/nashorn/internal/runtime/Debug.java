/*
 * Decompiled with CFR 0.152.
 */
package org.openjdk.nashorn.internal.runtime;

import org.openjdk.nashorn.api.scripting.NashornException;
import org.openjdk.nashorn.internal.parser.Lexer;
import org.openjdk.nashorn.internal.parser.Token;
import org.openjdk.nashorn.internal.parser.TokenStream;
import org.openjdk.nashorn.internal.parser.TokenType;
import org.openjdk.nashorn.internal.runtime.ECMAErrors;
import org.openjdk.nashorn.internal.runtime.Source;

public final class Debug {
    private Debug() {
    }

    public static String firstJSFrame(Throwable t) {
        for (StackTraceElement ste : t.getStackTrace()) {
            if (!ECMAErrors.isScriptFrame(ste)) continue;
            return ste.toString();
        }
        return "<native code>";
    }

    public static String firstJSFrame() {
        return Debug.firstJSFrame(new Throwable());
    }

    public static String scriptStack() {
        return NashornException.getScriptStackString(new Throwable());
    }

    public static String id(Object x) {
        return String.format("0x%08x", System.identityHashCode(x));
    }

    public static int intId(Object x) {
        return System.identityHashCode(x);
    }

    public static String stackTraceElementAt(int depth) {
        return new Throwable().getStackTrace()[depth + 1].toString();
    }

    public static String caller(int depth, int count, String ... ignores) {
        Object result = "";
        StackTraceElement[] callers = Thread.currentThread().getStackTrace();
        int c = count;
        block0: for (int i = depth + 1; i < callers.length && c != 0; ++i) {
            StackTraceElement element = callers[i];
            String method = element.getMethodName();
            for (String ignore : ignores) {
                if (method.compareTo(ignore) == 0) continue block0;
            }
            result = (String)result + (method + ":" + element.getLineNumber() + "                              ").substring(0, 30);
            --c;
        }
        return ((String)result).isEmpty() ? "<no caller>" : result;
    }

    public static void dumpTokens(Source source, Lexer lexer, TokenStream stream) {
        int k = 0;
        while (true) {
            if (k > stream.last()) {
                lexer.lexify();
                continue;
            }
            long token = stream.get(k);
            TokenType type = Token.descType(token);
            System.out.println(k + ": " + Token.toString(source, token, true));
            ++k;
            if (type == TokenType.EOF) break;
        }
    }
}

