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

    public List<LinguisticSummaryDTO> generateMultiSubject(
            List<DataEntity> group1,
            List<DataEntity> group2,
            String group1Name,
            String group2Name,
            List<Quantifier> quantifiers,
            List<FuzzyStatement> summarizers) {

        List<LinguisticSummaryDTO> results = new ArrayList<>();

        for (Quantifier q : quantifiers) {
            for (FuzzyStatement sumVar : summarizers) {
                List<FuzzyStatement> sumList = Collections.singletonList(sumVar);

                String text = String.format("%s artykułów z grupy %s w porównaniu do %s ma %s %s",
                        mapQuantifierName(q.getName()), group1Name, group2Name,
                        mapAttributeName(sumVar.getAttributeName()), sumVar.getLabel().replace("_", " "));

                LinguisticSummaryDTO dto = new LinguisticSummaryDTO(text);

                double t1 = QualityEvaluator.evaluateT1(group1, q, null, sumList);
                double t2 = QualityEvaluator.evaluateT1(group2, q, null, sumList);

                dto.setMeasure(1, Math.max(t1, t2));
                dto.setMeasure(2, QualityEvaluator.evaluateT2(sumList));
                dto.setMeasure(3, 0.5);
                dto.setMeasure(4, QualityEvaluator.evaluateT4(group1, null, sumList));
                dto.setMeasure(5, QualityEvaluator.evaluateT5(sumList));
                dto.setMeasure(6, QualityEvaluator.evaluateT6(q));
                dto.setMeasure(7, QualityEvaluator.evaluateT7(q));
                dto.setMeasure(8, QualityEvaluator.evaluateT8(sumList));
                dto.setMeasure(9, 0.0);
                dto.setMeasure(10, 0.0);
                dto.setMeasure(11, 0.0);

                results.add(dto);
            }
        }
        return results;
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
