package api;

import entities.Bus;

import java.util.List;

public interface BusDataBase {
    Bus getBus(int id);

    List<Bus> getAllBuses();


}
