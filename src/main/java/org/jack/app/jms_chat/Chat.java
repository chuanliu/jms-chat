package org.jack.app.jms_chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;

public class Chat implements MessageListener {
	private TopicSession pubSession;
	private TopicPublisher publisher;
	private TopicConnection connection;
	private String username;

	public Chat(String topicFactiory, String topicName, String username)
			throws Exception {
		InitialContext ctx = new InitialContext();
		TopicConnectionFactory conFactory = (TopicConnectionFactory) ctx
				.lookup(topicFactiory);
		TopicConnection connection = conFactory.createTopicConnection();
		TopicSession pubSession = connection.createTopicSession(false,
				Session.AUTO_ACKNOWLEDGE);
		TopicSession subSession = connection.createTopicSession(false,
				Session.AUTO_ACKNOWLEDGE);

		Topic chatTopic = (Topic) ctx.lookup(topicName);

		TopicPublisher publisher = pubSession.createPublisher(chatTopic);
		TopicSubscriber subscriber = subSession.createSubscriber(chatTopic,
				null, true);
		subscriber.setMessageListener(this);
		this.connection = connection;
		this.pubSession = pubSession;
		this.publisher = publisher;
		this.username = username;
		connection.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			TextMessage txtMessage = (TextMessage) message;
			System.out.println(txtMessage.getText());
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}

	}

	protected void writeMessage(String text) throws JMSException {
		TextMessage message = pubSession.createTextMessage();
		message.setText(username + ": " + text);
		publisher.publish(message);
	}

	public void close() throws JMSException {
		connection.close();
	}

	public static void main(String args[]) {

		try {
			Chat chat = new Chat("TopicCF", "topic1", "Jack");
			chat.writeMessage("Hello");
			chat.writeMessage("How are you!");
//			BufferedReader commandLine = new BufferedReader(
//					new InputStreamReader(System.in));
//			while (true) {
//				String s = commandLine.readLine();
//				if (s.equals("exit")) {
//					chat.close();
//					System.exit(0);
//				} else {
//					chat.writeMessage(s);
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
