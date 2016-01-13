import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class AfficheurFlux implements Runnable {

    private final InputStream inputStream;

    AfficheurFlux(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private BufferedReader getBufferedReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void run() {
        BufferedReader br = getBufferedReader(inputStream);
        String ligne = "";
        try {
            while ((ligne = br.readLine()) != null) {
                System.out.println(ligne);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class ShFromJava {
    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("/net/cremi/nodcosta001/test_dataMining/alignement.sh", "-c");
            Process p = pb.start();

            AfficheurFlux fluxSortie = new AfficheurFlux(p.getInputStream());
            AfficheurFlux fluxErreur = new AfficheurFlux(p.getErrorStream());
            new Thread(fluxSortie).start();
            new Thread(fluxErreur).start();
            p.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
