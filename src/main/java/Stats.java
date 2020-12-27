public class Stats {
    private int user_id;
    private int Elo;
    private int win;
    private int loss;

    public Stats (){}

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setElo(int elo) {
        Elo = elo;
    }

    public void setLoss(int loss) {
        this.loss = loss;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getElo() {
        return Elo;
    }

    public int getLoss() {
        return loss;
    }

    public int getWin() {
        return win;
    }

    @Override
    public String toString() {
        return "{" +
                "\"user_id\":\"" + user_id + "\"" +
                ", \"Elo\":\"" + Elo + "\"" +
                ", \"win\":\"" + win + "\"" +
                ", \"loss\":\"" + loss + "\"" +
                '}';
    }
}

