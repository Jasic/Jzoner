package org.jasic.qzoner;
import cn.tisson.framework.config.ConfigHandler;
import org.jasic.modue.ModuleManager;
import org.jasic.qzoner.common.Globalvariables;
/**
 * User: Jasic
 * Date: 13-9-18
 */
public class MainThread {

    private ModuleManager manager;

    public MainThread() {
        if (!ConfigHandler.loadConfigWithoutDB(Globalvariables.class)) {
            System.exit(-1);
        }
        manager = new ModuleManager();
    }


    public void start() {
        manager.loadModule(MainThread.class);
        manager.startModule();
    }

    public static void main(String[] args) {
        MainThread main = new MainThread();
        main.start();
    }

}
