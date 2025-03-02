/*********************************************************************
* Copyright (c) 2021 Kentyou and others
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.sensinact.gateway.tools.connector.mongodb;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

/**
 * A MongoDbDatabase provide CRUD methods to {@link MongoDatabase}
 */
public class MongoDbDatabase {

	private static final Logger LOG = LoggerFactory.getLogger(MongoDbDatabase.class);

	private MongoDatabase base;

	/**
	 * Constructor
	 * 
	 * @param base the {@link MongoDatabase} wrapped by the MongoDbDatabase to be instantiated
	 */
	public MongoDbDatabase(MongoDatabase base) {
		this.base = base;
	}

	//build a BasicDBObject to be used to query the MongoDB database
	private BasicDBObject createBasisDBObject(String key, Object value) {
		BasicDBObject obj = new BasicDBObject();
		obj.put(key, value);
		return obj;
	}

	//build a BasicDBObject to be used to query the MongoDB database
	private BasicDBObject createBasisDBObject(Dictionary<String,Object> props) {
		BasicDBObject obj = new BasicDBObject();
		for(Enumeration<String> keys = props.keys();keys.hasMoreElements();) {
			String key = keys.nextElement();
			obj.put(key, props.get(key));
		}
		return obj;
	}

	//build a Document to be used to feed or update the MongoDB database
	private Document createDocument(Dictionary<String,Object> props) {
		Document obj = new Document();
		for(Enumeration<String> keys = props.keys();keys.hasMoreElements();) {
			String key = keys.nextElement();
			obj.put(key, props.get(key));
		}
		return obj;
	}

	//Returns the List of Documents compliant with the specified tagged value
	private List<Document> getAll(MongoCollection<Document> collection, String identifierName, String identifier){
		List<Document> list = new ArrayList<>();
		BasicDBObject searchQuery = createBasisDBObject(identifierName,identifier);
		FindIterable<Document> iterable = collection.find(searchQuery);		
		MongoCursor<Document> cursor = iterable.cursor();
		try {
			cursor.forEachRemaining(t -> list.add(t));
		} finally {
			cursor.close();
		}
		return list;
	}

	//Returns the List of Documents compliant with the specified tagged values
	private List<Document> getAll(MongoCollection<Document> collection, Dictionary<String,Object> identifiers){
		List<Document> list = new ArrayList<>();
		BasicDBObject searchQuery = createBasisDBObject(identifiers);
		FindIterable<Document> iterable = collection.find(searchQuery);		
		MongoCursor<Document> cursor = iterable.cursor();
		try {
			cursor.forEachRemaining(t -> list.add(t));
		} finally {
			cursor.close();
		}
		return list;
	}

	//Returns the Document that complies to the specified tagged value
	private Document get(MongoCollection<Document> collection, String identifierName, String identifier) {
		Document result = null;
		BasicDBObject searchQuery = createBasisDBObject(identifierName,identifier);
		FindIterable<Document> iterable = collection.find(searchQuery);
		System.out.println("SEARCH QUERY :" + searchQuery.toJson());
		MongoCursor<Document> cursor = iterable.cursor();
		try {
			while(cursor.hasNext()) {
				result = cursor.next();
				break;
			}
		} finally {
			cursor.close();
		}
		return result;
	}                                                                     

	//Returns the Document that complies to the specified tagged values
	private Document get(MongoCollection<Document> collection, Dictionary<String,Object> identifiers) {
		Document result = null;
		BasicDBObject searchQuery = createBasisDBObject(identifiers);
		FindIterable<Document> iterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = iterable.cursor();
		try {
			while(cursor.hasNext()) {
				result = cursor.next();
				break;
			}
		} finally {
			cursor.close();
		}
		return result;
	}             

	//Returns the <T> typed Object value that complies to the specified tagged value
	private <T> T get(MongoCollection<Document> collection, String identifierName, String identifier, Class<T> resultType) {
		T result = null;
		BasicDBObject searchQuery = createBasisDBObject(identifierName,identifier);
		FindIterable<T> iterable = collection.find(searchQuery,resultType);
		MongoCursor<T> cursor = iterable.cursor();
		try {
			while(cursor.hasNext()) {
				result = cursor.next();
				break;
			}
		}finally {
			cursor.close();
		}
		return result;
	}

