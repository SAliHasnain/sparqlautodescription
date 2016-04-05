package sad.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Prov {

	 /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/ns/prov#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    public static final Resource Activity = m_model.createResource( NS_Hash_Prp(NS,"Activity") );
    
    public static final Property generated = m_model.createProperty(NS_Hash_Prp(NS,"generated"));
    public static final Property startedAtTime = m_model.createProperty(NS_Hash_Prp(NS,"startedAtTime"));
    public static final Property endedAtTime = m_model.createProperty(NS_Hash_Prp(NS,"endedAtTime"));
    public static final Property wasGeneratedBy = m_model.createProperty(NS_Hash_Prp(NS,"wasGeneratedBy"));
    public static final Property wasDerivedFrom = m_model.createProperty(NS_Hash_Prp(NS,"wasDerivedFrom"));
      
        
    /** <p>RDFS namespace</p> */ 
    public static final Property RDFS = m_model.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
  
    /**
     *  NS: Resource Name and Prp:roperty of resource
     * @param NS
     * @param Prp
     * @return : NS#Prp
     */
    private static String NS_Hash_Prp(String NS, String Prp){
    	return NS.concat(Prp);
    }

}
