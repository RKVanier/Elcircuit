/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package elcicruit;

import java.util.Objects;

/**
 *
 * @author olaja
 */
public class Battery {
Double emf;

    public Battery() {
        this.emf = null;
    }

    public Battery(Double emf) {
        this.emf = emf;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.emf);
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
        return Objects.equals(this.emf, other.emf);
    }

   

    @Override
    public String toString() {
        return "Battery{" + "emf=" + emf + '}';
    }

    public Double getEmf() {
        return emf;
    }

    public void setEmf(Double emf) {
        this.emf = emf;
    }
}
