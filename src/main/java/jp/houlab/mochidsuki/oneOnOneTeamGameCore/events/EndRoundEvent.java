package jp.houlab.mochidsuki.oneOnOneTeamGameCore.events;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * ラウンド終了時に呼び出される
 */
public class EndRoundEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final TeamProfile winner;

    public EndRoundEvent(TeamProfile winner) {
        this.winner = winner;
    }

    /**
     * 勝利したチームプロファイルを取得する
     * @return チームプロファイル
     */
    public TeamProfile getWinner() {
        return winner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
