//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.01 at 12:36:44 PM CET 
//


package io.github.agroportal.ncboproxy.servlet.handlers.omtdsharemeta.xsdmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for geographicCoverageInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="geographicCoverageInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="geographicCoverage">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="sizePerGeographicCoverage" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}sizeInfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "geographicCoverageInfoType", propOrder = {
    "geographicCoverage",
    "sizePerGeographicCoverage"
})
public class GeographicCoverageInfoType {

    @XmlElement(required = true)
    protected String geographicCoverage;
    protected SizeInfoType sizePerGeographicCoverage;

    /**
     * Gets the value of the geographicCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeographicCoverage() {
        return geographicCoverage;
    }

    /**
     * Sets the value of the geographicCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeographicCoverage(String value) {
        this.geographicCoverage = value;
    }

    /**
     * Gets the value of the sizePerGeographicCoverage property.
     * 
     * @return
     *     possible object is
     *     {@link SizeInfoType }
     *     
     */
    public SizeInfoType getSizePerGeographicCoverage() {
        return sizePerGeographicCoverage;
    }

    /**
     * Sets the value of the sizePerGeographicCoverage property.
     * 
     * @param value
     *     allowed object is
     *     {@link SizeInfoType }
     *     
     */
    public void setSizePerGeographicCoverage(SizeInfoType value) {
        this.sizePerGeographicCoverage = value;
    }

}