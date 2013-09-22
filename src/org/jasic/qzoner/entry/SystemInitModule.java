package org.jasic.qzoner.entry;
import org.jasic.modue.annotation.Module;
import org.jasic.modue.moduleface.AModuleable;
import org.jasic.modue.moduleface.IService;
import org.jasic.qzoner.common.GlobalConstans;
import org.jasic.qzoner.common.Refresher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * User: Jasic
 * Date: 13-9-18
 */
@Module(name = GlobalConstans.MODULE_NAME_SYSTEM_INIT, priority = GlobalConstans.MODULE_PRIORITY_SYSTEM_INIT, status = GlobalConstans.MODULE_STATUS_SYSTEM_INIT)
public class SystemInitModule extends AModuleable implements IService {
    private static final Logger logger = LoggerFactory.getLogger(StrategyModule.class);
    private static String logHeader = "[SysModule]";
    private Refresher fresher;

    public SystemInitModule() {

        super();
        fresher = new Refresher();
    }

    @Override
    public void service() {

        fresher.start();
    }
}
