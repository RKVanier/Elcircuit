/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package elcicruit;

import java.util.Objects;

/**
 *
 * @author Rayan
 */
public class Resistor extends Component{
    Double resistance;
    Double current;

    public Resistor() {
        super();
        this.resistance = null;
        this.current = null;
    }

    public Resistor(double resistance, double current, double voltage) {
        super(voltage);
        this.resistance = resistance;
        this.current = current;
    }

    @Override
    public void calculateVoltage() {
        this.voltage = this.current * this.resistance;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.resistance);
        hash = 97 * hash + Objects.hashCode(this.current);
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
        if (!Objects.equals(this.resistance, other.resistance)) {
            return false;
        }
        return Objects.equals(this.current, other.current);
    }

    @Override
    public String toString() {
        return "Resistor{" + "resistance=" + resistance + ", current=" + current + '}';
    }

    public Double getResistance() {
        return resistance;
    }

    public void setResistance(Double resistance) {
        this.resistance = resistance;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }
    
    
}
