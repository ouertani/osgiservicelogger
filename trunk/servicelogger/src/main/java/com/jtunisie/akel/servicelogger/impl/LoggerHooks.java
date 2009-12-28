/*
 *  @author : Slim Ouertani
 *  @mail : ouertani@gmail.com
 */
package com.jtunisie.akel.servicelogger.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import static org.osgi.framework.ServiceEvent.*;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.service.EventHook;
import org.osgi.framework.hooks.service.FindHook;

/**
 *
 * @author slim ouertani
 */
public class LoggerHooks implements FindHook, EventHook {

    private BundleContext bc;
    private static final String PROXY = "proxied";

    public LoggerHooks(BundleContext bc) {
        this.bc = bc;
    }

    @Override
    public void find(BundleContext bc, String name, String filter,
            boolean allServices, Collection references) {
        try {
            if (this.bc.equals(bc) || bc.getBundle().getBundleId() == 0) {
                return;
            }

            System.out.println(
                    " bundle : [" + bc.getBundle().getSymbolicName() + "] try to get reference  of " + name);
            Iterator iterator = references.iterator();

            while (iterator.hasNext()) {
                ServiceReference sr = (ServiceReference) iterator.next();

                System.out.println(
                        "from bundle" + sr.getBundle().getSymbolicName());

                if (sr.getProperty("proxied") == null) {
                    iterator.remove();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void event(ServiceEvent event, Collection contexts) {
        final ServiceReference serviceReference = event.getServiceReference();
        System.out.println("" + serviceReference.getBundle().getSymbolicName());

        if (serviceReference.getProperty(PROXY) == null && serviceReference.getBundle().getBundleContext() != bc) {
            Bundle bundle = serviceReference.getBundle();

            switch (event.getType()) {
                case REGISTERED: {

                    String[] propertyKeys = serviceReference.getPropertyKeys();
                    Properties properties = buildProps(propertyKeys, event);
                    String[] interfaces = (String[]) serviceReference.getProperty(
                            "objectClass");

                    Class<?>[] toClass = toClass(interfaces, bundle);
                    proxyService(bundle,
                            toClass,
                            properties,
                            this.getClass().getClassLoader(), new LoggerProxy(
                            bc, serviceReference));



                    break;
                }
                case UNREGISTERING: {
                    //TODO
                    break;
                }
                case MODIFIED:
                case MODIFIED_ENDMATCH: {
                    //TODO
                    break;
                }
            }
        }
    }

    private Properties buildProps(String[] propertyKeys, ServiceEvent event) {
        Properties properties = new Properties();
        for (String string : propertyKeys) {
            properties.put(string,
                    event.getServiceReference().getProperty(string));
        }
        return properties;
    }

    private static String[] toString(Class<?>[] interfaces) {
        String[] names = new String[interfaces.length];
        int i = 0;
        for (Class clazz : interfaces) {
            names[i++] = clazz.getName();
        }
        return names;
    }

    private static Class<?>[] toClass(String[] interfaces, Bundle bl) {
        Class<?>[] names = new Class<?>[interfaces.length];
        int i = 0;
        for (String clazz : interfaces) {
            try {
                names[i++] = bl.loadClass(clazz);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return names;
    }

    private static ServiceRegistration proxyService(Bundle bundleSource,
            Class<?>[] interfaces, Properties prop, ClassLoader cl,
            InvocationHandler proxy) {

        prop.put(PROXY, true);
        Object loggerProxy = Proxy.newProxyInstance(
                cl, interfaces,
                proxy);
        return bundleSource.getBundleContext().registerService(toString(
                interfaces),
                loggerProxy, prop);


    }
}
