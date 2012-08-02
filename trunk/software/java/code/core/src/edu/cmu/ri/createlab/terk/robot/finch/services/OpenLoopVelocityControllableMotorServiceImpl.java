package edu.cmu.ri.createlab.terk.robot.finch.services;

import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.services.motor.BaseOpenLoopVelocityControllableMotorServiceImpl;
import edu.cmu.ri.createlab.terk.services.motor.OpenLoopVelocityControllableMotorService;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class OpenLoopVelocityControllableMotorServiceImpl extends BaseOpenLoopVelocityControllableMotorServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(OpenLoopVelocityControllableMotorServiceImpl.class);

   static OpenLoopVelocityControllableMotorServiceImpl create(final FinchController finchController)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, finchController.getFinchProperties().getMotorDeviceCount());
      basicPropertyManager.setReadOnlyProperty(OpenLoopVelocityControllableMotorService.PROPERTY_NAME_MIN_VELOCITY, finchController.getFinchProperties().getMotorDeviceMinVelocity());
      basicPropertyManager.setReadOnlyProperty(OpenLoopVelocityControllableMotorService.PROPERTY_NAME_MAX_VELOCITY, finchController.getFinchProperties().getMotorDeviceMaxVelocity());

      return new OpenLoopVelocityControllableMotorServiceImpl(finchController,
                                                              basicPropertyManager,
                                                              finchController.getFinchProperties().getMotorDeviceCount());
      }

   private final FinchController finchController;
   private final int[] rememberedVelocities;

   private OpenLoopVelocityControllableMotorServiceImpl(final FinchController finchController,
                                                        final PropertyManager propertyManager,
                                                        final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchController = finchController;
      this.rememberedVelocities = new int[deviceCount];
      }

   /**
    * <p>
    * Sets the motor velocities.  This method does nothing and returns <code>false</code> if either (or both) of the
    * arrays is <code>null</code> or has a length less than {@link #getDeviceCount()}.
    * </p>
    * <p>
    * Note that we can't really support the mask, since the finchController's {@link FinchController#setMotorVelocities}
    * method requires that we always specify the velocity of both motors, but there's no way to know the *current*
    * velocity of a motor.  So, instead, if a motor is masked off, this method simply sets its velocity to the value
    * specified the last time the motor was set via this method, or 0 if it has never been set before. This is
    * in line with the docs for {@link OpenLoopVelocityControllableMotorService#setVelocities}.
    * </p>
    */
   @Override
   protected boolean execute(final boolean[] mask, final int[] velocities)
      {
      if (mask != null &&
          mask.length >= getDeviceCount() &&
          velocities != null &&
          velocities.length >= getDeviceCount())
         {
         // remember velocity of masked on motors, and set velocity of masked off motors to the remembered value
         for (int i = 0; i < getDeviceCount(); i++)
            {
            if (mask[i])
               {
               // for motors which are masked on, update the remembered velocities with the new value
               rememberedVelocities[i] = velocities[i];
               }
            else
               {
               // for motors which are masked off, use the remembered value
               velocities[i] = rememberedVelocities[i];
               }
            }
         return finchController.setMotorVelocities(velocities[0], velocities[1]);
         }
      else
         {
         LOG.debug("OpenLoopVelocityControllableMotorServiceImpl.execute(): given mask and/or velocities array was null or didn't contain enough elements");
         }
      return false;
      }
   }