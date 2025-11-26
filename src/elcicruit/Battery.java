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
public class Battery {
    Double emf;
    Double current;

    public Battery() {
        this.emf = null;
        this.current = null;
    }

    public Battery(Double emf, Double current) {
        this.emf = emf;
        this.current = current;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.emf);
        hash = 67 * hash + Objects.hashCode(this.current);
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
        final Battery other = (Battery) obj;
        if (!Objects.equals(this.emf, other.emf)) {
            return false;
        }
        return Objects.equals(this.current, other.current);
    }

    @Override
    public String toString() {
        return "Battery{" + "emf=" + emf + ", current=" + current + '}';
    }

    public Double getEmf() {
        return emf;
    }

    public void setEmf(Double emf) {
        this.emf = emf;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }
    
}
