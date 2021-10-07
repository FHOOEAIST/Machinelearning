package science.aist.machinelearning.algorithm.gp;

import science.aist.machinelearning.core.options.Descriptor;

import java.util.Map;

/**
 * Basic abstract class for the creation of GP-GraphNodes that can cache their values. If values get cached, will only
 * calculate the cachedValue once, later executes will then return the already calculated cachedValue.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class CacheableGPGraphNode<T> implements GPGraphNode<T> {

    /**
     * Activates caching for this node. true = execute will calculate cachedValue once, returns same cachedValue over
     * multiple execute calls afterwards false = execute will newly calculate the cachedValue
     */
    private boolean cached = false;

    private T cachedValue = null;

    public T execute() {
        //if caching is disabled, just calculate and return the new cachedValue
        if (!cached) {
            return calculateValue();
        }

        //if caching is enabled, check if we already cached before and return or calculate the result
        if (cachedValue == null) {
            cachedValue = calculateValue();
        }
        return cachedValue;
    }

    @Override
    public boolean setOptions(Map<String, Descriptor> options) {
        for (Map.Entry<String, Descriptor> entry : options.entrySet()) {
            //check if we can successfully set the option
            if (!setOption(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculate the cachedValue for the cache.
     *
     * @return calculated cachedValue that will be saved in the cache.
     */
    public abstract T calculateValue();

    public abstract T simpleReturnType();

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public T getCachedValue() {
        return cachedValue;
    }

    public void setCachedValue(T cachedValue) {
        this.cachedValue = cachedValue;
    }
}
