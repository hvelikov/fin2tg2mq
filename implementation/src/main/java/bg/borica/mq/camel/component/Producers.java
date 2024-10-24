package bg.borica.mq.camel.component;

import com.ibm.mq.jakarta.jms.MQXAConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import io.quarkiverse.messaginghub.pooled.jms.PooledJmsWrapper;
import io.smallrye.common.annotation.Identifier;
import jakarta.inject.Singleton;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.transaction.TransactionManager;
import org.apache.camel.component.jms.JmsComponent;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.util.Optional;

public class Producers {
    /**
     * Create a connection factory for IBM MQ.
     * <p/>
     * Since there is no IBM MQ extension for quarkus, we need to create the connection factory manually
     *
     * @param  wrapper wrapper that is used to add pooling capabilities to the connection factory
     * @return         a new connection factory instance
     */
    @Identifier("ibmConnectionFactory")
    public ConnectionFactory createXAConnectionFactory(PooledJmsWrapper wrapper) {
        MQXAConnectionFactory mq = new MQXAConnectionFactory();
        setupMQ(mq);
        return wrapper.wrapConnectionFactory(mq);
    }

    private  void setupMQ(MQXAConnectionFactory cf) {
        Optional<String>  p;
        try {
            cf.setHostName(ConfigProvider.getConfig().getValue("ibm.mq.host", String.class));
            cf.setPort(ConfigProvider.getConfig().getValue("ibm.mq.port", Integer.class));
            cf.setChannel(ConfigProvider.getConfig().getValue("ibm.mq.channel", String.class));
            cf.setQueueManager(ConfigProvider.getConfig().getValue("ibm.mq.queueManagerName", String.class));

            p =  ConfigProvider.getConfig().getOptionalValue("ibm.mq.user", String.class);
            if ( p.isPresent() )
                cf.setStringProperty(WMQConstants.USERID, p.get());
            p =  ConfigProvider.getConfig().getOptionalValue("ibm.mq.password", String.class);
            if(p.isPresent())
                cf.setStringProperty(WMQConstants.PASSWORD, p.get());
            cf.setIntProperty(WMQConstants.WMQ_EOQ_TIMEOUT, 15);
            cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, ConfigProvider.getConfig().getValue("ibm.mq.appName", String.class));
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT); // mq.setTransportType(WMQConstants.WMQ_CM_CLIENT);

            Optional<String> ov = ConfigProvider.getConfig().getOptionalValue("ibm.ssl.cipher", String.class);
            if(ov.isPresent())
                    cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SUITE, ov.get());

        } catch (JMSException e) {
            throw new RuntimeException("Unable to create IBM MQ Connection Factory", e);
        }
    }

    /**
     * Define the JtaTransactionManager instance that is used in jms components.
     *
     * @param  transactionManager transaction manager
     * @return                    JtaTransactionManager instance
     */
    @Singleton
    JtaTransactionManager manager(TransactionManager transactionManager) {
        return new JtaTransactionManager(transactionManager);
    }

    /**
     * Define the "ibmmq" jms component.
     *
     * @param  cf ibm mq connection factory that is automatically injected by Quarkus based on the given identifier
     * @param  tm transaction manager to use
     * @return    a new JmsComponent instance
     */
    @Identifier("ibmmq")
    JmsComponent ibmmq(@Identifier("ibmConnectionFactory") ConnectionFactory cf, JtaTransactionManager tm) {
        JmsComponent ibmmq = new JmsComponent();
        ibmmq.setConnectionFactory(cf);
        ibmmq.setTransactionManager(tm);
        return ibmmq;
    }

    /**
     * Define the "amq" jms component.
     *
     * @param  cf activemq connection factory that is automatically injected by Quarkus based on the given identifier
     * @param  tm transaction manager to use
     * @return    a new JmsComponent instance
     */
    @Identifier("amq")
    JmsComponent amq(ConnectionFactory cf, JtaTransactionManager tm) {
        JmsComponent amq = new JmsComponent();
        amq.setConnectionFactory(cf);
        amq.setTransactionManager(tm);
        return amq;
    }
}