package edu.cmu.ri.createlab.terk.robot.finch.commands.hid;

import edu.cmu.ri.createlab.terk.robot.finch.commands.GetPhotoresistorCommandStrategyHelper;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDReturnValueCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetPhotoresistorCommandStrategy extends CreateLabHIDReturnValueCommandStrategy<int[]>
   {
   private final GetPhotoresistorCommandStrategyHelper helper = new GetPhotoresistorCommandStrategyHelper();

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
   public int[] convertResponse(final HIDCommandResponse response)
      {
      return helper.convertResponse(response);
      }
   }