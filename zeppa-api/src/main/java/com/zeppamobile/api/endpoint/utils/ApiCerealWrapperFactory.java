package com.zeppamobile.api.endpoint.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;

import com.zeppamobile.api.datamodel.Employee;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;
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

	@Override
	public UserInfoCerealWrapper makeUserInfoCereal(Object userInfo) {
		UserInfoCerealWrapper result = null;
		if (userInfo instanceof ZeppaUserInfo) {
			ZeppaUserInfo info = (ZeppaUserInfo) userInfo;
			result = new UserInfoCerealWrapper(info.getCreated(),
					info.getUpdated(), info.getGivenName(),
					info.getFamilyName(), info.getImageUrl(), info.getGender()
							.toString(), info.getDateOfBirth());
		}
		return result;
	}

	@Override
	public UserInfoCerealWrapper getUserInfoCereal(String cereal) {
		UserInfoCerealWrapper userInfoCereal = null;

		byte[] data = cereal.getBytes(StandardCharsets.UTF_8);
		try {
			ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(data));

			userInfoCereal = (UserInfoCerealWrapper) ois.readObject();
			ois.close();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userInfoCereal;
	}

	@Override
	public EmployeeCerealWrapper makeEmployeeCereal(Object employeeObject) {
		EmployeeCerealWrapper result = null;
		if (employeeObject instanceof Employee) {
			Employee employee = (Employee) employeeObject;
			result = new EmployeeCerealWrapper(employee.getCreated(),
					employee.getUpdated(),
					makeUserInfoCereal(employee.getUserInfo()),
					employee.getVendorId(), employee.getEmailAddress(),
					employee.getPassword(), employee.getIsEmailVerified(),
					employee.getPrivakeyGuid());
		}
		return result;
	}

	@Override
	public EmployeeCerealWrapper getEmployeeCereal(String cereal) {
		EmployeeCerealWrapper employeeCereal = null;

		byte[] data = cereal.getBytes(StandardCharsets.UTF_8);
		try {
			ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(data));

			employeeCereal = (EmployeeCerealWrapper) ois.readObject();
			ois.close();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return employeeCereal;
	}

}
