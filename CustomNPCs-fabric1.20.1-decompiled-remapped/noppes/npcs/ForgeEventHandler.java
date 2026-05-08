/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package noppes.npcs;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ForgeEventHandler {
    public static List<String> eventNames = new ArrayList<String>();

    public static String getEventName(Class c) {
        String eventName = c.getName();
        int i = eventName.lastIndexOf(".");
        return StringUtils.uncapitalize((String)eventName.substring(i + 1).replace("$", ""));
    }
}

