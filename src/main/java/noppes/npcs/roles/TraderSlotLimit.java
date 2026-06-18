package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class TraderSlotLimit {
    public static final int PLAYER_NONE = 0;
    public static final int PLAYER_ONCE = 1;
    public static final int PLAYER_REAL_TIME = 2;
    public static final int PLAYER_GAME_TIME = 3;
    public static final int PLAYER_ONLINE_TIME = 4;

    public int playerLimitType = PLAYER_NONE;
    public int playerLimitCount = 0;
    public int playerLimitTime = 1;
    public int playerLimitTimeUnit = TraderTimeUnit.DAY;
    public List<TraderVariableOperation> variableOps = new ArrayList<>();

    public boolean hasPlayerLimit() {
        return this.playerLimitType != PLAYER_NONE;
    }

    public boolean hasVariableOps() {
        return !this.variableOps.isEmpty();
    }

    public boolean isLimited() {
        return this.hasPlayerLimit() || this.hasVariableOps();
    }

    public int getEffectivePlayerLimitCount() {
        if (this.playerLimitCount > 0) {
            return this.playerLimitCount;
        }
        return 1;
    }

    public boolean usesTimedPeriod() {
        return this.playerLimitType == PLAYER_REAL_TIME || this.playerLimitType == PLAYER_GAME_TIME || this.playerLimitType == PLAYER_ONLINE_TIME;
    }

    public NbtCompound writeNBT() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("PlayerLimitType", this.playerLimitType);
        tag.putInt("PlayerLimitCount", this.playerLimitCount);
        tag.putInt("PlayerLimitTime", this.playerLimitTime);
        tag.putInt("PlayerLimitTimeUnit", this.playerLimitTimeUnit);
        NbtList ops = new NbtList();
        for (TraderVariableOperation op : this.variableOps) {
            ops.add(op.writeNBT());
        }
        tag.put("VarOps", ops);
        if (tag.contains("GlobalStockMax")) {
            tag.remove("GlobalStockMax");
        }
        return tag;
    }

    public void readNBT(NbtCompound tag) {
        this.playerLimitType = tag.getInt("PlayerLimitType");
        this.playerLimitCount = tag.getInt("PlayerLimitCount");
        if (tag.contains("PlayerLimitTime")) {
            this.playerLimitTime = Math.max(1, tag.getInt("PlayerLimitTime"));
            this.playerLimitTimeUnit = tag.getInt("PlayerLimitTimeUnit");
        } else {
            this.playerLimitTime = Math.max(1, tag.getInt("PlayerLimitCooldown"));
            this.playerLimitTimeUnit = TraderTimeUnit.SECOND;
            if (this.playerLimitType == 2) {
                this.playerLimitType = PLAYER_REAL_TIME;
                this.playerLimitTimeUnit = TraderTimeUnit.DAY;
            } else if (this.playerLimitType == 3) {
                this.playerLimitType = PLAYER_REAL_TIME;
            } else if (this.playerLimitType == 4) {
                this.playerLimitType = PLAYER_REAL_TIME;
                this.playerLimitTimeUnit = TraderTimeUnit.WEEK;
            }
        }
        this.variableOps.clear();
        if (tag.contains("VarOps", 9)) {
            NbtList ops = tag.getList("VarOps", 10);
            for (int i = 0; i < ops.size(); ++i) {
                TraderVariableOperation op = new TraderVariableOperation();
                op.readNBT(ops.getCompound(i));
                this.variableOps.add(op);
            }
        } else if (tag.contains("GlobalStockMax") && tag.getInt("GlobalStockMax") >= 0) {
            TraderVariableOperation op = new TraderVariableOperation();
            op.variableName = "stock";
            op.delta = -1;
            this.variableOps.add(op);
        }
    }

    public static TraderSlotLimit[] createDefaults() {
        TraderSlotLimit[] limits = new TraderSlotLimit[18];
        for (int i = 0; i < 18; ++i) {
            limits[i] = new TraderSlotLimit();
        }
        return limits;
    }
}
