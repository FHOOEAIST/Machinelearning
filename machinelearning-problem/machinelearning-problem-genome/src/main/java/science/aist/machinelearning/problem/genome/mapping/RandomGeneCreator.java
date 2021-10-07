package science.aist.machinelearning.problem.genome.mapping;


import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.mapping.GeneCreator;
import science.aist.machinelearning.problem.genome.Element;

import java.util.Random;

/**
 * Creates a sequence of random genes using the length and values of the given problem.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class RandomGeneCreator implements GeneCreator<Element[], Element[]> {

    Random r = new Random();

    @Override
    public Element[] createGene(ProblemGene<Element[]> problem) {

        Element[] elements = new Element[problem.getGene().length];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = problem.getGene()[r.nextInt(problem.getGene().length)];
        }

        return elements;
    }

    public Random getR() {
        return r;
    }
}
