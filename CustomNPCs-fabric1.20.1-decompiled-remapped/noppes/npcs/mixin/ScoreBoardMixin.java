/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.ScoreboardPlayerScore
 *  net.minecraft.scoreboard.Scoreboard
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.npcs.mixin;

import java.util.Map;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Scoreboard.class})
public interface ScoreBoardMixin {
    @Accessor(value="playerScores")
    public Map<String, Map<ScoreboardObjective, ScoreboardPlayerScore>> getScores();
}

