package com.zeppamobile.api.datamodel;

import java.util.Calendar;

public class Bill {

	private Calendar cycleStartDate;
	private Calendar cycleEndDate;
	private Long amountOwed;
	private boolean paid;
	private Long vendorId;
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
