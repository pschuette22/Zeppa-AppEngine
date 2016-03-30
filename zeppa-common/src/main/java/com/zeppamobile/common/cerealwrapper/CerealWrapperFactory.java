package com.zeppamobile.common.cerealwrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

import org.datanucleus.util.Base64;


/**
 * 
 * @author Pete Schuette
 * 
 * <p>Factory for building CerealWrapper objects. CerealWrapper is a pun on
 *         serializable wrapper object. They are used to pass data between
 *         modules. Each *CerealWrapper object cooresponds to a datamodel object
 *         in the api module.</p>
 *
 */
public class CerealWrapperFactory {

	/**
	 * Build a cereal wrapper object from a given object. 
	 * @return cereal - cereal wrapper object of Employee option or null
	 */
	public CerealWrapper makeCereal(Object obj)
	{
		return null;
	}
	
	/**
	 * deserializes a cereal wrapper object from an encoded string
	 * @param cereal			- serialized user info object
	 * @return userInfoCereal 	- corresponding cereal wrapper object
	 */
	public CerealWrapper getFromCereal(String cereal) {

		// Result object that will be returned
		// Initialized to null in case object is not recognized
		CerealWrapper result = null;
		// Data of the cereal string
		
		byte[] data = Base64.decode(cereal);//cereal.getBytes(StandardCharsets.UTF_8);

		try {
			ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(data));

			result = (CerealWrapper) ois.readObject();
			ois.close();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Take a wrapper and make some cereal
	 * @param cerealWrapper
	 * @return serialized string representing this object
	 */
	public String toCereal(CerealWrapper cerealWrapper) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream( baos );
			oos.writeObject( cerealWrapper );
	        oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return new String(Base64.encode(baos.toByteArray())); 
	}
		
}
