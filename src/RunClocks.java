import java.sql.Time;

public class RunClocks {
    public static void main(String[] args) throws Exception {

        boolean exemploSlide = true;

        // Simulando exemplo do slide
        if (exemploSlide) {
            new ServerClock(1, 1, Time.valueOf("03:00:00")).start();
            new ServerClock(2, 2, Time.valueOf("02:50:00")).start();
            new ServerClock(3, 2, Time.valueOf("03:25:00")).start();
        } else {

        }
    }
}
