package edu.cmu.ri.createlab.terk.robot.finch;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class FinchUtils
   {
   /**
    * Given an array of booleans, this method returns a {@link Set} of {@link Integer}s containing the indeces of the
    * <code>mask</code> array which are <code>true</code>.  This method only considers indeces in the <code>mask</code>
    * array which are less than or equal to <code>maxIndexToConsider</code>.  Will return an empty {@link Set} if
    * the given <code>mask</code> array is empty or <code>null</code>.  Guaranteed to not return <code>null</code>.
    */
   public static Set<Integer> computeMaskedOnIndeces(final boolean[] mask, final int maxIndexToConsider)
      {
      final Set<Integer> maskedIndeces = new HashSet<Integer>();
      if (mask != null && mask.length > 0)
         {
         final int numIndecesToCheck = Math.min(mask.length, maxIndexToConsider);
         for (int i = 0; i < numIndecesToCheck; i++)
            {
            if (mask[i])
               {
               maskedIndeces.add(i);
               }
            }
         }
      return maskedIndeces;
      }

   private FinchUtils()
      {
      // private to prevent instantiation
      }
   }
