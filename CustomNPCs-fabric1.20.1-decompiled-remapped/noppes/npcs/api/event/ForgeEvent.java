/*
 * Decompiled with CFR 0.152.
 */
package noppes.npcs.api.event;

import noppes.npcs.api.event.CustomNPCsEvent;
import noppes.npcs.api.event.Event;

public class ForgeEvent
extends CustomNPCsEvent {
    public final Event event;

    public ForgeEvent(Event event) {
        this.event = event;
    }
}

