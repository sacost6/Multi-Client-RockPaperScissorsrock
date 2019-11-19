package code;

import javafx.scene.control.Button;

import java.io.Serializable;

/*
    Contains the player Sign and number of points they have, and what client number they are
 */
public class Player implements Serializable {

    private Sign sign;
    private Integer numberOfPoints = 0;

    private Integer number;

    private String name;

    Player personPlaying;

    public Player(Integer number){
        this.number = number;
        sign = null;

    }

    public Player(String name){
        this.name = name;
        sign = null;
    }

    Player(Integer number, Sign sign, Integer numberOfPoints){
        this.sign = sign;
        this.numberOfPoints = numberOfPoints;
        this.number = number;
    }

    Player(String name, Sign sign, Integer numberOfPoints){
        this.sign = sign;
        this.numberOfPoints = numberOfPoints;
        this.name = name;
    }

    Player(Player player){
        this.name = player.name;
        this.number = player.number;
        this.numberOfPoints = player.numberOfPoints;
        this.sign = player.sign;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
