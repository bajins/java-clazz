package com.bajins.clazz;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SentinelSimulation {
    // Redis主节点
    private RedisNode master;
    // Redis从节点列表
    private List<RedisNode> slaves;
    // Redis哨兵节点列表
    private List<SentinelNode> sentinels;
    // Redis主节点名称
    private String masterName;
    // Redis主节点故障次数
    private int failCount = 0;
    // Redis主节点最大故障次数
    private int maxFailCount = 3;
    // Redis主节点故障锁
    private Lock failLock = new ReentrantLock();

    public SentinelSimulation(String masterName) {
        this.masterName = masterName;
        this.master = new RedisNode("127.0.0.1", 6379, true);
        this.slaves = new ArrayList<>();
        this.sentinels = new ArrayList<>();
    }

    // 注册从节点
    public void addSlave(String host, int port) {
        RedisNode slave = new RedisNode(host, port, false);
        slaves.add(slave);
    }

    // 注册哨兵节点
    public void addSentinel(String host, int port) {
        SentinelNode sentinel = new SentinelNode(host, port, masterName);
        sentinels.add(sentinel);
    }

    // 获取可用的Redis节点
    public RedisNode getRedisNode() {
        if (master.isAvailable()) {
            return master;
        } else {
            for (RedisNode slave : slaves) {
                if (slave.isAvailable()) {
                    return slave;
                }
            }
        }
        return null;
    }

    // Redis主节点故障处理
    public void handleMasterFail() {
        failLock.lock();
        try {
            failCount++;
            if (failCount >= maxFailCount) {
                // 触发故障转移
                RedisNode newMaster = getRedisNode();
                if (newMaster != null) {
                    // 更新哨兵节点的主节点信息
                    for (SentinelNode sentinel : sentinels) {
                        sentinel.updateMaster(newMaster);
                    }
                    // 更新主节点和从节点的信息
                    master.setAvailable(false);
                    for (RedisNode slave : slaves) {
                        slave.setAvailable(true);
                        slave.setParent(newMaster);
                    }
                    newMaster.setAvailable(true);
                    newMaster.setSlaves(slaves);
                    slaves.clear();
                    master = newMaster;
                    failCount = 0;
                }
            }
        } finally {
            failLock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SentinelSimulation redis = new SentinelSimulation("mymaster");
        redis.addSlave("127.0.0.1", 6380);
        redis.addSlave("127.0.0.1", 6381);
        redis.addSentinel("127.0.0.1", 26379);
        redis.addSentinel("127.0.0.1", 26380);

        Thread masterThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                redis.master.setAvailable(false);
                System.out.println("Master is down");
                redis.handleMasterFail();
            }
        });
        masterThread.start();

        Thread slaveThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                redis.slaves.get(0).setAvailable(false);
                System.out.println("Slave is down");
            }
        });
        slaveThread.start();

        Thread sentinelThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                redis.sentinels.get(0).getMaster().setAvailable(false);
                System.out.println("Sentinel is down");
            }
        });
        sentinelThread.start();

        while (true) {
            Thread.sleep(1000);
        }
    }
}

// Redis节点类
class RedisNode {
    private String host;
    private int port;
    private boolean available;
    private RedisNode parent;
    private List<RedisNode> slaves;

    public RedisNode(String host, int port, boolean available) {
        this.host = host;
        this.port = port;
        this.available = available;
        this.slaves = new ArrayList<>();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public RedisNode getParent() {
        return parent;
    }

    public void setParent(RedisNode parent) {
        this.parent = parent;
    }

    public List<RedisNode> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<RedisNode> slaves) {
        this.slaves = slaves;
    }
}

// Redis哨兵节点类
class SentinelNode {
    private String host;
    private int port;
    private String masterName;
    private RedisNode master;

    public SentinelNode(String host, int port, String masterName) {
        this.host = host;
        this.port = port;
        this.masterName = masterName;
    }

    public void updateMaster(RedisNode newMaster) {
        if (newMaster != null && newMaster != master) {
            System.out.println("Update master from " + master.getHost() + ":" + master.getPort() + " to " + newMaster.getHost() + ":" + newMaster.getPort());
            master = newMaster;
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public RedisNode getMaster() {
        return master;
    }

    public void setMaster(RedisNode master) {
        this.master = master;
    }
}
