package com.zeppamobile.api.endpoint.utils;

import com.zeppamobile.api.datamodel.Employee;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;
import com.zeppamobile.common.cerealwrapper.CerealWrapper;
import com.zeppamobile.common.cerealwrapper.CerealWrapperFactory;
import com.zeppamobile.common.cerealwrapper.EmployeeCerealWrapper;
import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;

/**
 * 
 * @author Pete Schuette
 * 
 *         <p>
 *         Api Module Implementation of the Cereal Wrapper. This factory will
 *         build CerealWrappers from corresponding DataInfo objects
 *         </p>
 *
 */
public class ApiCerealWrapperFactory extends CerealWrapperFactory {
	
	/*
	 * Make some cereal
	 */
	@Override
	public CerealWrapper makeCereal(Object obj) {
		
		// Result object that will be returned
		// Initialized to null in case object is not recognized
		CerealWrapper result = null;
		if (obj instanceof ZeppaUserInfo) {
			ZeppaUserInfo info = (ZeppaUserInfo) obj;
			result = new UserInfoCerealWrapper(info.getCreated(),
					info.getUpdated(), info.getGivenName(),
					info.getFamilyName(), info.getImageUrl(), info.getGender().toString(), info.getDateOfBirth());
		} else if (obj instanceof Employee) {
			Employee employee = (Employee) obj;
			result = new EmployeeCerealWrapper(employee.getCreated(),
					employee.getUpdated(),
					(UserInfoCerealWrapper) makeCereal(employee.getUserInfo()),
					employee.getVendorId(), employee.getEmailAddress(),
					employee.getPassword(), employee.getIsEmailVerified(),
					employee.getPrivakeyGuid());
		}
		
		
		
		return result;
	}


}
