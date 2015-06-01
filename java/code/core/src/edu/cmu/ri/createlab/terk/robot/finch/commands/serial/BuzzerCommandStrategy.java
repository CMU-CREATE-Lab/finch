package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.terk.robot.finch.commands.BuzzerCommandStrategyHelper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BuzzerCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final BuzzerCommandStrategyHelper helper;

   public BuzzerCommandStrategy(final int frequency, final int durationInMilliseconds, final FinchProperties finchProperties)
      {
      helper = new BuzzerCommandStrategyHelper(frequency, durationInMilliseconds, finchProperties);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }