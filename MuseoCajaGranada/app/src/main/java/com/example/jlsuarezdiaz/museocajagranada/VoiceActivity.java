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

package com.example.jlsuarezdiaz.museocajagranada;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Abstract class for voice interaction that encapsulates the management of the ASR and TTS engines.
 * It contains abstract methods for processing the ASR and TTS events that may occur, which may be implemented
 * in a non-abstract subclass to carry out a detailed management.
 *
 * @author Zoraida Callejas, Michael McTear, David Griol
 * @version 3.0, 02/14/16
 *
 * @see http://developer.android.com/reference/android/speech/tts/TextToSpeech.html
 * @see http://developer.android.com/reference/android/speech/tts/UtteranceProgressListener.html
 */


public abstract class VoiceActivity extends AppCompatActivity implements RecognitionListener, OnInitListener {

    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 22;
    private SpeechRecognizer myASR;
    private TextToSpeech myTTS;
    Activity ctx;

    private static final String LOGTAG = "VOICEACTIVITY";



/**********************************************************************************************************************************************************************
 **********************************************************************************************************************************************************************
 *
 * 					AUTOMATIC SPEECH RECOGNITION
 *
 **********************************************************************************************************************************************************************
 **********************************************************************************************************************************************************************/

    /**
     * Creates the speech recognizer and text-to-speech synthesizer instances
     * @see RecognitionListener.java
     * @param ctx context of the interaction
     * */
    public void initSpeechInputOutput(Activity ctx) {

        this.ctx = ctx;
        PackageManager packManager = ctx.getPackageManager();

        setTTS();

        // Find out whether speech recognition is supported
        List<ResolveInfo> intActivities = packManager.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (intActivities.size() != 0 || "generic".equals(Build.BRAND.toLowerCase(Locale.US))) {
            myASR = SpeechRecognizer.createSpeechRecognizer(ctx);
            myASR.setRecognitionListener(this);
        }
        else
            myASR = null;
    }

    /**
     * Checks whether the user has granted permission to the microphone. If the permission has not been provided,
     * it is requested. The result of the request (whether the user finally grants the permission or not)
     * is processed in the onRequestPermissionsResult method.
     *
     * This is necessary from Android 6 (API level 23), in which users grant permissions to apps
     * while the app is running. In previous versions, the permissions were granted when installing the app
     * See: http://developer.android.com/intl/es/training/permissions/requesting.html
     */
    public void checkASRPermission() {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // If  an explanation is required, show it
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) ctx, Manifest.permission.RECORD_AUDIO))
                showRecordPermissionExplanation();

            // Request the permission.
            ActivityCompat.requestPermissions((Activity) ctx, new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO); //Callback in "onRequestPermissionResult"
        }
    }

    /**
     * Processes the result of the record audio permission request. If it is not granted, the
     * abstract method "onRecordAudioPermissionDenied" method is invoked. Such method must be implemented
     * by the subclasses of VoiceActivity.
     * More info: http://developer.android.com/intl/es/training/permissions/requesting.html
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(LOGTAG, "Record audio permission granted");
            } else {
                Log.i(LOGTAG, "Record audio permission denied");
                onRecordAudioPermissionDenied();
            }
        }
    }

    /**
     * Invoked when it is necessary to explain to the user why we need permission to record audio on the device
     */
    public abstract void showRecordPermissionExplanation();

    /**
     * Invoked when the permission to record audio on the device is denied
     */
    public abstract void onRecordAudioPermissionDenied();


    /**
     * Starts speech recognition after checking the ASR parameters
     *
     * @param language Language used for speech recognition (e.g. Locale.ENGLISH)
     * @param languageModel Type of language model used (free form or web search)
     * @param maxResults Maximum number of recognition results
     * @exception An exception is raised if the language specified is not available or the other parameters are not valid
     */
    public void listen(final Locale language, final String languageModel, final int maxResults) throws Exception
    {
        checkASRPermission();

        if((languageModel.equals(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM) || languageModel.equals(RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)) && (maxResults>=0))
        {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            // Specify the calling package to identify the application
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, ctx.getPackageName());
            //Caution: be careful not to use: getClass().getPackage().getName());

            // Specify language model
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, languageModel);

            // Specify how many results to receive. Results listed in order of confidence
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults);

            // Specify recognition language
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);

            myASR.startListening(intent);

        }
        else {
            Log.e(LOGTAG, "Invalid params to listen method");
            throw new Exception("Invalid params to listen method"); //If the input parameters are not valid, it throws an exception
        }

    }




    /**
     * Stops listening to the user
     */
    public void stopListening(){
        myASR.stopListening();
    }

    /********************************************************************************************************
     * This class implements the {@link android.speech.RecognitionListener} interface,
     * thus it implement its methods. However not all of them were interesting to us:
     * ******************************************************************************************************
     */

    @SuppressLint("InlinedApi")
	/*
	 * (non-Javadoc)
	 *
	 * Invoked when the ASR provides recognition results
	 *
	 * @see android.speech.RecognitionListener#onResults(android.os.Bundle)
	 */
    @Override
    public void onResults(Bundle results) {
        if(results!=null){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {  //Checks the API level because the confidence scores are supported only from API level 14:
                //http://developer.android.com/reference/android/speech/SpeechRecognizer.html#CONFIDENCE_SCORES
                //Processes the recognition results and their confidences
                processAsrResults(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION), results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES));
                //											Attention: It is not RecognizerIntent.EXTRA_RESULTS, that is for intents (see the ASRWithIntent app)
            }
            else {
                //Processes the recognition results and their confidences
                processAsrResults(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION), null);
            }
        }
        else
            //Processes recognition errors
            processAsrError(SpeechRecognizer.ERROR_NO_MATCH);
    }

    /*
     * (non-Javadoc)
     *
     * Invoked when the ASR is ready to start listening
     *
     * @see android.speech.RecognitionListener#onReadyForSpeech(android.os.Bundle)
     */
    @Override
    public void onReadyForSpeech(Bundle arg0) {
        processAsrReadyForSpeech();
    }

    /*
     * (non-Javadoc)
     *
     * Invoked when the ASR encounters an error
     *
     * @see android.speech.RecognitionListener#onError(int)
     */
    @Override
    public void onError(int errorCode) {
        processAsrError(errorCode);
    }

    /*
     * (non-Javadoc)
     * @see android.speech.RecognitionListener#onBeginningOfSpeech()
     */
    @Override
    public void onBeginningOfSpeech() {	}

    /*
     * (non-Javadoc)
     * @see android.speech.RecognitionListener#onBufferReceived(byte[])
     */
    @Override
    public void onBufferReceived(byte[] buffer) { }

    /*
     * (non-Javadoc)
     * @see android.speech.RecognitionListener#onBeginningOfSpeech()
     */
    @Override
    public void onEndOfSpeech() {}

    /*
     * (non-Javadoc)
     * @see android.speech.RecognitionListener#onEvent(int, android.os.Bundle)
     */
    @Override
    public void onEvent(int arg0, Bundle arg1) {}

    /*
     * (non-Javadoc)
     * @see android.speech.RecognitionListener#onPartialResults(android.os.Bundle)
     */
    @Override
    public void onPartialResults(Bundle arg0) {}

    /*
 * (non-Javadoc)
 * @see android.speech.RecognitionListener#onRmsChanged(float)
 */
    @Override
    public void onRmsChanged(float arg0) {
    }

    /**
     * Processes the ASR recognition results
     * @param nBestList	List of the N recognition results
     * @param nBestConfidences List of the N corresponding confidences
     */
    public abstract void processAsrResults(ArrayList<String> nBestList, float [] nBestConfidences);

    /**
     * Processes the situation in which the ASR engine is ready to listen
     */
    public abstract void processAsrReadyForSpeech();

    /**
     * Processes ASR error situations
     * @param errorCode code of the error (constant of the {@link android.speech.SpeechRecognizer} class
     */
    public abstract void processAsrError(int errorCode);




