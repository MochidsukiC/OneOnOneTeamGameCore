package jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint;

import jp.houlab.mochidsuki.oneOnOneTeamGameCore.TeamProfile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONStreamAware;

import javax.annotation.Nullable;
import java.util.*;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint.SpawnPointProfile.SpawnPointProfileMap;

public class SpawnPointUtils {
    // config.ymlから読み込んだ隣接リストを保持するマップ
    private static final Map<String, List<String>> adjacencyList = new HashMap<>();

    public static boolean isConnect(String startPoint, String destinationPoint, TeamProfile teamProfile) {
        List<String> route = getPathStations(startPoint, destinationPoint,teamProfile);
        return route != null && !route.isEmpty();
    }

    /**
     * config.ymlから隣接リストを読み込み、メモリ上のMapに格納します。
     * プラグインのパフォーマンス向上のため、onEnable時に一度だけ実行するのが推奨です。
     */
    public static void loadAdjacencyListFromConfig() {
        adjacencyList.clear(); // リロード時に備えて既存のデータをクリア
        ConfigurationSection section = config.getConfigurationSection("SpawnPoint");

        if (section == null) {
            plugin.getLogger().warning("config.ymlに 'SpawnPoint' セクションが見つかりません。");
            return;
        }

        for (String key : section.getKeys(false)) {
            List<String> neighbors = section.getStringList(key);
            adjacencyList.put(key, neighbors);
        }
        plugin.getLogger().info(adjacencyList.size() + "個の地点データを読み込みました。");
    }

    /**
     * [変更版] 幅優先探索(BFS)を用いて、有効な地点のみを通る最短経路を探索します。
     *
     * @param startPoint      出発地の名前
     * @param destinationPoint 目的地の名前
     * @return 経路が見つかった場合は、出発地から目的地までの地点リスト。見つからない場合はnull。
     */
    @Nullable
    public static List<String> getPathStations(String startPoint, String destinationPoint,TeamProfile teamProfile) {
        // 1. 地点がグラフ（隣接リスト）に存在するか基本的なチェック
        if (!adjacencyList.containsKey(startPoint) || !adjacencyList.containsKey(destinationPoint)) {
            return null;
        }

        // [変更点] 2. 出発地と目的地が有効かチェック
        final SpawnPointProfile startProfile = SpawnPointProfile.SpawnPointProfileMap.get(startPoint);
        final SpawnPointProfile destProfile = SpawnPointProfile.SpawnPointProfileMap.get(destinationPoint);

        // プロフィールが存在しない、または無効(isOwned=false)な場合は経路なし
        if (startProfile == null || !startProfile.isOwned(teamProfile) || destProfile == null || !destProfile.isOwned(teamProfile)) {
            return null;
        }

        // 3. 出発地と目的地が同じ場合
        if (startPoint.equals(destinationPoint)) {
            return Collections.singletonList(startPoint);
        }

        // 4. BFSのためのデータ構造を準備
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();

        // 5. 探索を開始
        queue.add(startPoint);
        visited.add(startPoint);

        while (!queue.isEmpty()) {
            String currentPoint = queue.poll();

            if (currentPoint.equals(destinationPoint)) {
                return reconstructPath(startPoint, destinationPoint, parentMap);
            }

            // 隣接する地点をすべてチェック
            for (String neighbor : adjacencyList.getOrDefault(currentPoint, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {

                    // [変更点] 隣接地点が有効(Owned)かチェックする
                    final SpawnPointProfile neighborProfile = SpawnPointProfile.SpawnPointProfileMap.get(neighbor);

                    // プロフィールが存在し、かつ有効な場合のみ、次の探索候補とする
                    if (neighborProfile != null && neighborProfile.isOwned(teamProfile)) {
                        visited.add(neighbor);
                        parentMap.put(neighbor, currentPoint);
                        queue.add(neighbor);
                    }
                }
            }
        }

        // 6. 目的地に到達できなかった場合
        return null;
    }
    /**
     * parentMapを元に、目的地から出発地まで遡って経路リストを構築します。
     */
    private static List<String> reconstructPath(String start, String destination, Map<String, String> parentMap) {
        // LinkedListは先頭への追加(addFirst)が効率的なので、経路復元に適しています
        LinkedList<String> path = new LinkedList<>();
        String current = destination;

        // 目的地から親をたどって出発点に着くまでループ
        while (current != null) {
            path.addFirst(current); // 常にリストの先頭に追加していく
            current = parentMap.get(current);
        }

        // 復元した結果、先頭が指定の出発地であれば正しい経路
        if (!path.isEmpty() && path.getFirst().equals(start)) {
            return path;
        }

        return null; // 通常ここには到達しません
    }

}
