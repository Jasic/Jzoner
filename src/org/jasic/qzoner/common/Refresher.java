package org.jasic.qzoner.common;
import cn.tisson.framework.utils.StringUtils;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.util.NetWorkUtil;
import org.jasic.utils.SystemUtil;
import org.jasic.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jasic.utils.StringUtils.entityToString;
/**
 * User: Jasic
 * Date: 13-9-19
 */
public class Refresher extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Refresher.class);
    private static final String logHeader = "[剧新]";
    private int interval;
    private boolean isInit;

    public Refresher() {
        this.interval = Globalvariables.SYSTEM_REFRESH_INTERVAL;
    }

    public Refresher(int interval) {
        this.interval = interval;
    }

    public void run() {
        while (!isInterrupted()) {
            String mac = Globalvariables.MAC_LOCAL_ETH_0;
            IpMacPair localIpMac = new IpMacPair();
            localIpMac.setIp(NetWorkUtil.getIpByMac(mac));
            localIpMac.setSubNet(NetWorkUtil.getSubNetByMac(mac));
            localIpMac.setMac(mac);

            if (localIpMac.getIp() != null) {
                logger.info(logHeader + "[本地ip对]" + entityToString(localIpMac));
                IpMacPair gateWay = new IpMacPair();
                String gateWayIp = SystemUtil.getGateWayIp(localIpMac.getIp());
                String gateWaySubNet = localIpMac.getSubNet();
                if (gateWayIp != null) {
                    String gateWayMac = SystemUtil.getLanMacByIp(gateWayIp);
                    gateWay.setIp(gateWayIp);
                    gateWay.setSubNet(gateWaySubNet);
                    gateWay.setMac(gateWayMac);

                    if (!StringUtils.hasEmpty(gateWayIp, gateWaySubNet, gateWayMac)) {
                        logger.info(logHeader + "[网关ip对]" + entityToString(gateWay));
                        Globalvariables.LOCAL_IP_MAC_PAIR = localIpMac;
                        Globalvariables.GATE_WAY_IP_MAC_PAIR = gateWay;
                        isInit = true;
                    }
                }
            }
            if (!isInit) {
                TimeUtil.sleep(this.interval);
            } else {
                TimeUtil.sleep(this.interval * 10);
            }
        }
    }
}
