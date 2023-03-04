import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.sql.Time;
import java.util.ArrayList;

public class ServerClock extends Thread {

    private int id;
    private int secondarysServers = 1;
    private ArrayList<Long> horariosServidores;

    private int typeServer; // 1 = Principal, 2 = Secundario
    private Time hora;

    public ServerClock(int id, int typeServer, Time hora) {
        this.id = id;
        this.typeServer = typeServer;
        this.hora = hora;
        if (typeServer == 1)
            horariosServidores = new ArrayList<Long>();
    }

    @Override
    public void run() {
        if (typeServer == 1) {
            try {
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

                    // Espera 2 segundos pros relógios secundários conectar
                    mcs.setSoTimeout(2000);
                    while (true) {
                        mcs.receive(pkg);// recebendo os dados enviados via multicast para o endere�o acima
                        if (pkg.getData().length > 0) {
                            secondarysServers++;
                        }
                    }
                } catch (SocketTimeoutException te) {
                    EnviarHora(hora, mcs);

                    ReceberHoras(horariosServidores, mcs);

                    Long totalTime = (long) 0;
                    for (Long time : horariosServidores) {
                        totalTime += time;
                    }

                    hora.setTime(hora.getTime() + (totalTime / secondarysServers));

                    EnviarHora(hora, mcs);

                    System.out.println("Novo horário: " + hora.toString());

                }
            } catch (IOException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } else {
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
                if (pkg.getData().length > 0) {
                    horaString = new String(pkg.getData(), 0, pkg.getLength());
                    timePrimaryServer = Time.valueOf(horaString);
                    System.out.println("Novo horário: " + timePrimaryServer.toString());
                }

            } catch (Exception e) {
                System.out.println("Nao foi possivel enviar a mensagem");
            }
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
            // Recebe o horario de todos os servidores
            mcs.setSoTimeout(2000);
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

    @Override
    public String toString() {
        return ("Id: " + this.id + " Tipo: " + (typeServer == 1 ? "Primário" : "Secundário") + " Hora: "
                + hora.toString());
    }

}
