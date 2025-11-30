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
public class Resistor extends Component{
    Double resistance;

    public Resistor() {
        super();
        this.resistance = null;
    }

    public Resistor(double resistance, double current, double voltage) {
        super(voltage);
        this.resistance = resistance;
    }

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

    public Double getResistance() {
        return resistance;
    }

    public void setResistance(Double resistance) {
        this.resistance = resistance;
    }


}
