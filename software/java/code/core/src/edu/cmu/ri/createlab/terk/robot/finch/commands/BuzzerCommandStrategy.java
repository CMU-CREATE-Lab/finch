package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BuzzerCommandStrategy extends CreateLabHIDCommandStrategy
   {
   /** The command character used to set the motor velocities. */
   private static final byte COMMAND_PREFIX = 'B';

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 0;

   private final byte[] command;

   public BuzzerCommandStrategy(final int frequency, final int durationInMilliseconds)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                getHighByteFromInt(durationInMilliseconds),
                                getLowByteFromInt(durationInMilliseconds),
                                getHighByteFromInt(frequency),
                                getLowByteFromInt(frequency)};
      }

   private byte getHighByteFromInt(final int val)
      {
      return (byte)((val << 16) >> 24);
      }

   private byte getLowByteFromInt(final int val)
      {
      return (byte)((val << 24) >> 24);
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