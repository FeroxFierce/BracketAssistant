package ferox.bracket;

public class Participant {

    int id;
    private String name;
    private int seed;

    public Participant() {
        this.id = 0;
        this.name = "";
        this.seed = 0;
    }

    public Participant(String name, int seed) {
        this.name = name;
        this.seed = seed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

}