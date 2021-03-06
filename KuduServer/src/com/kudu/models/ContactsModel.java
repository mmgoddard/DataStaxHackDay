package com.kudu.models;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import com.kudu.models.*;

public class ContactsModel {

private Cluster cluster;
	
	public void setCluster(Cluster cluster)
	{
		this.cluster = cluster;
	}
	
	public String[] retrieveContacts(String username)
	{
		LinkedList<String> values = new LinkedList<String>();
		Session session = cluster.connect("kududb");
		String query = "SELECT friendname FROM friends WHERE username='"+username+"';";
		
		PreparedStatement statement = session.prepare(query);
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement);
		
		if(!rs.isExhausted()) {
			for(Row row : rs) {
				values.add(row.getString("friendname")); 
			}
		}
		session.close();
		return values.toArray(new String[values.size()]);
	}
	
	public boolean addContact(String contact, String username)
	{
		boolean contactAdded = true;
		Session session = cluster.connect("kududb");
		
		String checkContact = "SELECT username FROM users WHERE username='"+contact+"';"; 
		PreparedStatement statement = session.prepare(checkContact);
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet result1 = session.execute(boundStatement);
		
		String checkFriends = "SELECT username,friendname FROM friends WHERE username='"+username+"' AND friendName='"+contact+"';"; 
		PreparedStatement statement4 = session.prepare(checkFriends);
		BoundStatement boundStatement4 = new BoundStatement(statement4);
		ResultSet result2 = session.execute(boundStatement4);
		
		
		if(!result1.isExhausted() & result2.isExhausted()) {
			String insertContact = "INSERT INTO friends (username, friendname) VALUES ('"+username+"', '"+contact+"' );"; 
			PreparedStatement statement1 = session.prepare(insertContact);
			BoundStatement boundStatement1 = new BoundStatement(statement1);
			session.execute(boundStatement1);
			
			String repeatInsert = "INSERT INTO friends (username, friendname) VALUES ('"+contact+"', '"+username+"' );";
			statement1 = session.prepare(repeatInsert);
			boundStatement1 = new BoundStatement(statement1);
			session.execute(boundStatement1);
			
			String checkAdded = "SELECT friendname FROM friends WHERE username='"+username+"';";
			PreparedStatement statement2 = session.prepare(checkAdded);
			BoundStatement boundStatement2 = new BoundStatement(statement2);
			ResultSet res = session.execute(boundStatement2);
			
			if(res.isExhausted())
				contactAdded = false;
			session.close();
		} else {
			contactAdded = false;
		}
		return contactAdded;
	}
	
	public void addConversation(String username, String friend)
	{
		UUID uuid = UUID.randomUUID();
		
		if(username != null && friend != null)
		{
		
			Session session = cluster.connect("kududb");
			String addConversation1 = "UPDATE friends SET conversation = " + uuid + " WHERE username = \'" + username + "\' and friendname =\'" +
					friend + "\';"; 
			PreparedStatement statement1 = session.prepare(addConversation1);
			BoundStatement boundStatement1 = new BoundStatement(statement1);
			session.execute(boundStatement1);

			String addConversation2 = "UPDATE friends SET conversation = " + uuid + " WHERE username = \'" + friend + "\' and friendname =\'" +
					username + "\';"; 
			PreparedStatement statement2 = session.prepare(addConversation2);
			BoundStatement boundStatement2 = new BoundStatement(statement2);
			session.execute(boundStatement2);
		}
	}
	
}
