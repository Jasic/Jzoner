package org.jasic.qzoner.core.handler;
import jpcap.packet.ARPPacket;
import jpcap.packet.Packet;

import static org.jasic.utils.StringUtils.fieldval2Map;
import static org.jasic.utils.StringUtils.mapToString;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class ArpHandler extends AHandler {


    @Override
    public void handle(Packet packet) {

        ARPPacket arpPacket = (ARPPacket) packet;

        switch (arpPacket.operation) {
            case 0x01: {

                packet = null;
                arpPacket = null;
//                System.out.print("捕捉到一个arp请求....将丢弃");
                break;
            }

            case 0x02: {

                System.out.print("捕捉到一个arp响应....");
                String rel = mapToString(fieldval2Map(arpPacket, ARPPacket.class, 3));
                System.out.println(rel);
                break;
            }

            default: {
                throw new RuntimeException("not support type~");
            }
        }
    }
}
