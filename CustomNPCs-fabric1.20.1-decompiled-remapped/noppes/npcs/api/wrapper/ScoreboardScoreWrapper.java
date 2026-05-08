/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.scoreboard.ScoreboardPlayerScore
 */
package noppes.npcs.api.wrapper;

import net.minecraft.scoreboard.ScoreboardPlayerScore;
import noppes.npcs.api.IScoreboardScore;

public class ScoreboardScoreWrapper
implements IScoreboardScore {
    private ScoreboardPlayerScore score;

    public ScoreboardScoreWrapper(ScoreboardPlayerScore score) {
        this.score = score;
    }

    @Override
    public int getValue() {
        return this.score.getScore();
    }

    @Override
    public void setValue(int val) {
        this.score.setScore(val);
    }

    @Override
    public String getPlayerName() {
        return this.score.getPlayerName();
    }
}

