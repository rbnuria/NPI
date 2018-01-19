package conversandroid;

/*
 *  Copyright 2016 Zoraida Callejas, Michael McTear and David Griol
 *
 *  This file is part of the Conversandroid Toolkit, from the book:
 *  The Conversational Interface, Michael McTear, Zoraida Callejas and David Griol
 *  Springer 2016 <https://github.com/zoraidacallejas/ConversationalInterface/>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIOutputContext;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;


/**
 * Receives a spoken input and shows its corresponding semantic parsing using
 * the technology of API.AI
 *
 * @author Michael McTear, Zoraida Callejas and David Griol
 * @version 4.1, 05/14/16
 *
 */

public class MainActivity extends VoiceActivity {

	private static final String LOGTAG = "UNDERSTAND";
	private TextView resultTextView;
	private ImageView dialogImageView;
	private AIDataService aiDataService=null;

	//TODO: INSERT YOUR CLIENT ACCESS KEY
	private final String accessToken = "5cc15a6fe3fc4721bca367dae209d089";
	private final String subscriptionKey = "Any String is valid here since April 2016";

	private static Integer ID_PROMPT_QUERY = 0;	//Id chosen to identify the prompts that involve posing questions to the user
	private static Integer ID_PROMPT_INFO = 1;	//Id chosen to identify the prompts that involve only informing the user
	private long startListeningTime = 0; // To skip errors (see processAsrError method)

	private String lang = "ES";

