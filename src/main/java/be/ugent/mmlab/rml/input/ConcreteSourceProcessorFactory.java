package be.ugent.mmlab.rml.input;

import be.ugent.mmlab.rml.mapdochandler.extraction.concrete.SourceExtractor;
import be.ugent.mmlab.rml.mapdochandler.extraction.source.concrete.DcatExtractor;
import be.ugent.mmlab.rml.mapdochandler.extraction.source.concrete.HydraExtractor;
import be.ugent.mmlab.rml.mapdochandler.extraction.source.concrete.JdbcExtractor;
import be.ugent.mmlab.rml.mapdochandler.extraction.source.concrete.LocalFileExtractor;
import be.ugent.mmlab.rml.mapdochandler.extraction.source.concrete.SparqlExtractor;
import be.ugent.mmlab.rml.model.Source;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 * RML - Data Retrieval Handler : ConcreteSourceProcessorFactory
 *
 * @author andimou
 */
public class ConcreteSourceProcessorFactory implements SourceProcessorFactory {
    
    // Log
    private static final Logger log = LoggerFactory.getLogger(ConcreteSourceProcessorFactory.class);
    
    public SourceExtractor createSourceProcessor(Repository repository, Source source) {
        SourceExtractor sourceExtractor = null;
        try {
            
            RepositoryConnection connection = repository.getConnection();
            if (value.getClass().getSimpleName().equals("MemLiteral")) {
                log.debug("Literal-valued Input Source");
                sourceExtractor = new LocalFileExtractor();
            } else {
                log.debug("Resource-valued Input Source");
                
                RepositoryResult<Statement> inputStatements =
                        connection.getStatements((Resource) value, RDF.TYPE, null, true);
                
                String sourceType = inputStatements.next().getObject().stringValue().toString();
                log.debug("source type " + sourceType);

                switch (sourceType) {
                    case ("http://www.w3.org/ns/hydra/core#APIDocumentation"):
                        log.debug("Source described with Hydra Core vocabulary.");
                        sourceExtractor = new HydraExtractor();
                        break;
                    case ("http://www.w3.org/ns/dcat#Distribution"):
                        log.debug("Source described with DCAT vocabulary.");
                        sourceExtractor = new DcatExtractor();
                        break;
                    case ("http://www.w3.org/ns/sparql-service-description#Service"):
                        log.debug("Source described with SPARQL-SD vocabulary.");
                        sourceExtractor = new SparqlExtractor();
                        break;
                    case ("http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#Database"):
                        log.debug("Source described with D2RQ vocabulary.");
                        sourceExtractor = new JdbcExtractor();
                        break;
                    default:
                        log.error("Not identified source type");
                }
                connection.close();
            }
        } catch (RepositoryException ex) {
            log.error("RepositoryException " + ex);
        }
        return sourceExtractor;
    }
    
    public Set<Source> chooseSource(Repository repository, Value value) {
        Set<Source> inputSources = null;
        try {
            SourceExtractor input;
            RepositoryConnection connection = repository.getConnection();
            if (value.getClass().getSimpleName().equals("MemLiteral")) {
                log.debug("Literal-valued Input Source");
                //TODO: Change extractInput to Value instead of Resource
                LocalFileExtractor localFileExtractor = new LocalFileExtractor();
                inputSources = localFileExtractor.extractInput(repository, value.stringValue());
                
            } else {
                log.debug("Resource-valued Input Source");
                
                RepositoryResult<Statement> inputStatements =
                        connection.getStatements((Resource) value, RDF.TYPE, null, true);
                
                String sourceType = inputStatements.next().getObject().stringValue().toString();
                log.debug("source type " + sourceType);

                //TODO:Change the followings not to compare with String
                switch (sourceType) {
                    case ("http://www.w3.org/ns/hydra/core#APIDocumentation"):
                        log.debug("Source described with Hydra Core vocabulary.");
                        input = new HydraExtractor();
                        inputSources = input.extractSources(repository, (Resource) value);
                        break;
                    case ("http://www.w3.org/ns/dcat#Distribution"):
                        log.debug("Source described with DCAT vocabulary.");
                        input = new DcatExtractor();
                        inputSources = input.extractSources(repository, (Resource) value);
                        break;
                    case ("http://www.w3.org/ns/sparql-service-description#Service"):
                        log.debug("Source described with SPARQL-SD vocabulary.");
                        input = new SparqlExtractor();
                        inputSources = input.extractSources(repository, (Resource) value);
                        break;
                    case ("http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#Database"):
                        log.debug("Source described with D2RQ vocabulary.");
                        input = new JdbcExtractor();
                        inputSources = input.extractSources(repository, (Resource) value);
                        break;
                    default:
                        log.error("Not identified source type");
                }
                connection.close();
            }
        } catch (RepositoryException ex) {
            log.error("RepositoryException " + ex);
        }
        return inputSources;
    }
    
}
