import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.sql.Time;

public class SecondaryClock {
    public static void main(String[] args) throws Exception {

        Time hora = Time.valueOf("03:20:00");
        Time timePrimaryServer;
        String horaString;

        try {
            byte[] b = "Conectando".getBytes();
            // Definindo o endere�o de envio do pacote neste caso o endere�o de multicast
            InetAddress addr = InetAddress.getByName("239.0.0.1");
            DatagramPacket pkg = new DatagramPacket(b, b.length, addr, 6000);
            DatagramSocket ds = new DatagramSocket();
            ds.send(pkg);// enviando pacote multicast

            // Classe java para trabalhar com multicast ou broadcast
            MulticastSocket mcs = new MulticastSocket(6001);// porta como parametro
            // Endere�o de um grupo multicast
            InetAddress grp = InetAddress.getByName("239.0.0.1");
            // ingressando em um grupo para receber mensagens enviadas para o mesmo

            mcs.joinGroup(grp);
            mcs.receive(pkg);

            if (pkg.getData().length > 0) {
                horaString = new String(pkg.getData(), 0, pkg.getLength());
                timePrimaryServer = Time.valueOf(horaString);

                byte[] horaByte = Long.toString(hora.getTime() - timePrimaryServer.getTime()).getBytes();
                pkg = new DatagramPacket(horaByte, horaByte.length, pkg.getAddress(), pkg.getPort());
                ds.send(pkg);
            }

            mcs.receive(pkg);
            horaString = new String(pkg.getData(), 0, pkg.getLength());
            timePrimaryServer = Time.valueOf(horaString);
            System.out.println("Novo horário: " + timePrimaryServer.toString());

        } catch (Exception e) {
            System.out.println("Nao foi possivel enviar a mensagem");
        }
    }
}
