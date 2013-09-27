package org.jasic.qzoner.common;
/**
 * User: Jasic
 * Date: 13-9-18
 */
public class GlobalConstans {

    /**
     * **************************************************************************************
     * --------------------------------- 模块启动状态配置 -----------------------------------
     * **************************************************************************************
     */

    // 系统初始化
    public final static String MODULE_NAME_SYSTEM_INIT = "[SystemInit]";
    public final static int MODULE_PRIORITY_SYSTEM_INIT = 0;
    public static final boolean MODULE_STATUS_SYSTEM_INIT = true;

    // Arp策略模块
    public final static String MODULE_NAME_PACKET_STRATEGY = "[StrategyModule]";
    public final static int MODULE_PRIORITY_PACKET_STRATEGY = 1;
    public static final boolean MODULE_STATUS_PACKET_STRATEGY = true;


}
