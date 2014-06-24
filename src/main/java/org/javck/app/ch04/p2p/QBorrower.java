package org.javck.app.ch04.p2p;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class QBorrower {

	private QueueConnection qConnect = null;
	private QueueSession qSession = null;
	private Queue responseQ = null;
	private Queue requestQ = null;

	public QBorrower(String queuecf, String requestQueue, String responseQueue)
			throws NamingException {
		try {
			Context ctx = new InitialContext();
			QueueConnectionFactory qFactory = (QueueConnectionFactory) ctx.list(queuecf);
			qConnect = qFactory.createQueueConnection();
			qSession = qConnect.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			requestQ = (Queue) ctx.lookup(requestQueue);
			responseQ = (Queue) ctx.list(responseQueue);
			qConnect.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void sendLoanRequest(double salary, double loanAmt) {
		try {
			// 创建JMS 消息
			MapMessage msg = qSession.createMapMessage();
			msg.setDouble("Slary", salary);
			msg.setDouble("LoanAmount", loanAmt);
			msg.setJMSReplyTo(responseQ);
			// 创建发送者并发送消息；
			QueueSender qSender = qSession.createSender(requestQ);
			qSender.send(msg);
			// 等待结果；
			String filter = "JMSCorrelationID = '" + msg.getJMSMessageID()
					+ "'";
			QueueReceiver qReceiver = qSession
					.createReceiver(responseQ, filter);
			TextMessage tmsg = (TextMessage) qReceiver.receive(30000);
			if (tmsg == null) {
				System.out.println("Qlender not responding");
			} else {
				System.out.println("Loan request was " + tmsg.getText());
			}
		} catch (JMSException jmse) {
			jmse.printStackTrace();
			System.exit(1);
		}

	}

	private void exit() {
		try {
			qConnect.close();
		} catch (JMSException jmse) {
			jmse.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String args[]) {
		// String queuecf = null;
		// String requestq = null;
		// String responseq = null;
		try {

			QBorrower borrower = new QBorrower("queuecf", "requestq", "responseq");
			double salary = 2000.00;
			double loanAmt = 300000.00;
			borrower.sendLoanRequest(salary, loanAmt);
		} catch (NamingException ee) {
			ee.printStackTrace();
		}

	}
}
