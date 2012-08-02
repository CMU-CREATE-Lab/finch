package edu.cmu.ri.createlab.terk.robot.finch.services;

import java.awt.Color;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.services.led.BaseFullColorLEDServiceImpl;
import edu.cmu.ri.createlab.terk.services.led.FullColorLEDService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FullColorLEDServiceImpl extends BaseFullColorLEDServiceImpl
   {
   static FullColorLEDServiceImpl create(final FinchController finchController)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, finchController.getFinchProperties().getFullColorLedDeviceCount());
      basicPropertyManager.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MIN_INTENSITY, finchController.getFinchProperties().getFullColorLedDeviceMinIntensity());
      basicPropertyManager.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MAX_INTENSITY, finchController.getFinchProperties().getFullColorLedDeviceMaxIntensity());

      return new FullColorLEDServiceImpl(finchController,
                                         basicPropertyManager,
                                         finchController.getFinchProperties().getFullColorLedDeviceCount());
      }

   private final FinchController finchController;

   private FullColorLEDServiceImpl(final FinchController finchController,
                                   final PropertyManager propertyManager,
                                   final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchController = finchController;
      }

   public Color[] set(final boolean[] mask, final Color[] colors)
      {
      if (mask != null && colors != null)
         {
         if (mask.length > 0 && colors.length > 0)
            {
            if (mask[0])
               {
               if (finchController.setFullColorLED(colors[0]))
                  {
                  return new Color[]{colors[0]};
                  }
               }
            }
         }
      return null;
      }
   }