package elcircuit.elcircuit.Models;

import java.util.Objects;

/**
 * Models an ideal resistor in a DC circuit.
 * The resistor stores its resistance in ohms and the voltage across it.
 *
 * @author Rayan
 */
public class Resistor extends Component {

    /**
     * Resistance of the resistor in ohms (Ω).
     * May be null if not yet set.
     */
    Double resistance;

    /**
     * Creates a resistor with no resistance value set.
     * The resistance and voltage will both be null.
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
     * V = I * R.
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
     * @return resistance in ohms (Ω), or null if not set
     */
    public Double getResistance() {
        return resistance;
    }

    /**
     * Sets the resistance of this resistor.
     *
     * @param resistance resistance in ohms (Ω), may be null
     */
    public void setResistance(Double resistance) {
        this.resistance = resistance;
    }
}
