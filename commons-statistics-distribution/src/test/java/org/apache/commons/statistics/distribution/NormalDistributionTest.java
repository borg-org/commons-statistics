/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.statistics.distribution;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link NormalDistribution}. Extends
 * {@link ContinuousDistributionAbstractTest}. See class javadoc of that class
 * for details.
 */
public class NormalDistributionTest extends ContinuousDistributionAbstractTest {

    private static final double DEFAULT_TOLERANCE = 1e-7;

    //---------------------- Override tolerance --------------------------------

    @BeforeEach
    public void customSetUp() {
        setTolerance(DEFAULT_TOLERANCE);
    }

    //-------------- Implementations for abstract methods ----------------------

    /** Creates the default real distribution instance to use in tests. */
    @Override
    public NormalDistribution makeDistribution() {
        return new NormalDistribution(2.1, 1.4);
    }

    /** Creates the default cumulative probability distribution test input values */
    @Override
    public double[] makeCumulativeTestPoints() {
        // quantiles computed using R
        return new double[] {-2.226325228634938d, -1.156887023657177d, -0.643949578356075d, -0.2027950777320613d, 0.305827808237559d,
                             6.42632522863494d, 5.35688702365718d, 4.843949578356074d, 4.40279507773206d, 3.89417219176244d};
    }

