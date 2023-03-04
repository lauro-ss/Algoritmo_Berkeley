import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.sql.Time;
import java.util.ArrayList;

public class PrimaryClock {

    public static void main(String[] args) throws Exception {

        int secondarysServers = 0;
        Time hora = Time.valueOf("03:00:00");
        ArrayList<Long> horariosServidores = new ArrayList<Long>();

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
            // System.out.println("Acabou o tempo de respostas, número de servidores: " +
            // secondarysServers);

            EnviarHora(hora, mcs);

            ReceberHoras(horariosServidores, mcs);

            for (Long time : horariosServidores) {
                System.out.println(time);
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

    }

    private static void EnviarHora(Time hora, MulticastSocket mcs) throws IOException {
        byte[] horaByte = hora.toString().getBytes();
        DatagramPacket pkgHora = new DatagramPacket(horaByte, horaByte.length,
                InetAddress.getByName("239.0.0.1"),
                6001);
        mcs.send(pkgHora);
    }

    private static void ReceberHoras(ArrayList<Long> horariosServidores, MulticastSocket mcs) throws IOException {
        byte rec[] = new byte[256];
        DatagramPacket pkg = new DatagramPacket(rec, rec.length);
        try {
            // Recebe o horario de todos os servidores
            mcs.setSoTimeout(5000);
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