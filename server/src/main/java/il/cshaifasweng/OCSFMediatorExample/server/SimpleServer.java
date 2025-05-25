package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Catalog;
import il.cshaifasweng.OCSFMediatorExample.entities.Flower;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static Catalog catalog = new Catalog();

	public SimpleServer(int port) {
		super(port);
		//init catalog
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();

		if(msgString.startsWith("add client")){
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				client.sendToClient("client added successfully");
				client.sendToClient(catalog);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if(msgString.startsWith("remove client")){
			if(!SubscribersList.isEmpty()){
				for(SubscribedClient subscribedClient: SubscribersList){
					if(subscribedClient.getClient().equals(client)){
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		}
		else if(msgString.contains("price")) {
			int id = eval(msgString, msgString.indexOf("ID:") + 3);
			int price = eval(msgString, msgString.indexOf("price:") + 6);

			org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession();
			org.hibernate.Transaction tx = null;

			try {
				tx = session.beginTransaction();

				Flower flower = session.get(Flower.class, id);
				if (flower != null) {
					flower.setPrice(price);
					// session.update(flower); // optional
					tx.commit();
					client.sendToClient("Price updated successfully for flower ID: " + id);
				} else {
					client.sendToClient("Flower with ID " + id + " not found.");
				}
			} catch (Exception e) {
				if (tx != null) tx.rollback();
				e.printStackTrace();
				try {
					client.sendToClient("Error updating price.");
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			} finally {
				session.close();
			}
		}
	}
	public int eval(String input,int index){
		int num=0;
		while(input.charAt(index) >= '0' && input.charAt(index) <= '9'){
			num =  num*10 + input.charAt(index) - '0';
			index++;
		}
		return num;
	}
	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
