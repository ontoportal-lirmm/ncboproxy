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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resourceCreationInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resourceCreationInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="resourceCreators" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="resourceCreator" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}actorInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="fundingProjects" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="fundingProject" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}projectInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="creationDate" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}dateCombinationType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourceCreationInfoType", propOrder = {
    "resourceCreators",
    "fundingProjects",
    "creationDate"
})
public class ResourceCreationInfoType {

    protected ResourceCreationInfoType.ResourceCreators resourceCreators;
    protected ResourceCreationInfoType.FundingProjects fundingProjects;
    protected DateCombinationType creationDate;

    /**
     * Gets the value of the resourceCreators property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceCreationInfoType.ResourceCreators }
     *     
     */
    public ResourceCreationInfoType.ResourceCreators getResourceCreators() {
        return resourceCreators;
    }

    /**
     * Sets the value of the resourceCreators property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceCreationInfoType.ResourceCreators }
     *     
     */
    public void setResourceCreators(ResourceCreationInfoType.ResourceCreators value) {
        this.resourceCreators = value;
    }

    /**
     * Gets the value of the fundingProjects property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceCreationInfoType.FundingProjects }
     *     
     */
    public ResourceCreationInfoType.FundingProjects getFundingProjects() {
        return fundingProjects;
    }

    /**
     * Sets the value of the fundingProjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceCreationInfoType.FundingProjects }
     *     
     */
    public void setFundingProjects(ResourceCreationInfoType.FundingProjects value) {
        this.fundingProjects = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link DateCombinationType }
     *     
     */
    public DateCombinationType getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateCombinationType }
     *     
     */
    public void setCreationDate(DateCombinationType value) {
        this.creationDate = value;
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
     *         &lt;element name="fundingProject" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}projectInfoType" maxOccurs="unbounded" minOccurs="0"/>
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
        "fundingProject"
    })
    public static class FundingProjects {

        protected List<ProjectInfoType> fundingProject;

        /**
         * Gets the value of the fundingProject property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the fundingProject property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getFundingProject().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ProjectInfoType }
         * 
         * 
         */
        public List<ProjectInfoType> getFundingProject() {
            if (fundingProject == null) {
                fundingProject = new ArrayList<ProjectInfoType>();
            }
            return this.fundingProject;
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
     *         &lt;element name="resourceCreator" type="{http://www.meta-share.org/OMTD-SHARE_XMLSchema}actorInfoType" maxOccurs="unbounded" minOccurs="0"/>
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
        "resourceCreator"
    })
    public static class ResourceCreators {

        protected List<ActorInfoType> resourceCreator;

        /**
         * Gets the value of the resourceCreator property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the resourceCreator property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getResourceCreator().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ActorInfoType }
         * 
         * 
         */
        public List<ActorInfoType> getResourceCreator() {
            if (resourceCreator == null) {
                resourceCreator = new ArrayList<ActorInfoType>();
            }
            return this.resourceCreator;
        }

    }

}