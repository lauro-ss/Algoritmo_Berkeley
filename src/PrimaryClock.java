import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class PrimaryClock {

    public static void main(String[] args) throws Exception {

        int secondarysServers = 0;
        // Classe java para trabalhar com multicast ou broadcast
        MulticastSocket mcs = new MulticastSocket(6000);// porta como parametro
        // Endere�o de um grupo multicast
        InetAddress grp = InetAddress.getByName("239.0.0.1");
        // ingressando em um grupo para receber mensagens enviadas para o mesmo
        mcs.joinGroup(grp);

        // Recebe a quantidade de relógios secundários
        try {
            byte rec[] = new byte[256];
            DatagramPacket pkg = new DatagramPacket(rec, rec.length);

            // Espera 5 segundos pros relógios secundários conectar
            mcs.setSoTimeout(5000);
            while (true) {
                mcs.receive(pkg);// recebendo os dados enviados via multicast para o endere�o acima
                if (pkg.getData().length > 0) {
                    secondarysServers++;
                }
            }
        } catch (SocketTimeoutException te) {
            System.out.println("Acabou o tempo de respostas, número e servidores: " + secondarysServers);
            byte[] hora = "Hora".getBytes();
            DatagramPacket pkgHora = new DatagramPacket(hora, hora.length, InetAddress.getByName("239.0.0.1"),
                    6000);
            mcs.send(pkgHora);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

    }

}