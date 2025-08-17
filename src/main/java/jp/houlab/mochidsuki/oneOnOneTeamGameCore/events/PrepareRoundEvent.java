package jp.houlab.mochidsuki.oneOnOneTeamGameCore.events;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * ラウンド準備フェーズ開始時に呼び出される
 */
public class PrepareRoundEvent extends Event {
    private static final HandlerList handlers = new HandlerList();


    public PrepareRoundEvent() {
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