/**********************************************************************************************************************************************************************
 **********************************************************************************************************************************************************************
 *
 * 					TEXT TO SPEECH
 *
 **********************************************************************************************************************************************************************
 **********************************************************************************************************************************************************************/

    /**
     * Starts the TTS engine. It is work-around to avoid implementing the UtteranceProgressListener abstract class.
     *
     * @author Method by Greg Milette (comments incorporated by us). Source: https://github.com/gast-lib/gast-lib/blob/master/library/src/root/gast/speech/voiceaction/VoiceActionExecutor.java
     * @see See the problem here: http://stackoverflow.com/questions/11703653/why-is-utteranceprogresslistener-not-an-interface
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void setTTS()
    {
        myTTS = new TextToSpeech(ctx,(OnInitListener) this);

		/*
		 * The listener for the TTS events varies depending on the Android version used:
		 * the most updated one is UtteranceProgressListener, but in SKD versions
		 * 15 or earlier, it is necessary to use the deprecated OnUtteranceCompletedListener
		 */

        if (Build.VERSION.SDK_INT >= 15)
        {
            myTTS.setOnUtteranceProgressListener(new UtteranceProgressListener()
            {
                @Override
                public void onDone(String utteranceId) //TTS finished synthesizing
                {
                    onTTSDone(utteranceId);
                }

                @Override
                public void onError(String utteranceId) //TTS encountered an error while synthesizing
                {
                    onTTSError(utteranceId);
                }

                @Override
                public void onStart(String utteranceId) //TTS has started synthesizing
                {
                    onTTSStart(utteranceId);
                }
            });
        }
        else
        {
            myTTS.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener()
            {
                @Override
                public void onUtteranceCompleted(final String utteranceId)
                {
                    onTTSDone(utteranceId);			//Earlier SDKs only consider the onTTSDone event
                }
            });
        }
    }

    /**
     * Invoked when the utterance uttId has successfully completed processing
     */
    public abstract void onTTSDone(String uttId);


    /**
     * Invoked when an error has occurred while processing the utterance uttId
     */
    public abstract void onTTSError(String uttId);

    /**
     * Invoked when the utterance uttId "starts" as perceived by the user
     */
    public abstract void onTTSStart(String uttId);

    /**
     * Sets the locale for speech synthesis taking into account the language and country codes
     * If the <code>countryCode</code> is null, it just sets the language, if the
     * <code>languageCode</code> is null, it uses the default language of the device
     * If any of the codes are not valid, it uses the default language
     *
     * @param languageCode a String representing the language code, e.g. EN
     * @param countryCode a String representing the country code for the language used, e.g. US.
     * @throws Exception when the codes supplied cannot be used and the default locale is selected
     */
    public void setLocale(String languageCode, String countryCode) throws Exception {
        if(languageCode==null)
        {
            setLocale();
            throw new Exception("Language code was not provided, using default locale");
        }
        else{
            if(countryCode==null)
                setLocale(languageCode);
            else {
                Locale lang = new Locale(languageCode, countryCode);
                if (myTTS.isLanguageAvailable(lang) == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE )
                    myTTS.setLanguage(lang);
                else
                {
                    setLocale();
                    throw new Exception("Language or country code not supported, using default locale");
                }
            }
        }
    }

    /**
     * Sets the locale for speech synthesis taking into account the language code
     * If the code is null or not valid, it uses the default language of the device
     *
     * @param languageCode a String representing the language code, e.g. EN
     * @throws Exception when the code supplied cannot be used and the default locale is selected
     */
    public void setLocale(String languageCode) throws Exception {
        if(languageCode==null)
        {
            setLocale();
            throw new Exception("Language code was not provided, using default locale");
        }
        else {
            Locale lang = new Locale(languageCode);
            if (myTTS.isLanguageAvailable(lang) != TextToSpeech.LANG_MISSING_DATA && myTTS.isLanguageAvailable(lang) != TextToSpeech.LANG_NOT_SUPPORTED)
                myTTS.setLanguage(lang);
            else
            {
                setLocale();
                throw new Exception("Language code not supported, using default locale");
            }
        }
    }

    /**
     * Sets the default language of the device as locale for speech synthesis
     */
    public void setLocale(){
        myTTS.setLanguage(Locale.getDefault());
    }

    /**
     * Synthesizes a text in the language indicated (or in the default language of the device
     * it it is not available)
     *
     * @param languageCode language for the TTS, e.g. EN
     * @param countryCode country for the TTS, e.g. US
     * @param text string to be synthesized
     * @param id integer that identifies the prompt uniquely
     * @throws Exception when the codes supplied cannot be used and the default locale is selected
     */
    public void speak(String text, String languageCode, String countryCode, Integer id) throws Exception {
        setLocale(languageCode, countryCode);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id.toString());
        myTTS.speak(text, TextToSpeech.QUEUE_ADD, params);
    }

    /**
     * Synthesizes a text in the language indicated (or in the default language of the device
     * if it is not available)
     *
     * @param languageCode language for the TTS, e.g. EN
     * @param text string to be synthesized
     * @param id integer that identifies the prompt uniquely
     * @throws Exception when the code supplied cannot be used and the default locale is selected
     */
    public void speak(String text, String languageCode, Integer id) throws Exception {
        setLocale(languageCode);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id.toString());
        myTTS.speak(text, TextToSpeech.QUEUE_ADD, params);
    }

    /**
     * Synthesizes a text using the default language of the device
     *
     * @param text string to be synthesized
     * @param id integer that identifies the prompt uniquely
     */
    public void speak(String text, Integer id){
        setLocale();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id.toString());
        myTTS.speak(text, TextToSpeech.QUEUE_ADD, params);
    }

    /**
     * Stops the synthesizer if it is speaking
     */
    public void stop(){
        if(myTTS.isSpeaking())
            myTTS.stop();
    }

    /**
     * Stops the speech synthesis and recognition engines. It is important to call it, as
     * it releases the native resources used.
     */
    public void shutdown(){
        myTTS.stop();
        myTTS.shutdown();
        myTTS=null;			/*
		 						This is necessary in order to force the creation of a new TTS instance after shutdown.
		 						It is useful for handling runtime changes such as a change in the orientation of the device,
		 						as it is necessary to create a new instance with the new context.
		 						See here: http://developer.android.com/guide/topics/resources/runtime-changes.html
							*/
        myASR.stopListening();
        myASR.destroy();
        myASR=null;
    }

    /*
     * A <code>TextToSpeech</code> instance can only be used to synthesize text once
     * it has completed its initialization.
     * (non-Javadoc)
     * @see android.speech.tts.TextToSpeech.OnInitListener#onInit(int)
     */
    @Override
    public void onInit(int status) {
        if(status != TextToSpeech.ERROR){
            setLocale();
        }
        else
        {
            Log.e(LOGTAG, "Error creating the TTS");
        }

    }

}


