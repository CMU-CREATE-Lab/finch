package edu.cmu.ri.createlab.terk.robot.finch;

import edu.cmu.ri.createlab.device.connectivity.BaseCreateLabDeviceConnectivityManager;
import edu.cmu.ri.createlab.device.connectivity.CreateLabDeviceConnectionEventListener;
import edu.cmu.ri.createlab.device.connectivity.CreateLabDeviceConnectionState;
import edu.cmu.ri.createlab.terk.application.ConnectionStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.services.DefaultFinchServiceFactoryHelper;
import edu.cmu.ri.createlab.terk.robot.finch.services.FinchServiceManager;
import edu.cmu.ri.createlab.terk.services.ServiceManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseFinchConnectionStrategy extends ConnectionStrategy
   {
   private static final Logger LOG = Logger.getLogger(BaseFinchConnectionStrategy.class);

   private ServiceManager serviceManager = null;
   private final BaseCreateLabDeviceConnectivityManager<FinchController> finchConnectivityManager;

   BaseFinchConnectionStrategy(final BaseCreateLabDeviceConnectivityManager<FinchController> finchConnectivityManager)
      {
      this.finchConnectivityManager = finchConnectivityManager;

      finchConnectivityManager.addConnectionEventListener(
            new CreateLabDeviceConnectionEventListener()
            {
            public void handleConnectionStateChange(final CreateLabDeviceConnectionState oldState, final CreateLabDeviceConnectionState newState, final String portName)
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("BaseFinchConnectionStrategy.handleConnectionStateChange(): OLD [" + oldState.getStateName() + "]  NEW [" + newState.getStateName() + "]  port [" + portName + "]");
                  }
               switch (newState)
                  {
                  case CONNECTED:
                     serviceManager = new FinchServiceManager(finchConnectivityManager.getCreateLabDeviceProxy(), DefaultFinchServiceFactoryHelper.getInstance());
                     notifyListenersOfConnectionEvent();
                     break;
                  case DISCONNECTED:
                     serviceManager = null;
                     notifyListenersOfDisconnectionEvent();
                     break;
                  case SCANNING:
                     notifyListenersOfAttemptingConnectionEvent();
                     break;
                  default:
                     if (LOG.isEnabledFor(Level.ERROR))
                        {
                        LOG.error("Unexpected CreateLabDeviceConnectionState [" + newState + "]");
                        }
                  }
               }
            });
      }

   public final boolean isConnected()
      {
      return CreateLabDeviceConnectionState.CONNECTED.equals(finchConnectivityManager.getConnectionState());
      }

   public final boolean isConnecting()
      {
      return CreateLabDeviceConnectionState.SCANNING.equals(finchConnectivityManager.getConnectionState());
      }

   public final ServiceManager getServiceManager()
      {
      return serviceManager;
      }

   public final void connect()
      {
      finchConnectivityManager.scanAndConnect();
      }

   public final void cancelConnect()
      {
      finchConnectivityManager.cancelConnecting();
      }

   public final void disconnect()
      {
      LOG.debug("BaseFinchConnectionStrategy.disconnect()");
      notifyListenersOfAttemptingDisconnectionEvent();
      finchConnectivityManager.disconnect();
      }

   public final void prepareForShutdown()
      {
      LOG.debug("BaseFinchConnectionStrategy.prepareForShutdown()");
      disconnect();
      }
   }
