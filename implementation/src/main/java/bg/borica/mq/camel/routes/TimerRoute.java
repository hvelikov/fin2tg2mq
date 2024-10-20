package bg.borica.mq.camel.routes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.AntPathMatcherGenericFileFilter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * A simple {@link RouteBuilder}.
 */
@ApplicationScoped
public class TimerRoute extends RouteBuilder {  // RouteConfigurationBuilder
    @Inject
    CamelContext context;

    @ConfigProperty( name="fin.file.filter", defaultValue = "*")
    String fileFilter;

    @Override
    public void configure() throws Exception {
        from("timer:foo?period={{timer.period}}")
                .autoStartup(false)
                .id("ibmmq-amq")
                .transacted()
               // .routeId("OutRoute")
                .setBody().constant("Test data 123")
                .to("ibmmq:queue:{{ibm.mq.send.QueueName}}")
                .to("log:TimerRoute")
                ;

       from("ibmmq:queue:{{ibm.mq.send.QueueName}}")
               .autoStartup(false)
                .transacted()
                .to("log:InRoute");
    }

    @Named("tg2filter")
    public AntPathMatcherGenericFileFilter fileFilter() {
        return  new AntPathMatcherGenericFileFilter(fileFilter);
    }
}
//getContext().getPropertiesComponent().setLocation("classpath:ftp.properties");