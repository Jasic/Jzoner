package org.jasic.qzoner.core.handler.proc;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import org.jasic.common.DefualtThreadFactory;
import org.jasic.qzoner.common.Globalvariables;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.core.handler.filter.DefaultFilterParallel;
import org.jasic.qzoner.core.handler.filter.FilterParallel;
import org.jasic.utils.SystemUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 对数据包的类，使用线程池去执行处理。
 * User: Jasic
 * Date: 13-9-18
 */
public abstract class AProcessor<P extends IPPacket> {

    // 本地ip_mac对
    protected IpMacPair localIpMacPair;

    //网关ip_mac对
    protected IpMacPair gateWayIpMacPair;

    // 并行过滤
    protected FilterParallel filterParallel;

    // 处理器的线程池
    private ExecutorService es;

    public AProcessor() {
        this(null);
    }

    public AProcessor(ExecutorService es) {
        this.es = es;

        this.init();
    }

    private void init() {
        this.localIpMacPair = Globalvariables.LOCAL_IP_MAC_PAIR;
        this.gateWayIpMacPair = Globalvariables.GATE_WAY_IP_MAC_PAIR;
        this.filterParallel = new DefaultFilterParallel();

        if (this.es == null) {
            this.es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new DefualtThreadFactory("IP包处理池"));
        }
    }

    public void process(final P packet) {
        es.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        // 由本机发出的不处理
                        if (packet.src_ip.getHostAddress().equals(localIpMacPair.getIp())) {
                            return;
                        }

                        //发往本机ip的包，即不是需要捕获的包，所以不处理
                        if (packet.dst_ip.getHostAddress().equals(localIpMacPair.getIp())) {
                            return;
                        }

                        EthernetPacket ethernetPacket = (EthernetPacket) packet.datalink;
                        String src_mac = SystemUtil.macByteToStr(ethernetPacket.src_mac);

                        //本机发出的的包不处理
                        if (src_mac.equals(localIpMacPair.getMac())) {
                            return;
                        }

                        doProcess(packet);
                    }
                }
        );
    }

    protected abstract void doProcess(P p);
}
