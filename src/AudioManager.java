import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioManager {
    private static Clip clip;

    public boolean playSound(String sound, int loops, boolean playSound) throws IOException, UnsupportedAudioFileException {
        if(playSound){
            try{
                URL file = AudioManager.class.getResource(sound);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.loop(loops);
                clip.start();
                return true;
            }catch(UnsupportedAudioFileException | LineUnavailableException e){
                e.printStackTrace();
                return false;
            }
        }else{
            return false;
        }
    }

    public boolean playSound(String sound, int loops) throws IOException, UnsupportedAudioFileException {
        try{
            URL file = AudioManager.class.getResource(sound);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(loops);
            clip.start();
            return true;
        }catch(UnsupportedAudioFileException | LineUnavailableException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean playSound(String sound) throws IOException, UnsupportedAudioFileException {
        try{
            URL file = AudioManager.class.getResource(sound);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            return true;
        }catch(UnsupportedAudioFileException | LineUnavailableException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean stopSound(boolean stop){
        if(stop){
            try{
                clip.stop();
                return true;
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }else{
            return false;
        }
    }

    public boolean stopSound(){
        try{
            clip.stop();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
