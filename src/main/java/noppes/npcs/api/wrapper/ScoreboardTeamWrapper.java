/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.text.Text
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.scoreboard.Scoreboard
 */
package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Scoreboard;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IScoreboardTeam;

public class ScoreboardTeamWrapper
implements IScoreboardTeam {
    private Team team;
    private Scoreboard board;

    protected ScoreboardTeamWrapper(Team team, Scoreboard board) {
        this.team = team;
        this.board = board;
    }

    @Override
    public String getName() {
        return this.team.getName();
    }

    @Override
    public String getDisplayName() {
        return this.team.getDisplayName().getString();
    }

    @Override
    public void setDisplayName(String name) {
        if (name.length() <= 0 || name.length() > 32) {
            throw new CustomNPCsException("Score team display name must be between 1-32 characters: %s", name);
        }
        this.team.setDisplayName((Text)Text.translatable((String)name));
    }

    @Override
    public void addPlayer(String player) {
        this.board.addPlayerToTeam(player, this.team);
    }

    @Override
    public void removePlayer(String player) {
        this.board.removePlayerFromTeam(player, this.team);
    }

    @Override
    public String[] getPlayers() {
        ArrayList<String> list = new ArrayList<String>(this.team.getPlayerList());
        return list.toArray(new String[0]);
    }

    @Override
    public void clearPlayers() {
        ArrayList<String> list = new ArrayList<String>(this.team.getPlayerList());
        for (String player : list) {
            this.board.removePlayerFromTeam(player, this.team);
        }
    }

    @Override
    public boolean getFriendlyFire() {
        return this.team.isFriendlyFireAllowed();
    }

    @Override
    public void setFriendlyFire(boolean bo) {
        this.team.setFriendlyFireAllowed(bo);
    }

    @Override
    public void setColor(String color) {
        Formatting enumchatformatting = Formatting.byName((String)color);
        if (enumchatformatting == null || enumchatformatting.isModifier()) {
            throw new CustomNPCsException("Not a proper color name: %s", color);
        }
        this.team.setPrefix((Text)Text.literal((String)enumchatformatting.toString()));
        this.team.setSuffix((Text)Text.literal((String)Formatting.RESET.toString()));
    }

    @Override
    public String getColor() {
        Text prefix = this.team.getPrefix();
        if (prefix == null || prefix.getString().isEmpty()) {
            return null;
        }
        for (Formatting format : Formatting.values()) {
            if (!prefix.getString().equals(format.toString()) || format == Formatting.RESET) continue;
            return format.getName();
        }
        return null;
    }

    @Override
    public void setSeeInvisibleTeamPlayers(boolean bo) {
        this.team.setShowFriendlyInvisibles(bo);
    }

    @Override
    public boolean getSeeInvisibleTeamPlayers() {
        return this.team.shouldShowFriendlyInvisibles();
    }

    @Override
    public boolean hasPlayer(String player) {
        return this.board.getPlayerTeam(player) != null;
    }
}

