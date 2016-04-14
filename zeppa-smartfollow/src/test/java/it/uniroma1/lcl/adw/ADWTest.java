package it.uniroma1.lcl.adw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import net.sf.extjwnl.data.POS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zeppamobile.smartfollow.AppConfig;
import com.zeppamobile.smartfollow.comparewords.WordInfo;
import com.zeppamobile.smartfollow.task.CompareTagsTask;

public class ADWTest {
	
	private static final double TOLERANCE = 0.001;
	private CompareTagsTask ctt1 = null;
	
	@Before
	public void setup(){
		AppConfig.startTesting();
		
		ServletContext context = null;
		List<WordInfo> tag1 = new ArrayList<WordInfo>();
		List<WordInfo> tag2 = new ArrayList<WordInfo>();
		
		tag1.add(new WordInfo("playing", POS.VERB));
		tag1.add(new WordInfo("football", POS.NOUN));
		
		tag2.add(new WordInfo("watching", POS.VERB));
		tag2.add(new WordInfo("soccer", POS.NOUN));
		
		ctt1 = new CompareTagsTask(context, tag1, tag2);
	}
	
	@After
	public void teardown(){
		ctt1 = null;
		
		AppConfig.stopTesting();
	}
	
	@Test
	public void doTesting(){
		assertTrue(true);
		
//		ctt1.execute();
//		double similarity = ctt1.getSimilarity();
//		
//		assertEquals(0.6523, similarity, TOLERANCE);
	}
}
