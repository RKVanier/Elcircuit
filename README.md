# ElCircuit – JavaFX RC Circuit Simulator

ElCircuit is an interactive JavaFX-based electrical circuit simulator that allows users to visually build simple DC circuits.  
Users can drag and drop batteries, resistors, and capacitors, connect them with wires, and run a real-time simulation that shows:

- Circuit current (I)
- Voltage across each component
- Capacitor charging and discharging
- Capacitor charge (Q)

This simulator is designed as a visual learning tool to help understand real RC circuit behavior.

---

## How to Use the Simulator

### Building the Circuit
- Drag components (battery, resistor, capacitor) into the grid.
- Before the simulation starts, click a component to apply the values you entered in the side panel.
- Draw wires by clicking and dragging between empty points in the grid.

### Running the Simulation
- Press Start to begin the simulation.
- Press Pause to pause the simulation.
- Press Reset to clear the circuit and restart.

### Inspecting Components During Simulation
Click a component to view:
- Voltage  
- Resistance / Capacitance  
- Capacitor charge  
- Current  

### Deleting Items
- Right-click any component or wire to delete it.

---

## Teamwork Summary

**Rayan**  
- Designed and initiated the core model classes (Battery, Resistor, Capacitor, Circuit).  
- Implemented the main controller logic, including drag-and-drop behavior, wire creation, simulation timing, and UI interaction flow.  
- Added documentation comments and JavaDoc throughout controller-related code.

**Olajare**  
- Completed and refined the model classes by implementing calculation and simulation methods.  
- Created the visual layout of the application, including the full FXML interface and component arrangement.  
- Added documentation comments and JavaDoc throughout the model classes.

**Shared Contributions**  
- Both team members collaborated on writing JavaDoc and inline comments.  
- Both team members collaborated on testing, debugging, and refining the overall simulator to ensure correct RC behavior and smooth user interaction.
