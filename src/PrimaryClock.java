import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.sql.Time;
import java.util.ArrayList;

public class PrimaryClock extends Thread {

    private int id;
    private Time hora;
    private ArrayList<Long> horariosServidores;

    public PrimaryClock(int id, Time hora) {
        this.id = id;
        this.hora = hora;
        horariosServidores = new ArrayList<Long>();
    }

    public void run() {

        try (MulticastSocket mcs = new MulticastSocket(6000)) {
            InetAddress grp = InetAddress.getByName("239.0.0.1");
            mcs.joinGroup(grp);

            EnviarHora(hora, mcs);

            ReceberHoras(horariosServidores, mcs);

            Long totalTime = (long) 0;
            for (Long time : horariosServidores) {
                totalTime += time;
            }

            hora.setTime(hora.getTime() + (totalTime / (horariosServidores.size() + 1)));

            EnviarHora(hora, mcs);

            System.out.println("Novo horário servidor primário[" + id + "]: " + hora.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void EnviarHora(Time hora, MulticastSocket mcs) throws IOException {
        byte[] horaByte = hora.toString().getBytes();
        DatagramPacket pkgHora = new DatagramPacket(horaByte, horaByte.length,
                InetAddress.getByName("239.0.0.1"),
                6001);
        mcs.send(pkgHora);
    }

    private void ReceberHoras(ArrayList<Long> horariosServidores, MulticastSocket mcs) throws IOException {
        byte rec[] = new byte[256];
        DatagramPacket pkg = new DatagramPacket(rec, rec.length);
        try {
            // Espera por 3 segundos a diferença de horario
            mcs.setSoTimeout(3000);
            while (true) {
                mcs.receive(pkg);// recebendo os dados enviados via multicast para o endere�o acima
                if (pkg.getData().length > 0) {
                    String horaString = new String(pkg.getData(), 0, pkg.getLength());
                    horariosServidores.add(Long.parseLong(horaString));
                }
            }
        } catch (SocketTimeoutException te) {
            return;
        }
    }
}