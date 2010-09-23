package edu.cmu.ri.createlab.terk.robot.finch.services;

import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.services.Service;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.terk.services.audio.AudioService;
import edu.cmu.ri.createlab.terk.services.buzzer.BuzzerService;
import edu.cmu.ri.createlab.terk.services.finch.FinchService;
import edu.cmu.ri.createlab.terk.services.led.FullColorLEDService;
import edu.cmu.ri.createlab.terk.services.motor.OpenLoopVelocityControllableMotorService;
import edu.cmu.ri.createlab.terk.services.obstacle.SimpleObstacleDetectorService;
import edu.cmu.ri.createlab.terk.services.photoresistor.PhotoresistorService;
import edu.cmu.ri.createlab.terk.services.thermistor.ThermistorService;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FinchServiceFactory
   {
   private static final Logger LOG = Logger.getLogger(FinchServiceFactory.class);

   private interface ServiceCreator
      {
      Service create(final FinchController finchController);
      }

   private final Map<String, ServiceCreator> typeIdToServiceCreatorsMap = new HashMap<String, ServiceCreator>();

   public FinchServiceFactory()
      {
      typeIdToServiceCreatorsMap.put(AccelerometerService.TYPE_ID,
                                     new ServiceCreator()
                                     {
                                     public Service create(final FinchController finchController)
                                        {
                                        return AccelerometerServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(AudioService.TYPE_ID,
                                     new ServiceCreator()
                                     {
                                     public Service create(final FinchController finchController)
                                        {
                                        return AudioServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(BuzzerService.TYPE_ID,
                                     new ServiceCreator()
                                     {
                                     public Service create(final FinchController finchController)
                                        {
                                        return BuzzerServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(FinchService.TYPE_ID,
                                     new ServiceCreator()
                                     {
                                     public Service create(final FinchController finchController)
                                        {
                                        return FinchServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(FullColorLEDService.TYPE_ID,
                                     new ServiceCreator()
                                     {
                                     public Service create(final FinchController finchController)
                                        {
                                        return FullColorLEDServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(PhotoresistorService.TYPE_ID,
                                     new ServiceCreator()
                                     {
                                     public Service create(final FinchController finchController)
                                        {
                                        return PhotoresistorServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleObstacleDetectorService.TYPE_ID,
                                     new ServiceCreator()
                                     {
                                     public Service create(final FinchController finchController)
                                        {
                                        return SimpleObstacleDetectorServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(ThermistorService.TYPE_ID,
                                     new ServiceCreator()
                                     {
                                     public Service create(final FinchController finchController)
                                        {
                                        return ThermistorServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(OpenLoopVelocityControllableMotorService.TYPE_ID,
                                     new ServiceCreator()
                                     {
                                     public Service create(final FinchController finchController)
                                        {
                                        return OpenLoopVelocityControllableMotorServiceImpl.create(finchController);
                                        }
                                     });
      }

   public Service createService(final String serviceTypeId, final FinchController finchController)
      {
      if (typeIdToServiceCreatorsMap.containsKey(serviceTypeId))
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("FinchServiceFactory.createService(" + serviceTypeId + ")");
            }
         return typeIdToServiceCreatorsMap.get(serviceTypeId).create(finchController);
         }
      return null;
      }
   }