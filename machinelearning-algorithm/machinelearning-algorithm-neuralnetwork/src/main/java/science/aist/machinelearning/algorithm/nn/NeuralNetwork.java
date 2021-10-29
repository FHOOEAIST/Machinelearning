/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.nn;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import science.aist.machinelearning.core.*;
import science.aist.machinelearning.core.options.Descriptor;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * Neuronal Network based on a MultiLayerNetwork from deeplearning4j Documentation:
 * https://deeplearning4j.org/documentation APIDoc: https://deeplearning4j.org/doc/ Examples:
 * https://github.com/deeplearning4j/dl4j-examples/ Dependencies: https://deeplearning4j.org/quickstart#using-dl4j-in-your-own-projects-configuring-the-pomxml-file
 *
 * @author Andreas Pointner
 * @since 1.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
// else there are many warnings, because not every setter is used in the test methods.
public class NeuralNetwork<GT, PT> extends AbstractAlgorithm<GT, PT> {
    /**
     * Seed to make sure, that the neural network always produces the same output (important for testing!) Challenge:
     * The seed is a special number, can you find out, what it stands for? ;-)
     */
    private static final long SEED = 65738384;
    /**
     * @see NeuralNetConfiguration.Builder#iterations(int)
     */
    private static final int iterations = 100;
    /**
     * @see NeuralNetConfiguration.Builder#learningRate(double)
     */
    private static final double learningRate = 0.03;
    /**
     * Logger
     */
    private final transient Logger log = LoggerFactory.getLogger(getClass());
    /**
     * Map to store the specific options.
     */
    private final Map<String, Descriptor> specificOptions;
    /**
     * How often the neural network is trained with the given training data
     */
    private int epochs = 10;
    /**
     * Internal value to store if the network has already been trained
     */
    private boolean trained = false;
    /**
     * Internal value to store if the network is already initialized
     */
    private boolean init = false;
    /**
     * The neural network itself
     */
    private MultiLayerNetwork neuralNetwork = null;
    /**
     * The Neural Network Configuration Builder
     */
    private NeuralNetConfiguration.Builder builder;
    /**
     * the size of the hidden layers. the number in the array represent the neurons for the specific layer. the size of
     * the array represents the number of hidden layers.
     */
    private int[] hiddenLayers;
    /**
     * @see OutputLayer.Builder#Builder(LossFunctions.LossFunction)
     */
    private LossFunctions.LossFunction outputLayerLossFunction = LossFunctions.LossFunction.MSE;
    /**
     * Which actions type is used for which layer. this has hiddenLayers.size + 1 because also the activation for the
     * output layer is stored here. Default is Sigmoid activation for hidden layers and Identity for output layer
     *
     * @see DenseLayer.Builder#activation(Activation)
     */
    private Activation[] activationForLayers;
    /**
     * This function is called before the configuration will be built. It give the ability to configure every
     * parameter.
     */
    private Consumer<NeuralNetConfiguration.Builder> builderConsumer;
    /**
     * This function is called before the neural network will be built. It give the ability to configure every
     * parameter.
     */
    private Consumer<NeuralNetConfiguration.ListBuilder> listBuilderConsumer;
    /**
     * This function is called before the neural network will be trained. It give the ability to configure every
     * parameter.
     */
    private Consumer<MultiLayerNetwork> multiLayerNetworkConsumer;
    /**
     * This function converts a PT to a double value
     */
    private ToDoubleFunction<PT> problemToDoubleTransformer;
    /**
     * This function converts a GT to a double value
     */
    private ToDoubleFunction<GT> solutionToDoubleTransformer;
    /**
     * This function converts a double value to a GT
     */
    private Function<Double, GT> doubleToSolutionTransformer;

    /**
     * The update Frequency of the visualization.
     */
    private int updateFrequency;

    /**
     * Default constructor where the number of hidden layers will be set.
     *
     * @param hiddenLayers number of hidden layers + their neuron count
     */
    public NeuralNetwork(int[] hiddenLayers) {
        this();
        if (hiddenLayers.length == 0) {
            throw new IllegalArgumentException("At least one hidden Layer is needed");
        }

        this.hiddenLayers = hiddenLayers;
        builder = new NeuralNetConfiguration.Builder();

        activationForLayers = new Activation[hiddenLayers.length + 1];
        for (int i = 0; i < activationForLayers.length - 1; i++) {
            activationForLayers[i] = Activation.SIGMOID;
        }
        activationForLayers[activationForLayers.length - 1] = Activation.IDENTITY;

        builder
                .seed(SEED)
                .iterations(iterations)
                .learningRate(learningRate);
    }

