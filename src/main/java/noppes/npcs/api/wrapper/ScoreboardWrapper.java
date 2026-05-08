/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  net.minecraft.text.Text
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.ScoreboardPlayerScore
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.scoreboard.ScoreboardCriterion
 *  net.minecraft.scoreboard.ScoreboardCriterion$RenderType
 *  net.minecraft.server.MinecraftServer
 */
package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IScoreboard;
import noppes.npcs.api.IScoreboardObjective;
import noppes.npcs.api.IScoreboardTeam;
import noppes.npcs.api.wrapper.ScoreboardObjectiveWrapper;
import noppes.npcs.api.wrapper.ScoreboardTeamWrapper;

public class ScoreboardWrapper
implements IScoreboard {
    private Scoreboard board;
    private MinecraftServer server;

    protected ScoreboardWrapper(MinecraftServer server) {
        this.server = server;
        this.board = server.getWorld(World.OVERWORLD).getScoreboard();
    }

    @Override
    public IScoreboardObjective[] getObjectives() {
        ArrayList collection = new ArrayList(this.board.getObjectives());
        IScoreboardObjective[] objectives = new IScoreboardObjective[collection.size()];
        for (int i = 0; i < collection.size(); ++i) {
            objectives[i] = new ScoreboardObjectiveWrapper(this.board, (ScoreboardObjective)collection.get(i));
        }
        return objectives;
    }

    @Override
    public String[] getPlayerList() {
        Collection<String> collection = this.board.getObjectiveNames();
        return collection.toArray(new String[0]);
    }

    @Override
    public IScoreboardObjective getObjective(String name) {
        ScoreboardObjective obj = this.board.getNullableObjective(name);
        if (obj == null) {
            return null;
        }
        return new ScoreboardObjectiveWrapper(this.board, obj);
    }

    @Override
    public boolean hasObjective(String objective) {
        return this.board.getNullableObjective(objective) != null;
    }

    @Override
    public void removeObjective(String objective) {
        ScoreboardObjective obj = this.board.getNullableObjective(objective);
        if (obj != null) {
            this.board.removeObjective(obj);
        }
    }

    @Override
    public IScoreboardObjective addObjective(String objective, String criteria) {
        ScoreboardCriterion icriteria = ScoreboardCriterion.getOrCreateStatCriterion((String)criteria).orElse(null);
        if (icriteria == null) {
            throw new CustomNPCsException("Unknown score criteria: %s", criteria);
        }
        if (objective.length() <= 0 || objective.length() > 16) {
            throw new CustomNPCsException("Score objective must be between 1-16 characters: %s", objective);
        }
        ScoreboardObjective obj = this.board.addObjective(objective, icriteria, (Text)Text.translatable((String)objective), ScoreboardCriterion.RenderType.INTEGER);
        return new ScoreboardObjectiveWrapper(this.board, obj);
    }

    @Override
    public void setPlayerScore(String player, String objective, int score) {
        ScoreboardObjective objec = this.getObjectiveWithException(objective);
        if (objec.getCriterion().isReadOnly() || score < Integer.MIN_VALUE || score > Integer.MAX_VALUE) {
            return;
        }
        ScoreboardPlayerScore sco = this.board.getPlayerScore(player, objec);
        sco.setScore(score);
    }

    private ScoreboardObjective getObjectiveWithException(String objective) {
        ScoreboardObjective objec = this.board.getNullableObjective(objective);
        if (objec == null) {
            throw new CustomNPCsException("Score objective does not exist: %s", objective);
        }
        return objec;
    }

    @Override
    public int getPlayerScore(String player, String objective) {
        ScoreboardObjective objec = this.getObjectiveWithException(objective);
        if (objec.getCriterion().isReadOnly()) {
            return 0;
        }
        return this.board.getPlayerScore(player, objec).getScore();
    }

    @Override
    public boolean hasPlayerObjective(String player, String objective) {
        ScoreboardObjective objec = this.getObjectiveWithException(objective);
        return this.board.getPlayerObjectives(player).get(objec) != null;
    }

    @Override
    public void deletePlayerScore(String player, String objective) {
        ScoreboardObjective objec = this.getObjectiveWithException(objective);
        if (this.board.getPlayerObjectives(player).remove(objec) != null) {
            this.board.clearPlayerTeam(player);
        }
    }

    @Override
    public IScoreboardTeam[] getTeams() {
        ArrayList list = new ArrayList(this.board.getTeams());
        IScoreboardTeam[] teams = new IScoreboardTeam[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            teams[i] = new ScoreboardTeamWrapper((Team)list.get(i), this.board);
        }
        return teams;
    }

    @Override
    public boolean hasTeam(String name) {
        return this.board.getTeam(name) != null;
    }

    @Override
    public IScoreboardTeam addTeam(String name) {
        if (this.hasTeam(name)) {
            throw new CustomNPCsException("Team %s already exists", name);
        }
        return new ScoreboardTeamWrapper(this.board.addTeam(name), this.board);
    }

    @Override
    public IScoreboardTeam getTeam(String name) {
        Team team = this.board.getTeam(name);
        if (team == null) {
            return null;
        }
        return new ScoreboardTeamWrapper(team, this.board);
    }

    @Override
    public void removeTeam(String name) {
        Team team = this.board.getTeam(name);
        if (team != null) {
            this.board.removeTeam(team);
        }
    }

    @Override
    public IScoreboardTeam getPlayerTeam(String player) {
        Team team = this.board.getPlayerTeam(player);
        if (team == null) {
            return null;
        }
        return new ScoreboardTeamWrapper(team, this.board);
    }

    @Override
    public void removePlayerTeam(String player) {
        this.board.clearPlayerTeam(player);
    }
}

