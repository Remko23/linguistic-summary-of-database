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

/**
 * Utility loader designed to parse and load FCL (Fuzzy Control Language) files using jFuzzyLogic.
 * Converts the variables and linguistic terms defined in the FCL file into domain LinguisticVariable objects.
 */
public class FclLoader {

    /**
     * Loads and parses an FCL file, extracting all input and output variables and their linguistic terms.
     *
     * @param fclFilePath Path to the FCL file.
     * @return List of LinguisticVariables mapped from the FCL definition.
     */
    public static List<LinguisticVariable> loadLinguisticVariables(String fclFilePath) {
        List<LinguisticVariable> variablesList = new ArrayList<>();

        File file = new File(fclFilePath);
        if (!file.exists() || file.length() <= 10) {
            // Return empty list if the file does not exist or is practically empty (placeholder comment only)
            return variablesList;
        }

        // Load FCL file using jFuzzyLogic
        FIS fis = FIS.load(fclFilePath, false);
        if (fis == null) {
            return variablesList;
        }

        // Extract variables from all loaded function blocks by iterating over FIS (which implements Iterable<FunctionBlock>)
        for (FunctionBlock functionBlock : fis) {
            for (Variable variable : functionBlock.getVariables().values()) {
                // Ensure universe limits are calculated
                variable.estimateUniverse();
                double uMin = variable.getUniverseMin();
                double uMax = variable.getUniverseMax();

                // Create continuous ClassicSet representing the universe of discourse
                ClassicSet universe = new ClassicSet(uMin, uMax);
                LinguisticVariable linguisticVariable = new LinguisticVariable(variable.getName());

                // Add each defined linguistic term (label)
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