    /** Creates the default cumulative probability density test expected values */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0.001d, 0.01d, 0.025d, 0.05d, 0.1d, 0.999d,
                             0.990d, 0.975d, 0.950d, 0.900d};
    }

    /** Creates the default probability density test expected values */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] {0.00240506434076, 0.0190372444310, 0.0417464784322, 0.0736683145538, 0.125355951380,
                             0.00240506434076, 0.0190372444310, 0.0417464784322, 0.0736683145538, 0.125355951380};
    }

    //-------------------- Additional test cases -------------------------------

    private void verifyQuantiles() {
        // Requires the current instance set by setDistribution(...)
        final NormalDistribution distribution = (NormalDistribution) getDistribution();
        final double mu = distribution.getMean();
        final double sigma = distribution.getStandardDeviation();
        setCumulativeTestPoints(new double[] {mu - 2 * sigma, mu - sigma,
                                              mu,             mu + sigma,
                                              mu + 2 * sigma, mu + 3 * sigma,
                                              mu + 4 * sigma, mu + 5 * sigma});
        // Quantiles computed using R (same as Mathematica)
        setCumulativeTestValues(new double[] {0.02275013194817921, 0.158655253931457, 0.5, 0.841344746068543,
                                              0.977249868051821, 0.99865010196837, 0.999968328758167,  0.999999713348428});
        verifyCumulativeProbabilities();
    }

    @Test
    public void testQuantiles() {
        setDensityTestValues(new double[] {0.0385649760808, 0.172836231799, 0.284958771715, 0.172836231799, 0.0385649760808,
                                           0.00316560600853, 9.55930184035e-05, 1.06194251052e-06});
        verifyQuantiles();
        verifyDensities();

        setDistribution(new NormalDistribution(0, 1));
        setDensityTestValues(new double[] {0.0539909665132, 0.241970724519, 0.398942280401, 0.241970724519, 0.0539909665132,
                                           0.00443184841194, 0.000133830225765, 1.48671951473e-06});
        verifyQuantiles();
        verifyDensities();

        setDistribution(new NormalDistribution(0, 0.1));
        setDensityTestValues(new double[] {0.539909665132, 2.41970724519, 3.98942280401, 2.41970724519,
                                           0.539909665132, 0.0443184841194, 0.00133830225765, 1.48671951473e-05});
        verifyQuantiles();
        verifyDensities();
    }

    @Test
    public void testInverseCumulativeProbabilityExtremes() {
        setInverseCumulativeTestPoints(new double[] {0, 1});
        setInverseCumulativeTestValues(new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY});
        verifyInverseCumulativeProbabilities();
    }

    // MATH-1257
    @Test
    public void testCumulativeProbability() {
        final ContinuousDistribution dist = new NormalDistribution(0, 1);
        final double x = -10;
        final double expected = 7.61985e-24;
        final double v = dist.cumulativeProbability(x);
        final double tol = 1e-5;
        Assertions.assertEquals(1, v / expected, 1e-5);
    }

    @Test
    public void testParameterAccessors() {
        final NormalDistribution distribution = makeDistribution();
        Assertions.assertEquals(2.1, distribution.getMean());
        Assertions.assertEquals(1.4, distribution.getStandardDeviation());
    }

    @Test
    public void testConstructorPrecondition1() {
        Assertions.assertThrows(DistributionException.class, () -> new NormalDistribution(1, 0));
    }

    @Test
    public void testMoments() {
        final double tol = 1e-9;
        NormalDistribution dist;

        dist = new NormalDistribution(0, 1);
        Assertions.assertEquals(0, dist.getMean(), tol);
        Assertions.assertEquals(1, dist.getVariance(), tol);

        dist = new NormalDistribution(2.2, 1.4);
        Assertions.assertEquals(2.2, dist.getMean(), tol);
        Assertions.assertEquals(1.4 * 1.4, dist.getVariance(), tol);

        dist = new NormalDistribution(-2000.9, 10.4);
        Assertions.assertEquals(-2000.9, dist.getMean(), tol);
        Assertions.assertEquals(10.4 * 10.4, dist.getVariance(), tol);
    }

    @Test
    public void testDensity() {
        final double[] x = new double[] {-2, -1, 0, 1, 2};
        // R 2.5: print(dnorm(c(-2,-1,0,1,2)), digits=10)
        checkDensity(0, 1, x, new double[] {0.05399096651, 0.24197072452, 0.39894228040, 0.24197072452, 0.05399096651});
        // R 2.5: print(dnorm(c(-2,-1,0,1,2), mean=1.1), digits=10)
        checkDensity(1.1,  1,  x,  new double[] {0.003266819056, 0.043983595980, 0.217852177033, 0.396952547477, 0.266085249899});
    }

    private void checkDensity(double mean, double sd, double[] x, double[] expected) {
        final NormalDistribution d = new NormalDistribution(mean, sd);
        for (int i = 0; i < x.length; i++) {
            Assertions.assertEquals(expected[i], d.density(x[i]), 1e-9);
        }
    }

    /**
     * Check to make sure top-coding of extreme values works correctly.
     * Verifies fixes for JIRA MATH-167, MATH-414
     */
    @Test
    public void testLowerTail() {
        final NormalDistribution distribution = new NormalDistribution(0, 1);
        for (int i = 0; i < 100; i++) { // make sure no convergence exception
            final double lowerTail = distribution.cumulativeProbability(-i);
            if (i < 39) { // make sure not top-coded
                Assertions.assertTrue(lowerTail > 0);
            } else { // make sure top coding not reversed
                Assertions.assertEquals(0, lowerTail, 0d);
            }
        }
    }

    /**
     * Check to make sure top-coding of extreme values works correctly.
     * Verifies fixes for JIRA MATH-167, MATH-414
     */
    @Test
    public void testUpperTail() {
        final NormalDistribution distribution = new NormalDistribution(0, 1);
        for (int i = 0; i < 100; i++) { // make sure no convergence exception
            final double upperTail = distribution.cumulativeProbability(i);
            if (i < 9) { // make sure not top-coded
                Assertions.assertTrue(upperTail < 1);
            } else { // make sure top coding not reversed
                Assertions.assertEquals(1, upperTail, 0d);
            }
        }
    }

    @Test
    public void testExtremeValues() {
        final NormalDistribution distribution = new NormalDistribution(0, 1);

        Assertions.assertEquals(1, distribution.cumulativeProbability(Double.MAX_VALUE));
        Assertions.assertEquals(0, distribution.cumulativeProbability(-Double.MAX_VALUE));
        Assertions.assertEquals(1, distribution.cumulativeProbability(Double.POSITIVE_INFINITY));
        Assertions.assertEquals(0, distribution.cumulativeProbability(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testMath280() {
        final NormalDistribution normal = new NormalDistribution(0, 1);
        double result = normal.inverseCumulativeProbability(0.9986501019683698);
        Assertions.assertEquals(3.0, result, DEFAULT_TOLERANCE);
        result = normal.inverseCumulativeProbability(0.841344746068543);
        Assertions.assertEquals(1.0, result, DEFAULT_TOLERANCE);
        result = normal.inverseCumulativeProbability(0.9999683287581673);
        Assertions.assertEquals(4.0, result, DEFAULT_TOLERANCE);
        result = normal.inverseCumulativeProbability(0.9772498680518209);
        Assertions.assertEquals(2.0, result, DEFAULT_TOLERANCE);
    }
}
