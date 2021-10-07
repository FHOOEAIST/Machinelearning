package science.aist.machinelearning.analytics.graph.it.nodes;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
public class NodiestNode {

    private Long id;

    private String description;

    public NodiestNode() {
    }

    public NodiestNode(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
