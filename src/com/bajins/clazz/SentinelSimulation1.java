package com.bajins.clazz;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SentinelSimulation1 {
    private static final String MASTER_NAME = "mymaster";
    private static final int QUORUM = 2;
    private Map<String, RedisNode1> nodes = new HashMap<>();
    private List<SentinelNode1> sentinels = new ArrayList<>();
    private RedisNode1 master;
    private boolean failoverInProgress = false;
    private ScheduledExecutorService executorService;

    public SentinelSimulation1() {
        // 创建Redis节点
        nodes.put("127.0.0.1:6380", new RedisNode1("127.0.0.1", 6380, false));
        nodes.put("127.0.0.1:6381", new RedisNode1("127.0.0.1", 6381, false));
        nodes.put("127.0.0.1:6382", new RedisNode1("127.0.0.1", 6382, false));
        nodes.put("127.0.0.1:6383", new RedisNode1("127.0.0.1", 6383, false));

        // 创建哨兵节点
        sentinels.add(new SentinelNode1("127.0.0.1", 26379, MASTER_NAME));
        sentinels.add(new SentinelNode1("127.0.0.1", 26380, MASTER_NAME));
        sentinels.add(new SentinelNode1("127.0.0.1", 26381, MASTER_NAME));
    }

    public void start() {
        // 启动定时任务检查主节点状态
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::checkMaster, 0, 2, TimeUnit.SECONDS);
    }

    public void stop() {
        // 停止定时任务
        executorService.shutdown();
    }

    private void checkMaster() {
        try {
            // 执行SENTINEL get-master-addr-by-name命令获取主节点信息
            SentinelNode1 sentinel = getAvailableSentinel();
            String[] masterAddr = sentinel.getMasterAddr();
            if (master == null || !master.getHost().equals(masterAddr[0]) || master.getPort() != Integer.parseInt(masterAddr[1])) {
                // 如果主节点发生变化，触发故障转移
                failover();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void failover() {
        if (failoverInProgress) {
            return;
        }
        failoverInProgress = true;
        try {
            // 执行SENTINEL is-master-down-by-addr命令检查主节点是否已经下线
            SentinelNode1 sentinel = getAvailableSentinel();
            boolean isMasterDown = sentinel.isMasterDown(master.getHost(), master.getPort());
            if (!isMasterDown) {
                return;
            }

            // 获取所有从节点信息
            List<RedisNode1> slaves = new ArrayList<>();
            for (Map.Entry<String, RedisNode1> entry : nodes.entrySet()) {
                RedisNode1 node = entry.getValue();
                if (!node.isMaster() && node.getParent().equals(master)) {
                    slaves.add(node);
                }
            }

            // 选择新的主节点
            RedisNode1 newMaster = null;
            for (Map.Entry<String, RedisNode1> entry : nodes.entrySet()) {
                RedisNode1 node = entry.getValue();
                if (node.isMaster() && !node.equals(master) && isQuorum(node, slaves)) {
                    newMaster = node;
                    break;
                }
            }

            if (newMaster == null) {
                throw new RuntimeException("Failed to select a new master node");
            }

            // 更新所有哨兵节点的主节点信息
            for (SentinelNode1 s : sentinels) {
                s.updateMaster(newMaster);
            }

            // 更新新的主节点信息
            //master.setMaster(false);
            master.setAvailable(true);
            //newMaster.setMaster(true);
            newMaster.setAvailable(true);
            newMaster.setSlaves(slaves);
            for (RedisNode1 slave : slaves) {
                slave.setParent(newMaster);
                slave.setAvailable(true);
            }

            // 更新本地主节点信息
            master = newMaster;
            System.out.println("Failover succeeded, new master is " + master.getHost() + ":" + master.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            failoverInProgress = false;
        }
    }

    private boolean isQuorum(RedisNode1 node, List<RedisNode1> slaves) {
        int count = 1;
        for (RedisNode1 slave : slaves) {
            if (slave.getParent().equals(node)) {
                count++;
            }
        }
        return count >= QUORUM;
    }

    private SentinelNode1 getAvailableSentinel() {
        for (SentinelNode1 sentinel : sentinels) {
            if (sentinel.isAvailable()) {
                return sentinel;
            }
        }
        throw new RuntimeException("No available sentinels");
    }

    public static void main(String[] args) throws InterruptedException {
        SentinelSimulation1 redis = new SentinelSimulation1();
        redis.start();

        // 模拟节点故障
        Thread failureThread = new Thread(() -> {
            try {
                Thread.sleep(10000);
                redis.nodes.get("127.0.0.1:6380").setAvailable(false);
                System.out.println("Node 127.0.0.1:6380 is down");
                Thread.sleep(10000);
                redis.nodes.get("127.0.0.1:6381").setAvailable(false);
                System.out.println("Node 127.0.0.1:6381 is down");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        failureThread.start();

        Thread.sleep(60000);
        redis.stop();
    }
}

class RedisNode1 {
    private String host;
    private int port;
    private boolean isMaster;
    private boolean available;
    private RedisNode1 parent;
    private List<RedisNode1> slaves;

    public RedisNode1(String host, int port, boolean isMaster) {
        this.host = host;
        this.port = port;
        this.isMaster = isMaster;
        this.available = true;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public RedisNode1 getParent() {
        return parent;
    }

    public void setParent(RedisNode1 parent) {
        this.parent = parent;
    }

    public List<RedisNode1> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<RedisNode1> slaves) {
        this.slaves = slaves;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}

class SentinelNode1 {
    private String host;
    private int port;
    private String masterName;
    private boolean available;

    public SentinelNode1(String host, int port, String masterName) {
        this.host = host;
        this.port = port;
        this.masterName = masterName;
        this.available = true;
    }

    public String[] getMasterAddr() {
        // 在实际的Redis哨兵模式中，该方法会执行SENTINEL get-master-addr-by-name命令获取主节点信息
        return new String[]{"127.0.0.1", "6380"};
    }

    public boolean isMasterDown(String host, int port) {
        // 在实际的Redis哨兵模式中，该方法会执行SENTINEL is-master-down-by-addr命令检查主节点是否已经下线
        return true;
    }

    public void updateMaster(RedisNode1 newMaster) {
        // 在实际的Redis哨兵模式中，该方法会执行SENTINEL failover命令进行故障转移
        System.out.println("Update master to " + newMaster.getHost() + ":" + newMaster.getPort());
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
