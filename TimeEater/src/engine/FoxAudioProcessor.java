package engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import fox.adds.Out;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;


public class FoxAudioProcessor {
	private static Map<String, File> musicMap = new LinkedHashMap<String, File>();
	private static Map<String, File> soundMap = new LinkedHashMap<String, File>();
	
	private static JavaSoundAudioDevice auDevMusic;
	private static AdvancedPlayer musicPlayer;
	private static Player soundPlayer;
	
	private static Boolean soundEnabled = true, musicEnabled = true;
	private static Float gVolume = 1f;
	
	
	public static void addSound(String name, File audioFile) {soundMap.put(name, audioFile);}
	
	public static void addMusic(String name, File audioFile) {musicMap.put(name, audioFile);}
	
	
	public static void playSound(String trackName) {
		if (!soundEnabled) return;
		
		if (soundMap.containsKey(trackName)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try (InputStream fis = new FileInputStream(soundMap.get(trackName).toString())) {
						soundPlayer = new Player(fis);
						soundPlayer.play();
					} catch (IOException | JavaLayerException e) {e.printStackTrace();}
					
//					javafx.scene.media.Media sound = new javafx.scene.media.Media(soundMap.get(trackName).toURI().toString());
//					MediaPlayer soundPlayer = new MediaPlayer(sound);
//			        soundPlayer.setVolume(gVolume);
//			        soundPlayer.play();
				}
			}).start();
		}
	}
	
	private static void playMusic(String trackName, Boolean rep) {
//		System.out.println("Into playMusic...");
		if (!musicEnabled) {return;}
		
//		System.out.println("chek music existing...");
		if (musicMap.containsKey(trackName)) {
			stopMusic();
			
		    // wav file playes
//		    AudioClip audioClip = AppletManager.applet.getAudioClip(soundFile.toURL());
//		    audioClip.play();
//			System.out.println("start new music-thread...");
	        new Thread(new Runnable() {
				@Override
				public void run() {
//					try (InputStream fis = new FileInputStream(musicMap.get(trackName).toString())) {
//						musicPlayer2 = new Player(fis);
//						musicPlayer2.play();
//					} catch (IOException | JavaLayerException e) {e.printStackTrace();
//					} finally {musicPlayer2.close();}
					auDevMusic = new JavaSoundAudioDevice();
					try (InputStream potok = new FileInputStream(musicMap.get(trackName))) {
						auDevMusic.setLineGain(0.75f);
						musicPlayer = new AdvancedPlayer(potok, auDevMusic);
						PlaybackListener listener = new PlaybackListener() {
					        @Override
					        public void playbackStarted(PlaybackEvent arg0) {
					        	System.out.println("Playback started..");
					        }

					        @Override
					        public void playbackFinished(PlaybackEvent event) {
					        	System.out.println("Playback finished..");
					        }
					    };
					    musicPlayer.setPlayBackListener(listener);					    
						musicPlayer.play();
					} catch (Exception e) {e.printStackTrace();}			        
				}
			}).start();
		
			Out.Print("Media: music: the '" + trackName + "' exist into musicMap and play now...");
		} else {Out.Print("Media: music: music '" + trackName + "' is NOT exist in the musicMap");}
	}
	
	public static void nextMusic() {
		int playingNowCount = new Random().nextInt(musicMap.size());
		System.out.println("Music maps sise: " + musicMap.size() + ". random count is: " + playingNowCount);
		
		int tmpCount = 0;
		for (String musikName : musicMap.keySet()) {
			if (tmpCount == playingNowCount) {
				playMusic(musikName, true);
				break;
			}
			tmpCount++;
		}
	}
	
	public static void pauseMusic() {
//		try {
//			for (Entry<String, OggClip> iterable_element : musicMap.entrySet()) {
//				if (!iterable_element.getValue().isPaused()) {musicMap.get(iterable_element.getKey()).pause();}
//			}
//		} catch (Exception e) {Out.Print(Media.class, 2, "Can`t paused all music: " + e.getLocalizedMessage());}
	}
	
	public static void stopMusic() {
//		for (Entry<String, OggClip> iterable_element : musicMap.entrySet()) {
//		if (iterable_element.getValue() == null) {
//			Out.Print(Media.class, 2, "iterable_element into musicMap is NULL. Returning...");
//			return;
//		}
//
//		try {iterable_element.getValue().stop();} catch (Exception e) {/* IGNORE */}
		try {
			musicPlayer.stop();
//			musicPlayer2.close();
		} catch (Exception e) {/* IGNORE */}
	}
	
	public static void resumeMusic() {
		if (!musicEnabled) return;
		
	}
	
	
	public static void setVolume(Float vol) {gVolume = vol;}
	public static Float getVolume() {return gVolume;}
	
	public static void setSoundEnabled(Boolean _soundEnabled) {soundEnabled = _soundEnabled;}
	public static Boolean getSoundEnabled() {return soundEnabled;}

	public static void setMusicEnabled(Boolean _musicEnabled) {musicEnabled = _musicEnabled;}
	public static Boolean getMusicEnabled() {return musicEnabled;}
}
