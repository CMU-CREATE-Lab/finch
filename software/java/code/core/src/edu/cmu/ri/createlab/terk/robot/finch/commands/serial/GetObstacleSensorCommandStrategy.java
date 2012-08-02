package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.terk.robot.finch.commands.GetObstacleSensorCommandStrategyHelper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetObstacleSensorCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<boolean[]>
   {
   private final GetObstacleSensorCommandStrategyHelper helper = new GetObstacleSensorCommandStrategyHelper();

   @Override
   protected int getSizeOfExpectedResponse()
      {
      return helper.getSizeOfExpectedResponse();
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }

   @Override
   public boolean[] convertResponse(final SerialDeviceCommandResponse result)
      {
      return helper.convertResponse(result);
      }
   }