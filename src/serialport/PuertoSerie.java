package serialport;

import app.Com;
import app.Parameters;
import core.SerialPort;
import java.util.Date;
import java.util.List;
import javax.swing.JLabel;

/**
 *
 * @author sergio
 */
public class PuertoSerie extends Thread {

    private SerialPort puerto = new SerialPort();
    private List<String> listaPuertos;
    private Com com1;
    private float a0, b1, b2, c12, c11, c22;
    private DynamicDataDemo panel;
    private JLabel info;
    private boolean debug = false;
    private String port = "COM2";
    private String baud = "19200";

    public PuertoSerie(DynamicDataDemo panel, String port, String baud) {
        this.panel = panel;
        this.port = port;
        this.baud = baud;
    }

    @Override
    public void run() {
        try {
            /*listaPuertos = puerto.getFreeSerialPort();
            
            Integer c;
            for (String string : listaPuertos) {
            System.out.println(string);
            }*/
            Parameters settings = new Parameters();
            settings.setPort(port);
            settings.setBaudRate(baud);
            /*settings.setParity("N");
            settings.setByteSize("8");
            settings.setStopBits("1");*/

            com1 = new Com(settings);
            com1.setTimeOutSerialPortC(port, 0, 0, 0, 0, 0);

            System.out.println("Listening Serial Port (" + port  + "@" + baud + ")");

            while (true) {
                int start = com1.receiveSingleDataInt();
                System.out.println(new Date() +  ": " + start + "("+ Integer.toBinaryString(start) +")");
                if (start == 1) {
                    break;
                }
            }
            getConstantes();
            while (true) {
                int start = com1.receiveSingleDataInt();
                if (start == 2) {
                    System.out.println(new Date());
                    float p = getPresion();
                    if (debug) {
                        System.out.println(p);
                    }
                    panel.setTitulo(String.format("Presion: %.2fkPa (%.2f Atm)", p, p / 101.3));
                    panel.update(p);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public float getPresion() throws Exception {
        float Padc = parseWord(getBinaryWord(com1.receiveSingleChar(), com1.receiveSingleChar()), false, 10, 0, 0);
        float Tadc = parseWord(getBinaryWord(com1.receiveSingleChar(), com1.receiveSingleChar()), false, 10, 0, 0);

        if (debug) {
            System.out.println("Padc: " + Padc);
            System.out.println("Tadc: " + Tadc);
        }

        float Pcomp = a0 + (b1 + c11 * Padc + c12 * Tadc) * Padc + (b2 + c22 * Tadc) * Tadc;

        Pcomp = Pcomp * ((115f - 50f) / 1023f) + 50f;

        return Pcomp;
    }

    public void getConstantes() throws Exception {
        a0 = parseWord(getBinaryWord(com1.receiveSingleChar(), com1.receiveSingleChar()), true, 12, 3, 0);
        b1 = parseWord(getBinaryWord(com1.receiveSingleChar(), com1.receiveSingleChar()), true, 2, 13, 0);
        b2 = parseWord(getBinaryWord(com1.receiveSingleChar(), com1.receiveSingleChar()), true, 1, 14, 0);
        c12 = parseWord(getBinaryWord(com1.receiveSingleChar(), com1.receiveSingleChar()), true, 0, 13, 9);
        c11 = parseWord(getBinaryWord(com1.receiveSingleChar(), com1.receiveSingleChar()), true, 0, 11, 11);
        c22 = parseWord(getBinaryWord(com1.receiveSingleChar(), com1.receiveSingleChar()), true, 0, 10, 15);

        if (debug) {
            System.out.println("A0: " + a0);
            System.out.println("B1: " + b1);
            System.out.println("B2: " + b2);
            System.out.println("C12: " + c12);
            System.out.println("C11: " + c11);
            System.out.println("C12: " + c22);
        }
    }

    private float parseWord(String word, boolean sign, int integer, int fractional, int padding) {
        int offset = sign ? 1 : 0;
        char si = word.charAt(0);
        String in = new String();
        String fr = new String();
        int i = offset;
        for (; i < integer + offset; i++) {
            in += word.charAt(i);
        }
        in = in.equals("") ? "0" : in;
        for (; i < integer + fractional + offset; i++) {
            fr += word.charAt(i);
        }
        fr = padding(fr, padding);

        int entero = sign && si == '1' ? (~Integer.parseInt(in, 2)) : Integer.parseInt(in, 2);
        float fraccion = sign && si == '1' ? (-1 + getFractional(fr)) : getFractional(fr);

        float suma = entero + fraccion;

        return suma;
    }

    private String getBinaryWord(char MSB, char LSB) {
        int M = MSB & 0x00FF;
        int L = LSB & 0x00FF;
        String binary = zeroPad(Integer.toBinaryString(M), 8) + zeroPad(Integer.toBinaryString(L), 8);
        if (debug) {
            System.out.println("MSB:" + Integer.toHexString(MSB & 0x00FF) + ", LSB: " + Integer.toHexString(LSB & 0x00FF) + ", " + binary);
        }
        return binary;
    }

    private float getFractional(String binary) {
        float suma = 0f;
        for (int i = 0; i < binary.length(); i++) {
            char bit = binary.charAt(i);
            suma += bit == '1' ? (1 / Math.pow(2, i + 1)) : 0f;
        }
        return suma;
    }

    private String zeroPad(String s1, int size) {
        String s = "0000000000000000" + s1;
        return s.substring(s.length() - size);
    }

    private String padding(String s, int p) {
        String pad = new String();
        for (int i = 0; i < p; i++) {
            pad += "0";
        }
        pad += s;
        return pad;
    }
}
