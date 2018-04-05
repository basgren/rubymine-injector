package net.bitpot.injector.config;

/**
 * Interface that should be provided by classed that can assign some values from another objects.
 * The main reason of implement of this interface is to make deep copies of object, and then if
 * necessary to get all properties from modified copy back. Useful for use in Settings dialog.
 */
public interface Assignable<T>
{
    /**
     * Method should assign necessary structure  from source object.
     * @param source Source object which properties should be copied.
     */
    void assign(T source);
}
