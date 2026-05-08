/*
 * Decompiled with CFR 0.152.
 */
package noppes.npcs.api.event;

public class Event {
    boolean canceled = false;

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean val) {
        this.canceled = val;
    }
}

