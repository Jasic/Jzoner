package org.jasic.qzoner.entry;
import org.jasic.modue.annotation.Module;
import org.jasic.modue.moduleface.AModuleable;
import org.jasic.modue.moduleface.IService;
import org.jasic.qzoner.common.GlobalConstans;
import org.jasic.qzoner.common.Globalvariables;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.util.NetWorkUtil;
import org.jasic.utils.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jasic.utils.StringUtils.entityToString;
/**
 * User: Jasic
 * Date: 13-9-18
 */
@Module(name = GlobalConstans.MODULE_NAME_SYSTEM_INIT, priority = GlobalConstans.MODULE_PRIORITY_SYSTEM_INIT, status = GlobalConstans.MODULE_STATUS_SYSTEM_INIT)
public class SystemInitModule extends AModuleable implements IService {
    private static final Logger logger = LoggerFactory.getLogger(ArpStrategyModule.class);
    private static String logHeader = "[SysModule]";

    @Override
    public void service() {
        String mac = Globalvariables.MAC_LOCAL_ETH_0;
        IpMacPair localIpMac = new IpMacPair();
        localIpMac.setIp(NetWorkUtil.getIpByMac(mac));
        localIpMac.setSubNet(NetWorkUtil.getSubNetByMac(mac));
        localIpMac.setMac(mac);

        IpMacPair gateWay = new IpMacPair();

        gateWay.setIp(SystemUtil.getGateWayIp(localIpMac.getIp()));
        gateWay.setSubNet(localIpMac.getSubNet());

        logger.info(entityToString(gateWay));
        Globalvariables.GATE_WAY_IP_MAC_PAIR = gateWay;
    }
}
