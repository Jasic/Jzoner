package org.jasic.qzoner.test;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.PacketReceiver;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import org.jasic.qzoner.util.NetWorkUtil;
import org.jasic.utils.ByteUtil;
import org.junit.Test;

import java.io.IOException;
/**
 * User: Jasic
 * Date: 13-9-10
 */
public class ArpTest {
    {
//    1.首先，获得所有网卡列表
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        try {
            JpcapCaptor captor = JpcapCaptor.openDevice(devices[0], 65535, false, 20);

            captor.processPacket(-1, new PacketReceiver() {
                @Override
                public void receivePacket(Packet packet) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetDevice() {
        NetworkInterface intf = NetWorkUtil.getIfByMac("78-45-C4-05-80-1D");

        try {
            JpcapCaptor captor = JpcapCaptor.openDevice(intf, 65535, false, 20);
            captor.setFilter("arp", true);
            captor.loopPacket(-1, new PacketReceiver() {
                @Override
                public void receivePacket(Packet packet) {
                    EthernetPacket eth = (EthernetPacket) packet.datalink;
                    ARPPacket arpPacket = ((ARPPacket) packet);

                    String type;

                    if (arpPacket.operation == 0x01) {
                        type = "request";
                    } else {
                        type = "reply";
                    }

                    System.out.print(type + "    src:[" + ByteUtil.toHexString(eth.src_mac));
                    System.out.print(" --> ");
                    System.out.println("dsc:[" + ByteUtil.toHexString(eth.dst_mac));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
