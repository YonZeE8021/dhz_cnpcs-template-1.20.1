/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.ScoreboardPlayerScore
 *  net.minecraft.scoreboard.Scoreboard
 */
package noppes.npcs.api.wrapper;

import java.util.Collection;
import net.minecraft.text.Text;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Scoreboard;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IScoreboardObjective;
import noppes.npcs.api.IScoreboardScore;
import noppes.npcs.api.wrapper.ScoreboardScoreWrapper;

public class ScoreboardObjectiveWrapper
implements IScoreboardObjective {
    private ScoreboardObjective objective;
    private Scoreboard board;

    protected ScoreboardObjectiveWrapper(Scoreboard board, ScoreboardObjective objective) {
        this.objective = objective;
        this.board = board;
    }

    @Override
    public String getName() {
        return this.objective.getName();
    }

    @Override
    public String getDisplayName() {
        return this.objective.getDisplayName().getString();
    }

    @Override
    public void setDisplayName(String name) {
        if (name.length() <= 0 || name.length() > 32) {
            throw new CustomNPCsException("Score objective display name must be between 1-32 characters: %s", name);
        }
        this.objective.setDisplayName((Text)Text.translatable((String)name));
    }

    @Override
    public String getCriteria() {
        return this.objective.getCriterion().getName();
    }

    @Override
    public boolean isReadyOnly() {
        return this.objective.getCriterion().isReadOnly();
    }

    @Override
    public IScoreboardScore[] getScores() {
        Collection<ScoreboardPlayerScore> list = this.board.getAllPlayerScores(this.objective);
        IScoreboardScore[] scores = new IScoreboardScore[list.size()];
        int i = 0;
        for (ScoreboardPlayerScore score : list) {
            scores[i] = new ScoreboardScoreWrapper(score);
            ++i;
        }
        return scores;
    }

    @Override
    public IScoreboardScore getScore(String player) {
        if (!this.hasScore(player)) {
            return null;
        }
        return new ScoreboardScoreWrapper(this.board.getPlayerScore(player, this.objective));
    }

    @Override
    public IScoreboardScore createScore(String player) {
        return new ScoreboardScoreWrapper(this.board.getPlayerScore(player, this.objective));
    }

    @Override
    public void removeScore(String player) {
        this.board.resetPlayerScore(player, this.objective);
    }

    @Override
    public boolean hasScore(String player) {
        return this.board.playerHasObjective(player, this.objective);
    }
}