	/**
	 * Sets up the activity initializing the GUI, the ASR and TTS
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//Set layout
		setContentView(R.layout.main);

		//Initialize the speech recognizer and synthesizer
		initSpeechInputOutput(this);

		//Set up the speech button
		setSpeakButton();

		//Set up text view to display results
		resultTextView = (TextView) findViewById(R.id.resultTextView);
		//dialogImageView = (ImageView) findViewById(R.id.dialogImageView);

		//Api.ai configuration parameters (the subscriptionkey is not longer mandatory, so you
		//can use the new constructor without that parameter or keep this one which accepts any
		//subscription key
		final AIConfiguration config = new AIConfiguration(accessToken,
				subscriptionKey, AIConfiguration.SupportedLanguages.English,
				AIConfiguration.RecognitionEngine.System);
		aiDataService = new AIDataService(this, config);
	}

	/**
	 * Initializes the search button and its listener. When the button is pressed, a feedback is shown to the user
	 * and the recognition starts
	 */
	private void setSpeakButton() {

		// gain reference to speak button
		ImageButton speak = (ImageButton) findViewById(R.id.speech_btn);
		speak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					//Show a feedback to the user indicating that the app has started to listen
					indicateListening();
					startListening();
			}
		});
	}

	/**
	 * Explain to the user why we need their permission to record audio on the device
	 * See the checkASRPermission in the VoiceActivity class
	 */
	public void showRecordPermissionExplanation(){
		Toast.makeText(getApplicationContext(), "UNDERSTAND must access the microphone in order to perform speech recognition", Toast.LENGTH_SHORT).show();
	}

	/**
	 * If the user does not grant permission to record audio on the device, a message is shown and the app finishes
	 */
	public void onRecordAudioPermissionDenied(){
		Toast.makeText(getApplicationContext(), "Sorry, UNDERSTAND cannot work without accessing the microphone", Toast.LENGTH_SHORT).show();
		System.exit(0);
	}

	/**
	 * Starts listening for any user input.
	 * When it recognizes something, the <code>processAsrResult</code> method is invoked. 
	 * If there is any error, the <code>processAsrError</code> method is invoked.
	 */
	private void startListening(){

		if(deviceConnectedToInternet()){
			try {
				
				/*Start listening, with the following default parameters:
					* Recognition model = Free form, 
					* Number of results = 1 (we will use the best result to perform the search)
					*/
				startListeningTime = System.currentTimeMillis();
				listen(Locale.ENGLISH, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, 1); //Start listening
			} catch (Exception e) {
				Log.e(LOGTAG, e.getMessage());
			}
		} else {
			Log.e(LOGTAG, "Device not connected to Internet");
		}
	}

	/**
	 * Provides feedback to the user to show that the app is listening:
	 * 		* It changes the color and the message of the speech button
	 *      * It synthesizes a voice message
	 */
	private void indicateListening() {
		ImageButton button = (ImageButton) findViewById(R.id.speech_btn); //Obtains a reference to the button
		//button.setText(getResources().getString(R.string.speechbtn_listening)); //Changes the button's message to the text obtained from the resources folder
        //button.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.speechbtn_listening),PorterDuff.Mode.MULTIPLY);  //Changes the button's background to the color obtained from the resources folder
	}

	/**
	 * Provides feedback to the user to show that the app is idle:
	 * 		* It changes the color and the message of the speech button
	 */
	private void changeButtonAppearanceToDefault(){
		ImageButton button = (ImageButton) findViewById(R.id.speech_btn); //Obtains a reference to the button
		//button.setText(getResources().getString(R.string.speechbtn_default)); //Changes the button's message to the text obtained from the resources folder
        //button.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.speechbtn_default),PorterDuff.Mode.MULTIPLY); 	//Changes the button's background to the color obtained from the resources folder
	}

	/**
	 * Provides feedback to the user when the ASR encounters an error
	 */
	@Override
	public void processAsrError(int errorCode) {
		changeButtonAppearanceToDefault();

		//Possible bug in Android SpeechRecognizer: NO_MATCH errors even before the the ASR
		// has even tried to recognized. We have adopted the solution proposed in:
		// http://stackoverflow.com/questions/31071650/speechrecognizer-throws-onerror-on-the-first-listening
		long duration = System.currentTimeMillis() - startListeningTime;
		if (duration < 500 && errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
			Log.e(LOGTAG, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. Going to ignore the error");
			stopListening();
		}
		else {
            String errorMsg = "";
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMsg = "Error grabando audio";//"Audio recording error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMsg = "Error desconocido en el lado del cliente";//"Unknown client side error";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMsg = "Permisos insuficientes";//"Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMsg = "Error de red.";//"Network related error";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMsg = "Tiempo de conexión agotado.";//"Network operation timed out";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMsg = "No hay coincidencias ";//"No recognition result matched";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMsg = "Servicio de reconocimiento ocupado";//"RecognitionService busy";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorMsg = "Error del sevidor";//"Server sends error status";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMsg = "No se detectó ninguna entrada";//"No speech input";
                    break;
                default:
                    errorMsg = ""; //Another frequent error that is not really due to the ASR, we will ignore it
            }
            if (errorMsg != "") {
                Log.e(LOGTAG, "Error when attempting to listen: " + errorMsg);

                try {
                    speak(errorMsg, lang, ID_PROMPT_INFO);
                } catch (Exception e) {
                    Log.e(LOGTAG, "English not available for TTS, default language used instead");
                }
            }
        }

	}

	@Override
	public void processAsrReadyForSpeech() { }

	/**
	 * Sends the best recognition result to api.ai
	 */
	@Override
	public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {

		if (nBestList != null) {
			if (nBestList.size() > 0) {
				String userQuery = nBestList.get(0); //We will use the best result
				changeButtonAppearanceToDefault();
				apiSLU(userQuery);
			}
		}
	}

	/**
	 * Connects to api.ai and indicates how to process the response with the semantic parsing
	 * @param userQuery recognized utterance
     */
	private void apiSLU(String userQuery) {

		new AsyncTask<String,Void,AIResponse>() {

            /**
             * Connects to the api.ai service
             * @param strings Contains the user request
             * @return language understanding result
             */
			@Override
			protected AIResponse doInBackground(String... strings) {
				final String request = strings[0];
				try {
					final AIRequest aiRequest = new AIRequest(request);
					final AIResponse response = aiDataService.request(aiRequest);
					Log.d(LOGTAG,"Request: "+aiRequest);
					Log.d(LOGTAG,"Response: "+response);


					return response;
				} catch (AIServiceException e) {
                    try {
                        speak("Could not retrieve a response from API.AI", lang, ID_PROMPT_INFO);
                        Log.e(LOGTAG,"Problems retrieving a response");
                    } catch (Exception ex) {
                        Log.e(LOGTAG, "English not available for TTS, default language used instead");
                    }
				}
				return null;
			}

			/**
			 * The semantic parsing is decomposed in its different elements and shown in a textview
			 * @param aiResponse semantic parsing
             */
			@Override
			protected void onPostExecute(AIResponse aiResponse) {
				if (aiResponse != null) {
					// process aiResponse here
					// extracts intent and parameters - we can change this to do other things

					Result result = aiResponse.getResult();
					Log.d(LOGTAG,"Result: "+result);
					Log.d(LOGTAG,"Parameters: "+result.getParameters());

					// Get parameters
					String parameterString = "";
					if (result.getParameters() != null && !result.getParameters().isEmpty()) {
						for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
							parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
						}

					}

                    Log.d(LOGTAG,parameterString);

					//dialogImageView.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/megamandelbrot.png", null, getPackageName())));

					// Show results in TextView.

					List<AIOutputContext> contexts = result.getContexts();
					String context_str = "";
					for(AIOutputContext a : contexts){
						context_str+= a.getName() + "\n";
					}
				/*	resultTextView.setText("Query: " + result.getResolvedQuery() +
							"\nAction: " + result.getAction() +
							"\nParameters: " + parameterString +
							"\nRespuesta¿?: " + result.getFulfillment().getSpeech() +
							"\nContext size: " + contexts.size() +
							"\nContextos¿?: " + context_str
					);*/


					String response = result.getFulfillment().getSpeech();



					try {
						speak(response, lang, ID_PROMPT_INFO);
					} catch (Exception e) {
						Log.e(LOGTAG, "TTS not accessible");
					}
				}
			}
		}.execute(userQuery);

	}

	/**
	 * Checks whether the device is connected to Internet (returns true) or not (returns false)
	 *
	 * @author http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
	 */
	public boolean deviceConnectedToInternet() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}

	/**
	 * Shuts down the TTS engine when finished
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		shutdown();
	}

	/**
	 * Invoked when the TTS has finished synthesizing.
	 *
	 * In this case, it starts recognizing if the message that has just been synthesized corresponds to a question (its id is ID_PROMPT_QUERY),
	 * and does nothing otherwise.
	 *
	 * @param uttId identifier of the prompt that has just been synthesized (the id is indicated in the speak method when the text is sent
	 * to the TTS engine)
	 */
	@Override
	public void onTTSDone(String uttId) {
		if(uttId.equals(ID_PROMPT_QUERY.toString()))
			startListening();

	}

	/**
	 * Invoked when the TTS encounters an error.
	 *
	 * In this case it just writes in the log.
	 */
	@Override
	public void onTTSError(String uttId) {
		Log.e(LOGTAG, "TTS error");
	}

	/**
	 * Invoked when the TTS starts synthesizing
	 *
	 * In this case it just writes in the log.
	 */
	@Override
	public void onTTSStart(String uttId) {
		Log.e(LOGTAG, "TTS starts speaking");
	}
} 

