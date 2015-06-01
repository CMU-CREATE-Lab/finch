package edu.cmu.ri.createlab.terk.robot.finch.commands;

import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetAccelerometerCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to request the value of the finch's accelerometer. */
   private static final byte[] COMMAND = {'A'};

   public GetAccelerometerCommandStrategyHelper()
      {
      }

   public byte[] getCommand()
      {
      return COMMAND.clone();
      }

   public AccelerometerState convertResponse(final byte x,
                                             final byte y,
                                             final byte z,
                                             final byte tapShake)
      {
      final boolean wasTapped = (tapShake & 32) == 32;
      final boolean wasShaken = (tapShake & 128) == 128;

      return new AccelerometerState(ByteUtils.unsignedByteToInt(x),
                                    ByteUtils.unsignedByteToInt(y),
                                    ByteUtils.unsignedByteToInt(z),
                                    wasShaken,
                                    wasTapped);
      }
   }
