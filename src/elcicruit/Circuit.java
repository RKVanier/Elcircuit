/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package elcicruit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author olaja
 */
public class Circuit {
Double current;  // Changed from static
    Battery equivalentBattery;  // Changed from static
    List<Battery> batterys;
    Resistor equivalentResistor;  // Changed from static
    List<Resistor> resistors;
    Capacitor equivalentCapacitor;  // Changed from static
    List<Capacitor> capacitors;

    public Circuit() {
        this.current = null;
        this.equivalentBattery = null;
        this.batterys = new ArrayList<>();
        this.equivalentResistor = null;
        this.resistors = new ArrayList<>();
        this.equivalentCapacitor = null;
        this.capacitors = new ArrayList<>();
    }

    public Circuit(Double current, Battery equivalentBattery, List<Battery> batterys, Resistor equivalentResistor, List<Resistor> resistors, Capacitor equivalentCapacitor, List<Capacitor> capacitors) {
        this.current = current;
        this.equivalentBattery = equivalentBattery;
        this.batterys = batterys;
        this.equivalentResistor = equivalentResistor;
        this.resistors = resistors;
        this.equivalentCapacitor = equivalentCapacitor;
        this.capacitors = capacitors;
    }

    public void calculateCapacitor() {
        double capacitance = 0;
        for (Capacitor cap : capacitors) {
            capacitance += (1 / cap.getCapacitance());
        }
        equivalentCapacitor.setCapacitance(1 / capacitance);
    }
    
    public void calculateResistor() {
        double resistance = 0;
        for (Resistor res : resistors) {
            resistance += res.getResistance();
        }
        equivalentResistor.setResistance(resistance);
    }
    
    public void calculateBattery() {
        double emf = 0;
        for (Battery bat : batterys) {
            emf += bat.getEmf();
        }
        equivalentBattery.setEmf(emf);  // Fixed: was setting resistor instead of battery
    }
    
    public void calculateCurrent() {
        if (equivalentBattery != null && equivalentResistor != null 
            && equivalentBattery.getEmf() != null && equivalentResistor.getResistance() != null
            && equivalentResistor.getResistance() != 0) {
            this.current = equivalentBattery.getEmf() / equivalentResistor.getResistance();
        }
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.current);
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
        if (!Objects.equals(this.current, other.current)) {
            return false;
        }
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
        return "Circuit{" + "current=" + current + ", equivalentBattery=" + equivalentBattery + ", batterys=" + batterys + ", equivalentResistor=" + equivalentResistor + ", resistors=" + resistors + ", equivalentCapacitor=" + equivalentCapacitor + ", capacitors=" + capacitors + '}';
    }

    // Getters and Setters
    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }

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
}