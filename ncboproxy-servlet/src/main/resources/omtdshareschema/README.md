# OMTDShare JAXB Schema Generation 

The implementation of the OMTD Share Metadata export for *Portal instances is based on JAXB, that allows to generate a Java object model that matches the specification in a XSD file. The Java classes can then be instanciated in a type safe manner and then unmarshalled to generate valid XML output. 

The current directory contains all the XSD files of the OMTDShare 3.0.2 specification as well as a configuration file for the JAXB generator. In order to regenerate the java classes with a newer version of the specification model, please replace all the XSD file with the newer version and then recompile them into java classes with `xjc`. The package of the java model classes is `io.github.agroportal.ncboproxy.handlers.omtdsharemeta.xsdmodel`

**Note:** `xjc` is included in all standard JDK 1.6+ installations 

## Generating the object model

Place yourself in the directory of this README file and run: 

```Shell
xjc OMTD-SHARE-LexicalConceptualResource.xsd -p io.github.agroportal.ncboproxy.handlers.omtdsharemeta.xsdmodel -b omtdshre.xjb -d ../ncboproxy-servlet/src/main/java/
```

This should regenerate all the classes matching the new version of the schema. Before this step, make sure to delete `../ncboproxy-servlet/src/main/java/io/github/agroportal/ncboproxy/servlet/handlers/omtdsharemeta/xsdmodel`

After a new set of classes is generated, the code in `io.github.agroportal.ncboproxy.handlers.omtdsharemeta.OMTDShareOutputGenerator` may have to be updated 