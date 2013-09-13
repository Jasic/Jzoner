package org.jasic.qzoner.core.entity;
/**
 * ip、物理地址对
 * User: Jasic
 * Date: 13-9-11
 */
public class IpMacPair {

    public IpMacPair() {
    }

    public IpMacPair(String ip, String mac) {
        this.ip = ip;
        this.mac = mac;
    }

    private String ip;
    private String mac;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "IpMacPair{" +
                "ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
