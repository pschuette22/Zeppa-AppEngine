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
	private Key key;
	
	@Persistent
	private String indexedWord;
	
	@Persistent
	private Long wordSenseId;
	
	@Persistent
	private List<Long> synsetIdList;
	
	@Persistent
	private List<Key> entities;
	
	
	public MetaTag(String indexWord, Long wordSenseId, List<Long> synsetIdList) {
		this.indexedWord = indexWord;
		this.wordSenseId = wordSenseId;
		this.synsetIdList = synsetIdList;
		this.entities = new ArrayList<Key>();
	}

	
	public Key getKey() {
		return key;
	}
	
	public String getIndexedWord() {
		return indexedWord;
	}

	public void setIndexedWord(String indexedWord) {
		this.indexedWord = indexedWord;
	}

	public Long getWordSenseId() {
		return wordSenseId;
	}

	public void setWordSenseId(Long wordSenseId) {
		this.wordSenseId = wordSenseId;
	}

	public List<Long> getSynsetIdList() {
		return synsetIdList;
	}

	public void setSynsetIdList(List<Long> synsetIdList) {
		this.synsetIdList = synsetIdList;
	}

	public List<Key> getEntities() {
		return entities;
	}

	public void setEntities(List<Key> entities) {
		this.entities = entities;
	}
	
}
