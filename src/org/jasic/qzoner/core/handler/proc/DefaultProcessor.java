package org.jasic.qzoner.core.handler.proc;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import org.jasic.common.DefualtThreadFactory;
import org.jasic.qzoner.common.GlobalCaches;
import org.jasic.utils.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
/**
 * User: Jasic
 * Date: 13-9-22
 */
public class DefaultProcessor extends AProcessor<IPPacket> {
    private final static Logger loger = LoggerFactory.getLogger(DefaultProcessor.class);
    private final static String logHeader = "转发包";

    private List<Packet> list;

    private Timer timer;

    /**
     *
     */
    public DefaultProcessor() {
        super(Executors.newFixedThreadPool(1, new DefualtThreadFactory(logHeader)));
        list = new ArrayList<Packet>();

        this.init();
    }

    private void init() {
        timer = new Timer(logHeader, true);
        timer.schedule(new RedirectStrategy(), 0, 1);
    }

    @Override
    protected void doProcess(IPPacket packet) {

        EthernetPacket ethPacket = (EthernetPacket) packet.datalink;
        String old_mac = SystemUtil.macByteToStr(ethPacket.dst_mac);
        String new_mac = this.gateWayIpMacPair.getMac();

        // 需要转发的数据包为：1、dst_mac：为本网卡mac；2、dst_ip非本网卡ip
        if (old_mac.equals(localIpMacPair.getMac()) && !packet.dst_ip.getHostAddress().equals(localIpMacPair.getIp())) {
            ((EthernetPacket) packet.datalink).dst_mac = SystemUtil.macStrToByte(new_mac);
//            loger.info(logHeader + "数据包目的mac由[" + old_mac + "] 转成 [" + new_mac + "]");
            synchronized (this.list) {
                this.list.add(packet);
//                loger.info(logHeader + packet);
            }
        }
    }

    private class RedirectStrategy extends TimerTask {

        @Override
        public void run() {
            synchronized (list) {
                if (list.size() == 0) return;
                try {
                    GlobalCaches.IP_MAC_LAN_PACKET_TO_BE_SEND_QUEUE.put(list);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                list = new ArrayList<Packet>();
            }
        }
    }
}
