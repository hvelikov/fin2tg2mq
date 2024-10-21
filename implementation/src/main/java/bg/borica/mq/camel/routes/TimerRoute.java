package bg.borica.mq.camel.routes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.AntPathMatcherGenericFileFilter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TimerRoute extends RouteBuilder { // RouteBuilder   // EndpointRouteBuilder
    @Inject
    CamelContext context;

    @ConfigProperty( name="fin.file.filter", defaultValue = "*")
    String fileFilter;

    @Override
    public void configure() throws Exception {
        from("{{fin.basedir}}{{fin.request.subdir}}")
            .id("route").autoStartup(false)
//            .onException(java.lang.RuntimeException.class)
//                .maximumRedeliveries(5000)  //.maximumRedeliveryDelay(60*1000) //.backOffMultiplier(2)
//                .redeliveryDelay("{{redelivery.interval}}")
//                .handled(true).rollback()
//                .logStackTrace(false)
//                .log(LoggingLevel.ERROR, "${header.CamelExceptionCaught}")
//            .end()
            .transacted()
            .log(" ** Begin to send input file: ${header.CamelFileName} ")
            .to("ibmmq:queue:{{ibm.mq.send.QueueName}}")
            .log("Completed file transfer to MQ: ${header.CamelFileName} --> {{ibm.mq.send.QueueName}} queue.")
        ;
    }

    @Named("tg2filter")
    public AntPathMatcherGenericFileFilter fileFilter() {
        return  new AntPathMatcherGenericFileFilter(fileFilter);
    }
}
//getContext().getPropertiesComponent().setLocation("classpath:ftp.properties");