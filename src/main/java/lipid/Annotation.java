package lipid;


import adduct.*;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class to represent the annotation over a lipid
 */
public class Annotation {

    private final Lipid lipid;
    private final double mz;
    private final double intensity; // intensity of the most abundant peak in the groupedPeaks
    private final double rtMin;
    private final IonizationMode ionizationMode;
    private String adduct; // !!TODO The adduct will be detected based on the groupedSignals
    private final Set<Peak> groupedSignals;
    private int score;
    private int totalScoresApplied;


    /**
     * @param lipid
     * @param mz
     * @param intensity
     * @param retentionTime
     * @param ionizationMode
     */
    public Annotation(Lipid lipid, double mz, double intensity, double retentionTime, IonizationMode ionizationMode) {
        this(lipid, mz, intensity, retentionTime, ionizationMode, Collections.emptySet());
    }

    /**
     * @param lipid
     * @param mz
     * @param intensity
     * @param retentionTime
     * @param ionizationMode
     * @param groupedSignals
     */
    public Annotation(Lipid lipid, double mz, double intensity, double retentionTime, IonizationMode ionizationMode, Set<Peak> groupedSignals) {
        this.lipid = lipid;
        this.mz = mz;
        this.rtMin = retentionTime;
        this.intensity = intensity;
        this.ionizationMode = ionizationMode;
        // !!TODO This set should be sorted according to help the program to deisotope the signals plus detect the adduct
        this.groupedSignals = new TreeSet<>(groupedSignals);
        this.score = 0;
        this.totalScoresApplied = 0;
        adductDetection();
    }

    public Lipid getLipid() {
        return lipid;
    }

    public double getMz() {
        return mz;
    }

    public double getRtMin() {
        return rtMin;
    }

    public String getAdduct() {
        return adduct;
    }

    public void setAdduct(String adduct) {
        this.adduct = adduct;
    }

    public double getIntensity() {
        return intensity;
    }

    public IonizationMode getIonizationMode() {
        return ionizationMode;
    }

    public Set<Peak> getGroupedSignals() {
        return Collections.unmodifiableSet(groupedSignals);
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // !CHECK Take into account that the score should be normalized between -1 and 1
    public void addScore(int delta) {
        this.score += delta;
        this.totalScoresApplied++;
    }

    /**
     * @return The normalized score between 0 and 1 that consists on the final number divided into the times that the rule
     * has been applied.
     */
    public double getNormalizedScore() {
        return (double) this.score / this.totalScoresApplied;
    }



    public void adductDetection () {
        double error;
        double BasePeakMz=this.getMz();
        final double tolerance= 10;
        if (ionizationMode == IonizationMode.POSITIVE) {
            for (String BaseAdduct : AdductList.MAPMZPOSITIVEADDUCTS.keySet()) {
                for (String CandidateAdduct : AdductList.MAPMZPOSITIVEADDUCTS.keySet()) {
                    if (BaseAdduct.equals(CandidateAdduct)) continue;
                    for (Peak CandidatePeak : groupedSignals) {
                        if (Double.valueOf(CandidatePeak.getMz()).equals(BasePeakMz)) continue;
                        Double MonoMassBasePeak = Adduct.getMonoisotopicMassFromMZ(BasePeakMz, BaseAdduct);
                        Double MonoMassCandidatePeak = Adduct.getMonoisotopicMassFromMZ(CandidatePeak.getMz(), CandidateAdduct);
                        if (MonoMassBasePeak != null && MonoMassCandidatePeak != null) {
                            error = Adduct.calculatePPMIncrement(MonoMassBasePeak, MonoMassCandidatePeak);
                            System.out.println(error + ", " +BaseAdduct+ " = " + MonoMassBasePeak +  ", mz1 = " + BasePeakMz +",   "+ CandidateAdduct+ " = " + MonoMassCandidatePeak+ ", mz2 = " + CandidatePeak.getMz());

                            if (error <= tolerance) {
                                this.adduct = BaseAdduct;
                                return;
                            }

                        }
                    }
                }
            }
        }

        if (ionizationMode == IonizationMode.NEGATIVE) {
            for (String BaseAdduct : AdductList.MAPMZNEGATIVEADDUCTS.keySet()) {
                for (String CandidateAdduct : AdductList.MAPMZNEGATIVEADDUCTS.keySet()) {
                    if (BaseAdduct.equals(CandidateAdduct)) continue;
                        for (Peak CandidatePeak : groupedSignals) {
                            if (Double.valueOf(CandidatePeak.getMz()).equals(BasePeakMz)) continue;
                            Double MonoMassBasePeak = Adduct.getMonoisotopicMassFromMZ(BasePeakMz, BaseAdduct);
                            Double MonoMassCandidatePeak = Adduct.getMonoisotopicMassFromMZ(CandidatePeak.getMz(), CandidateAdduct);
                            if (MonoMassBasePeak != null && MonoMassCandidatePeak != null) {
                                error = Adduct.calculatePPMIncrement(MonoMassBasePeak, MonoMassCandidatePeak);
                                System.out.println(error + ", " +BaseAdduct+ " = " + MonoMassBasePeak +  ", mz1 = " + BasePeakMz +",   "+ CandidateAdduct+ " = " + MonoMassCandidatePeak+ ", mz2 = " + CandidatePeak.getMz());

                                if (error <= tolerance) {
                                    this.adduct = BaseAdduct;
                                    return;
                                }

                            }
                        }
                    }
                }

        }


    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Annotation)) return false;
        Annotation that = (Annotation) o;
        return Double.compare(that.mz, mz) == 0 &&
                Double.compare(that.rtMin, rtMin) == 0 &&
                Objects.equals(lipid, that.lipid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lipid, mz, rtMin);
    }

    @Override
    public String toString() {
        return String.format("Annotation(%s, mz=%.4f, RT=%.2f, adduct=%s, intensity=%.1f, score=%d)",
                lipid.getName(), mz, rtMin, adduct, intensity, score);
    }

    // !!TODO Detect the adduct with an algorithm or with drools, up to the user.
}
