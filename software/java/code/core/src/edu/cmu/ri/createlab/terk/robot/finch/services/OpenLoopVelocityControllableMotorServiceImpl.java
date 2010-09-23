package edu.cmu.ri.createlab.terk.robot.finch.services;

import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.robot.finch.FinchConstants;
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

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.MOTOR_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(OpenLoopVelocityControllableMotorService.PROPERTY_NAME_MIN_VELOCITY, FinchConstants.MOTOR_DEVICE_MIN_VELOCITY);
      basicPropertyManager.setReadOnlyProperty(OpenLoopVelocityControllableMotorService.PROPERTY_NAME_MAX_VELOCITY, FinchConstants.MOTOR_DEVICE_MAX_VELOCITY);

      return new OpenLoopVelocityControllableMotorServiceImpl(finchController,
                                                              basicPropertyManager,
                                                              FinchConstants.MOTOR_DEVICE_COUNT);
      }

   private final FinchController finchController;

   private OpenLoopVelocityControllableMotorServiceImpl(final FinchController finchController,
                                                        final PropertyManager propertyManager,
                                                        final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchController = finchController;
      }

   public boolean setVelocities(final int[] velocities)
      {
      if (velocities != null && velocities.length >= FinchConstants.MOTOR_DEVICE_COUNT)
         {
         return finchController.setMotorVelocities(velocities[0], velocities[1]);
         }
      else
         {
         LOG.debug("OpenLoopVelocityControllableMotorServiceImpl.setVelocities(): given velocities array was null or didn't contain enough elements");
         }
      return false;
      }
   }