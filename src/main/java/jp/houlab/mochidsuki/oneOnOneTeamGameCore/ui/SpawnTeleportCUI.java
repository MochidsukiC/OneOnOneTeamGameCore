package jp.houlab.mochidsuki.oneOnOneTeamGameCore.ui;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile.SpawnPointProfileMap;

public class SpawnTeleportCUI {
    /**
     * config.ymlに定義されたCUIを解析し、プレイヤーに送信します。
     * @param player 送信先のプレイヤー
     */
    public static void sendSpawnPointCui(Player player) {
        // 1. config.ymlからCUIレイアウトをリストとして読み込む
        TeamProfile teamProfile = TeamProfile.getTeamProfileFromPlayer(player);
        if(teamProfile == null) return;

        List<String> layoutLines = config.getStringList("SpawnPointCUI."+teamProfile.getSiteProfile().getName());

        if (layoutLines.isEmpty()) {
            player.sendMessage(Component.text("CUIのレイアウトがconfig.ymlに設定されていません。", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("テレポートする地点を選択してください:", NamedTextColor.GREEN));

        // 2. レイアウトを一行ずつ処理してプレイヤーに送信
        for (String line : layoutLines) {
            Component lineComponent = buildLineComponent(line);
            player.sendMessage(lineComponent);
        }
    }

    /**
     * 文字列一行を解析し、プレースホルダーをコマンドに置換したComponentを生成します。
     * @param line 解析する文字列 (例: "    [1]   [2]")
     * @return 生成されたComponent
     */
    private static Component buildLineComponent(String line) {
        // 最終的に一行全体となるComponentのビルダー
        final TextComponent.Builder lineBuilder = Component.text();

        // "[n]" という形式のプレースホルダーを検出するための正規表現パターン
        final Pattern placeholderPattern = Pattern.compile("\\[(\\d+)]");

        // 文字列をプレースホルダーの前後で分割する (例: "    ", "[1]", "   ", "[2]")
        final String[] parts = line.split("(?=\\[\\d+])|(?<=\\])");

        for (final String part : parts) {
            if (placeholderPattern.matcher(part).matches()) {
                // --- partがプレースホルダー "[n]" の場合 ---
                // 数字部分 "n" を抽出
                final String id = part.replaceAll("[\\[\\]]", "");
                // 仮置きのコマンドと色を設定
                final String commandToRun = "/rsp " + id;
                TextColor color = NamedTextColor.WHITE; // 仮置きの色
                if(SpawnPointProfileMap.containsKey(id)) {
                    SpawnPointProfile profile = SpawnPointProfileMap.get(id);
                    if(profile.getOwner() != null && profile.isOwned(profile.getOwner())){
                        color = profile.getOwner().getTeam().color();
                    }
                    if(!profile.isEnable()) color = NamedTextColor.GRAY;
                }

                // クリックイベントとホバーイベントを持つComponentを作成
                final Component placeholderComponent = Component.text()
                        .content(part) // 表示されるテキストは "[n]" のまま
                        .color(color)  // 指定された色を適用
                        .clickEvent(ClickEvent.runCommand(commandToRun)) // クリックしたらコマンドを実行
                        .hoverEvent(HoverEvent.showText(Component.text("クリックして " + commandToRun + " を実行"))) // カーソルを合わせたらヒントを表示
                        .build();

                lineBuilder.append(placeholderComponent);

            } else {
                // --- partがプレーンテキストの場合 ---
                // スタイルを持たない通常のテキストとして追加
                lineBuilder.append(Component.text(part));
            }
        }

        return lineBuilder.build();
    }
}
