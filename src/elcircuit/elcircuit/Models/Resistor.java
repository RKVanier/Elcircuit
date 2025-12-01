/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package elcircuit.Models;

import java.util.Objects;

/**
 * Models an ideal resistor in a DC circuit.
 * <p>
 * The resistor stores its resistance (in ohms) and the voltage
 * across it, inherited from {@link Component}.
 * </p>
 *
 * @author Rayan
 */
public class Resistor extends Component {

    /**
     * Resistance of the resistor in ohms (Ω).
     * May be {@code null} if not yet set.
     */
    Double resistance;

    /**
     * Creates a resistor with no resistance value set.
     * The resistance and voltage will both be {@code null}.
     */
    public Resistor() {
        super();
        this.resistance = null;
    }

    /**
     * Creates a resistor with an initial resistance and voltage.
     *
     * @param resistance resistance in ohms (Ω)
     * @param current    unused parameter (kept for compatibility with earlier design)
     * @param voltage    initial voltage in volts (V)
     */
    public Resistor(double resistance, double current, double voltage) {
        super(voltage);
        this.resistance = resistance;
    }

    /**
     * Calculates and stores the voltage across the resistor using Ohm's law:
     * {@code V = I * R}.
     *
     * @param current current through the resistor in amperes (A)
     */
    public void calculateVoltage(double current) {
        this.voltage = current * this.resistance;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.resistance);
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
        final Resistor other = (Resistor) obj;
        return Objects.equals(this.resistance, other.resistance);
    }

    @Override
    public String toString() {
        return "Resistor{" + "resistance=" + resistance + '}';
    }

    /**
     * Returns the resistance of this resistor.
     *
     * @return resistance in ohms (Ω), or {@code null} if not set
     */
    public Double getResistance() {
        return resistance;
    }

    /**
     * Sets the resistance of this resistor.
     *
     * @param resistance resistance in ohms (Ω), may be {@code null}
     */
    public void setResistance(Double resistance) {
        this.resistance = resistance;
    }
}
