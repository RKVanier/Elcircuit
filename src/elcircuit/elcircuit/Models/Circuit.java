package elcircuit.elcircuit.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a simple series DC circuit model.
 * <p>
 * The circuit can contain multiple batteries, resistors, and capacitors
 * all assumed to be connected in series. It keeps track of equivalent
 * values and the total current through the branch.
 * </p>
 *
 * @author Rayan
 */
public class Circuit {

    /**
     * Series current through the circuit in amperes (A).
     * Stored as a static value so it can be displayed from controllers easily.
     */
    static Double current;

    /**
     * Equivalent battery representing the sum of all series sources.
     */
    Battery equivalentBattery;

    /**
     * List of individual batteries in the circuit.
     */
    List<Battery> batterys;

    /**
     * Equivalent resistor representing the sum of all series resistors.
     */
    Resistor equivalentResistor;

    /**
     * List of individual resistors in the circuit.
     */
    List<Resistor> resistors;

    /**
     * Equivalent capacitor for series capacitors.
     */
    Capacitor equivalentCapacitor;

    /**
     * List of individual capacitors in the circuit.
     */
    List<Capacitor> capacitors;

    /**
     * Creates an empty circuit with no components and no current.
     */
    public Circuit() {
        Circuit.current = null;
        this.equivalentBattery = null;
        this.batterys = new ArrayList<>();
        this.equivalentResistor = null;
        this.resistors = new ArrayList<>();
        this.equivalentCapacitor = null;
        this.capacitors = new ArrayList<>();
    }

    /**
     * Creates a circuit with already-initialized values.
     *
     * @param current            initial current (A)
     * @param equivalentBattery  equivalent battery
     * @param batterys           list of batteries
     * @param equivalentResistor equivalent resistor
     * @param resistors          list of resistors
     * @param equivalentCapacitor equivalent capacitor
     * @param capacitors         list of capacitors
     */
    public Circuit(Double current,
                   Battery equivalentBattery,
                   List<Battery> batterys,
                   Resistor equivalentResistor,
                   List<Resistor> resistors,
                   Capacitor equivalentCapacitor,
                   List<Capacitor> capacitors) {

        Circuit.current = current;
        this.equivalentBattery = equivalentBattery;
        this.batterys = batterys;
        this.equivalentResistor = equivalentResistor;
        this.resistors = resistors;
        this.equivalentCapacitor = equivalentCapacitor;
        this.capacitors = capacitors;
    }

    /**
     * Calculates the equivalent capacitance for capacitors in series.
     * <p>
     * Uses the formula: {@code 1/Ceq = Σ(1/Ci)}.
     * If there are no valid capacitors, {@code equivalentCapacitor} is set to {@code null}.
     * </p>
     */
    public void calculateCapacitor() {
        if (capacitors.isEmpty()) {
            equivalentCapacitor = null;
            return;
        }

        double invCsum = 0.0;
        for (Capacitor cap : capacitors) {
            if (cap.getCapacitance() != null && cap.getCapacitance() != 0.0) {
                invCsum += 1.0 / cap.getCapacitance();
            }
        }

        if (invCsum == 0.0) {
            equivalentCapacitor = null;
            return;
        }

        if (equivalentCapacitor == null) {
            equivalentCapacitor = new Capacitor();
        }
        equivalentCapacitor.setCapacitance(1.0 / invCsum);
    }

    /**
     * Calculates the equivalent resistance for resistors in series:
     * {@code Req = ΣRi}.
     * If there are no resistors, {@code equivalentResistor} is set to {@code null}.
     */
    public void calculateResistor() {
        if (resistors.isEmpty()) {
            equivalentResistor = null;
            return;
        }

        double resistance = 0.0;
        for (Resistor res : resistors) {
            if (res.getResistance() != null) {
                resistance += res.getResistance();
            }
        }

        if (equivalentResistor == null) {
            equivalentResistor = new Resistor();
        }
        equivalentResistor.setResistance(resistance);
    }

