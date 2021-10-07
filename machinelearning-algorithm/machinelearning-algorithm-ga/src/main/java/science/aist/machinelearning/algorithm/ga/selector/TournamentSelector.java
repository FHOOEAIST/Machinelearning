package science.aist.machinelearning.algorithm.ga.selector;

import org.apache.log4j.Logger;
import science.aist.machinelearning.algorithm.ga.Selector;
import science.aist.machinelearning.core.Configurable;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The tournament selector "holds a tournament" by randomly selecting solutions from the population. The best mapping
 * out of those in the tournament is selected.
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class TournamentSelector<GT, PT> implements Selector<GT, PT>, Configurable {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(TournamentSelector.class);
    private final Random r = new Random();
    private int tournamentSize = 2;

    @Override
    public Solution<GT, PT> select(List<Solution<GT, PT>> population) {

        if (population == null || population.size() == 0) {
            return null;
        }

        logger.trace("Starting tournament selection");
        Solution<GT, PT> best = population.get(r.nextInt(population.size()));

        for (int i = 0; i < getTournamentSize() - 1; i++) {
            Solution<GT, PT> solution = population.get(r.nextInt(population.size()));
            if (best.getQuality() > solution.getQuality()) {
                best = solution;
            }
        }

        logger.trace("Finished tournament selection");
        return best;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }


    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();
        options.put("tournamentSize", new Descriptor<>(tournamentSize));
        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("tournamentSize")) {
                setTournamentSize((Integer) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
