package jp.houlab.mochidsuki.oneOnOneTeamGameCore.spawnPoint;

import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.*;

import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.config;
import static jp.houlab.mochidsuki.oneOnOneTeamGameCore.OneOnOneTeamGameCoreMain.plugin;

public class SpawnPointUtils {
    // config.ymlから読み込んだ隣接リストを保持するマップ
    private static final Map<String, List<String>> adjacencyList = new HashMap<>();


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
     * 幅優先探索(BFS)を用いて、出発地から目的地までの最短経路を探索します。
     *
     * @param startPoint      出発地の名前
     * @param destinationPoint 目的地の名前
     * @return 経路が見つかった場合は、出発地が先頭、到着地が末尾に来る地点リスト。
     * 見つからない場合はnullを返します。
     */
    @Nullable
    public static List<String> getPathStations(String startPoint, String destinationPoint) {
        // 1. 出発地または目的地がグラフ（隣接リスト）に存在しない場合は、経路がないのでnullを返す
        if (!adjacencyList.containsKey(startPoint) || !adjacencyList.containsKey(destinationPoint)) {
            return null;
        }

        // 2. 出発地と目的地が同じ場合は、その地点のみを含むリストを返す
        if (startPoint.equals(destinationPoint)) {
            return Collections.singletonList(startPoint);
        }

        // 3. BFSのためのデータ構造を準備
        Queue<String> queue = new LinkedList<>();         // これから探索する地点を入れるキュー
        Set<String> visited = new HashSet<>();           // 一度訪れた地点を記録し、ループを防ぐセット
        Map<String, String> parentMap = new HashMap<>(); // 経路を復元するため、どの地点から来たかを記録するマップ (キー:子, バリュー:親)

        // 4. 探索を開始
        queue.add(startPoint);
        visited.add(startPoint);

        while (!queue.isEmpty()) {
            String currentPoint = queue.poll();

            // 目的地に到達したら、経路を復元して返す
            if (currentPoint.equals(destinationPoint)) {
                return reconstructPath(startPoint, destinationPoint, parentMap);
            }

            // 現在地から行ける隣の地点をすべてチェック
            for (String neighbor : adjacencyList.getOrDefault(currentPoint, Collections.emptyList())) {
                // まだ訪れていない地点であれば
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor); // 訪問済みにする
                    parentMap.put(neighbor, currentPoint); // 親（どこから来たか）を記録
                    queue.add(neighbor); // 次の探索対象としてキューに追加
                }
            }
        }

        // 5. キューが空になっても目的地に到達できなかった場合は、経路がないのでnullを返す
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
