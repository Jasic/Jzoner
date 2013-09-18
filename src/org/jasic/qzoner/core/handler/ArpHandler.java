package org.jasic.qzoner.core.handler;
import jpcap.packet.ARPPacket;
import jpcap.packet.DatalinkPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import org.jasic.qzoner.common.GlobalCaches;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.utils.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jasic.utils.StringUtils.fieldval2Map;
import static org.jasic.utils.StringUtils.mapToString;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class ArpHandler extends AHandler {
    private final static Logger loger = LoggerFactory.getLogger(AHandler.class);

    @Override
    public void handle(Packet packet) {

        ARPPacket arpPacket = (ARPPacket) packet;

        switch (arpPacket.operation) {
            //arp请求，直接丢包
            case 0x01: {
                arpPacket = null;
                packet = null;
                break;
            }
            //有arp响应则保存
            case 0x02: {
                System.out.print("捕捉到一个arp响应....");
                DatalinkPacket dataLink = arpPacket.datalink;
                if (dataLink == null) {
                    break;
                }
                EthernetPacket ethPacket = (EthernetPacket) dataLink;

                // 发送者的ip以ethernet包为准（arp包内有可能假）
                byte[] src_mac_byte = ethPacket.src_mac;
                byte[] src_ip_byte = arpPacket.sender_protoaddr;
                if (src_mac_byte == null) break;

                String src_mac = SystemUtil.macByteToStr(src_mac_byte);
                String src_ip = SystemUtil.ipByteArrToStr(src_ip_byte);

                IpMacPair pair = new IpMacPair(src_ip, src_mac);

                GlobalCaches.IP_MAC_LAN_CONNECTIVITY_CACHE.put(src_ip, pair);

                String rel = mapToString(fieldval2Map(pair));
                System.out.println(rel);
                break;
            }
            default: {
                throw new RuntimeException("not support type arp operation[" + arpPacket.operation + "]");
            }
        }
    }
}
