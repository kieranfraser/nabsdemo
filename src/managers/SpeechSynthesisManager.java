package managers;

import java.io.File;
import java.io.InputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

public class SpeechSynthesisManager {

	private TextToSpeech service;
	private Voice voice;

	public SpeechSynthesisManager() {
		service = new TextToSpeech();
		service.setUsernameAndPassword("25f9b299-1da3-4ae9-962b-0e0ee9fc6f79", "N7jio3JADYlt");
		voice = service.getVoice(Voice.EN_ALLISON.getName()).execute();
	}
	
	public InputStream getSpeech(String text){
		/*AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(service.synthesize(text, voice, AudioFormat.WAV).execute());
			System.out.println(audioInputStream.getFormat());
		} catch (UnsupportedAudioFileException | IOException | RuntimeException e) {
			System.out.println("Text not converted to speech/audio!");
		}*/
		return null;
	}
	
	
}
