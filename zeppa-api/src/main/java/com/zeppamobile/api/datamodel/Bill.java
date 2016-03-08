package com.zeppamobile.api.datamodel;

import java.util.Calendar;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Bill {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Calendar cycleStartDate;
	
	@Persistent
	private Calendar cycleEndDate;
	
	@Persistent
	private Long amountOwed;
	
	@Persistent
	private boolean paid;
	
	@Persistent
	private Long vendorId;
	
	@Persistent
	private Long billId;
	
	public Calendar getCycleStartDate() {
		return cycleStartDate;
	}
	public void setCycleStartDate(Calendar cycleStartDate) {
		this.cycleStartDate = cycleStartDate;
	}
	public Calendar getCycleEndDate() {
		return cycleEndDate;
	}
	public void setCycleEndDate(Calendar cycleEndDate) {
		this.cycleEndDate = cycleEndDate;
	}
	public Long getAmountOwed() {
		return amountOwed;
	}
	public void setAmountOwed(Long amountOwed) {
		this.amountOwed = amountOwed;
	}
	public boolean isPaid() {
		return paid;
	}
	public void setPaid(boolean paid) {
		this.paid = paid;
	}
	public Long getVendorId() {
		return vendorId;
	}
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}
	public Long getBillId() {
		return billId;
	}
	public void setBillId(Long billId) {
		this.billId = billId;
	}
	
	
}
