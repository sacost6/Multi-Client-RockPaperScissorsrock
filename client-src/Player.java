package code;

/*
    Contains the player Sign and number of points they have, and what client number they are
 */
public class Player {

    private Sign sign;
    private Integer numberOfPoints = 0;

    private Integer number;

    public Player(Integer number){
        this.number = number;
        sign = null;
    }

    Player(Integer number, Sign sign, Integer numberOfPoints){
        this.sign = sign;
        this.numberOfPoints = numberOfPoints;
        this.number = number;
    }

    public Sign getSign(){
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public void reset(){
        numberOfPoints = 0;
        sign = null;
    }

    public Integer getNumber() {
        return number;
    }

    public void addPoint(){
        numberOfPoints++;
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints;
    }
}