	//Returns the <T> typed Object value that complies to the specified tagged values
	private <T> T get(MongoCollection<Document> collection, Dictionary<String,Object> identifiers, Class<T> resultType) {
		T result = null;
		BasicDBObject searchQuery = createBasisDBObject(identifiers);
		FindIterable<T> iterable = collection.find(searchQuery,resultType);
		MongoCursor<T> cursor = iterable.cursor();
		try {
			while(cursor.hasNext()) {
				result = cursor.next();
				break;
			}
		}finally {
			cursor.close();
		}
		return result;
	}

	//Returns the List <T> typed Object values that complies to the specified tagged value
	private <T> List<T> getAll(MongoCollection<Document> collection, String identifierName, String identifier, Class<T> resultType){
		List<T> list = new ArrayList<>();
		BasicDBObject searchQuery = createBasisDBObject(identifierName,identifier);
		FindIterable<T> iterable = collection.find(searchQuery,resultType);		
		MongoCursor<T> cursor = iterable.cursor();
		try {
			cursor.forEachRemaining(t -> list.add(t));
		} finally {
			cursor.close();
		}
		return list;
	}

	//Returns the List <T> typed Object values that complies to the specified tagged values
	private <T> List<T> getAll(MongoCollection<Document> collection, Dictionary<String,Object> identifiers, Class<T> resultType){
		List<T> list = new ArrayList<>();
		BasicDBObject searchQuery = createBasisDBObject(identifiers);
		FindIterable<T> iterable = collection.find(searchQuery,resultType);		
		MongoCursor<T> cursor = iterable.cursor();
		try {
			cursor.forEachRemaining(t -> list.add(t));
		} finally {
			cursor.close();
		}
		return list;
	}
	
	/**
	 * Operates the aggregation command String passed as parameter on the {@link MongoCollection} 
	 * whose name is also passed as parameter
	 * 
	 * @param collection the {@link MongoCollection} name
	 * @param aggregation the String aggregation command
	 * 
	 * @return the {@link Iterable} of {@link Document}s resulting from the aggregation command
	 */
	public Iterable<Document> aggregate(String collection, String...aggregations){
		return aggregate(this.base.getCollection(collection), aggregations);
	}
	
	//
	private Iterable<Document> aggregate(MongoCollection<Document> collection, String...aggregations){
		List<Document> pipeline = new ArrayList<>();
		for(String aggregation : aggregations) {
			Document doc = Document.parse(aggregation);
			pipeline.add(doc);
		}
		AggregateIterable<Document> iterable = collection.aggregate(pipeline);
		return iterable;
	}
	
	/**
	 * Returns the names List of existing Collections in the current database
	 * 
	 * @return the List of names of existing Collections 
	 */
	public List<String> getCollectionNames() {
		List<String> collectionNames = new ArrayList<>();
		for(MongoCursor<String> cursor = this.base.listCollectionNames().iterator();cursor.hasNext();) 
			collectionNames.add(cursor.next());
		return collectionNames;
	}
	
	/**
	 * Adds a collection whose name is the one passed as parameter if it does not already exist
	 *   
	 * @param collectionName the name of the collection
	 * 
	 * @return true if the collection with the specified name is created ; false if it already exists
	 */
	public boolean addCollection(String collectionName) {
		boolean found = false;
		for(MongoCursor<String> cursor = this.base.listCollectionNames().iterator();cursor.hasNext();) {
			if(cursor.next().equals(collectionName)) {
				found = true;
				break;
			}
		}
		if(found)
			return false;
		this.base.createCollection(collectionName);
		return true;
	}
		
