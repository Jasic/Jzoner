package org.jasic.qzoner.core;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.*;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.util.NetWorkUtil;
import org.slf4j.Logger;

import java.io.IOException;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class PacketSender {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(PacketSender.class);

    private JpcapSender jpcapSender;

    private IpMacPair ip_mac;

    /**
     * 根据指定网卡的物理地址
     */
    public PacketSender(IpMacPair ip_mac) {
        this.ip_mac = ip_mac;
        this.init();
    }

    private void init() {
        NetworkInterface nif = NetWorkUtil.getIpByMac(this.ip_mac.getMac());
        try {
            this.jpcapSender = JpcapSender.openDevice(nif);
        } catch (IOException e) {
            logger.error("Can't init the interface [" + this.ip_mac + "], please check it~");
            e.printStackTrace();
        }
    }

    /**
     * 此方法的包需要完整
     * @param packet
     */
    public void send(Packet packet) {
        packet = autoWrapDataLink(packet, null);
        this.jpcapSender.sendPacket(packet);
    }

    private void send(Packet packet, byte[] dst_addr) {
        packet = autoWrapDataLink(packet, dst_addr);
        this.jpcapSender.sendPacket(packet);
    }

    /**
     *
     * @param packet 数据包
     * @param dst_addr 目的地址mac
     */
    public void send(Packet packet, String dst_addr) {
        send(packet, NetWorkUtil.macStrToByte(dst_addr));
    }

    /**
     * 自动添另数据链路头
     * 相当于程序初始化时使用JpcapSender.openRawSocket()
     */
    private Packet autoWrapDataLink(Packet packet, byte[] dst_addr) {

        DatalinkPacket datalinkPacket = packet.datalink;
        EthernetPacket ethPacket;

        if (datalinkPacket != null && (datalinkPacket instanceof EthernetPacket)) {
            ethPacket = (EthernetPacket) datalinkPacket;
        } else {
            ethPacket = new EthernetPacket();
        }

        //1、设置目标地址
        if (dst_addr != null) {
            ethPacket.dst_mac = dst_addr;
        }
        // 2、设置源地址
        ethPacket.src_mac = NetWorkUtil.macStrToByte(this.ip_mac.getMac());

        // 3、设置帧类型
        if (packet instanceof ARPPacket) {
            ethPacket.frametype = EthernetPacket.ETHERTYPE_ARP;
        } else if (packet instanceof IPPacket) {
            ethPacket.frametype = EthernetPacket.ETHERTYPE_IP;
        }

        packet.datalink = ethPacket;
        return packet;
    }
}
