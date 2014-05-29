package sad.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Sad {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://vocab.deri.ie/sad#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final Property countTriples = m_model.createProperty( "http://vocab.deri.ie/sad#countTriples" );
    
    public static final Property endpoint = m_model.createProperty( "http://vocab.deri.ie/sad#endpoint" );
    
    public static final Property execEndTime = m_model.createProperty( "http://vocab.deri.ie/sad#execEndTime" );
    
    public static final Property execStartTime = m_model.createProperty( "http://vocab.deri.ie/sad#execStartTime" );
    
    public static final Property language = m_model.createProperty( "http://vocab.deri.ie/sad#language" );
    
    public static final Property query = m_model.createProperty( "http://vocab.deri.ie/sad#query" );
    
    public static final Property responseCode = m_model.createProperty( "http://vocab.deri.ie/sad#responseCode" );
    
    public static final Property responseHeader = m_model.createProperty( "http://vocab.deri.ie/sad#responseHeader" );
    
    public static final Property resultDataset = m_model.createProperty( "http://vocab.deri.ie/sad#resultDataset" );
    
    public static final Property resultGraph = m_model.createProperty( "http://vocab.deri.ie/sad#resultGraph" );
    
    public static final Property resultTupleCount = m_model.createProperty( "http://vocab.deri.ie/sad#resultTupleCount" );
    
    public static final Resource QueryRun = m_model.createResource( "http://vocab.deri.ie/sad#QueryRun" );
    
    
    /** <p>RDFS namespace</p> */ 
    public static final Property RDFS = m_model.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
    
}
