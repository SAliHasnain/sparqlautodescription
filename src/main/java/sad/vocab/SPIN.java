package sad.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class SPIN {


	 /** <p>The RDF model that holds the vocabulary terms</p> */
   private static Model m_model = ModelFactory.createDefaultModel();
   
   /** <p>The namespace of the vocabulary as a string</p> */
   public static final String NS = "http://spinrdf.org/sp#";
   
   /** <p>The namespace of the vocabulary as a string</p>
    *  @see #NS */
   public static String getURI() {return NS;}
   
   /** <p>The namespace of the vocabulary as a resource</p> */
   public static final Resource NAMESPACE = m_model.createResource( NS );
   
   public static final Property text = m_model.createProperty(NS_Hash_Prp(NS,"text"));

     
       
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
