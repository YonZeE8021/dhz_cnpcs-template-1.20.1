package noppes.npcs.roles;

import net.minecraft.nbt.NbtCompound;

public class TraderVariableOperation {
    public String variableName = "";
    public int delta = -1;

    public NbtCompound writeNBT() {
        NbtCompound tag = new NbtCompound();
        tag.putString("Var", this.variableName);
        tag.putInt("Delta", this.delta);
        return tag;
    }

    public void readNBT(NbtCompound tag) {
        this.variableName = tag.getString("Var");
        this.delta = tag.getInt("Delta");
    }
}
