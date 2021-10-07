package science.aist.machinelearning.core;

import org.neo4j.ogm.annotation.Relationship;

import java.io.Serializable;

/**
 * An abstract genome of a Type T
 *
 * @param <T> Type that the genome has
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class Gene<T> implements Serializable {

    @Relationship(type = "RWGENE", direction = "OUTGOING")
    protected T gene;

    public T getGene() {
        return gene;
    }

    public void setGene(T gene) {
        this.gene = gene;
    }
}
