package edu.cmu.ri.createlab.terk.robot.finch.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.robot.finch.FinchHardwareType;
import edu.cmu.ri.createlab.terk.robot.finch.FinchProperties;
import edu.cmu.ri.createlab.terk.services.Service;
import edu.cmu.ri.createlab.terk.services.ServiceCreator;
import edu.cmu.ri.createlab.terk.services.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.terk.services.analog.AnalogInputsService;
import edu.cmu.ri.createlab.terk.services.audio.AudioService;
import edu.cmu.ri.createlab.terk.services.buzzer.BuzzerService;
import edu.cmu.ri.createlab.terk.services.finch.FinchBackpackService;
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

   private final Map<String, ServiceCreator<FinchController>> typeIdToServiceCreatorsMap = new HashMap<String, ServiceCreator<FinchController>>();

   public FinchServiceFactory(final FinchServiceFactoryHelper finchServiceFactoryHelper, final FinchProperties finchProperties)
      {
      typeIdToServiceCreatorsMap.put(AccelerometerService.TYPE_ID,
                                     new ServiceCreator<FinchController>()
                                     {
                                     @Override
                                     public Service createService(final FinchController finchController)
                                        {
                                        return AccelerometerServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(AudioService.TYPE_ID,
                                     new ServiceCreator<FinchController>()
                                     {
                                     public Service createService(final FinchController finchController)
                                        {
                                        return AudioServiceImpl.create(finchController, finchServiceFactoryHelper.getAudioDirectory());
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(BuzzerService.TYPE_ID,
                                     new ServiceCreator<FinchController>()
                                     {
                                     public Service createService(final FinchController finchController)
                                        {
                                        return BuzzerServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(FinchService.TYPE_ID,
                                     new ServiceCreator<FinchController>()
                                     {
                                     public Service createService(final FinchController finchController)
                                        {
                                        return FinchServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(FullColorLEDService.TYPE_ID,
                                     new ServiceCreator<FinchController>()
                                     {
                                     public Service createService(final FinchController finchController)
                                        {
                                        return FullColorLEDServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(PhotoresistorService.TYPE_ID,
                                     new ServiceCreator<FinchController>()
                                     {
                                     public Service createService(final FinchController finchController)
                                        {
                                        return PhotoresistorServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleObstacleDetectorService.TYPE_ID,
                                     new ServiceCreator<FinchController>()
                                     {
                                     public Service createService(final FinchController finchController)
                                        {
                                        return SimpleObstacleDetectorServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(ThermistorService.TYPE_ID,
                                     new ServiceCreator<FinchController>()
                                     {
                                     public Service createService(final FinchController finchController)
                                        {
                                        return ThermistorServiceImpl.create(finchController);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(OpenLoopVelocityControllableMotorService.TYPE_ID,
                                     new ServiceCreator<FinchController>()
                                     {
                                     public Service createService(final FinchController finchController)
                                        {
                                        return OpenLoopVelocityControllableMotorServiceImpl.create(finchController);
                                        }
                                     });

      // these services are only supported via the Finch Backpack
      if (FinchHardwareType.BACKPACK.equals(finchProperties.getHardwareType()))
         {
         typeIdToServiceCreatorsMap.put(AnalogInputsService.TYPE_ID,
                                        new ServiceCreator<FinchController>()
                                        {
                                        public Service createService(final FinchController finchController)
                                           {
                                           return AnalogInputsServiceImpl.create(finchController);
                                           }
                                        });
         typeIdToServiceCreatorsMap.put(FinchBackpackService.TYPE_ID,
                                        new ServiceCreator<FinchController>()
                                        {
                                        public Service createService(final FinchController finchController)
                                           {
                                           return FinchBackpackServiceImpl.create(finchController);
                                           }
                                        });
         }
      }

   public Set<String> getSupportedServices()
      {
      return Collections.unmodifiableSet(typeIdToServiceCreatorsMap.keySet());
      }

   public Service createService(final String serviceTypeId, final FinchController finchController)
      {
      if (typeIdToServiceCreatorsMap.containsKey(serviceTypeId))
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("FinchServiceFactory.createService(" + serviceTypeId + ")");
            }
         return typeIdToServiceCreatorsMap.get(serviceTypeId).createService(finchController);
         }
      return null;
      }
   }