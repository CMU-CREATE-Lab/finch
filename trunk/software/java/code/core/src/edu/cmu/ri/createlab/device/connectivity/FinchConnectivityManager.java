package edu.cmu.ri.createlab.device.connectivity;

import edu.cmu.ri.createlab.device.CreateLabDeviceProxy;
import edu.cmu.ri.createlab.terk.robot.finch.DefaultFinchController;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FinchConnectivityManager extends BaseCreateLabDeviceConnectivityManager
   {
   @Override
   protected CreateLabDeviceProxy scanForDeviceAndCreateProxy()
      {
      return DefaultFinchController.create();
      }
   }
