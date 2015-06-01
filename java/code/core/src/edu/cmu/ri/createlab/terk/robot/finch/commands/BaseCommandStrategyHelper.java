package edu.cmu.ri.createlab.terk.robot.finch.commands;

/**
 * <p>
 * <code>BaseCommandStrategyHelper</code> provides common functionality for all command strategy helper classes.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseCommandStrategyHelper
   {
   /* Convert the given index to an ASCII character (note that this implies that the greatest index possible is 9). */
   protected final byte convertDeviceIndex(final int index)
      {
      return (byte)String.valueOf(index).charAt(0);
      }
   }
