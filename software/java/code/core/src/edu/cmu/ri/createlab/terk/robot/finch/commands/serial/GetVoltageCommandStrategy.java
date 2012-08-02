package edu.cmu.ri.createlab.terk.robot.finch.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.terk.robot.finch.commands.GetVoltageCommandStrategyHelper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetVoltageCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<Integer>
   {
   private final GetVoltageCommandStrategyHelper helper = new GetVoltageCommandStrategyHelper();

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
   public Integer convertResponse(final SerialDeviceCommandResponse response)
      {
      return helper.convertResponse(response);
      }
   }
