//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.01 at 12:36:44 PM CET 
//


package io.github.agroportal.ncboproxy.servlet.handlers.omtdsharemeta.xsdmodel;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documentDistributionInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentDistributionInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}distributionLocation"/>
 *         &lt;element name="hashkey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}dataFormatInfo" minOccurs="0"/>
 *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}characterEncoding" minOccurs="0"/>
 *         &lt;element name="sizes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}sizeInfo" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
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
@XmlType(name = "documentDistributionInfoType", propOrder = {
    "distributionLocation",
    "hashkey",
    "dataFormatInfo",
    "characterEncoding",
    "sizes"
})
public class DocumentDistributionInfoType {

    @XmlElement(required = true)
    protected String distributionLocation;
    @XmlElement(required = true)
    protected String hashkey;
    protected DataFormatInfo dataFormatInfo;
    protected String characterEncoding;
    protected DocumentDistributionInfoType.Sizes sizes;

    /**
     * Gets the value of the distributionLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistributionLocation() {
        return distributionLocation;
    }

    /**
     * Sets the value of the distributionLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistributionLocation(String value) {
        this.distributionLocation = value;
    }

    /**
     * Gets the value of the hashkey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHashkey() {
        return hashkey;
    }

    /**
     * Sets the value of the hashkey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHashkey(String value) {
        this.hashkey = value;
    }

    /**
     * Gets the value of the dataFormatInfo property.
     * 
     * @return
     *     possible object is
     *     {@link DataFormatInfo }
     *     
     */
    public DataFormatInfo getDataFormatInfo() {
        return dataFormatInfo;
    }

    /**
     * Sets the value of the dataFormatInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataFormatInfo }
     *     
     */
    public void setDataFormatInfo(DataFormatInfo value) {
        this.dataFormatInfo = value;
    }

    /**
     * Gets the value of the characterEncoding property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /**
     * Sets the value of the characterEncoding property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCharacterEncoding(String value) {
        this.characterEncoding = value;
    }

    /**
     * Gets the value of the sizes property.
     * 
     * @return
     *     possible object is
     *     {@link DocumentDistributionInfoType.Sizes }
     *     
     */
    public DocumentDistributionInfoType.Sizes getSizes() {
        return sizes;
    }

    /**
     * Sets the value of the sizes property.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentDistributionInfoType.Sizes }
     *     
     */
    public void setSizes(DocumentDistributionInfoType.Sizes value) {
        this.sizes = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}sizeInfo" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "sizeInfo"
    })
    public static class Sizes {

        protected List<SizeInfoType> sizeInfo;

        /**
         * Gets the value of the sizeInfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the sizeInfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSizeInfo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SizeInfoType }
         * 
         * 
         */
        public List<SizeInfoType> getSizeInfo() {
            if (sizeInfo == null) {
                sizeInfo = new ArrayList<SizeInfoType>();
            }
            return this.sizeInfo;
        }

    }

}