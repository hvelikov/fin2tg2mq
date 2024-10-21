package bg.borica.mq.camel.routes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.file.AntPathMatcherGenericFileFilter;
import org.apache.camel.processor.errorhandler.DefaultErrorHandler;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.sql.SQLException;

/**
 * A simple {@link RouteBuilder}.
 */
@ApplicationScoped
public class TimerRoute extends EndpointRouteBuilder { // RouteBuilder   // EndpointRouteBuilder
    @Inject
    CamelContext context;

    @ConfigProperty( name="fin.file.filter", defaultValue = "*")
    String fileFilter;

    @Override
    public void configure() throws Exception {
        from(timer("ticks").period("{{redelivery.interval}}"))
                .id("qqqq")
                .autoStartup(false)
            .onException(SQLException.class)
                .maximumRedeliveries(60)  //.maximumRedeliveryDelay(60*1000) //.backOffMultiplier(2)
                .redeliveryDelay(15 * 1000L)
                .handled(true).rollback()
                .logStackTrace(false)
                .log(LoggingLevel.ERROR, "${header.CamelExceptionCaught}")
            .end()
        .log("Test log message")
        ;
    }

    @Named("tg2filter")
    public AntPathMatcherGenericFileFilter fileFilter() {
        return  new AntPathMatcherGenericFileFilter(fileFilter);
    }
}
//getContext().getPropertiesComponent().setLocation("classpath:ftp.properties");