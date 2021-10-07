package science.aist.machinelearning.algorithm.mutation;

import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.problem.genome.Element;

/**
 * This class is specific to the problem instance of genome, and only needs to be applied to the specific algorithm GA
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class RandomGeneMutator extends RandomNGenesMutator<Element[], Element[]> {

    protected SolutionGene<Element[], Element[]> createGeneByMutation(SolutionGene<Element[], Element[]> gene) {

        //create a new SolutionGene with the previous data
        //mutate it afterwards
        SolutionGene<Element[], Element[]> mutated = new SolutionGene<>();

        if (gene != null && gene.getProblemGenes() != null && gene.getProblemGenes().size() > 0 && gene.getGene() != null) {
            mutated.setProblemGenes(gene.getProblemGenes());
            Element[] mutatedElement = new Element[gene.getGene().length];
            for (int i = 0; i < gene.getGene().length; i++) {
                mutatedElement[i] = new Element(gene.getGene()[i].getValue());
            }
            mutatedElement[getR().nextInt(gene.getGene().length)].setValue(
                    gene.getProblemGenes().get(0).getGene()
                            [getR().nextInt(gene.getProblemGenes().get(0).getGene().length)].getValue()
            );
            mutated.setGene(mutatedElement);
        }

        return mutated;
    }

}
