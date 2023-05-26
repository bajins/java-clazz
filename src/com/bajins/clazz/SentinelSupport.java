package com.bajins.clazz;

import java.util.HashSet;
import java.util.Set;

/**
 * 哨兵模式是一种高可用方案。它可以监控主服务器,当主服务器下线时自动将从服务器提升为主服务器
 * 主要用在任务只能单进程单线程执行的情况下
 * <p>
 * <ul>
 * <li>1. 维护主服务器地址和从服务器集合
 * <li>2. 检查主服务器是否在线
 * <li>3. 当主服务器下线时,从从服务器集合中选举一个作为新的主服务器
 * <li>4. 返回主服务器地址,自动失败转移
 * </ul>
 */
public class SentinelSupport {
    private String masterName;
    private String masterAddress;
    private Set<String> slaveAddresses = new HashSet<>();

    public SentinelSupport(String masterName, String masterAddress) {
        this.masterName = masterName;
        this.masterAddress = masterAddress;
    }

    public void addSlave(String slaveAddress) {
        slaveAddresses.add(slaveAddress);
    }

    public String getMasterAddress() {
        // 连接主服务器,检查其是否在线
        if (isMasterOnline()) {
            return masterAddress;
        }

        // 主服务器下线,需要从从服务器中选举一个作为新的主服务器
        String newMaster = electNewMaster();
        masterAddress = newMaster;
        return newMaster;
    }

    private boolean isMasterOnline() {
        // 连接主服务器,检查其是否在线
        // ...
        return true;
    }

    private String electNewMaster() {
        // 选举算法:从slaveAddresses中选出一个服务器作为新的主服务器
        // ...
        return slaveAddresses.iterator().next();
    }
}
