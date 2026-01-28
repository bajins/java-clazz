package com.bajins.clazz;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 使用 字典树/前缀树（Trie Tree）匹配分割字符串
 * <p>
 * 查询的时间复杂度O(n)仅与“待匹配字符串的长度”有关，与规则库的大小（几百个还是几万个Key）几乎无关。
 * <p>
 * 1.  **不使用正则表达式 (No Regex)**：O(1)
 *     *   正则方案：`Pattern.compile("^(ST320|ST280|...)")`。当Key有几百个时，正则引擎的回溯极其消耗性能。
 *     *   Trie方案：直接字符导航，时间复杂度为 $O(L)$（L为单个Token的长度），与Key的总数量无关。
 * <p>
 * 2.  **不使用暴力循环 (No Loop startsWith)**： O(n * m)
 *     *   普通方案：`for (String key : keyList) if (token.startsWith(key)) ...`。这是 $O(N \times M)$ 的复杂度（N是token数，M是key的数量）。如果规则库有1000个Key，每处理一个逗号片段就要循环1000次。
 *     *   Trie方案：扫描一遍字符串即完成，不需要循环规则库。
 * <p>
 * 3.  **解决了前缀冲突**：
 *     *   如果规则里同时有 `ST320` 和 `ST320_X1_LeftSide`。
 *     *   输入 `ST320_X1_LeftSide1.287`。
 *     *   代码中的 `findLongestPrefixKey` 会在路过 `ST320` 时记录一次，但继续往下走到 `ST320_X1_LeftSide` 时会发现更长的匹配，从而返回正确的最长Key。
 *
 * @author bajins
 */
public class TrieSplitterUtil {
    /**
     * 定义Trie树节点
     * 为了极致性能，如果字符集仅限ASCII，可以用数组 Node[128] 代替 HashMap。
     * 但考虑到你的Key包含中文（如“配方名”），使用 HashMap 兼容性更好。
     */
    static class TrieNode {
        Map<Character, TrieNode> children = new ConcurrentHashMap<>();
        boolean isEnd = false; // 标记是否是一个完整的 Key
        String fullKey = null; // 存储完整 Key 方便回溯提取
    }

    /**
     * 字典树管理类
     */
   public static class KeyTrie {
        private final TrieNode root = new TrieNode();

        // 构建 Trie：传入所有前缀
        public void buildTrie(List<String> prefixes) {
            for (String prefix : prefixes) {
                insert(prefix);
            }
        }

        /**
         * 插入 Key
         * @param key
         */
        public void insert(String key) {
            TrieNode node = root;
            for (char c : key.toCharArray()) {
                node = node.children.computeIfAbsent(c, k -> new TrieNode());
            }
            node.isEnd = true;
            node.fullKey = key; // 标记这是一个完整前缀
        }

        /**
         * 动态匹配：在Trie树中寻找字符串的“最长”匹配Key
         *
         * @param text 输入的字符串，如 "ST320_Y1_LowerSide1.424"
         * @return 匹配到的最长Key，如果没有匹配则返回 null
         */
        public String findLongestPrefix(String text) {
            TrieNode node = root;
            String longestMatch = null;

            // 逐个字符向下走
            for (char c : text.toCharArray()) {
                if (!node.children.containsKey(c)) {
                    // 树中断了，停止查找
                    break;
                }
                node = node.children.get(c);
                // 每次遇到结束点，记录一次，这样保证最后记录的是最长的
                if (node.isEnd) {
                    // 记录当前匹配到的Key，继续往后找看有没有更长的
                    // 例如：匹配到了 "ST320"，记录下来，继续看后面是不是 "ST320_X..."
                    longestMatch = node.fullKey;
                }
            }
            return longestMatch;
        }
    }
}