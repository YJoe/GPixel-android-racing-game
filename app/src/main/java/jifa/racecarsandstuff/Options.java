package jifa.racecarsandstuff;

public class Options {
    public int trackFlag;
    public boolean oil, cracks, ai;
    public String car;

    public Options(){
        // default level options
        trackFlag = 0;
        oil = false;
        cracks = false;
        ai = false;
        car = "blue";
    }
}
