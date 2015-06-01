package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.EmergencyStopCommandStrategyHelper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class EmergencyStopCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final EmergencyStopCommandStrategyHelper helper = new EmergencyStopCommandStrategyHelper();

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }