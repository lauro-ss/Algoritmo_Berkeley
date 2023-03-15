import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.Time;

public class SecondaryClock extends Thread {

    private int id;
    private Time hora;

    public SecondaryClock(int id, Time hora) {
        this.id = id;
        this.hora = hora;
    }

    public void run() {
        Time timePrimaryServer;
        String horaString;

        try {
            byte[] b = "Conectando".getBytes();

            InetAddress addr = InetAddress.getByName("239.0.0.1");
            DatagramPacket pkg = new DatagramPacket(b, b.length, addr, 6000);
            DatagramSocket ds = new DatagramSocket();

            MulticastSocket mcs = new MulticastSocket(6001);

            InetAddress grp = InetAddress.getByName("239.0.0.1");

            // Grupo que recebe as horas na porta 6001
            mcs.joinGroup(grp);
            // Recebendo o horario do servidor primario
            mcs.receive(pkg);

            if (pkg.getData().length > 0) {
                horaString = new String(pkg.getData(), 0, pkg.getLength());
                timePrimaryServer = Time.valueOf(horaString);

                byte[] horaByte = Long.toString(hora.getTime() - timePrimaryServer.getTime()).getBytes();
                pkg = new DatagramPacket(horaByte, horaByte.length, pkg.getAddress(), pkg.getPort());
                // Enviando diferenca de hora
                ds.send(pkg);
            }

            // Recebendo novo horario
            mcs.receive(pkg);
            horaString = new String(pkg.getData(), 0, pkg.getLength());
            timePrimaryServer = Time.valueOf(horaString);
            System.out.println("Novo horário servidor secundário[" + id + "]: " + timePrimaryServer.toString());

        } catch (Exception e) {
            System.out.println("Nao foi possivel enviar a mensagem");
        }
    }
}
