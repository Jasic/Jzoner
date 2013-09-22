package org.jasic.qzoner.core.handler;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import org.jasic.qzoner.common.Globalvariables;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.utils.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public abstract class AHandler<P extends Packet> {

    private final static Logger loger = LoggerFactory.getLogger(AHandler.class);
    private IpMacPair localIpMacPair;

    public void before(P packet) {

        this.localIpMacPair = Globalvariables.LOCAL_IP_MAC_PAIR;
    }

    public void after(P packet) {
    }

    public void process(P packet) {
        before(packet);

        /**
         * 1、src_mac为本机地址不处理
         * 2、dst_mac为广播地址不处理
         */
        boolean shouldHandle = false;
        EthernetPacket eth = (EthernetPacket) packet.datalink;
        if (eth != null) {
            byte[] src_mac = eth.src_mac;
            byte[] dst_mac = eth.dst_mac;
            if (src_mac != null && dst_mac != null && localIpMacPair != null) {//基本条件
                // 上面两条件
                if (!localIpMacPair.getMac().equals(SystemUtil.macByteToStr(src_mac)) &&
                        !SystemUtil.macByteToStr(dst_mac).equals("FF-FF-FF-FF-FF-FF") &&
                        !SystemUtil.macByteToStr(dst_mac).equals("00-00-00-00-00-00")) {

                    shouldHandle = true;
                }
            }
        }

        if (!shouldHandle) return;

        handle(packet);
        after(packet);
    }

    public abstract void handle(P packet);
}
