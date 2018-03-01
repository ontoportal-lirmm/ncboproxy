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
 * <p>Java class for sizeInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sizeInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}size"/>
 *         &lt;element name="sizeUnit">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="30"/>
 *               &lt;enumeration value="terms"/>
 *               &lt;enumeration value="entries"/>
 *               &lt;enumeration value="articles"/>
 *               &lt;enumeration value="files"/>
 *               &lt;enumeration value="items"/>
 *               &lt;enumeration value="elements"/>
 *               &lt;enumeration value="units"/>
 *               &lt;enumeration value="texts"/>
 *               &lt;enumeration value="sentences"/>
 *               &lt;enumeration value="bytes"/>
 *               &lt;enumeration value="tokens"/>
 *               &lt;enumeration value="words"/>
 *               &lt;enumeration value="keywords"/>
 *               &lt;enumeration value="idiomaticExpressions"/>
 *               &lt;enumeration value="triples"/>
 *               &lt;enumeration value="neologisms"/>
 *               &lt;enumeration value="multiWordUnits"/>
 *               &lt;enumeration value="expressions"/>
 *               &lt;enumeration value="synsets"/>
 *               &lt;enumeration value="classes"/>
 *               &lt;enumeration value="concepts"/>
 *               &lt;enumeration value="lexicalTypes"/>
 *               &lt;enumeration value="phoneticUnits"/>
 *               &lt;enumeration value="morphologicalUnits"/>
 *               &lt;enumeration value="syntacticUnits"/>
 *               &lt;enumeration value="semanticUnits"/>
 *               &lt;enumeration value="predicates"/>
 *               &lt;enumeration value="frames"/>
 *               &lt;enumeration value="kb"/>
 *               &lt;enumeration value="mb"/>
 *               &lt;enumeration value="gb"/>
 *               &lt;enumeration value="tb"/>
 *               &lt;enumeration value="unigrams"/>
 *               &lt;enumeration value="bigrams"/>
 *               &lt;enumeration value="trigrams"/>
 *               &lt;enumeration value="4-grams"/>
 *               &lt;enumeration value="5-grams"/>
 *               &lt;enumeration value="rules"/>
 *               &lt;enumeration value="other"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sizeInfoType", propOrder = {
    "size",
    "sizeUnit"
})
public class SizeInfoType {

    @XmlElement(required = true)
    protected String size;
    @XmlElement(required = true)
    protected String sizeUnit;

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSize(String value) {
        this.size = value;
    }

    /**
     * Gets the value of the sizeUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSizeUnit() {
        return sizeUnit;
    }

    /**
     * Sets the value of the sizeUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSizeUnit(String value) {
        this.sizeUnit = value;
    }

}