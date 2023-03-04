import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SecondaryClock {
    public static void main(String[] args) throws Exception {

        try {
            byte[] b = "Conectando".getBytes();
            // Definindo o endere�o de envio do pacote neste caso o endere�o de multicast
            InetAddress addr = InetAddress.getByName("239.0.0.1");
            DatagramPacket pkg = new DatagramPacket(b, b.length, addr, 6000);
            DatagramSocket ds = new DatagramSocket();
            ds.send(pkg);// enviando pacote multicast

            // Classe java para trabalhar com multicast ou broadcast
            MulticastSocket mcs = new MulticastSocket(6000);// porta como parametro
            // Endere�o de um grupo multicast
            InetAddress grp = InetAddress.getByName("239.0.0.1");
            // ingressando em um grupo para receber mensagens enviadas para o mesmo
            mcs.joinGroup(grp);
            while (true) {
                mcs.receive(pkg);
                String t = new String(pkg.getData(), 0, pkg.getLength());
                System.out.println(t);
            }
        } catch (Exception e) {
            System.out.println("Nao foi possivel enviar a mensagem");
        }
    }
}
