package com.zeppamobile.smartfollow.nlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletContext;

import opennlp.tools.postag.POSModel;

/**
 * 
 * @author Pete Schuette
 * 
 * Factory for building part of speech models
 *
 */
public class POSFactory {

	// Holds the context of the servlet that called on this factory
	private ServletContext context;
	
	/**
	 * Construct a POSFactory
	 * @param context
	 */
	public POSFactory(ServletContext context){
		this.context = context;
	}
	
	
	/**
	 * Get part of speech model used to determine the parts of speech of a tag
	 */
	public POSModel buildPOSModel() {
		FileInputStream modelIn = null;
		try {
			URL resource;
			if(context == null){
				resource = POSFactory.class.getClass().getResource("/en-pos-maxent.bin");
			} else {
				resource = context.getResource("/WEB-INF/classes/en-pos-maxent.bin");
			}
			
			File file = new File(resource.toURI());
			modelIn = new FileInputStream(file);
			POSModel model = new POSModel(modelIn);
			
			return model;
		} catch (IOException e) {
			System.out.println("Caught I/O Exception");
			// Model loading failed, handle the error
			e.printStackTrace();
			return null;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
}
