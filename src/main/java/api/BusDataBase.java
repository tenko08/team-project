package api;

import entities.Bus;

public interface BusDataBase {
    Bus getBus(int id);

    Bus[] getAllBuses();


}
