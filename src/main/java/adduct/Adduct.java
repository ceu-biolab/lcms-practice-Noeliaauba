package adduct;

public class Adduct {

    /**
     * Calculate the mass to search depending on the adduct hypothesis
     *
     * @param mz mz
     * @param adduct adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     *
     * @return the monoisotopic mass of the experimental mass mz with the adduct @param adduct
     */
    public static Double getMonoisotopicMassFromMZ(Double mz, String adduct) {
        Double massToSearch;
       /*
        if Adduct is single charge the formula is M = m/z +- adductMass. Charge is 1 so it does not affect
        if Adduct is double or triple charged the formula is M = ( mz +- adductMass ) * charge
        if adduct is a dimer or multimer the formula is M =  (mz +- adductMass) / numberOfMultimer
        return monoisotopicMass;
        */
        if (AdductList.MAPMZPOSITIVEADDUCTS.containsKey(adduct)) {
            massToSearch = AdductList.MAPMZPOSITIVEADDUCTS.get(adduct);
        } else if (AdductList.MAPMZNEGATIVEADDUCTS.containsKey(adduct)) {
            massToSearch = AdductList.MAPMZNEGATIVEADDUCTS.get(adduct);
        } else {
            throw new IllegalArgumentException("Uknown detected adduct: " + adduct);
        }

        // Detect charge (negative & positive)
        int charge_value = 1;
        if (adduct.contains("3+") || adduct.contains("3−") || adduct.contains("3-")) {
            charge_value = 3;
        } else if (adduct.contains("2+") ||adduct.contains("2−") || adduct.contains("2-")) {
            charge_value = 2;
        } else if (adduct.contains("+") || adduct.contains("−") || adduct.contains("-")) {
            charge_value = 1;
        }

        // Detect multimer
        int multimer = 1;
        if (adduct.contains("3M")) {
            multimer = 3;
        }else if (adduct.contains("2M")) {
            multimer = 2;
        }

        double monoisotopicMass;
        monoisotopicMass = ((mz * charge_value) + massToSearch) / multimer;
        return monoisotopicMass;
    }

    /**
     * Calculate the mz of a monoisotopic mass with the corresponding adduct
     *
     * @param monoisotopicMass
     * @param adduct adduct name ([M+H]+, [2M+H]+, [M+2H]2+, etc..)
     *
     * @return
     */
    public static Double getMZFromMonoisotopicMass(Double monoisotopicMass, String adduct) {
        Double massToSearch;
        // !! TODO METHOD
        // !! TODO Create the necessary regex to obtain the multimer (number before the M) and the charge (number before the + or - (if no number, the charge is 1).

        /*
        if Adduct is single charge the formula is m/z = M +- adductMass. Charge is 1 so it does not affect

        if Adduct is double or triple charged the formula is mz = M/charge +- adductMass

        if adduct is a dimer or multimer the formula is mz = M * numberOfMultimer +- adductMass

        return monoisotopicMass;

         */
        return null;
    }

    /**
     * Returns the ppm difference between measured mass and theoretical mass
     *
     * @param experimentalMass    Mass measured by MS
     * @param theoreticalMass Theoretical mass of the compound
     */
    public static int calculatePPMIncrement(Double experimentalMass, Double theoreticalMass) {
        int ppmIncrement;
        ppmIncrement = (int) Math.round(Math.abs((experimentalMass - theoreticalMass) * 1000000
                / theoreticalMass));
        return ppmIncrement;
    }

    /**
     * Returns the ppm difference between measured mass and theoretical mass
     *
     * @param measuredMass    Mass measured by MS
     * @param ppm ppm of tolerance
     */
    public static double calculateDeltaPPM(Double measuredMass, int ppm) {
        double deltaPPM;
        deltaPPM =  Math.round(Math.abs((measuredMass * ppm) / 1000000));
        return deltaPPM;

    }




}
