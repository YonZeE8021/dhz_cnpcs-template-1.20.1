package noppes.npcs.packets.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.client.ClientTraderLimitCache;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.util.TraderLimitHelper;
import noppes.npcs.util.TraderVariableHelper;

public class PacketTraderLimitSync
extends PacketBasic {
    private final String scopeKey;
    private final int[] playerRemaining;
    private final int[] playerMax;
    private final long[] limitRefreshRemaining;
    private final int[] limitRefreshType;
    private final boolean[] tradeBlocked;
    private final int[] blockReason;
    private final Map<String, Integer> visibleVariables;
    private final List<ClientTraderLimitCache.VariableTip>[] slotVariables;

    public PacketTraderLimitSync(String scopeKey, int[] playerRemaining, int[] playerMax, long[] limitRefreshRemaining, int[] limitRefreshType, boolean[] tradeBlocked, int[] blockReason, Map<String, Integer> visibleVariables, List<ClientTraderLimitCache.VariableTip>[] slotVariables) {
        this.scopeKey = scopeKey;
        this.playerRemaining = playerRemaining;
        this.playerMax = playerMax;
        this.limitRefreshRemaining = limitRefreshRemaining;
        this.limitRefreshType = limitRefreshType;
        this.tradeBlocked = tradeBlocked;
        this.blockReason = blockReason;
        this.visibleVariables = visibleVariables;
        this.slotVariables = slotVariables;
    }

    public static PacketTraderLimitSync forTrader(PlayerEntity player, RoleTrader role) {
        return new PacketTraderLimitSync(RoleTrader.getScopeKey(role), TraderLimitHelper.getPlayerRemainingAll(player, role), TraderLimitHelper.getPlayerMaxAll(role), TraderLimitHelper.getLimitRefreshRemainingAll(player, role), TraderLimitHelper.getLimitRefreshTypeAll(role), TraderLimitHelper.getTradeBlockedAll(player, role), TraderLimitHelper.getBlockReasonAll(player, role), TraderVariableHelper.getVisibleValues(role), TraderLimitHelper.getSlotVariableTipsAll(player, role));
    }

    public static void encode(PacketTraderLimitSync msg, PacketByteBuf buf) {
        buf.writeString(msg.scopeKey);
        for (int i = 0; i < 18; ++i) {
            buf.writeInt(msg.playerRemaining[i]);
        }
        for (int i = 0; i < 18; ++i) {
            buf.writeInt(msg.playerMax[i]);
        }
        for (int i = 0; i < 18; ++i) {
            buf.writeLong(msg.limitRefreshRemaining[i]);
        }
        for (int i = 0; i < 18; ++i) {
            buf.writeInt(msg.limitRefreshType[i]);
        }
        for (int i = 0; i < 18; ++i) {
            buf.writeBoolean(msg.tradeBlocked[i]);
        }
        for (int i = 0; i < 18; ++i) {
            buf.writeInt(msg.blockReason[i]);
        }
        buf.writeInt(msg.visibleVariables.size());
        for (Map.Entry<String, Integer> entry : msg.visibleVariables.entrySet()) {
            buf.writeString(entry.getKey());
            buf.writeInt(entry.getValue());
        }
        for (int i = 0; i < 18; ++i) {
            List<ClientTraderLimitCache.VariableTip> tips = msg.slotVariables[i];
            buf.writeInt(tips.size());
            for (ClientTraderLimitCache.VariableTip tip : tips) {
                buf.writeString(tip.name);
                buf.writeInt(tip.value);
                buf.writeInt(tip.maxValue);
                buf.writeLong(tip.restockRemaining);
                buf.writeInt(tip.restockType);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static PacketTraderLimitSync decode(PacketByteBuf buf) {
        String scopeKey = buf.readString();
        int[] playerRemaining = new int[18];
        int[] playerMax = new int[18];
        long[] limitRefreshRemaining = new long[18];
        int[] limitRefreshType = new int[18];
        boolean[] tradeBlocked = new boolean[18];
        int[] blockReason = new int[18];
        for (int i = 0; i < 18; ++i) {
            playerRemaining[i] = buf.readInt();
        }
        for (int i = 0; i < 18; ++i) {
            playerMax[i] = buf.readInt();
        }
        for (int i = 0; i < 18; ++i) {
            limitRefreshRemaining[i] = buf.readLong();
        }
        for (int i = 0; i < 18; ++i) {
            limitRefreshType[i] = buf.readInt();
        }
        for (int i = 0; i < 18; ++i) {
            tradeBlocked[i] = buf.readBoolean();
        }
        for (int i = 0; i < 18; ++i) {
            blockReason[i] = buf.readInt();
        }
        HashMap<String, Integer> visibleVariables = new HashMap<>();
        int count = buf.readInt();
        for (int i = 0; i < count; ++i) {
            visibleVariables.put(buf.readString(), buf.readInt());
        }
        List<ClientTraderLimitCache.VariableTip>[] slotVariables = new List[18];
        for (int i = 0; i < 18; ++i) {
            int varCount = buf.readInt();
            ArrayList<ClientTraderLimitCache.VariableTip> tips = new ArrayList<>();
            for (int j = 0; j < varCount; ++j) {
                tips.add(new ClientTraderLimitCache.VariableTip(buf.readString(), buf.readInt(), buf.readInt(), buf.readLong(), buf.readInt()));
            }
            slotVariables[i] = tips;
        }
        return new PacketTraderLimitSync(scopeKey, playerRemaining, playerMax, limitRefreshRemaining, limitRefreshType, tradeBlocked, blockReason, visibleVariables, slotVariables);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        ClientTraderLimitCache.set(this.scopeKey, this.playerRemaining, this.playerMax, this.limitRefreshRemaining, this.limitRefreshType, this.tradeBlocked, this.blockReason, this.visibleVariables, this.slotVariables);
    }
}
