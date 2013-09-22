package org.jasic.qzoner.entry;
import org.jasic.modue.annotation.Module;
import org.jasic.modue.moduleface.AModuleable;
import org.jasic.modue.moduleface.IService;
import org.jasic.qzoner.common.GlobalCaches;
import org.jasic.qzoner.common.GlobalConstans;
import org.jasic.qzoner.common.Globalvariables;
import org.jasic.qzoner.core.ArpStrategy;
import org.jasic.qzoner.core.PacketClient;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.util.NetWorkUtil;
import org.jasic.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.jasic.utils.StringUtils.fieldval2Map;
/**
 * User: Jasic
 * Date: 13-9-18
 */
@Module(name = GlobalConstans.MODULE_NAME_PACKET_STRATEGY, priority = GlobalConstans.MODULE_PRIORITY_PACKET_STRATEGY, status = GlobalConstans.MODULE_STATUS_PACKET_STRATEGY)
public class StrategyModule extends AModuleable implements IService {

    private static final Logger logger = LoggerFactory.getLogger(StrategyModule.class);
    private static String logHeader = "[ArpModule]";
    private PacketClient arpClient;

    private ArpStrategy arpStrategy;

    private ExecutorService es;
    IpMacPair localPair = new IpMacPair();


    public StrategyModule() {
        super();
        this.init();
    }

    private void init() {
        this.es = Executors.newCachedThreadPool();
        this.initLocalPair();
    }

    /**
     * 初始化本地网卡信息
     */
    private void initLocalPair() {
        String mac = Globalvariables.MAC_LOCAL_ETH_0;
        localPair.setMac(mac);
        localPair.setIp(NetWorkUtil.getIpByMac(mac));
        localPair.setSubNet(NetWorkUtil.getSubNetByMac(mac));
    }


    @Override
    public void service() {
        while (true) {
            if (localPair.getIp() != null && localPair.getSubNet() != null) {
                break;
            }
            logger.info(logHeader + ":本地网卡[" + localPair.getMac() + "]，信息读取失败，继续尝试");
            this.initLocalPair();
            TimeUtil.sleep(1);
        }

        logger.info(logHeader + ":本地网卡[" + fieldval2Map(localPair) + "]，初始化成功");

        this.arpClient = new PacketClient(localPair, GlobalCaches.IP_MAC_LAN_ARP_REQUEST_QUEUE);
        this.arpStrategy = new ArpStrategy(localPair);

        this.arpStrategy.start();
        this.arpClient.start();
    }
}
