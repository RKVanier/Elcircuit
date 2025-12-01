package elcircuit.Models;

import java.util.Objects;

/**
 * Models an ideal capacitor in a DC series circuit.
 * <p>
 * The capacitor stores its capacitance, the current charge on the plates,
 * and the voltage (from {@link Component}). It also supports basic
 * RC charging/discharging behaviour for a series circuit.
 * </p>
 *
 * @author Rayan
 */
public class Capacitor extends Component {

    /**
     * Capacitance in farads (F).
     */
    Double capacitance;

    /**
     * Electric charge on the capacitor in coulombs (C).
     */
    Double charge;

    /**
     * Instantaneous current through the capacitor in amperes (A).
     */
    Double current;

    /**
     * Creates a capacitor with no initial values.
     * Capacitance, charge, and voltage will all be {@code null}.
     */
    public Capacitor() {
        super();
        this.capacitance = null;
        this.charge = null;
    }

    /**
     * Creates a capacitor with an initial capacitance, charge, and voltage.
     *
     * @param capacitance capacitance in farads (F)
     * @param charge      charge in coulombs (C)
     * @param voltage     initial voltage in volts (V)
     */
    public Capacitor(Double capacitance, Double charge, double voltage) {
        super(voltage);
        this.capacitance = capacitance;
        this.charge = charge;
    }

    /**
     * Calculates the charge on the capacitor using {@code Q = C * V}
     * and stores it in the {@code charge} field.
     */
    public void calculateCharge() {
        this.charge = this.voltage * this.capacitance;
    }

    /**
     * Updates the capacitor voltage according to the RC charging/discharging
     * equation in a series circuit and also updates the charge and current.
     * <p>
     * Uses:
     * {@code Vc(t) = Vf + (Vi - Vf) * e^(-t / (R*C))}.
     * </p>
     *
     * @param time                time since the start of the simulation in seconds
     * @param equivalentResistance equivalent series resistance (Ω)
     * @param supplyVoltage       total supply voltage (V). If {@code null}, a discharge
     *                             towards 0 V is assumed.
     */
    public void updateVoltage(double time,
                              double equivalentResistance,
                              Double supplyVoltage) {

        if (capacitance == null || capacitance == 0.0 || equivalentResistance == 0.0) {
            return;
        }

        double tau = equivalentResistance * this.capacitance;

        double vFinal = (supplyVoltage == null) ? 0.0 : supplyVoltage;
        double vInitial = (this.voltage == null) ? 0.0 : this.voltage;

        // RC charging/discharging law
        this.voltage = vFinal + (vInitial - vFinal) * Math.exp(-time / tau);

        // Update Q = C * V
        calculateCharge();

        // Instantaneous current through the series branch
        if (supplyVoltage != null && supplyVoltage != 0.0) {
            this.current = (supplyVoltage - this.voltage) / equivalentResistance;
        } else {
            this.current = -this.voltage / equivalentResistance;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.capacitance);
        hash = 97 * hash + Objects.hashCode(this.charge);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Capacitor other = (Capacitor) obj;
        if (!Objects.equals(this.capacitance, other.capacitance)) {
            return false;
        }
        return Objects.equals(this.charge, other.charge);
    }

    @Override
    public String toString() {
        return "Capacitor{" + "capacitance=" + capacitance
                + ", charge=" + charge + '}';
    }

    /**
     * Returns the capacitance.
     *
     * @return capacitance in farads (F), or {@code null} if undefined
     */
    public Double getCapacitance() {
        return capacitance;
    }

    /**
     * Sets the capacitance.
     *
     * @param capacitance capacitance in farads (F)
     */
    public void setCapacitance(Double capacitance) {
        this.capacitance = capacitance;
    }

    /**
     * Returns the current charge on the capacitor.
     *
     * @return charge in coulombs (C), may be {@code null}
     */
    public Double getCharge() {
        return charge;
    }

    /**
     * Sets the charge on the capacitor.
     *
     * @param charge charge in coulombs (C)
     */
    public void setCharge(Double charge) {
        this.charge = charge;
    }

    /**
     * Returns the instantaneous current through the capacitor.
     *
     * @return current in amperes (A), may be {@code null}
     */
    public Double getCurrent() {
        return current;
    }

    /**
     * Sets the instantaneous current through the capacitor.
     *
     * @param current current in amperes (A)
     */
    public void setCurrent(Double current) {
        this.current = current;
    }

    /**
     * Returns the voltage across the capacitor.
     *
     * @return voltage in volts (V), or {@code null} if undefined
     */
    public Double getVoltage() {
        return voltage;
    }

    /**
     * Sets the voltage across the capacitor.
     *
     * @param voltage voltage in volts (V)
     */
    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }
}

