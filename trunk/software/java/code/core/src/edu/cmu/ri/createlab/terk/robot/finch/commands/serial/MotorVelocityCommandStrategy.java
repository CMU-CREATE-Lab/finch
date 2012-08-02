package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.terk.robot.finch.commands.MotorVelocityCommandStrategyHelper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MotorVelocityCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final MotorVelocityCommandStrategyHelper helper;

   public MotorVelocityCommandStrategy(final int leftVelocity, final int rightVelocity, final FinchProperties finchProperties)
      {
      helper = new MotorVelocityCommandStrategyHelper(leftVelocity, rightVelocity, finchProperties);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }