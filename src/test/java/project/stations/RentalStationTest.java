package project.stations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.mocks.MockObserver;
import project.mocks.MockVehicle;
import project.stations.spaces.ParkingSpace;
import project.vehicles.State;
import project.vehicles.Vehicle;

public class RentalStationTest {
    protected static final int CAPACITY = 20;

    protected RentalStation station;

    protected MockObserver observer;

    protected List<ParkingSpace> spaces;

    @BeforeEach
    public void init() {
        this.spaces = new ArrayList<>();
        for (int i = 0; i < CAPACITY; i++)
            this.spaces.add(new ParkingSpace());
        this.station = new RentalStation(1, this.spaces);
        this.observer = new MockObserver();
        this.station.attach(this.observer);
    }

    @Test
    public void storeVehicleWhenStationEmpty() throws StationFullException {
        assertTrue(station.isEmpty());
        Vehicle vehicle = new MockVehicle(0);
        vehicle.setLives(10);
        vehicle.setState(State.RENTED); // else the station would be considered as empty because the vehicle was not rentable
        assertSame(0, observer.cpt);
        station.storeVehicle(vehicle);
        assertSame(1, observer.cpt);
        assertFalse(station.isEmpty());
        assertSame(State.STORED, vehicle.getState());
        ParkingSpace firstStationSpace = station.getSpaces().get(0);
        assertSame(vehicle, firstStationSpace.getVehicle());
    }

    @Test
    public void storeVehicleWhenStationFull() throws StationFullException {
        for (int i = 0; i < CAPACITY; i++) {
            station.storeVehicle(new MockVehicle(0));
        }
        assertThrows(StationFullException.class, () -> station.storeVehicle(new MockVehicle(0)));
    }

    @Test
    public void storeVehicleWithoutLives() throws StationFullException {
        Vehicle vehicle = new MockVehicle(0);
        vehicle.setLives(0);
        vehicle.setState(State.RENTED);
        assertSame(0, observer.cpt);
        station.storeVehicle(vehicle);
        assertSame(State.OUT_OF_SERVICE, vehicle.getState());
        assertSame(2, observer.cpt);
    }

    @Test
    public void storeWorkingVehicle() throws StationFullException {
        Vehicle vehicle = new MockVehicle(0);
        vehicle.setLives(10);
        vehicle.setState(State.RENTED);
        assertSame(0, observer.cpt);
        station.storeVehicle(vehicle);
        assertSame(State.STORED, vehicle.getState());
        assertSame(1, observer.cpt);
        ParkingSpace firstSpace = station.getSpaces().get(0);
        assertSame(firstSpace.getVehicle(), vehicle);
    }

    @Test
    public void rentVehicleWhenStationEmpty() throws StationEmptyException {
        assertThrows(StationEmptyException.class, () -> station.rentVehicle());
    }
}
