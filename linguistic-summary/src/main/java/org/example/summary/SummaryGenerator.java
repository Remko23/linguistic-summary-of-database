package org.example.summary;

import org.example.database.DataEntity;
import org.example.fuzzy.LinguisticVariable;
import org.example.fuzzy.quantifier.Quantifier;
import org.example.fuzzy.set.FuzzySet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SummaryGenerator {

    public List<LinguisticSummaryDTO> generateSingleSubject(
            List<DataEntity> records,
            List<Quantifier> quantifiers,
            List<FuzzyStatement> qualifiers,
            List<FuzzyStatement> summarizers) {

        List<LinguisticSummaryDTO> results = new ArrayList<>();

        for (Quantifier q : quantifiers) {
            List<FuzzyStatement> activeQualifiers = new ArrayList<>(qualifiers);
            activeQualifiers.add(null);

            for (FuzzyStatement qual : activeQualifiers) {
                for (FuzzyStatement sumVar : summarizers) {
                    List<FuzzyStatement> sumList = Collections.singletonList(sumVar);

                    String text = buildSentence(q.getName(), qual, sumVar);
                    LinguisticSummaryDTO dto = new LinguisticSummaryDTO(text);

                    dto.setMeasure(1, QualityEvaluator.evaluateT1(records, q, qual, sumList));
                    dto.setMeasure(2, QualityEvaluator.evaluateT2(sumList));
                    dto.setMeasure(3, QualityEvaluator.evaluateT3(records, qual, sumList));
                    dto.setMeasure(4, QualityEvaluator.evaluateT4(records, qual, sumList));
                    dto.setMeasure(5, QualityEvaluator.evaluateT5(sumList));
                    dto.setMeasure(6, QualityEvaluator.evaluateT6(q));
                    dto.setMeasure(7, QualityEvaluator.evaluateT7(q));
                    dto.setMeasure(8, QualityEvaluator.evaluateT8(sumList));
                    dto.setMeasure(9, QualityEvaluator.evaluateT9(qual));
                    dto.setMeasure(10, QualityEvaluator.evaluateT10(qual));
                    dto.setMeasure(11, QualityEvaluator.evaluateT11(qual));

                    results.add(dto);
                }
            }
        }
        return results;
    }

    public List<LinguisticSummaryDTO> generateMultiSubjectAll(
            List<DataEntity> records,
            FuzzyStatement subjectP1,
            FuzzyStatement subjectP2,
            String p1Name,
            String p2Name,
            List<Quantifier> quantifiers,
            List<FuzzyStatement> qualifiers,
            List<FuzzyStatement> summarizers,
            boolean[] enabledForms) {

        List<LinguisticSummaryDTO> results = new ArrayList<>();

        for (FuzzyStatement sumVar : summarizers) {
            List<FuzzyStatement> sumList = Collections.singletonList(sumVar);
            String sumDesc = mapAttributeName(sumVar.getAttributeName()) + " " + sumVar.getLabel().replace("_", " ");

            if (enabledForms[0]) {
                for (Quantifier q : quantifiers) {
                    String qName = mapQuantifierName(q.getName());
                    qName = qName.substring(0, 1).toUpperCase() + qName.substring(1);
                    String text = String.format("%s artykułów będących %s w porównaniu do %s ma %s.",
                            qName, p1Name, p2Name, sumDesc);

                    LinguisticSummaryDTO dto = new LinguisticSummaryDTO(text);
                    dto.setMeasure(1, QualityEvaluator.evaluateT1MultiForm1(records, subjectP1, subjectP2, q, sumList));
                    fillCommonMeasures(dto, records, q, null, sumList);
                    results.add(dto);
                }
            }

            if (enabledForms[1]) {
                for (Quantifier q : quantifiers) {
                    for (FuzzyStatement qual : qualifiers) {
                        String qName = mapQuantifierName(q.getName());
                        qName = qName.substring(0, 1).toUpperCase() + qName.substring(1);
                        String qualDesc = mapAttributeName(qual.getAttributeName()) + " "
                                + qual.getLabel().replace("_", " ");
                        String text = String.format("%s artykułów będących %s w porównaniu do %s, mających %s, ma %s.",
                                qName, p1Name, p2Name, qualDesc, sumDesc);

                        LinguisticSummaryDTO dto = new LinguisticSummaryDTO(text);
                        dto.setMeasure(1,
                                QualityEvaluator.evaluateT1MultiForm2(records, subjectP1, subjectP2, q, qual, sumList));
                        fillCommonMeasures(dto, records, q, qual, sumList);
                        results.add(dto);
                    }
                }
            }

            if (enabledForms[2]) {
                double[] t1vals = QualityEvaluator.evaluateT1MultiForm3(records, subjectP1, subjectP2, sumList);
                String textMore = String.format("Więcej artykułów będących %s niż %s ma %s.", p1Name, p2Name, sumDesc);
                LinguisticSummaryDTO dtoMore = new LinguisticSummaryDTO(textMore);
                dtoMore.setMeasure(1, t1vals[0]);
                fillCommonMeasuresNoQuantifier(dtoMore, records, null, sumList);
                results.add(dtoMore);

                String textLess = String.format("Mniej artykułów będących %s niż %s ma %s.", p1Name, p2Name, sumDesc);
                LinguisticSummaryDTO dtoLess = new LinguisticSummaryDTO(textLess);
                dtoLess.setMeasure(1, t1vals[1]);
                fillCommonMeasuresNoQuantifier(dtoLess, records, null, sumList);
                results.add(dtoLess);
            }

            if (enabledForms[3]) {
                for (FuzzyStatement qual : qualifiers) {
                    String qualDesc = mapAttributeName(qual.getAttributeName()) + " "
                            + qual.getLabel().replace("_", " ");
                    double[] t1vals = QualityEvaluator.evaluateT1MultiForm4(records, subjectP1, subjectP2, qual,
                            sumList);

                    String textMore = String.format("Więcej artykułów będących %s niż %s, mających %s, ma %s.",
                            p1Name, p2Name, qualDesc, sumDesc);
                    LinguisticSummaryDTO dtoMore = new LinguisticSummaryDTO(textMore);
                    dtoMore.setMeasure(1, t1vals[0]);
                    fillCommonMeasuresNoQuantifier(dtoMore, records, qual, sumList);
                    results.add(dtoMore);

                    String textLess = String.format("Mniej artykułów będących %s niż %s, mających %s, ma %s.",
                            p1Name, p2Name, qualDesc, sumDesc);
                    LinguisticSummaryDTO dtoLess = new LinguisticSummaryDTO(textLess);
                    dtoLess.setMeasure(1, t1vals[1]);
                    fillCommonMeasuresNoQuantifier(dtoLess, records, qual, sumList);
                    results.add(dtoLess);
                }
            }
        }

        return results;
    }

    private void fillCommonMeasures(LinguisticSummaryDTO dto, List<DataEntity> records,
            Quantifier q, FuzzyStatement qual, List<FuzzyStatement> sumList) {
        dto.setMeasure(2, QualityEvaluator.evaluateT2(sumList));
        dto.setMeasure(3, QualityEvaluator.evaluateT3(records, qual, sumList));
        dto.setMeasure(4, QualityEvaluator.evaluateT4(records, qual, sumList));
        dto.setMeasure(5, QualityEvaluator.evaluateT5(sumList));
        dto.setMeasure(6, QualityEvaluator.evaluateT6(q));
        dto.setMeasure(7, QualityEvaluator.evaluateT7(q));
        dto.setMeasure(8, QualityEvaluator.evaluateT8(sumList));
        dto.setMeasure(9, QualityEvaluator.evaluateT9(qual));
        dto.setMeasure(10, QualityEvaluator.evaluateT10(qual));
        dto.setMeasure(11, QualityEvaluator.evaluateT11(qual));
    }

    private void fillCommonMeasuresNoQuantifier(LinguisticSummaryDTO dto, List<DataEntity> records,
            FuzzyStatement qual, List<FuzzyStatement> sumList) {
        dto.setMeasure(2, QualityEvaluator.evaluateT2(sumList));
        dto.setMeasure(3, QualityEvaluator.evaluateT3(records, qual, sumList));
        dto.setMeasure(4, QualityEvaluator.evaluateT4(records, qual, sumList));
        dto.setMeasure(5, QualityEvaluator.evaluateT5(sumList));
        dto.setMeasure(6, 0.0);
        dto.setMeasure(7, 0.0);
        dto.setMeasure(8, QualityEvaluator.evaluateT8(sumList));
        dto.setMeasure(9, QualityEvaluator.evaluateT9(qual));
        dto.setMeasure(10, QualityEvaluator.evaluateT10(qual));
        dto.setMeasure(11, QualityEvaluator.evaluateT11(qual));
    }

    private String buildSentence(String quantifier, FuzzyStatement qualifier, FuzzyStatement summarizer) {
        String qName = mapQuantifierName(quantifier);
        qName = qName.substring(0, 1).toUpperCase() + qName.substring(1);

        String sumAttr = mapAttributeName(summarizer.getAttributeName());
        String sumLabel = summarizer.getLabel().replace("_", " ");

        if (qualifier == null) {
            return String.format("%s artykułów ma %s %s.",
                    qName, sumAttr, sumLabel);
        } else {
            String qualAttr = mapAttributeName(qualifier.getAttributeName());
            String qualLabel = qualifier.getLabel().replace("_", " ");
            return String.format("%s artykułów mających %s %s ma %s %s.",
                    qName, qualAttr, qualLabel, sumAttr, sumLabel);
        }
    }

    public static String mapQuantifierName(String fclName) {
        switch (fclName) {
            case "prawie_zaden":
                return "prawie żaden";
            case "okolo_1_4":
                return "około 1/4";
            case "okolo_polowy":
                return "około połowy";
            case "okolo_3_4":
                return "około 3/4";
            case "prawie_wszystkie":
                return "prawie wszystkie";
            case "mniejszosc":
                return "mniejszość";
            case "wiekszosc":
                return "większość";
            case "okolo_0":
                return "około 0";
            case "okolo_10_tys":
                return "około 10 tys.";
            case "okolo_20_tys":
                return "około 20 tys.";
            case "okolo_30_tys":
                return "około 30 tys.";
            case "okolo_40_tys":
                return "około 40 tys.";
            default:
                return fclName.replace("_", " ");
        }
    }

    private String mapAttributeName(String fclName) {
        switch (fclName) {
            case "a_r":
                return "atrakcyjność wizualna";
            case "a_h":
                return "bogactwo źródeł";
            case "t_r":
                return "unikalność słów artykułu";
            case "w_l":
                return "średnia długość słowa";
            case "a_s":
                return "współczynnik subiektywności artykułu";
            case "a_e":
                return "współczynnik nacechowania emocjonalnego artykułu";
            case "t_s":
                return "współczynnik subiektywności tytułu";
            case "t_e":
                return "współczynnik nacechowania emocjonalnego tytułu";
            case "p":
                return "stosunek pozytywnych słów";
            case "s":
                return "popularność artykułu";
            default:
                return fclName;
        }
    }
}
