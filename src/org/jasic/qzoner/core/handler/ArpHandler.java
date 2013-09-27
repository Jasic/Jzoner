package org.jasic.qzoner.core.handler;
import jpcap.packet.ARPPacket;
import jpcap.packet.DatalinkPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import org.jasic.qzoner.common.GlobalCaches;
import org.jasic.qzoner.common.Globalvariables;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.utils.ByteUtil;
import org.jasic.utils.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.jasic.utils.StringUtils.fieldval2Map;
import static org.jasic.utils.StringUtils.mapToString;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class ArpHandler extends AHandler {
    private final static Logger logger = LoggerFactory.getLogger(AHandler.class);
    private final static String logHeader = "[arp响应]";
    private ExecutorService es;

    private IpMacPair localIpMac;

    public ArpHandler() {
        this.es = Executors.newFixedThreadPool(10);
    }

    @Override
    public void handle(Packet packet) {
        this.es.execute(new ArpHandable((ARPPacket) packet));
    }

    class ArpHandable implements Runnable {

        private ARPPacket arpPacket;

        public ArpHandable(ARPPacket arpPacket) {
            this.arpPacket = arpPacket;
        }

        @Override
        public void run() {
            localIpMac = Globalvariables.LOCAL_IP_MAC_PAIR;
            switch (arpPacket.operation) {
                //arp请求，如果是广播请求网关，则即刻响应
                case 0x01: {
                    arpPacket = null;
                    break;
                }
                //有arp响应则保存
                case 0x02: {
                    DatalinkPacket dataLink = arpPacket.datalink;
                    if (dataLink == null) {
                        break;
                    }
                    EthernetPacket ethPacket = (EthernetPacket) dataLink;

                    // 发送者的ip以ethernet包为准（arp包内有可能假）
                    byte[] src_mac_byte = ethPacket.src_mac;
                    byte[] src_ip_byte = arpPacket.sender_protoaddr;
                    if (src_mac_byte == null) break;

                    // 捕获的为本机发出的arp不处理
                    if (localIpMac != null && SystemUtil.macByteToStr(src_mac_byte).equals(localIpMac.getMac())) {
                        break;
                    }

                    String src_mac = macByteToStr(src_mac_byte);
                    String src_ip = SystemUtil.ipByteArrToStr(src_ip_byte);

                    IpMacPair pair = new IpMacPair(src_ip, src_mac);

                    GlobalCaches.IP_MAC_LAN_CONNECTIVITY_CACHE.put(src_ip, pair);

                    String rel = mapToString(fieldval2Map(pair));
//                    logger.info(logHeader + rel);
                    break;
                }
                default: {
                    throw new RuntimeException("not support type arp operation[" + arpPacket.operation + "]");
                }
            }
        }
    }

    public static String macByteToStr(byte[] arr) {
        if (arr == null) {
            return null;
        }
        StringBuilder mac_str = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            mac_str.append(ByteUtil.toHexString(new byte[]{arr[i]}));
            if (i < arr.length - 1)
                mac_str.append("-");
        }
        return mac_str.toString();
    }
}
