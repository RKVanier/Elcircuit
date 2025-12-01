package elcircuit.elcircuit.Models;

/**
 * Abstract representation of a circuit component that has a voltage across it.
 * This base class is extended by concrete components such as
 * Resistor and Capacitor.
 *
 * @author Rayan
 */
public class Component {

    /**
     * Voltage across this component in volts (V).
     * May be null if not yet initialized.
     */
    Double voltage;

    /**
     * Creates a component with no initial voltage.
     * The voltage will be null.
     */
    public Component() {
        this.voltage = null;
    }

    /**
     * Creates a component with an initial voltage.
     *
     * @param voltage initial voltage in volts (V), may be null
     */
    public Component(Double voltage) {
        this.voltage = voltage;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash
                + (int) (Double.doubleToLongBits(this.voltage)
                ^ (Double.doubleToLongBits(this.voltage) >>> 32));
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
        final Component other = (Component) obj;
        return Double.doubleToLongBits(this.voltage)
                == Double.doubleToLongBits(other.voltage);
    }

    @Override
    public String toString() {
        return "Component{" + "voltage=" + voltage + '}';
    }

    /**
     * Returns the voltage across this component.
     *
     * @return voltage in volts (V), or null if undefined
     */
    public Double getVoltage() {
        return voltage;
    }

    /**
     * Sets the voltage across this component.
     *
     * @param voltage voltage in volts (V), may be null
     */
    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }
}