    /**
     * Empty constructor only used for loading
     */
    private NeuralNetwork() {
        specificOptions = new HashMap<>();
    }

    /**
     * @param path                      Path to a saved network
     * @param multiLayerNetworkConsumer callback to configure specific network options after loading
     * @return the network
     */
    @SuppressWarnings("unchecked") // this function makes some unchecked casts.
    public static <GT, PT> NeuralNetwork<GT, PT> load(String path, Consumer<MultiLayerNetwork> multiLayerNetworkConsumer) {
        URI uri = URI.create("jar:" + Paths.get(path).toUri());
        try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>());
             ObjectInputStream reader = new ObjectInputStream(Files.newInputStream(fs.getPath("neuralNetwork.obj")))) {
            NeuralNetwork<GT, PT> nn = new NeuralNetwork<>();
            nn.setProblemToDoubleTransformer((ToDoubleFunction<PT>) reader.readObject());
            nn.setSolutionToDoubleTransformer((ToDoubleFunction<GT>) reader.readObject());
            nn.setDoubleToSolutionTransformer((Function<Double, GT>) reader.readObject());
            nn.setEpochs(reader.readInt());
            nn.neuralNetwork = ModelSerializer.restoreMultiLayerNetwork(path);
            nn.setTrained();
            multiLayerNetworkConsumer.accept(nn.neuralNetwork);
            return nn;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the neuronal network with a given input stream
     *
     * @param is input stream to the saved network
     * @return the network
     */
    public static <GT, PT> NeuralNetwork<GT, PT> load(InputStream is) {
        return load(is, (n) -> {
        });
    }

    /**
     * Loads the neuronal network with a given input stream
     *
     * @param is                        input stream to the saved network
     * @param multiLayerNetworkConsumer callback to configure specific network options after loading
     * @return the network
     */
    public static <GT, PT> NeuralNetwork<GT, PT> load(InputStream is, Consumer<MultiLayerNetwork> multiLayerNetworkConsumer) {
        File uniqueFile = null;
        try {
            uniqueFile = File.createTempFile(UUID.randomUUID().toString(), ".zip");
            FileUtils.copyInputStreamToFile(is, uniqueFile);
            return load(uniqueFile.getPath(), multiLayerNetworkConsumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (uniqueFile != null && !uniqueFile.delete()) {
                LoggerFactory.getLogger(NeuralNetwork.class).warn("Could not delete temp file: " + uniqueFile.getAbsolutePath());
            }
        }
    }

    /**
     * @param path Path to a saved network
     * @return the network
     */
    public static <GT, PT> NeuralNetwork<GT, PT> load(String path) {
        return load(path, (n) -> {
        });
    }

    /**
     * This functions trains the neuronal network Make sure. that the input.get(i) problem matches to the output.get(i)
     * solution Make sure if you call this function multiple time to train the network, that the Problem and Solution
     * size is always the same.
     *
     * @param input  input values
     * @param output solution to the given input
     */
    public NeuralNetwork<GT, PT> train(List<Problem<PT>> input, List<Solution<GT, PT>> output) {
        // Check Sizes
        if (input.isEmpty() || output.isEmpty() || input.size() != output.size()) {
            throw new IllegalArgumentException("Input and Output must have the same size and non empty!");
        }

        init(input, output);

        // Create a iterator of the test data
        DataSet dataSet = createDataSet(input, output);

        // Train the neural network
        for (int i = 0; i < epochs; i++) {
            neuralNetwork.fit(dataSet);
        }

        setTrained();
        return this;
    }

    /**
     * Initialize the network
     *
     * @param input  input values
     * @param output solution to the given input
     */
    private void init(List<Problem<PT>> input, List<Solution<GT, PT>> output) {
        // Do not init it again if it has already been initialized
        if (isInit()) return;

        // Get the input / output neuron connections
        int inputSize = input.get(0).getProblemGenes().size();
        int outputSize = output.get(0).getSolutionGenes().size();

        // Create a possibility to set every property.
        if (builderConsumer != null)
            builderConsumer.accept(builder);

        // Create the list builder
        NeuralNetConfiguration.ListBuilder lb = builder.list();

        // Add the hidden layers.
        for (int i = 0; i < hiddenLayers.length; i++) {
            int nIn = i == 0 ? inputSize : hiddenLayers[i - 1];
            lb.layer(i, new DenseLayer.Builder()
                    .nIn(nIn)
                    .nOut(hiddenLayers[i])
                    .activation(activationForLayers[i])
                    .build());
        }

        // Add the output layer
        lb.layer(hiddenLayers.length, new OutputLayer.Builder(outputLayerLossFunction)
                .activation(activationForLayers[activationForLayers.length - 1])
                .nIn(hiddenLayers[hiddenLayers.length - 1])
                .nOut(outputSize).build());

        // Set some default params.
        lb.pretrain(false)
                .backprop(true);

        // Create a possibility to set every property.
        if (listBuilderConsumer != null)
            listBuilderConsumer.accept(lb);

        // Create and init the neural network
        neuralNetwork = new MultiLayerNetwork(lb.build());
        neuralNetwork.init();

        // Create a possibility to set every property.
        if (multiLayerNetworkConsumer != null)
            multiLayerNetworkConsumer.accept(neuralNetwork);

        setInit();
    }

    /**
     * Internal function to create the data set to train the network
     *
     * @param input  the input problems
     * @param output the output solutions
     * @return a data set representing the trainee data
     */
    private DataSet createDataSet(List<Problem<PT>> input, List<Solution<GT, PT>> output) {
        int taineeDataSize = input.size();
        int inputSize = input.get(0).getProblemGenes().size();
        int outputSize = output.get(0).getSolutionGenes().size();

        // Create empty Arrays
        INDArray inputData = Nd4j.zeros(taineeDataSize, inputSize);
        INDArray outputData = Nd4j.zeros(taineeDataSize, outputSize);

        // Iterate over the Trainee Data
        for (int i = 0; i < taineeDataSize; i++) {
            // Convert input data
            List<ProblemGene<PT>> problemGenes = input.get(i).getProblemGenes();
            for (int j = 0; j < inputSize; j++) {
                PT gene = problemGenes.get(j).getGene();
                double value = problemToDoubleTransformer.applyAsDouble(gene);
                inputData.putScalar(new int[]{i, j}, value);
            }

            // Convert output data
            List<SolutionGene<GT, PT>> solutionGenes = output.get(i).getSolutionGenes();
            for (int j = 0; j < outputSize; j++) {
                GT gene = solutionGenes.get(j).getGene();
                double value = solutionToDoubleTransformer.applyAsDouble(gene);
                outputData.putScalar(new int[]{i, j}, value);
            }
        }

        // Create the Data set
        return new DataSet(inputData, outputData);
    }

    /**
     * @return map of specific options
     */
    @Override
    protected Map<String, Descriptor> getSpecificOptions() {
        return specificOptions;
    }

    /**
     * Sets a specific options. Therefore it calls the setter of the given property.
     *
     * @param name       name of the option
     * @param descriptor descriptor to set
     * @return false if the type does not match with the property type or if the property does not exist.
     */
    @Override
    protected boolean setSpecificOption(String name, Descriptor descriptor) {
        // Every specific option requires a setter method. So we can directly invoke this setter method.
        // Create the setter name
        String setterFunctionName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);

        // Get all methods of this class
        Method[] methods = getClass().getDeclaredMethods();
        // Search for the according setter method
        for (Method method : methods) {
            if (method.getName().equals(setterFunctionName)) {
                try {
                    // If the method was found invoke it
                    method.invoke(this, descriptor.getValue());
                    return true;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("Failed to call setter method", e);
                    return false;
                } catch (IllegalArgumentException e) {
                    log.error("Illegal Type for this option", e);
                    return false;
                }
            }
        }
        // Exiting the for loop means, that no setter function was found.
        log.error("Specific options does not exist");
        return false;
    }

    /**
     * sets value of field epochs How often the neural network is trained with the given training data
     *
     * @param epochs value of field epochs
     * @return this
     */
    public NeuralNetwork<GT, PT> setEpochs(int epochs) {
        this.epochs = epochs;
        specificOptions.put("epochs", new Descriptor<>(epochs));
        return this;
    }

    /**
     * sets value of field builderConsumer This function is called before the configuration will be built. It give the
     * ability to configure every parameter.
     *
     * @param builderConsumer value of field builderConsumer
     * @return this
     */
    public NeuralNetwork<GT, PT> setBuilderConsumer(Consumer<NeuralNetConfiguration.Builder> builderConsumer) {
        this.builderConsumer = builderConsumer;
        specificOptions.put("builderConsumer", new Descriptor<>(builderConsumer));
        return this;
    }

    /**
     * sets value of field listBuilderConsumer This function is called before the neural network will be built. It give
     * the ability to configure every parameter.
     *
     * @param listBuilderConsumer value of field listBuilderConsumer
     * @return this
     */
    public NeuralNetwork<GT, PT> setListBuilderConsumer(Consumer<NeuralNetConfiguration.ListBuilder> listBuilderConsumer) {
        this.listBuilderConsumer = listBuilderConsumer;
        specificOptions.put("listBuilderConsumer", new Descriptor<>(listBuilderConsumer));
        return this;
    }

    /**
     * sets value of field multiLayerNetworkConsumer This function is called before the neural network will be trained.
     * It give the ability to configure every parameter.
     *
     * @param multiLayerNetworkConsumer value of field multiLayerNetworkConsumer
     * @return this
     */
    public NeuralNetwork<GT, PT> setMultiLayerNetworkConsumer(Consumer<MultiLayerNetwork> multiLayerNetworkConsumer) {
        this.multiLayerNetworkConsumer = multiLayerNetworkConsumer;
        specificOptions.put("multiLayerNetworkConsumer", new Descriptor<>(multiLayerNetworkConsumer));
        return this;
    }

    /**
     * sets value of field problemTransformer This function converts a PT to a double value
     *
     * @param problemTransformer value of field problemTransformer
     * @return this
     */
    public NeuralNetwork<GT, PT> setProblemToDoubleTransformer(ToDoubleFunction<PT> problemTransformer) {
        this.problemToDoubleTransformer = problemTransformer;
        specificOptions.put("problemTransformer", new Descriptor<>(problemTransformer));
        return this;
    }

    /**
     * sets value of field solutionToDoubleTransformer This function converts a GT to a double value
     *
     * @param solutionToDoubleTransformer value of field solutionToDoubleTransformer
     * @return this
     */
    public NeuralNetwork<GT, PT> setSolutionToDoubleTransformer(ToDoubleFunction<GT> solutionToDoubleTransformer) {
        this.solutionToDoubleTransformer = solutionToDoubleTransformer;
        specificOptions.put("solutionToDoubleTransformer", new Descriptor<>(solutionToDoubleTransformer));
        return this;
    }

    /**
     * sets value of field doubleToSolutionTransformer This function converts a double value to a GT
     *
     * @param doubleToSolutionTransformer value of field doubleToSolutionTransformer
     * @return this
     */
    public NeuralNetwork<GT, PT> setDoubleToSolutionTransformer(Function<Double, GT> doubleToSolutionTransformer) {
        this.doubleToSolutionTransformer = doubleToSolutionTransformer;
        specificOptions.put("doubleToSolutionTransformer", new Descriptor<>(doubleToSolutionTransformer));
        return this;
    }

    /**
     * sets value of field outputLayerLossFunction
     *
     * @param outputLayerLossFunction value of field outputLayerLossFunction
     * @return this
     * @see OutputLayer.Builder#Builder(LossFunctions.LossFunction)
     */
    public NeuralNetwork<GT, PT> setOutputLayerLossFunction(LossFunctions.LossFunction outputLayerLossFunction) {
        this.outputLayerLossFunction = outputLayerLossFunction;
        specificOptions.put("outputLayerLossFunction", new Descriptor<>(outputLayerLossFunction));
        return this;
    }

    /**
     * @param iterations number of iterations
     * @return this
     * @see NeuralNetConfiguration.Builder#iterations(int)
     */
    public NeuralNetwork<GT, PT> setIterations(int iterations) {
        builder.iterations(iterations);
        specificOptions.put("iterations", new Descriptor<>(iterations));
        return this;
    }

    /**
     * @param algo optimization algorithm
     * @return this
     * @see NeuralNetConfiguration.Builder#setOptimizationAlgo(OptimizationAlgorithm)
     */
    public NeuralNetwork<GT, PT> setOptimizationAlgo(OptimizationAlgorithm algo) {
        builder.setOptimizationAlgo(algo);
        specificOptions.put("optimizationAlgo", new Descriptor<>(algo));
        return this;
    }

    /**
     * @param learningRate learning rate
     * @return this
     * @see NeuralNetConfiguration.Builder#setLearningRate(double)
     */
    public NeuralNetwork<GT, PT> setLearningRate(double learningRate) {
        builder.setLearningRate(learningRate);
        specificOptions.put("learningRate", new Descriptor<>(learningRate));
        return this;
    }

    /**
     * @param updater updater
     * @return this
     * @see NeuralNetConfiguration.Builder#updater(Updater)
     */
    public NeuralNetwork<GT, PT> setUpdater(Updater updater) {
        builder.updater(updater);
        specificOptions.put("updater", new Descriptor<>(updater));
        return this;
    }

    /**
     * @param weightInit weight init
     * @return this
     * @see NeuralNetConfiguration.Builder#setWeightInit(WeightInit)
     */
    public NeuralNetwork<GT, PT> setWeightInit(WeightInit weightInit) {
        builder.setWeightInit(weightInit);
        specificOptions.put("weightInit", new Descriptor<>(weightInit));
        return this;
    }

    /**
     * Which actions type is used for which layer. this has hiddenLayers.size + 1 because also the activation for the
     * output layer is stored here. Default is Sigmoid activation for hidden layers and Identity for output layer
     *
     * @param activations activations
     * @return this
     * @see DenseLayer.Builder#activation(Activation)
     */
    public NeuralNetwork<GT, PT> setActivation(Activation[] activations) {
        this.activationForLayers = activations;
        specificOptions.put("activation", new Descriptor<>(activations));
        return this;
    }

    /**
     * Which actions type is used for which layer. this has hiddenLayers.size + 1 because also the activation for the
     * output layer is stored here. Default is Sigmoid activation for hidden layers and Identity for output layer
     *
     * @param layerIdx   index of the layer. 0 .. first hiddenLayer. SizeOfHiddenLayers-1 .. last hidden layer;
     *                   sizeOfHiddenLayers .. output layer
     * @param activation activations
     * @return this
     * @see DenseLayer.Builder#activation(Activation)
     */
    public NeuralNetwork<GT, PT> setActivation(int layerIdx, Activation activation) {
        this.activationForLayers[layerIdx] = activation;
        specificOptions.put("activation" + layerIdx, new Descriptor<>(activation));
        return this;
    }

    /**
     * @param path Where to save the network. Note: the file is in .zip format - can be opened externally
     */
    public void save(String path) {
        if (!isInit()) {
            throw new IllegalStateException("Network is created after initialization, So before saving at least one training has to be executed.");
        }
        try {
            ModelSerializer.writeModel(neuralNetwork, path, true /*Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future*/);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        // The network also needs the three transformers to work properly. Therefore also store them inside the zip.
        URI uri = URI.create("jar:" + Paths.get(path).toUri());
        try (FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>());
             ObjectOutputStream writer = new ObjectOutputStream(Files.newOutputStream(fs.getPath("neuralNetwork.obj"), StandardOpenOption.CREATE))) {
            writer.writeObject(problemToDoubleTransformer);
            writer.writeObject(solutionToDoubleTransformer);
            writer.writeObject(doubleToSolutionTransformer);
            writer.writeInt(epochs);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            if (!(new File(path)).delete()) {
                log.warn("Could not delete file on error.");
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Solves the given problem
     *
     * @param problem to be solved
     * @return a solution for the given problem
     */
    @Override
    public Solution<GT, PT> solve(Problem<PT> problem) {
        if (!isTrained()) {
            throw new IllegalStateException("Network has first to be trained via train() function");
        }

        // Convert input data
        double[] problemData = problem.getProblemGenes().stream()
                .map(Gene::getGene)
                .mapToDouble(problemToDoubleTransformer)
                .toArray();

        INDArray inputArr = Nd4j.create(problemData);

        // calculate output data
        INDArray out = neuralNetwork.output(inputArr, false);

        // Create solution
        Solution<GT, PT> solution = new Solution<>();
        List<SolutionGene<GT, PT>> solutionGenes = new ArrayList<>();
        for (int i = 0; i < out.length(); i++) {
            double val = out.getDouble(i);
            SolutionGene<GT, PT> solutionGene = new SolutionGene<>();
            GT gene = doubleToSolutionTransformer.apply(val);
            solutionGene.setGene(gene);
            solutionGenes.add(solutionGene);
        }
        solution.setSolutionGenes(solutionGenes);

        return solution;
    }

    /**
     * @return if the network is already trained
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isTrained() {
        return trained;
    }

    /**
     * Just internally used to set trained to true.
     */
    private void setTrained() {
        setInit();
        this.trained = true;
    }

    /**
     * @return if the network is already initialized
     */
    private boolean isInit() {
        return init;
    }

    /**
     * Just internally used to set init to true
     */
    private void setInit() {
        this.init = true;
    }

    /**
     * At the moment there is on possibility to start with a best solution. this function will only call solve(problem)
     *
     * @param problem      problem to be solved
     * @param bestSolution start search with this mapping
     * @return solve(problem)
     */
    @Override
    public Solution<GT, PT> solve(Problem<PT> problem, Solution<GT, PT> bestSolution) {
        return solve(problem);
    }
}
