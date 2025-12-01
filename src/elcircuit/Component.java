/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package elcircuit;

/**
 *
 * @author Rayan
 */
public class Component {
    Double voltage;

    public Component() {
        this.voltage = null;
    }
    
    public Component(Double voltage) {
        this.voltage = voltage;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int) (Double.doubleToLongBits(this.voltage) ^ (Double.doubleToLongBits(this.voltage) >>> 32));
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
        return Double.doubleToLongBits(this.voltage) == Double.doubleToLongBits(other.voltage);
    }

    @Override
    public String toString() {
        return "Component{" + "voltage=" + voltage + '}';
    }
    
    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }
}