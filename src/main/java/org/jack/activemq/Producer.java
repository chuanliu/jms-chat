package org.jack.activemq;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * @author Jack
 *
 */
public class Producer {
	public static void main(String[] args) {
		String user = ActiveMQConnection.DEFAULT_USER;
		String password = ActiveMQConnection.DEFAULT_PASSWORD;
		String url = ActiveMQConnection.DEFAULT_BROKER_URL;
		String subject = "TOOL.DEFAULT";
		ConnectionFactory contectionFactory = new ActiveMQConnectionFactory(
				user, password, url);
		try {
			Connection connection = contectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(Boolean.TRUE,
					Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(subject);
			MessageProducer producer = session.createProducer(destination);
			for (int i = 0; i <= 20; i++) {
				MapMessage message = session.createMapMessage();
				message.setLong("count", new Date().getTime());
				producer.send(message);
				System.out.println("--Produce message " + new Date());
				Thread.sleep(1000);
			}
			session.commit();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
