package org.jack.app.ch05.pubsub;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TBorrower implements MessageListener{
	private TopicConnection tConnect = null;
	private TopicSession tSession = null;
	private Topic topic =null;
	private double currentRate;
	
	public TBorrower(String topiccf, String topicName, String rate){
		try {
			currentRate = Double.valueOf(rate);
			Context ctx = new InitialContext();
			TopicConnectionFactory qFacory = (TopicConnectionFactory) ctx.lookup(topiccf);
			tConnect = qFacory.createTopicConnection();
			tSession = tConnect.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			topic = tSession.createTopic(topicName);
			TopicSubscriber subscriber = tSession.createSubscriber(topic);
			subscriber.setMessageListener(this);
			tConnect.start();
		}catch(JMSException jmsex){
			jmsex.printStackTrace();
			System.exit(1);
		}catch (NamingException nex){
			nex.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void onMessage(Message message) {
		
		try{
			BytesMessage msg = (BytesMessage) message;
			double newRate = msg.readDouble();
			if (currentRate - newRate>=1.0){
				System.out.println("New rate = " + newRate + " - Consider refinancing loan");
			}else {
				System.out.println("New rate = " + newRate + " - keep existion loan");
			}
			System.out.println("\nWaiting for rate updates...");
		}catch(JMSException jmse){
			jmse.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String arg[]){
		TBorrower tborrower = new  TBorrower("TopicCF", "RateTopic", "11.1");
		System.out.println("tborrower is started");
	}

}
