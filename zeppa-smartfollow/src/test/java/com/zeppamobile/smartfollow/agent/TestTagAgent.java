package com.zeppamobile.smartfollow.agent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zeppamobile.common.datamodel.EventTag;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.smartfollow.Configuration;

public class TestTagAgent {

	private UserAgent userAgent;
	private EventTag tag;
	
	@Before
	private void setup(){
		Configuration.startTesting();
		userAgent = new UserAgent(Long.valueOf(1));
		tag = new EventTag(userAgent.getUserId(), "TestingPrograms");
	}
	
	@After
	public void teardown(){
		Configuration.stopTesting();
	}
	
	@Test
	private void doTesting(){
//		TagAgent agent = new TagAgent(userAgent, tag);
	}
	
	
	
	private EventTag fetchTagById(Long tagId) throws MalformedURLException{
		EventTag result = null;
		
		
		Dictionary<String,String> params = new Hashtable<String,String>();
		params.put("id",String.valueOf(tagId));
		
		URL url = ModuleUtils.getZeppaAPIUrl("getEventTag", params);
		
		
		
		return result;
	}
	
}
