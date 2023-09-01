import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.file.Files;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.SourceDataLine;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public class Main {
    public static void main(String[] args) throws Exception {
        File soundFile = new File("Будь Здоров.mp3");//Звуковой файл
        //voicePower(soundFile);
        try (Model model = new Model("vosk-model-small-ru-0.22");
             InputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(Files.newInputStream(soundFile.toPath())));
             Recognizer recognizer = new Recognizer(model, 16000)) {

            int nbytes;
            byte[] b = new byte[4096];
            while ((nbytes = ais.read(b)) >= 0) {
                if (recognizer.acceptWaveForm(b, nbytes)) {
                    System.out.println(recognizer.getResult());
                } else {
                    System.out.println(recognizer.getPartialResult());
                }
            }

            System.out.println(recognizer.getFinalResult());
        }

        }


    public static void stream(AudioInputStream in, SourceDataLine line) throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }
    public static void voicePower(File soundFile)throws Exception{
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);//Получаем AudioInputStream
        int ch = ais.getFormat().getChannels();
        final float rate = ais.getFormat().getSampleRate();
        final AudioFormat outFormat = new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
        final Info info = new Info(SourceDataLine.class, outFormat);
        final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

        if (line != null) {
            line.open(outFormat);
            line.start();
            stream(getAudioInputStream(outFormat, ais), line);
            line.drain();
            line.stop();
        }
    }
}


