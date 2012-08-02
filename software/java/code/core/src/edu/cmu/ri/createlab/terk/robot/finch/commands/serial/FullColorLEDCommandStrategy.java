package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.terk.robot.finch.commands.FullColorLEDCommandStrategyHelper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FullColorLEDCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final FullColorLEDCommandStrategyHelper helper;

   public FullColorLEDCommandStrategy(final int red, final int green, final int blue, final FinchProperties finchProperties)
      {
      helper = new FullColorLEDCommandStrategyHelper(red, green, blue, finchProperties);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }