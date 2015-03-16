Introduction :
In my current post, I will present an elegant solution to log all services invocation in Osgi environment.
After developing and using some bundles we need to know for different reasons witch services are used by such bundle, when invocation are occurred , what parameters are passed to services? should I log all service invocation ? It's impossible because, first I need to change all source code of my bundles and other bundles are proprietary and I haven't there source code .

Thanks to service hooks introduced in 4.2 Osgi release, we can do it in easy way.

Service hooks :

Service hooks are the second face of Osgi :)  As we knows how to get and listen services,  hooks provides a way to know whose bundles are waiting or tacking services.

The goal of this solution is to present shortly FindHook and  EventHook services use case to log all service invocations.


Solution description :

When service is registered, we create another copy (delegation) using dynamic proxy  and register it using source bundle class loader and adding proxied property.

When some bundle need to use this service we hide the first one and effectively he get the proxied service.