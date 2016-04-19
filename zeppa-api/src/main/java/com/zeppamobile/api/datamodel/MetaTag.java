package com.zeppamobile.api.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class MetaTag {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String indexedWordId;
	
	@Persistent
	private List<String> synonymIndexWordIds;
	
	@Persistent
	private List<Key> entities;
	
	
	public MetaTag(String indexedWordId, List<String> synonymIndexWordIds) {
		this.indexedWordId = indexedWordId;
		this.synonymIndexWordIds = synonymIndexWordIds;
		this.entities = new ArrayList<Key>();
	}

	public String getIndexedWordId() {
		return indexedWordId;
	}

	public List<String> getSynonymIndexWordIds() {
		return synonymIndexWordIds;
	}

	public List<Key> getEntities() {
		return entities;
	}

	public void setEntities(List<Key> entities) {
		this.entities = entities;
	}
	
}
