package org.example.summary;

import org.example.database.DataEntity;
import org.example.fuzzy.quantifier.Quantifier;
import org.example.fuzzy.quantifier.RelativeQuantifier;
import org.example.fuzzy.set.FuzzySet;

import java.util.List;

public class QualityEvaluator {

    public static double evaluateT1(List<DataEntity> records, Quantifier quantifier, FuzzyStatement qualifier, List<FuzzyStatement> summarizers) {
        double numerator = 0.0;
        double denominator = 0.0;

        for (DataEntity record : records) {
            double muS = evaluateCombinedMembership(record, summarizers);
            double muW = (qualifier != null) ? evaluateSingleMembership(record, qualifier) : 1.0;

            numerator += Math.min(muS, muW);
            denominator += muW;
        }

        if (denominator == 0.0) return 0.0;

        double r = numerator / denominator;
        
        if (quantifier instanceof RelativeQuantifier) {
            return quantifier.getMembership(r);
        } else {
            return quantifier.getMembership(numerator);
        }
    }

    public static double evaluateT2(List<FuzzyStatement> summarizers) {
        double product = 1.0;
        for (FuzzyStatement sum : summarizers) {
            product *= degreeOfImprecision(sum.getFuzzySet());
        }
        return 1.0 - Math.pow(product, 1.0 / summarizers.size());
    }

    public static double evaluateT3(List<DataEntity> records, FuzzyStatement qualifier, List<FuzzyStatement> summarizers) {
        double countH = 0.0;
        double countD = 0.0;

        for (DataEntity record : records) {
            double muS = evaluateCombinedMembership(record, summarizers);
            double muW = (qualifier != null) ? evaluateSingleMembership(record, qualifier) : 1.0;

            if (muW > 0.0) {
                countD++;
                if (muS > 0.0) {
                    countH++;
                }
            }
        }

        return countD == 0 ? 0.0 : countH / countD;
    }

    public static double evaluateT4(List<FuzzyStatement> summarizers) {
        double product = 1.0;
        for (FuzzyStatement sum : summarizers) {
            product *= (1.0 - degreeOfImprecision(sum.getFuzzySet()));
        }
        return Math.pow(product, 1.0 / summarizers.size());
    }

    public static double evaluateT5(List<FuzzyStatement> summarizers) {
        return 2.0 * Math.pow(0.5, summarizers.size());
    }

    public static double evaluateT6(Quantifier quantifier) {
        return 1.0 - degreeOfImprecision(quantifier.getFuzzySet());
    }

    public static double evaluateT7(Quantifier quantifier) {
        return degreeOfImprecision(quantifier.getFuzzySet());
    }

    public static double evaluateT8(List<FuzzyStatement> summarizers) {
        double sum = 0.0;
        for (FuzzyStatement s : summarizers) {
            sum += degreeOfImprecision(s.getFuzzySet());
        }
        return 1.0 - (sum / summarizers.size());
    }

    public static double evaluateT9(List<FuzzyStatement> summarizers) {
        double product = 1.0;
        for (FuzzyStatement s : summarizers) {
            product *= degreeOfImprecision(s.getFuzzySet());
        }
        return Math.pow(product, 1.0 / summarizers.size());
    }

    public static double evaluateT10(FuzzyStatement qualifier) {
        if (qualifier == null) return 0.0;
        return 1.0 - degreeOfImprecision(qualifier.getFuzzySet());
    }

    public static double evaluateT11(FuzzyStatement qualifier) {
        if (qualifier == null) return 0.0;
        return degreeOfImprecision(qualifier.getFuzzySet());
    }

    private static double evaluateCombinedMembership(DataEntity record, List<FuzzyStatement> summarizers) {
        double result = 1.0;
        for (FuzzyStatement s : summarizers) {
            result = Math.min(result, evaluateSingleMembership(record, s));
        }
        return result;
    }

    private static double evaluateSingleMembership(DataEntity record, FuzzyStatement statement) {
        String attr = statement.getAttributeName();
        if (record.getNumericAttributes().containsKey(attr)) {
            double val = record.getAttribute(attr);
            FuzzySet set = statement.getFuzzySet();
            if (set.getUniverse().contains(val)) {
                return set.getMembership(val);
            }
        }
        return 0.0;
    }

    private static double degreeOfImprecision(FuzzySet set) {
        if (set.getUniverse().isContinuous()) {
            double uLen = set.getUniverse().getMaxBound() - set.getUniverse().getMinBound();
            if (uLen <= 0.0) return 0.0;
            
            double start = set.getUniverse().getMaxBound();
            double end = set.getUniverse().getMinBound();
            boolean found = false;
            double step = uLen / 500.0;
            
            for (double x = set.getUniverse().getMinBound(); x <= set.getUniverse().getMaxBound(); x += step) {
                if (set.getMembership(x) > 0.0) {
                    start = Math.min(start, x);
                    end = Math.max(end, x);
                    found = true;
                }
            }
            if (!found) return 0.0;
            return (end - start) / uLen;
        } else {
            double uLen = set.getUniverse().getDiscreteElements().size();
            if (uLen == 0.0) return 0.0;
            double supCount = 0.0;
            for (double val : set.getUniverse().getDiscreteElements()) {
                if (set.getMembership(val) > 0.0) {
                    supCount++;
                }
            }
            return supCount / uLen;
        }
    }
}
