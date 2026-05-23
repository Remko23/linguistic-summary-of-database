package org.example.fuzzy;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import org.example.fuzzy.set.ClassicSet;
import org.example.fuzzy.set.FuzzySet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FclLoader {

    public static List<LinguisticVariable> loadLinguisticVariables(String fclFilePath) {
        List<LinguisticVariable> variablesList = new ArrayList<>();

        File file = new File(fclFilePath);
        if (!file.exists() || file.length() <= 10) {
            return variablesList;
        }

        FIS fis = FIS.load(fclFilePath, false);
        if (fis == null) {
            return variablesList;
        }

        for (FunctionBlock functionBlock : fis) {
            for (Variable variable : functionBlock.getVariables().values()) {
                variable.estimateUniverse();
                double uMin = variable.getUniverseMin();
                double uMax = variable.getUniverseMax();

                ClassicSet universe = new ClassicSet(uMin, uMax);
                LinguisticVariable linguisticVariable = new LinguisticVariable(variable.getName());

                for (LinguisticTerm term : variable.getLinguisticTerms().values()) {
                    FuzzySet fuzzySet = new FuzzySet(universe, term.getMembershipFunction());
                    linguisticVariable.addLabel(term.getTermName(), fuzzySet);
                }

                variablesList.add(linguisticVariable);
            }
        }

        return variablesList;
    }
}
