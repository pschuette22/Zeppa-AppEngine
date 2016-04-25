package it.uniroma1.lcl.adw;

import static org.junit.Assert.assertEquals;

import it.uniroma1.lcl.jlt.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import net.sf.extjwnl.data.POS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zeppamobile.smartfollow.AppConfig;
import com.zeppamobile.smartfollow.StorageUtils;
import com.zeppamobile.smartfollow.comparewords.WordInfo;
import com.zeppamobile.smartfollow.task.CompareTagsTask;

public class ADWTest {
	
	private static final double TOLERANCE = 0.001;
	private CompareTagsTask ctt1 = null, ctt2 = null;
	private List<WordInfo> tag1, tag2, tag3, tag4;
	
	@Before
	public void setup(){
		AppConfig.startTesting();
		
		// set up directories for testing
		Configuration.getInstance().setConfigurationFile(new File("src/main/webapp/WEB-INF/config/jlt.properties"));
		ADWConfiguration.setConfigDir("src/main/webapp/WEB-INF/config/");
		StorageUtils.setCredentialsPath("src/main/webapp/WEB-INF/config/serviceAccountCredentials.json");
		
		ServletContext context = null;
		tag1 = new ArrayList<WordInfo>();
		tag2 = new ArrayList<WordInfo>();
		
		tag1.add(new WordInfo("playing", POS.VERB));
		tag1.add(new WordInfo("football", POS.NOUN));
		
		tag2.add(new WordInfo("watching", POS.VERB));
		tag2.add(new WordInfo("soccer", POS.NOUN));
		
		ctt1 = new CompareTagsTask(context, tag1, tag2);
		
		tag3 = new ArrayList<WordInfo>();
		tag4 = new ArrayList<WordInfo>();
		
		tag3.add(new WordInfo("noses", POS.NOUN));
		tag3.add(new WordInfo("that", POS.ADJECTIVE));
		tag3.add(new WordInfo("run", POS.VERB));
		
		tag4.add(new WordInfo("feet", POS.NOUN));
		tag4.add(new WordInfo("that", POS.ADJECTIVE));
		tag4.add(new WordInfo("smell", POS.VERB));
		
		ctt2 = new CompareTagsTask(context, tag3, tag4);
	}
	
	@After
	public void teardown(){
		ctt1 = null;
		ctt2 = null;
		tag1.clear();
		tag2.clear();
		tag3.clear();
		tag4.clear();
		
		AppConfig.stopTesting();
	}
	
	@Test
	public void doTesting(){
		System.out.println("CWD: " + System.getProperty("user.dir"));
		
		ctt1.execute();
		double similarity1 = ctt1.getSimilarity();
		ctt2.execute();
		double similarity2 = ctt2.getSimilarity();
		
		assertEquals(0.6523, similarity1, TOLERANCE);
		assertEquals(0.2885, similarity2, TOLERANCE);
	}
}