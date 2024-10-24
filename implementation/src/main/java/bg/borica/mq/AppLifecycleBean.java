package bg.borica.mq;

import io.quarkus.logging.Log;
import io.quarkus.runtime.Shutdown;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.PropertyInject;
import org.apache.camel.impl.event.CamelContextStartedEvent;
import org.apache.camel.impl.event.CamelContextStoppedEvent;
import org.apache.camel.util.InetAddressUtil;

@ApplicationScoped
public class AppLifecycleBean {

    void onStart(@Observes StartupEvent ev) {
        Log.info("The application fin2tgmq ver. 1.0 is starting...");
    }

    void onStop(@Observes ShutdownEvent ev) {               
        Log.info("The application is stopping...");
    }

    @Startup
    void startup() {
        System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
        Log.info("The application startup... ");
    }

    @Shutdown
    void shutdown() {
        Log.info("The application shutdown...");
    }

    void onContextStarted(@Observes CamelContextStartedEvent event) {
        System.out.println("*******************************");
        System.out.println("* Camel started " + event.getContext().getName());
        System.out.println("*******************************");
    }

    void onContextStarted(@Observes CamelContextStoppedEvent event) {
        System.out.println("******************");
        System.out.println("* Camel stopped  *");
        System.out.println("******************");
    }
}