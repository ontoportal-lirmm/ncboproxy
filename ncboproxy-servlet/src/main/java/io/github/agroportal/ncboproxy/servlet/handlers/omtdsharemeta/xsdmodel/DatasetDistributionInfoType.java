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
 * <p>Java class for datasetDistributionInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="datasetDistributionInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}distributionMedium"/>
 *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}distributionLocation" minOccurs="0"/>
 *         &lt;element name="sizes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}sizeInfo" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="textFormats">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}textFormatInfo" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="characterEncodings" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}characterEncodingInfo" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "datasetDistributionInfoType", propOrder = {
    "distributionMedium",
    "distributionLocation",
    "sizes",
    "textFormats",
    "characterEncodings"
})
public class DatasetDistributionInfoType {

    @XmlElement(required = true)
    protected String distributionMedium;
    protected String distributionLocation;
    @XmlElement(required = true)
    protected DatasetDistributionInfoType.Sizes sizes;
    @XmlElement(required = true)
    protected DatasetDistributionInfoType.TextFormats textFormats;
    protected DatasetDistributionInfoType.CharacterEncodings characterEncodings;

    /**
     * Gets the value of the distributionMedium property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDistributionMedium() {
        return distributionMedium;
    }

    /**
     * Sets the value of the distributionMedium property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDistributionMedium(String value) {
        this.distributionMedium = value;
    }

    /**
     * Any location where the resource can be downloaded from
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
     * Gets the value of the sizes property.
     * 
     * @return
     *     possible object is
     *     {@link DatasetDistributionInfoType.Sizes }
     *     
     */
    public DatasetDistributionInfoType.Sizes getSizes() {
        return sizes;
    }

    /**
     * Sets the value of the sizes property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatasetDistributionInfoType.Sizes }
     *     
     */
    public void setSizes(DatasetDistributionInfoType.Sizes value) {
        this.sizes = value;
    }

    /**
     * Gets the value of the textFormats property.
     * 
     * @return
     *     possible object is
     *     {@link DatasetDistributionInfoType.TextFormats }
     *     
     */
    public DatasetDistributionInfoType.TextFormats getTextFormats() {
        return textFormats;
    }

    /**
     * Sets the value of the textFormats property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatasetDistributionInfoType.TextFormats }
     *     
     */
    public void setTextFormats(DatasetDistributionInfoType.TextFormats value) {
        this.textFormats = value;
    }

    /**
     * Gets the value of the characterEncodings property.
     * 
     * @return
     *     possible object is
     *     {@link DatasetDistributionInfoType.CharacterEncodings }
     *     
     */
    public DatasetDistributionInfoType.CharacterEncodings getCharacterEncodings() {
        return characterEncodings;
    }

    /**
     * Sets the value of the characterEncodings property.
     * 
     * @param value
     *     allowed object is
     *     {@link DatasetDistributionInfoType.CharacterEncodings }
     *     
     */
    public void setCharacterEncodings(DatasetDistributionInfoType.CharacterEncodings value) {
        this.characterEncodings = value;
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
     *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}characterEncodingInfo" maxOccurs="unbounded" minOccurs="0"/>
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
        "characterEncodingInfo"
    })
    public static class CharacterEncodings {

        protected List<CharacterEncodingInfoType> characterEncodingInfo;

        /**
         * Gets the value of the characterEncodingInfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the characterEncodingInfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCharacterEncodingInfo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CharacterEncodingInfoType }
         * 
         * 
         */
        public List<CharacterEncodingInfoType> getCharacterEncodingInfo() {
            if (characterEncodingInfo == null) {
                characterEncodingInfo = new ArrayList<CharacterEncodingInfoType>();
            }
            return this.characterEncodingInfo;
        }

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
     *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}sizeInfo" maxOccurs="unbounded"/>
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

        @XmlElement(required = true)
        protected List<SizeInfoType> sizeInfo;

        /**
         * Groups information on the size of the resource or of resource parts Gets the value of the sizeInfo property.
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
     *         &lt;element ref="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}textFormatInfo" maxOccurs="unbounded"/>
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
        "textFormatInfo"
    })
    public static class TextFormats {

        @XmlElement(required = true)
        protected List<TextFormatInfoType> textFormatInfo;

        /**
         * Gets the value of the textFormatInfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the textFormatInfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTextFormatInfo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TextFormatInfoType }
         * 
         * 
         */
        public List<TextFormatInfoType> getTextFormatInfo() {
            if (textFormatInfo == null) {
                textFormatInfo = new ArrayList<TextFormatInfoType>();
            }
            return this.textFormatInfo;
        }

    }

}