package edu.cmu.ri.createlab.terk.robot.finch.commands.hid;

import edu.cmu.ri.createlab.terk.robot.finch.commands.GetObstacleSensorCommandStrategyHelper;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDReturnValueCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetObstacleSensorCommandStrategy extends CreateLabHIDReturnValueCommandStrategy<boolean[]>
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
   public boolean[] convertResponse(final HIDCommandResponse result)
      {
      return helper.convertResponse(result);
      }
   }