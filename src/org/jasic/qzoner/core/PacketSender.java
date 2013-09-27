package org.jasic.qzoner.core;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.*;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.util.NetWorkUtil;
import org.jasic.utils.TimeUtil;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.jasic.utils.SystemUtil.macStrToByte;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class PacketSender {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(PacketSender.class);

    private static final Map<String, PacketSender> mac_senders;

    private JpcapSender jpcapSender;

    private IpMacPair ip_mac;
    private NetworkInterface nif;
    private long openDeviceTimeOut;

    static {
        mac_senders = new ConcurrentHashMap<String, PacketSender>();
    }

    /**
     * 根据指定网卡的物理地址
     */
    private PacketSender(IpMacPair ip_mac) {
        this.ip_mac = ip_mac;
        this.openDeviceTimeOut = 10;
        this.init();
    }

    /**
     * 根据ip mac地址对获取发送实例
     *
     * @param ipMacPair
     * @return
     */
    public static final PacketSender newPacketSender(IpMacPair ipMacPair) {
        PacketSender sender = mac_senders.get(ipMacPair.getMac());
        if (sender == null) {
            sender = new PacketSender(ipMacPair);
            mac_senders.put(ipMacPair.getMac(), sender);
        }
        return sender;
    }

    private void init() {
        try {

            long t1 = System.currentTimeMillis();
            long t2 = System.currentTimeMillis();

            while (!initIf() && (t2 - t1 < this.openDeviceTimeOut)) {
                t2 = System.currentTimeMillis();
                logger.info("网卡[" + this.ip_mac + "]未初始化成功，继续尝试");
                TimeUtil.sleep(1);
            }
            logger.info("网卡[" + this.ip_mac + "]初始化成功!");

            this.jpcapSender = JpcapSender.openDevice(nif);
        } catch (Exception e) {
            logger.error("Can't init the interface [" + this.ip_mac + "], please check it~");
            System.exit(-1);
        }
    }

    private boolean initIf() {
        this.nif = NetWorkUtil.getIfByMac(this.ip_mac.getMac());
        return this.nif != null;
    }

    /**
     * 此方法的包需要完整
     *
     * @param packet
     */
    public void send(Packet packet) {
        send(packet, true);
    }

    public void send(Packet packet, boolean autoWrapDataLink) {
        send(packet, null, autoWrapDataLink);
    }

    private void send(Packet packet, byte[] dst_addr, boolean autoWrapDataLink) {
        if (autoWrapDataLink) {
            packet = autoWrapDataLink(packet, dst_addr);
        }
        this.jpcapSender.sendPacket(packet);
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
        ethPacket.src_mac = macStrToByte(this.ip_mac.getMac());

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
