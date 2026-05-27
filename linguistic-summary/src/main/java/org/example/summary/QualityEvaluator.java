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

    // T4: |Π r_j - T3|, gdzie r_j = |supp(S_j) ∩ D| / |D|
    public static double evaluateT4(List<DataEntity> records, FuzzyStatement qualifier, List<FuzzyStatement> summarizers) {
        double t3 = evaluateT3(records, qualifier, summarizers);

        double product = 1.0;
        for (FuzzyStatement sum : summarizers) {
            double suppCount = 0.0;
            for (DataEntity record : records) {
                if (evaluateSingleMembership(record, sum) > 0.0) {
                    suppCount++;
                }
            }
            double rj = records.isEmpty() ? 0.0 : suppCount / records.size();
            product *= rj;
        }

        return Math.abs(product - t3);
    }

    public static double evaluateT5(List<FuzzyStatement> summarizers) {
        return 2.0 * Math.pow(0.5, summarizers.size());
    }

    // T6: 1 - |supp(Q)| / |X_Q|
    public static double evaluateT6(Quantifier quantifier) {
        return 1.0 - degreeOfImprecision(quantifier.getFuzzySet());
    }

    // T7: 1 - rc(Q), gdzie rc = relative cardinality
    public static double evaluateT7(Quantifier quantifier) {
        return 1.0 - relativeCardinality(quantifier.getFuzzySet());
    }

    // T8: 1 - (Π rc(S_j))^{1/n}
    public static double evaluateT8(List<FuzzyStatement> summarizers) {
        double product = 1.0;
        for (FuzzyStatement s : summarizers) {
            product *= relativeCardinality(s.getFuzzySet());
        }
        return 1.0 - Math.pow(product, 1.0 / summarizers.size());
    }

    // T9: 1 - in(W) (stopień nieprecyzyjności kwalifikatora)
    public static double evaluateT9(FuzzyStatement qualifier) {
        if (qualifier == null) return 0.0;
        return 1.0 - degreeOfImprecision(qualifier.getFuzzySet());
    }

    // T10: 1 - rc(W) (stopień kardynalności kwalifikatora)
    public static double evaluateT10(FuzzyStatement qualifier) {
        if (qualifier == null) return 0.0;
        return 1.0 - relativeCardinality(qualifier.getFuzzySet());
    }

    // T11: in(W) — dodatkowa miara (brak w pliku md)
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

    /**
     * Stopień nieprecyzyjności (degree of imprecision) — in(S).
     * Dla zbiorów ciągłych: |supp(S)| / |X| (długość nośnika / długość uniwersum).
     * Dla zbiorów dyskretnych: |{x : µ(x) > 0}| / |X|.
     */
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

    /**
     * Relative cardinality — rc(S) = (Σ µ(x)) / |X|.
     * Suma stopni przynależności podzielona przez rozmiar uniwersum.
     */
    private static double relativeCardinality(FuzzySet set) {
        if (set.getUniverse().isContinuous()) {
            double uLen = set.getUniverse().getMaxBound() - set.getUniverse().getMinBound();
            if (uLen <= 0.0) return 0.0;

            int steps = 500;
            double step = uLen / steps;
            double sumMembership = 0.0;

            for (double x = set.getUniverse().getMinBound(); x <= set.getUniverse().getMaxBound(); x += step) {
                sumMembership += set.getMembership(x);
            }
            // Normalizacja: średnia wartość przynależności ≈ (Σ µ) / (steps+1)
            // rc = pole pod krzywą µ / długość uniwersum ≈ (Σ µ * step) / uLen = (Σ µ) / (steps+1)
            return sumMembership / (steps + 1);
        } else {
            double uLen = set.getUniverse().getDiscreteElements().size();
            if (uLen == 0.0) return 0.0;
            double sumMembership = 0.0;
            for (double val : set.getUniverse().getDiscreteElements()) {
                sumMembership += set.getMembership(val);
            }
            return sumMembership / uLen;
        }
    }
}
