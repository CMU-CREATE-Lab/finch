package edu.cmu.ri.createlab.device.connectivity;

import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.robot.finch.HIDFinchController;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDFinchConnectivityManager extends FinchConnectivityManager
   {
   @Override
   protected FinchController scanForDeviceAndCreateProxy()
      {
      return HIDFinchController.create();
      }
   }
