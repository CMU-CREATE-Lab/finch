package edu.cmu.ri.createlab.terk.robot.finch.applications;

import edu.cmu.ri.createlab.terk.robot.finch.Finch;

/**
 * Created by:
 * Date:
 * A starter file to use the Finch
 */

public class FinchTemplateFile
   {
   public static void main(final String[] args)
      {
      // Instantiating the Finch object
      final Finch myFinch = new Finch();

      // Always end your program with finch.quit()
      myFinch.quit();
      System.exit(0);
      }

   private FinchTemplateFile()
      {
      // private to prevent instantiation
      }
   }

