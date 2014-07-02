package org.jack.app.ch05.pubsub;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TLender {
	private TopicConnection tConnect = null;
	private TopicSession tSession = null;
	private Topic topic = null;
	
	public TLender(String topiccf, String topicName){
		try{
			Context ctx = new InitialContext();
			TopicConnectionFactory qFactory = (TopicConnectionFactory) ctx.lookup(topiccf);
			tConnect = qFactory.createTopicConnection();
			tSession = tConnect.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			topic = tSession.createTopic(topicName);
			tConnect.start();
		}catch(JMSException ex){
			ex.printStackTrace();
			System.exit(1);
		}catch (NamingException ex){
			ex.printStackTrace();
			System.exit(1);
		}
		
	}
	
	private void publishRate(double newRate){
		try{
			BytesMessage msg =tSession.createBytesMessage();
			msg.writeDouble(newRate);
			TopicPublisher publisher = tSession.createPublisher(topic);
			publisher.publish(msg);
		}catch(JMSException jmsex){
			jmsex.printStackTrace();
			System.exit(1);
		}
	}
	private void exit(){
		try{
			tConnect.close();
		}catch(JMSException jmsex){
			jmsex.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void main(String args[]){
		
		TLender lender = new TLender ("TopicCF", "RateTopic");
		lender.publishRate(9);
	}

}
