/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package elcircuit;

import java.util.Objects;

/**
 *
 * @author Rayan
 */
public class Capacitor extends Component {

    Double capacitance;
    Double charge;

    public Capacitor() {
        super();
        this.capacitance = null;
        this.charge = null;
    }

    public Capacitor(Double capacitance, Double charge, double voltage) {
        super(voltage);
        this.capacitance = capacitance;
        this.charge = charge;
    }

    public void calculateVoltage() {
        this.voltage = this.charge / this.capacitance;
    }

    public void calculateCharging(double time, double equivalentResistance, double maxVoltage) {
        double tau = equivalentResistance * this.capacitance;
        this.voltage = maxVoltage * (1 - Math.exp(-time / tau));
        this.charge = this.capacitance * this.voltage;
    }

    public void calculateDischarging(double time, double equivalentResistance, double maxVoltage) {
        double tau = equivalentResistance * this.capacitance;
        this.voltage = maxVoltage * Math.exp(-time / tau);
        this.charge = this.capacitance * this.voltage;
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
        return "Capacitor{" + "capacitance=" + capacitance + ", charge=" + charge + '}';
    }

    public Double getCapacitance() {
        return capacitance;
    }

    public void setCapacitance(Double capacitance) {
        this.capacitance = capacitance;
    }

    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }
}
