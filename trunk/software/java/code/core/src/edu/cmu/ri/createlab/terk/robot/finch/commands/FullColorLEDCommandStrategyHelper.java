package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FullColorLEDCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to turn on a full-color LED. */
   private static final byte COMMAND_PREFIX = 'O';

   private final byte[] command;

   private final int minIntensity;
   private final int maxIntensity;

   public FullColorLEDCommandStrategyHelper(final int red, final int green, final int blue, final FinchProperties finchProperties)
      {
      this.minIntensity = finchProperties.getFullColorLedDeviceMinIntensity();
      this.maxIntensity = finchProperties.getFullColorLedDeviceMaxIntensity();

      // construct the command
      this.command = new byte[]{COMMAND_PREFIX,
                                cleanIntensity(red),
                                cleanIntensity(green),
                                cleanIntensity(blue)};
      }

   private byte cleanIntensity(final int rawIntensity)
      {
      // clamp the intensity to the allowed range
      int intensity = Math.min(Math.max(rawIntensity, minIntensity), maxIntensity);

      // finally, convert it to an unsigned byte
      return ByteUtils.intToUnsignedByte(intensity);
      }

   public byte[] getCommand()
      {
      return command.clone();
      }
   }
