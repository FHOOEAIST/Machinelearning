package science.aist.machinelearning.core.util;

import java.util.Random;

/**
 * This random util should be used in every class needing to create random values, as we can force the seed centrally.
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class RandomUtil {

    public static Random random = new Random();

}
