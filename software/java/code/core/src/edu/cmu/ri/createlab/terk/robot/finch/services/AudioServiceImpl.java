package edu.cmu.ri.createlab.terk.robot.finch.services;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.terk.robot.finch.FinchController;
import edu.cmu.ri.createlab.terk.services.ExceptionHandler;
import edu.cmu.ri.createlab.terk.services.audio.AudioService;
import edu.cmu.ri.createlab.terk.services.audio.BaseAudioServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AudioServiceImpl extends BaseAudioServiceImpl
   {
   private static final Log LOG = LogFactory.getLog(AudioServiceImpl.class);

   static AudioServiceImpl create(final FinchController finchController)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.AUDIO_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_AMPLITUDE, FinchConstants.AUDIO_DEVICE_MIN_AMPLITUDE);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_AMPLITUDE, FinchConstants.AUDIO_DEVICE_MAX_AMPLITUDE);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_DURATION, FinchConstants.AUDIO_DEVICE_MIN_DURATION);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_DURATION, FinchConstants.AUDIO_DEVICE_MAX_DURATION);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_FREQUENCY, FinchConstants.AUDIO_DEVICE_MIN_FREQUENCY);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_FREQUENCY, FinchConstants.AUDIO_DEVICE_MAX_FREQUENCY);

      return new AudioServiceImpl(finchController,
                                  basicPropertyManager);
      }

   private final FinchController finchController;
   private final Executor executor = Executors.newCachedThreadPool();

   private AudioServiceImpl(final FinchController finchController,
                            final PropertyManager propertyManager)
      {
      super(propertyManager);
      this.finchController = finchController;
      }

   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      finchController.playTone(frequency, amplitude, duration);
      }

   public void playSound(final byte[] sound)
      {
      finchController.playClip(sound);
      }

   public void playToneAsynchronously(final int frequency, final int amplitude, final int duration, final ExceptionHandler callback)
      {
      try
         {
         executor.execute(
               new Runnable()
               {
               public void run()
                  {
                  finchController.playTone(frequency, amplitude, duration);
                  }
               });
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to play the tone asynchronously", e);
         callback.handleException(e);
         }
      }

   public void playSoundAsynchronously(final byte[] sound, final ExceptionHandler callback)
      {
      try
         {
         executor.execute(
               new Runnable()
               {
               public void run()
                  {
                  finchController.playClip(sound);
                  }
               });
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to play the clip asynchronously", e);
         callback.handleException(e);
         }
      }
   }