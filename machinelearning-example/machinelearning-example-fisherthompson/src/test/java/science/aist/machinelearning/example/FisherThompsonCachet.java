package science.aist.machinelearning.example;

import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.fitness.Cachet;
import science.aist.machinelearning.core.fitness.CachetEvaluator;

import java.util.*;

/**
 * Cachet that checks the quality depending on a specific array. The specific array in this cachet implements the
 * fisher-thompson problem.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class FisherThompsonCachet implements CachetEvaluator<List<Integer>, Integer> {
    public static int runs = 0;
    /**
     * Times each machine needs for a project. Data taken from the table above, but sorted by machineNr.
     */
    private int[][] times;

    @Override
    public double evaluateQuality(Solution<List<Integer>, Integer> solution) {

        runs = runs + 1;

        double quality = 0; //equals makespan time for the production

        int jobsToBeDone = times.length;

        Map<Integer, Collection<Integer>> jobsToFinishedMachines = new HashMap<>();
        Map<Integer, Integer> machineTimeRequired = new HashMap<>();
        Map<Integer, Integer> jobTimeRequired = new HashMap<>();

        for (int i = 0; i < times.length; i++) {
            jobsToFinishedMachines.put(i, new ArrayList<>());
            machineTimeRequired.put(i, 0);
            jobTimeRequired.put(i, 0);
        }

        for (int i = 0; i < times[0].length; i++) {
            machineTimeRequired.put(i, 0);
        }

        while (jobsToBeDone > 0) {

            jobsToBeDone = times.length;

            //reduce time for the jobs
            for (int i = 0; i < times.length; i++) {
                jobTimeRequired.put(i, jobTimeRequired.get(i) - 1);
            }
            //reduce time for the machines
            for (int i = 0; i < times[0].length; i++) {
                machineTimeRequired.put(i, machineTimeRequired.get(i) - 1);
            }

            int currentJob = 0;
            //Look through the settings of each job
            for (SolutionGene<List<Integer>, Integer> gene : solution.getSolutionGenes()) {
                //check if we're still working in this job or should look at the next machine
                if (jobTimeRequired.get(currentJob) <= 0) {
                    //Reduce required jobs if all machines have worked for this job
                    if (jobsToFinishedMachines.get(currentJob).size() == times[0].length) {
                        jobsToBeDone--;
                    }
                    //Otherwise calculate next machine in line for this job
                    else {
                        //look through the single machines of the job
                        for (Integer machine : gene.getGene()) {
                            //if we need this machine but its currently working, wait and don't look ahead in the job
                            if (!jobsToFinishedMachines.get(currentJob).contains(machine)) {
                                //if its not occupied, use it for the current task
                                if (machineTimeRequired.get(machine) <= 0) {
                                    jobsToFinishedMachines.get(currentJob).add(machine);
                                    machineTimeRequired.put(machine, times[currentJob][machine]);
                                    jobTimeRequired.put(currentJob, times[currentJob][machine]);
                                    //System.out.println("Time: " + quality + ", job " + currentJob + " starts machine " + machine);
                                }
                                break;
                            }
                        }
                    }
                }
                currentJob++;
            }
            quality++;
        }
        solution.getCachets().add(new Cachet(quality, "FisherThompsonCachet"));

        return quality;
    }

    @Override
    public String getName() {
        return "FisherThompsonCachet";
    }

    public void setTimes(int[][] times) {
        this.times = times;
    }
}
