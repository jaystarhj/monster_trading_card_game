public class Stats {
    private int user_id;
    private int win;
    private int loss;
    private int draw;

    public Stats (){}

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public int getLoss() {
        return loss;
    }

    public int getWin() {
        return win;
    }

    public int getELOScore(){
        return 100 + 3*win -5*loss;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    @Override
    public String toString() {
        return "{" +
                "\"user_id\":\"" + user_id + "\"" +
                ", \"win\":\"" + win + "\"" +
                ", \"loss\":\"" + loss + "\"" +
                '}';
    }
}

