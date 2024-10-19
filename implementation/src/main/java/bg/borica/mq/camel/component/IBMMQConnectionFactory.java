package bg.borica.mq.camel.component;

import com.ibm.mq.jakarta.jms.MQConnectionFactory;
import com.ibm.mq.jakarta.jms.MQXAConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import io.quarkiverse.messaginghub.pooled.jms.PooledJmsWrapper;
import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.arc.properties.UnlessBuildProperty;
import io.smallrye.common.annotation.Identifier;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.jms.ConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.eclipse.microprofile.config.ConfigProvider;
import org.springframework.transaction.jta.JtaTransactionManager;

public class IBMMQConnectionFactory {

    @Produces
    @UnlessBuildProperty(name = "quarkus.pooled-jms.transaction", stringValue = "xa")
    public ConnectionFactory createConnectionFactory(PooledJmsWrapper wrapper) {
        MQConnectionFactory mq = new MQConnectionFactory();
        //setupMQ(mq);
        return wrapper.wrapConnectionFactory(mq);
    }

    @Produces
    @IfBuildProperty(name = "quarkus.pooled-jms.transaction", stringValue = "xa")
    public ConnectionFactory createXAConnectionFactory(PooledJmsWrapper wrapper) {
        MQXAConnectionFactory mq = new MQXAConnectionFactory();
        //setupMQ(mq);
        return wrapper.wrapConnectionFactory(mq);
    }

 /*   private void setupMQ(MQConnectionFactory mq) {
        try {
            mq.setHostName(ConfigProvider.getConfig().getValue("ibm.mq.host", String.class));
            mq.setPort(ConfigProvider.getConfig().getValue("ibm.mq.port", Integer.class));
            mq.setChannel(ConfigProvider.getConfig().getValue("ibm.mq.channel", String.class));
            mq.setQueueManager(ConfigProvider.getConfig().getValue("ibm.mq.queueManagerName", String.class));
            mq.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            mq.setStringProperty(WMQConstants.USERID,
                    ConfigProvider.getConfig().getValue("ibm.mq.user", String.class));
            mq.setStringProperty(WMQConstants.PASSWORD,
                    ConfigProvider.getConfig().getValue("ibm.mq.password", String.class));
        } catch (Exception e) {
            throw new RuntimeException("Unable to create new IBM MQ connection factory", e);
        }

    }*/
}