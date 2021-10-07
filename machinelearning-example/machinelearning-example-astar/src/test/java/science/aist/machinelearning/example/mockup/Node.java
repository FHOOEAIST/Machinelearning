package science.aist.machinelearning.example.mockup;

/**
 * @author Lukas Reithmeier
 * @since 1.0
 */
public class Node {
    private final String name;

    private boolean special = false;

    private double beeLine;

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, double beeLine) {
        this.name = name;
        this.beeLine = beeLine;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    @Override
    public String toString() {
        return name;
    }

    public Double getBeeLine() {
        return beeLine;
    }
}
