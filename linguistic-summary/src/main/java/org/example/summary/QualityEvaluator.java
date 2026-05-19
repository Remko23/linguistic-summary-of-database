package org.example.summary;

import org.example.database.DataEntity;
import org.example.fuzzy.quantifier.Quantifier;
import org.example.fuzzy.quantifier.RelativeQuantifier;
import org.example.fuzzy.set.FuzzySet;

import java.util.List;

/**
 * Calculates the eleven quality measures (T1 to T11) for evaluating
 * generated linguistic summaries.
 */
public class QualityEvaluator {

    /**
     * T1: Degree of Truth (Stopień Prawdy)
     * For a relative summary "Q of W is S" (e.g. "Most clients who are young are wealthy"):
     * r = sum(min(mu_W(x), mu_S(x))) / sum(mu_W(x))
     * T1 = mu_Q(r)
     */
    public static double evaluateT1(List<DataEntity> records, Quantifier quantifier, FuzzySet qualifier, List<FuzzySet> summarizers) {
        double numerator = 0.0;
        double denominator = 0.0;

        for (DataEntity record : records) {
            // Retrieve value for mapping (for template we combine attribute lookups)
            double muS = evaluateCombinedMembership(record, summarizers);
            double muW = (qualifier != null) ? evaluateSingleMembership(record, qualifier) : 1.0;

            numerator += Math.min(muS, muW);
            denominator += muW;
        }

        if (denominator == 0.0) return 0.0;

        double r = numerator / denominator;
        
        // If it's a relative quantifier, evaluate ratio.
        // Otherwise, absolute quantifiers evaluate absolute counts.
        if (quantifier instanceof RelativeQuantifier) {
            return quantifier.getMembership(r);
        } else {
            return quantifier.getMembership(numerator);
        }
    }

    /**
     * T2: Degree of Imprecision (Stopień nieprecyzyjności)
     */
    public static double evaluateT2(List<FuzzySet> summarizers) {
        double product = 1.0;
        for (FuzzySet sum : summarizers) {
            product *= degreeOfImprecision(sum);
        }
        return 1.0 - Math.pow(product, 1.0 / summarizers.size());
    }

    /**
     * T3: Degree of Cover (Stopień pokrycia)
     */
    public static double evaluateT3(List<DataEntity> records, FuzzySet qualifier, List<FuzzySet> summarizers) {
        double countH = 0.0; // matching both qualifier and summarizers > 0
        double countD = 0.0; // matching qualifier > 0

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

    /**
     * T4: Degree of Specificity (Stopień specyficzności)
     */
    public static double evaluateT4(List<FuzzySet> summarizers) {
        double product = 1.0;
        for (FuzzySet sum : summarizers) {
            product *= (1.0 - degreeOfImprecision(sum)); // specific is opposite of imprecise
        }
        return Math.pow(product, 1.0 / summarizers.size());
    }

    /**
     * T5: Degree of Length (Stopień długości)
     */
    public static double evaluateT5(List<FuzzySet> summarizers) {
        return 2.0 * Math.pow(0.5, summarizers.size());
    }

    /**
     * T6: Degree of Quantifier Cardinality (Stopień kardynalności kwantyfikatora)
     */
    public static double evaluateT6(Quantifier quantifier) {
        // T6 = 1 - (cardinality of quantifier / domain length)
        return 1.0 - degreeOfImprecision(quantifier.getFuzzySet());
    }

    /**
     * T7: Degree of Quantifier Imprecision (Stopień nieprecyzyjności kwantyfikatora)
     */
    public static double evaluateT7(Quantifier quantifier) {
        return degreeOfImprecision(quantifier.getFuzzySet());
    }

    /**
     * T8: Degree of Summarizer Cardinality (Stopień kardynalności sumaryzatorów)
     */
    public static double evaluateT8(List<FuzzySet> summarizers) {
        double sum = 0.0;
        for (FuzzySet s : summarizers) {
            sum += degreeOfImprecision(s);
        }
        return 1.0 - (sum / summarizers.size());
    }

    /**
     * T9: Degree of Summarizer Imprecision (Stopień nieprecyzyjności sumaryzatorów)
     */
    public static double evaluateT9(List<FuzzySet> summarizers) {
        double product = 1.0;
        for (FuzzySet s : summarizers) {
            product *= degreeOfImprecision(s);
        }
        return Math.pow(product, 1.0 / summarizers.size());
    }

    /**
     * T10: Degree of Qualifier Cardinality (Stopień kardynalności kwalifikatora)
     */
    public static double evaluateT10(FuzzySet qualifier) {
        if (qualifier == null) return 0.0;
        return 1.0 - degreeOfImprecision(qualifier);
    }

    /**
     * T11: Degree of Qualifier Imprecision (Stopień nieprecyzyjności kwalifikatora)
     */
    public static double evaluateT11(FuzzySet qualifier) {
        if (qualifier == null) return 0.0;
        return degreeOfImprecision(qualifier);
    }

    // Helper: evaluates intersection of summarizers for a record
    private static double evaluateCombinedMembership(DataEntity record, List<FuzzySet> summarizers) {
        double result = 1.0;
        for (FuzzySet s : summarizers) {
            result = Math.min(result, evaluateSingleMembership(record, s));
        }
        return result;
    }

    // Helper: mapping record attribute to fuzzy set universe
    private static double evaluateSingleMembership(DataEntity record, FuzzySet set) {
        // Assume mapping is wired up (e.g. by looking up matching attributes)
        // Here we mock by checking available attributes, falling back to a dummy value
        for (String attr : record.getNumericAttributes().keySet()) {
            double val = record.getAttribute(attr);
            if (set.getUniverse().contains(val)) {
                return set.getMembership(val);
            }
        }
        return 0.0;
    }

    // Helper: card(Support) / card(Universe)
    private static double degreeOfImprecision(FuzzySet set) {
        if (set.getUniverse().isContinuous()) {
            double uLen = set.getUniverse().getMaxBound() - set.getUniverse().getMinBound();
            if (uLen == 0.0) return 0.0;
            // Approximate support length
            double start = set.getUniverse().getMaxBound();
            double end = set.getUniverse().getMinBound();
            double step = uLen / 500.0;
            for (double x = set.getUniverse().getMinBound(); x <= set.getUniverse().getMaxBound(); x += step) {
                if (set.getMembership(x) > 0.0) {
                    start = Math.min(start, x);
                    end = Math.max(end, x);
                }
            }
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
