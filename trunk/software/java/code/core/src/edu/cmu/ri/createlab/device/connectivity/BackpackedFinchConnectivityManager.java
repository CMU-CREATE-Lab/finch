package edu.cmu.ri.createlab.device.connectivity;

import edu.cmu.ri.createlab.terk.robot.finch.BackpackedFinchController;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BackpackedFinchConnectivityManager extends FinchConnectivityManager
   {
   private final String serialPortName;

   public BackpackedFinchConnectivityManager(final String serialPortName)
      {
      this.serialPortName = serialPortName;
      }

   @Override
   protected FinchController scanForDeviceAndCreateProxy()
      {
      return BackpackedFinchController.create(serialPortName);
      }
   }