	public List<Document> getAll(String collectionName, String identifierName, String identifier) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		return getAll(collection, identifierName, identifier);
	}
	
	public List<Document> getAll(String collectionName,  Dictionary<String,Object> identifiers) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		return getAll(collection, identifiers);
	}                                                
	
	public Document get(String collectionName, String identifierName, String identifier) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);		
		return get(collection, identifierName, identifier) ;
	}	

	public Document get(String collectionName, Dictionary<String,Object> identifiers) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);		
		return get(collection, identifiers) ;
	}	

	public <T> List<T>  getAll(String collectionName, String identifierName, String identifier, Class<T> resultType) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		return getAll(collection, identifierName, identifier, resultType);
	}
	
	public <T> List<T>  getAll(String collectionName, Dictionary<String,Object> identifiers, Class<T> resultType) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		return getAll(collection, identifiers, resultType);
	}
	
	public <T> T get(String collectionName, String identifierName, String identifier, Class<T> resultType) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);		
		return get(collection, identifierName, identifier, resultType) ;
	}
	
	public <T> T get(String collectionName, Dictionary<String,Object> identifiers, Class<T> resultType) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);		
		return get(collection, identifiers, resultType) ;
	}
	
	public void drop(String collectionName) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		collection.drop();
	}
	
	/**
	 * Deletes all documents from the collection whose name is passed as parameter, compliant
	 * with the key-value pair field identifier also passed as parameter
	 * 
	 * @param collectionName the name of the targeted {@link MongoCollection}
	 * @param identifierName the String name of the identifier field 
	 * @param identifier the String value of the identifier field  
	 * 
	 * @return the number of deleted entries from the specified {@link MongoCollection}
	 */ 
	public long deleteAll(String collectionName, String identifierName, String identifier) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		BasicDBObject searchQuery = createBasisDBObject(identifierName,identifier);
		DeleteResult r = collection.deleteMany(searchQuery);
		return r.getDeletedCount();
	}

	
	/**
	 * Deletes all documents from the collection whose name is passed as parameter, compliant
	 * with the set of key-value pair field identifiers also passed as parameter
	 * 
	 * @param collectionName the name of the targeted {@link MongoCollection}
	 * @param identifiers the set of field identifiers 
	 * 
	 * @return the number of deleted entries from the specified {@link MongoCollection}
	 */
	public long deleteAll(String collectionName, Dictionary<String,Object> identifiers) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		BasicDBObject searchQuery = createBasisDBObject(identifiers);
		DeleteResult r = collection.deleteMany(searchQuery);
		return r.getDeletedCount();
	}

	/**
	 * Deletes an document from the collection whose name is passed as parameter, compliant
	 * with the key-value pair field identifier also passed as parameter
	 * 
	 * @param collectionName the name of the targeted {@link MongoCollection}
	 * @param identifierName the String name of the identifier field 
	 * @param identifier the String value of the identifier field  
	 * 
	 * @return the number of deleted entries from the specified {@link MongoCollection}
	 */
	public long delete(String collectionName, String identifierName, String identifier) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		BasicDBObject searchQuery = createBasisDBObject(identifierName,identifier);
		DeleteResult r = collection.deleteOne(searchQuery);
		return r.getDeletedCount();
	} 

	/**
	 * Deletes an document from the collection whose name is passed as parameter, compliant
	 * with the set of key-value pair field identifiers also passed as parameter
	 * 
	 * @param collectionName the name of the targeted {@link MongoCollection}
	 * @param identifiers the set of field identifiers 
	 * 
	 * @return the number of deleted entries from the specified {@link MongoCollection}
	 */
	public long delete(String collectionName, Dictionary<String,Object> identifiers) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		BasicDBObject searchQuery = createBasisDBObject(identifiers);
		DeleteResult r = collection.deleteOne(searchQuery);
		return r.getDeletedCount();
	} 

	public List<Document> removeAll(String collectionName, String identifierName, String identifier) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		List<Document> list = getAll(collection,identifierName,identifier);		
		Long l = deleteAll(collectionName, identifierName, identifier);
		if(l.intValue() != list.size())
			LOG.warn("DeleteResult's deleted count is not equal to the returned List's size");
		return list;
	}

	public List<Document> removeAll(String collectionName,Dictionary<String,Object> identifiers) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		List<Document> list = getAll(collection,identifiers);		
		Long l = deleteAll(collectionName, identifiers);
		if(l.intValue() != list.size())
			LOG.warn("DeleteResult's deleted count is not equal to the returned List's size");
		return list;
	}

	public Document remove(String collectionName, String identifierName, String identifier) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		Document result = this.get(collection, identifierName, identifier);		
		Long l = delete(collectionName, identifierName, identifier);
		if(l.intValue() != 1)
			LOG.warn("DeleteResult's deleted count is not equal to one");
		return result;
	}

	public Document remove(String collectionName, Dictionary<String,Object> identifiers) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		Document result = this.get(collection, identifiers);		
		Long l = delete(collectionName, identifiers);
		if(l.intValue() != 1)
			LOG.warn("DeleteResult's deleted count is not equal to one");
		return result;
	}
	
	public <T> List<T> removeAll(String collectionName, String identifierName, String identifier, Class<T> resultType) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		List<T> list = getAll(collection,identifierName,identifier,resultType);		
		Long l = deleteAll(collectionName, identifierName, identifier);
		if(l.intValue() != list.size())
			LOG.warn("DeleteResult's deleted count is not equal to the returned List's size");
		return list;
	}

	public <T> List<T> removeAll(String collectionName, Dictionary<String,Object> identifiers, Class<T> resultType) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		List<T> list = getAll(collection,identifiers,resultType);		
		Long l = deleteAll(collectionName, identifiers);
		if(l.intValue() != list.size())
			LOG.warn("DeleteResult's deleted count is not equal to the returned List's size");
		return list;
	}
	
	public <T> T remove(String collectionName, String identifierName, String identifier, Class<T> resultType) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		T result = this.get(collection, identifierName, identifier, resultType);		
		Long l = delete(collectionName, identifierName, identifier);
		if(l.intValue() != 1)
			LOG.warn("DeleteResult's deleted count is not equal to one");
		return result;
	}

	public <T> T remove(String collectionName, Dictionary<String,Object> identifiers, Class<T> resultType) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		T result = this.get(collection, identifiers, resultType);		
		Long l = delete(collectionName, identifiers);
		if(l.intValue() != 1)
			LOG.warn("DeleteResult's deleted count is not equal to one");
		return result;
	}
	
	public Document update(String collectionName, String identifierName, String identifier, Dictionary<String,Object> updates) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		BasicDBObject query = createBasisDBObject(identifierName,identifier);
		BasicDBObject newDocument = createBasisDBObject(updates);
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$set", newDocument);
		collection.updateOne(query, updateObject);
		return this.get(collection, identifierName, identifier);		
	}

	public Document update(String collectionName,  Dictionary<String,Object> identifiers, Dictionary<String,Object> updates) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		BasicDBObject query = createBasisDBObject(identifiers);
		BasicDBObject newDocument = createBasisDBObject(updates);
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$set", newDocument);
		collection.updateOne(query, updateObject);
		return this.get(collection, identifiers);		
	}
	
	public <T> T update(String collectionName, String identifierName, String identifier, Class<T> resultType, Dictionary<String,Object> updates) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);		
		BasicDBObject query = createBasisDBObject(identifierName,identifier);
		BasicDBObject newDocument = createBasisDBObject(updates);
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$set", newDocument);
		collection.updateOne(query, updateObject);
		return this.get(collection, identifierName, identifier, resultType);	
	}

	public <T> T update(String collectionName, Dictionary<String,Object> identifiers, Class<T> resultType, Dictionary<String,Object> updates) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);		
		BasicDBObject query = createBasisDBObject(identifiers);
		BasicDBObject newDocument = createBasisDBObject(updates);
		BasicDBObject updateObject = new BasicDBObject();
		updateObject.put("$set", newDocument);
		collection.updateOne(query, updateObject);
		return this.get(collection, identifiers, resultType);	
	}
	
	public void add(String collectionName,  Dictionary<String,Object> props) {
		try {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);		
		Document newDocument = createDocument(props);
		collection.insertOne(newDocument);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public String createIndex(String collectionName, String index) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		String json = String.format("{\"%s\":1}", index);
		BsonDocument bson = BsonDocument.parse(json);
		return collection.createIndex(bson);
	}

	public String createIndexes(String collectionName, List<String> indexes) {
		try {
			List<String> names = new ArrayList<>();
			for(String index : indexes)
				names.add(createIndex(collectionName, index));
			System.out.println("NAMES : "+ names);
			return names.toString();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String createCompoundIndex(String collectionName, List<String> indexes) {
		MongoCollection<Document> collection = this.base.getCollection(collectionName);
		StringBuilder builder = new StringBuilder();
		for(String index : indexes) {
			builder.append(String.format("\"%s\":1", index));
			builder.append(",");
		}
		builder.deleteCharAt(builder.length()-1);
		builder.insert(0, '{');
		builder.append('}');
		BsonDocument bson = BsonDocument.parse(builder.toString());
		return collection.createIndex(bson);
	}
}
