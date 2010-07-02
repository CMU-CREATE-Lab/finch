package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FullColorLEDCommandStrategy extends CreateLabHIDCommandStrategy
   {
   /** The command character used to turn on a full-color LED. */
   private static final byte COMMAND_PREFIX = 'O';

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 0;

   private final byte[] command;

   public FullColorLEDCommandStrategy(final int red, final int green, final int blue)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                ByteUtils.intToUnsignedByte(red),
                                ByteUtils.intToUnsignedByte(green),
                                ByteUtils.intToUnsignedByte(blue)};
      }

   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }