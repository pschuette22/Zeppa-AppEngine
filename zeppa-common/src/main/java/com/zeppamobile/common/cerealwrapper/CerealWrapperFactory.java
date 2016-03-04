package com.zeppamobile.common.cerealwrapper;

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
public abstract class CerealWrapperFactory {

	/**
	 * Builds an User Info Cereal Object
	 * @return cereal - cereal wrapper object of Employee option
	 */
	public abstract UserInfoCerealWrapper makeUserInfoCereal(Object userInfo);
	
	/**
	 * deserializes a UserInfo cereal object
	 * @param cereal			- serialized user info object
	 * @return userInfoCereal 	- corresponding cereal wrapper object
	 */
	public abstract UserInfoCerealWrapper getUserInfoCereal(String cereal);
	
	
	/**
	 * Builds an Employee Cereal Object
	 * @return cereal - cereal wrapper object of Employee option
	 */
	public abstract EmployeeCerealWrapper makeEmployeeCereal(Object employee);
	
	
	/**
	 * deserializes an employee cereal object
	 * @param cereal			- serialized employee object
	 * @return employeeCereal 	- corresponding cereal wrapper object
	 */
	public abstract EmployeeCerealWrapper getEmployeeCereal(String cereal);
	

	
}
