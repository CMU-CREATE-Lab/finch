package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResult;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class ReturnValueCommandStrategy<T> extends CreateLabHIDCommandStrategy
   {
   public abstract T convertResult(final HIDCommandResult result);
   }
