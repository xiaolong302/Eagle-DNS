/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.eagledns.zoneproviders.db.beans;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import se.unlogic.standardutils.annotations.WebPopulate;
import se.unlogic.standardutils.dao.annotations.DAOManaged;
import se.unlogic.standardutils.dao.annotations.Key;
import se.unlogic.standardutils.dao.annotations.ManyToOne;
import se.unlogic.standardutils.dao.annotations.OrderBy;
import se.unlogic.standardutils.dao.annotations.Table;
import se.unlogic.standardutils.xml.Elementable;
import se.unlogic.standardutils.xml.XMLElement;
import se.unlogic.standardutils.xml.XMLGenerator;

@XMLElement
@Table(name="records")
public class DBRecord implements Elementable {

	@DAOManaged(autoGenerated = true)
	@Key
	@XMLElement
	private Integer recordID;

	@DAOManaged(columnName = "zoneID")
	@ManyToOne(remoteKeyField="zoneID")
	@XMLElement
	private DBZone zone;

	@DAOManaged
	@OrderBy
	@XMLElement
	@WebPopulate(required=true,maxLength=255)
	private String name;

	@DAOManaged
	@OrderBy
	@XMLElement
	@WebPopulate(required=true,maxLength=6)
	private String type;

	@DAOManaged
	@XMLElement
	@WebPopulate(required=true,maxLength=6)
	private String dclass;

	@DAOManaged
	@XMLElement
	@WebPopulate(required=true,maxLength=255)
	private String content;

	@DAOManaged
	@XMLElement
	@WebPopulate
	private Long ttl;

	public DBRecord(){}

	public DBRecord(Record record, Name origin, long zoneTTL) {

		this.name = record.getName().relativize(origin).toString();
		this.type = Type.string(record.getType());
		this.dclass = DClass.string(record.getDClass());
		this.content = record.rdataToString();
		
		if(record.getTTL() == zoneTTL){
			
			this.ttl = null;
			
		}else{
		
			this.ttl = record.getTTL();
		}
	}

	public Integer getRecordID() {

		return recordID;
	}

	public void setRecordID(Integer recordID) {

		this.recordID = recordID;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	public String getContent() {

		return content;
	}

	public void setContent(String content) {

		this.content = content;
	}

	public Long getTtl() {

		return ttl;
	}

	public void setTtl(Long ttl) {

		this.ttl = ttl;
	}

	public Element toXML(Document doc) {

		return XMLGenerator.toXML(this, doc);
	}

	public DBZone getZone() {

		return zone;
	}

	public void setZone(DBZone zone) {

		this.zone = zone;
	}

	public Record getRecord(long zoneTTL, Name origin) throws TextParseException, IOException {

		long ttl;

		if(this.ttl == null){

			ttl = zoneTTL;

		}else{

			ttl = this.ttl;
		}

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(this.content);

		String rdata = stringBuilder.toString();

		Record record =  Record.fromString(Name.fromString(this.name,origin), Type.value(type), DClass.value(dclass), ttl, rdata, origin);

		return record;
	}


	public String getDclass() {

		return dclass;
	}


	public void setDclass(String dclass) {

		this.dclass = dclass;
	}

	@Override
	public String toString() {

		if(zone != null){
			
			return name + " (ID: " + zone.getZoneID() + ")";
			
		}else{
			
			return name;
		}
		
	}
}
