package bg.borica.mq.camel.component;

import com.ibm.mq.jakarta.jms.MQXAConnectionFactory;
import io.quarkiverse.messaginghub.pooled.jms.PooledJmsWrapper;
import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.inject.Produces;
import jakarta.jms.ConnectionFactory;

public class IBMMQConnectionFactory {

    /*@Produces
    @UnlessBuildProperty(name = "quarkus.pooled-jms.transaction", stringValue = "xa")
    public ConnectionFactory createConnectionFactory(PooledJmsWrapper wrapper) {
        MQConnectionFactory mq = new MQConnectionFactory();
        //setupMQ(mq);
        return wrapper.wrapConnectionFactory(mq);
    }*/

    @Produces
    @IfBuildProperty(name = "quarkus.pooled-jms.transaction", stringValue = "xa")
    public ConnectionFactory createXAConnectionFactory(PooledJmsWrapper wrapper) {
        MQXAConnectionFactory mq = new MQXAConnectionFactory();
        //setupMQ(mq);
        return wrapper.wrapConnectionFactory(mq);
    }
}