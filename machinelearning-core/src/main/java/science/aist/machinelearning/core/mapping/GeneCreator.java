package science.aist.machinelearning.core.mapping;

import science.aist.machinelearning.core.ProblemGene;

/**
 * Interface for the implementation of geneCreators. GeneCreators create genes depending on the implementation and use
 * the given problemGene.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface GeneCreator<ST, PT> {

    ST createGene(ProblemGene<PT> problem);
}
