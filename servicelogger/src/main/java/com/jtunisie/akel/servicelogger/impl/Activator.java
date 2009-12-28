/*
 *  @author : Slim Ouertani
 *  @mail : ouertani@gmail.com
 */
package com.jtunisie.akel.servicelogger.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.FindHook;

/**
 *
 * @author slim ouertani
 */
public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {       
        LoggerHooks loggerHooks = new LoggerHooks(context);
        context.registerService(new String[]{FindHook.class.getName(), EventHook.class.getName()},
                loggerHooks, null);

    }

    @Override
    public void stop(BundleContext context) throws Exception {       
    }
}
