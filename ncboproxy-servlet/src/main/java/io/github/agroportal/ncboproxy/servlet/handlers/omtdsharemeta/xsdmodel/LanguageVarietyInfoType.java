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
 * <p>Java class for languageVarietyInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="languageVarietyInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="languageVarietyType">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="20"/>
 *               &lt;enumeration value="dialect"/>
 *               &lt;enumeration value="jargon"/>
 *               &lt;enumeration value="other"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}languageVarietyName"/>
 *         &lt;element name="sizePerLanguageVariety" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}sizeInfoType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "languageVarietyInfoType", propOrder = {
    "languageVarietyType",
    "languageVarietyName",
    "sizePerLanguageVariety"
})
public class LanguageVarietyInfoType {

    @XmlElement(required = true)
    protected String languageVarietyType;
    @XmlElement(required = true)
    protected String languageVarietyName;
    protected SizeInfoType sizePerLanguageVariety;

    /**
     * Gets the value of the languageVarietyType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguageVarietyType() {
        return languageVarietyType;
    }

    /**
     * Sets the value of the languageVarietyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguageVarietyType(String value) {
        this.languageVarietyType = value;
    }

    /**
     * The name of the language variety that occurs in the resource or is supported by a tool/service
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguageVarietyName() {
        return languageVarietyName;
    }

    /**
     * Sets the value of the languageVarietyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguageVarietyName(String value) {
        this.languageVarietyName = value;
    }

    /**
     * Gets the value of the sizePerLanguageVariety property.
     * 
     * @return
     *     possible object is
     *     {@link SizeInfoType }
     *     
     */
    public SizeInfoType getSizePerLanguageVariety() {
        return sizePerLanguageVariety;
    }

    /**
     * Sets the value of the sizePerLanguageVariety property.
     * 
     * @param value
     *     allowed object is
     *     {@link SizeInfoType }
     *     
     */
    public void setSizePerLanguageVariety(SizeInfoType value) {
        this.sizePerLanguageVariety = value;
    }

}