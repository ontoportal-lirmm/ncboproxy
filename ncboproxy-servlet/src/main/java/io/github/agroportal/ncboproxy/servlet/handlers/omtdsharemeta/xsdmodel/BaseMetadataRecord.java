//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.01 at 12:36:44 PM CET 
//


package io.github.agroportal.ncboproxy.servlet.handlers.omtdsharemeta.xsdmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for baseMetadataRecord complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="baseMetadataRecord">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="metadataHeaderInfo" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}metadataHeaderInfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseMetadataRecord", propOrder = {
    "metadataHeaderInfo"
})
@XmlSeeAlso({
    LcrMetadataRecord.class
})
public class BaseMetadataRecord {

    protected MetadataHeaderInfoType metadataHeaderInfo;

    /**
     * Gets the value of the metadataHeaderInfo property.
     * 
     * @return
     *     possible object is
     *     {@link MetadataHeaderInfoType }
     *     
     */
    public MetadataHeaderInfoType getMetadataHeaderInfo() {
        return metadataHeaderInfo;
    }

    /**
     * Sets the value of the metadataHeaderInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link MetadataHeaderInfoType }
     *     
     */
    public void setMetadataHeaderInfo(MetadataHeaderInfoType value) {
        this.metadataHeaderInfo = value;
    }

}