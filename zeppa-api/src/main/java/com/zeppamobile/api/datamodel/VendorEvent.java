package com.zeppamobile.api.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.json.simple.JSONObject;

import com.zeppamobile.api.datamodel.ZeppaEvent;

@PersistenceCapable
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
public class VendorEvent extends ZeppaEvent {
	
	

	public VendorEvent(JSONObject json) {
		super(json);
		// TODO Auto-generated constructor stub
	}

	
	public VendorEvent(String title,String description,Long start, Long end, Long vendorId,List<Long> tagIds, String address){
		super("","","",ZeppaEvent.EventPrivacyType.PUBLIC,vendorId,title,description,true,start,end,address,"",tagIds,null);		
	}
	
	/**
	 * Convert this object to a json object
	 * 
	 * @return jsonObject
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();

		obj.put("id", key.getId());
		obj.put("created", created == null ? Long.valueOf(-1) : created);
		obj.put("updated", updated == null ? Long.valueOf(-1) : updated);
		obj.put("googleCalendarId",
				googleCalendarId == null ? "googleCalendarId"
						: googleCalendarId);
		obj.put("googleCalendarEventId",
				googleCalendarEventId == null ? "googleCalendarEventId"
						: googleCalendarEventId);
		obj.put("iCalUID", iCalUID == null ? "iCalUID" : iCalUID);
		obj.put("privacy", privacy == null ? "privacy" : privacy.toString());
		obj.put("hostId", hostId == null ? Long.valueOf(-1) : hostId);
		obj.put("title", title == null ? "title" : title);
		obj.put("description", description == null ? "description"
				: description);
		obj.put("guestsMayInvite", guestsMayInvite == null ? false
				: guestsMayInvite);
		obj.put("start", start == null ? Long.valueOf(-1) : start);
		obj.put("end", end == null ? Long.valueOf(-1) : end);
		obj.put("displayLocation", displayLocation == null ? "displayLocation"
				: displayLocation);
		obj.put("mapsLocation", mapsLocation == null ? "mapsLocation"
				: mapsLocation);
		obj.put("tagIds",
				tagIds == null ? (new ArrayList<Long>())
						: tagIds);
		obj.put("invitedUserIds",
				invitedUserIds == null ? (new ArrayList<Long>())
						: invitedUserIds);

		return obj;
	}
	
	@Persistent
	private String placeId;
	
	@NotPersistent
	private  VendorEventRelationship relationship;
	

	public String getPlaceId() {
		return placeId;
	}


	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}


	public VendorEventRelationship getRelationship() {
		return relationship;
	}


	public void setRelationship(VendorEventRelationship relationship) {
		this.relationship = relationship;
	}
	

}