    /**
     * Calculates the equivalent battery emf for series sources:
     * {@code Veq = ΣEi}.
     * If there are no batteries, {@code equivalentBattery} is set to {@code null}.
     */
    public void calculateBattery() {
        if (batterys.isEmpty()) {
            equivalentBattery = null;
            return;
        }

        double emf = 0.0;
        for (Battery bat : batterys) {
            if (bat.getEmf() != null) {
                emf += bat.getEmf();
            }
        }

        if (equivalentBattery == null) {
            equivalentBattery = new Battery();
        }
        equivalentBattery.setEmf(emf);
    }

    /**
     * Calculates the series current using Ohm's law:
     * {@code I = Veq / Req}.
     * <p>
     * If there is no valid equivalent battery or resistor, or {@code Req == 0},
     * the current is set to {@code null}.
     * </p>
     */
    public void calculateCurrent() {
        if (equivalentBattery == null
                || equivalentResistor == null
                || equivalentResistor.getResistance() == null
                || equivalentResistor.getResistance() == 0.0) {

            current = null;
            return;
        }

        current = equivalentBattery.getEmf() / equivalentResistor.getResistance();
    }

    /**
     * Recalculates all equivalent values in the circuit:
     * battery, resistor, capacitor, and current.
     * <p>
     * Also updates the voltage drop across each resistor if the current
     * is defined.
     * </p>
     */
    public void recalculateAll() {
        calculateBattery();
        calculateResistor();
        calculateCapacitor();
        calculateCurrent();

        if (current != null) {
            for (Resistor r : resistors) {
                if (r.getResistance() != null) {
                    r.calculateVoltage(current);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.equivalentBattery);
        hash = 11 * hash + Objects.hashCode(this.batterys);
        hash = 11 * hash + Objects.hashCode(this.equivalentResistor);
        hash = 11 * hash + Objects.hashCode(this.resistors);
        hash = 11 * hash + Objects.hashCode(this.equivalentCapacitor);
        hash = 11 * hash + Objects.hashCode(this.capacitors);
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
        final Circuit other = (Circuit) obj;
        if (!Objects.equals(this.equivalentBattery, other.equivalentBattery)) {
            return false;
        }
        if (!Objects.equals(this.batterys, other.batterys)) {
            return false;
        }
        if (!Objects.equals(this.equivalentResistor, other.equivalentResistor)) {
            return false;
        }
        if (!Objects.equals(this.resistors, other.resistors)) {
            return false;
        }
        if (!Objects.equals(this.equivalentCapacitor, other.equivalentCapacitor)) {
            return false;
        }
        return Objects.equals(this.capacitors, other.capacitors);
    }

    @Override
    public String toString() {
        return "Circuit{" + "equivalentBattery=" + equivalentBattery
                + ", batterys=" + batterys
                + ", equivalentResistor=" + equivalentResistor
                + ", resistors=" + resistors
                + ", equivalentCapacitor=" + equivalentCapacitor
                + ", capacitors=" + capacitors + '}';
    }

    // ---------- Getters and setters ----------

    public Battery getEquivalentBattery() {
        return equivalentBattery;
    }

    public void setEquivalentBattery(Battery equivalentBattery) {
        this.equivalentBattery = equivalentBattery;
    }

    public List<Battery> getBatterys() {
        return batterys;
    }

    public void setBatterys(List<Battery> batterys) {
        this.batterys = batterys;
    }

    public Resistor getEquivalentResistor() {
        return equivalentResistor;
    }

    public void setEquivalentResistor(Resistor equivalentResistor) {
        this.equivalentResistor = equivalentResistor;
    }

    public List<Resistor> getResistors() {
        return resistors;
    }

    public void setResistors(List<Resistor> resistors) {
        this.resistors = resistors;
    }

    public Capacitor getEquivalentCapacitor() {
        return equivalentCapacitor;
    }

    public void setEquivalentCapacitor(Capacitor equivalentCapacitor) {
        this.equivalentCapacitor = equivalentCapacitor;
    }

    public List<Capacitor> getCapacitors() {
        return capacitors;
    }

    public void setCapacitors(List<Capacitor> capacitors) {
        this.capacitors = capacitors;
    }

    /**
     * Returns the current shared by all series components.
     *
     * @return current in amperes (A), or {@code null} if undefined
     */
    public static Double getCurrent() {
        return current;
    }

    /**
     * Sets the global circuit current.
     *
     * @param current current in amperes (A)
     */
    public static void setCurrent(Double current) {
        Circuit.current = current;
    }
}
