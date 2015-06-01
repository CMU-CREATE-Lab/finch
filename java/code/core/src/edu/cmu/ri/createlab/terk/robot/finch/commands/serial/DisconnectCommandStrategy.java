package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.commands.DisconnectCommandStrategyHelper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DisconnectCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final DisconnectCommandStrategyHelper helper = new DisconnectCommandStrategyHelper();

   @Override
   public byte[] getCommand()
      {
      return helper.getCommand();
      }
   }
