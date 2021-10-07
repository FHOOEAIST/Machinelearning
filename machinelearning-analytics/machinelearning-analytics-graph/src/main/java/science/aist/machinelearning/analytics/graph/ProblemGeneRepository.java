package science.aist.machinelearning.analytics.graph;

import science.aist.machinelearning.core.ProblemGene;
import science.aist.neo4j.reflective.ReflectiveNeo4JNodeRepositoryImpl;
import science.aist.neo4j.transaction.TransactionManager;

/**
 * Repository for storing Problem-Gene-Description nodes
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class ProblemGeneRepository extends ReflectiveNeo4JNodeRepositoryImpl<ProblemGene> {

    public ProblemGeneRepository(TransactionManager manager) {
        super(manager, ProblemGene.class);
    }

    /**
     * Returns a problem gene by it's description (Unlike solution genes a description makes the problem gene unique)
     *
     * @param description the description
     * @return problem gene
     */
    public ProblemGene findByDescription(String description) {
        return findBy("description", description);
    }
}
