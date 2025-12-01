/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package elcircuit.Models;

import java.util.Objects;

/**
 * Models an ideal DC voltage source (battery).
 * <p>
 * The battery is represented by its electromotive force (emf).
 * </p>
 *
 * @author Rayan
 */
public class Battery {

    /**
     * Electromotive force of the battery in volts (V).
     */
    Double emf;

    /**
     * Creates a battery with no emf defined.
     * The emf will be {@code null}.
     */
    public Battery() {
        this.emf = null;
    }

    /**
     * Creates a battery with an initial emf.
     *
     * @param emf emf in volts (V), may be {@code null}
     */
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

    /**
     * Returns the emf of the battery.
     *
     * @return emf in volts (V), or {@code null} if undefined
     */
    public Double getEmf() {
        return emf;
    }

    /**
     * Sets the emf of the battery.
     *
     * @param emf emf in volts (V), may be {@code null}
     */
    public void setEmf(Double emf) {
        this.emf = emf;
    }
}
