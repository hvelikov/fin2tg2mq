package bg.borica.mq.camel.routes;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.camel.builder.RouteBuilder;

/**
 * A simple {@link RouteBuilder}.
 */
@ApplicationScoped
public class TimerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:foo?period={{timer.period}}")
                .transacted()
                .routeId("OutRoute")
                .setBody().constant("Test data")
                .to("ibmmq:queue:{{ibm.mq.send.QueueName}}")
                .to("log:TimerRoute")
                .id("ibmmq-amq");

        from("ibmmq:queue:{{ibm.mq.send.QueueName}}")
                .transacted()
                .to("log:InRoute");
    }
}