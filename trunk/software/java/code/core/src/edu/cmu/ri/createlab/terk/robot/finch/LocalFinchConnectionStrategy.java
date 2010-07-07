package edu.cmu.ri.createlab.terk.robot.finch;

import edu.cmu.ri.createlab.device.connectivity.CreateLabDeviceConnectionEventListener;
import edu.cmu.ri.createlab.device.connectivity.CreateLabDeviceConnectionState;
import edu.cmu.ri.createlab.device.connectivity.FinchConnectivityManager;
import edu.cmu.ri.createlab.terk.application.ConnectionStrategy;
import edu.cmu.ri.createlab.terk.robot.finch.services.FinchServiceManager;
import edu.cmu.ri.createlab.terk.services.ServiceManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LocalFinchConnectionStrategy extends ConnectionStrategy
   {
   private static final Log LOG = LogFactory.getLog(LocalFinchConnectionStrategy.class);

   private ServiceManager serviceManager = null;
   private final FinchConnectivityManager finchConnectivityManager = new FinchConnectivityManager();

   public LocalFinchConnectionStrategy()
      {
      finchConnectivityManager.addConnectionEventListener(
            new CreateLabDeviceConnectionEventListener()
            {
            public void handleConnectionStateChange(final CreateLabDeviceConnectionState oldState, final CreateLabDeviceConnectionState newState, final String portName)
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("LocalFinchConnectionStrategy.handleConnectionStateChange(): OLD [" + oldState.getStateName() + "]  NEW [" + newState.getStateName() + "]");
                  }
               switch (newState)
                  {
                  case CONNECTED:
                     serviceManager = new FinchServiceManager((FinchController)finchConnectivityManager.getCreateLabDeviceProxy());
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
                     if (LOG.isErrorEnabled())
                        {
                        LOG.error("Unexpected CreateLabDeviceConnectionState [" + newState + "]");
                        }
                  }
               }
            });
      }

   public boolean isConnected()
      {
      return CreateLabDeviceConnectionState.CONNECTED.equals(finchConnectivityManager.getConnectionState());
      }

   public boolean isConnecting()
      {
      return CreateLabDeviceConnectionState.SCANNING.equals(finchConnectivityManager.getConnectionState());
      }

   public ServiceManager getServiceManager()
      {
      return serviceManager;
      }

   public void connect()
      {
      finchConnectivityManager.scanAndConnect();
      }

   public void cancelConnect()
      {
      finchConnectivityManager.cancelScanning();
      }

   public void disconnect()
      {
      LOG.debug("LocalFinchConnectionStrategy.disconnect()");
      notifyListenersOfAttemptingDisconnectionEvent();
      finchConnectivityManager.disconnect();
      }

   public void prepareForShutdown()
      {
      LOG.debug("LocalFinchConnectionStrategy.prepareForShutdown()");
      disconnect();
      }
   }
