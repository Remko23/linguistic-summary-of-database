package org.example.fuzzy.membership;

/**
 * Implements a Gaussian (bell-shaped) membership function.
 * Defined by mean (center) and sigma (standard deviation).
 */
public class GaussianMembershipFunction implements MembershipFunction {
    private final double mean;
    private final double sigma;

    public GaussianMembershipFunction(double mean, double sigma) {
        if (sigma <= 0) {
            throw new IllegalArgumentException("Sigma must be positive");
        }
        this.mean = mean;
        this.sigma = sigma;
    }

    @Override
    public double getMembership(double x) {
        return Math.exp(-0.5 * Math.pow((x - mean) / sigma, 2));
    }

    public double getMean() { return mean; }
    public double getSigma() { return sigma; }
}
