import java.sql.Time;

public class RunClocks {
    public static void main(String[] args) throws Exception {

        boolean exemploSlide = true;

        // Simulando exemplo do slide
        if (exemploSlide) {
            new SecondaryClock(2, Time.valueOf("02:50:00")).start();
            new SecondaryClock(3, Time.valueOf("03:25:00")).start();
        } else {
            // 60 / 4 = 15 ~ esperado = 03:15:00
            new SecondaryClock(2, Time.valueOf("03:20:00")).start();
            new SecondaryClock(3, Time.valueOf("03:20:00")).start();
            new SecondaryClock(3, Time.valueOf("03:20:00")).start();
        }
    }
}
