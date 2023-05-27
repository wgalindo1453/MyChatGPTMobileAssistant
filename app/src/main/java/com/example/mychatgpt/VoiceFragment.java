package com.example.mychatgpt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoiceFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private ImageView mMicrophoneIcon;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;

    private TextToSpeech mTextToSpeech;

    public VoiceFragment() {
        // Required empty public constructor
    }

    public static VoiceFragment newInstance(String param1, String param2) {
        VoiceFragment fragment = new VoiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mTextToSpeech = new TextToSpeech(getContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                mTextToSpeech.setLanguage(Locale.US);
            }
        });
        // Check for permission
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.RECORD_AUDIO)) {
                // Explain to the user why we need to record the audio
                Toast.makeText(getContext(), "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
            }

            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            // Permission has already been granted
            Toast.makeText(getContext(), "Permissions granted to record audio", Toast.LENGTH_LONG).show();
        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice, container, false);

        //Initialize the Spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_language);
        RecyclerView recyclerChat = view.findViewById(R.id.recycler_chat);
        List<Message> messageList = new ArrayList<>();
        ChatAdapter chatAdapter = new ChatAdapter(messageList);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChat.setAdapter(chatAdapter);



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // You may want to get the language code here from the selected item
                String selectedLanguage = (String) parentView.getItemAtPosition(position);
                // Then you can set the language for your TextToSpeech instance here
                switch (selectedLanguage) {
                    case "English":
                        mTextToSpeech.setLanguage(Locale.US);
                        break;
                    case "French":
                        mTextToSpeech.setLanguage(Locale.FRENCH);
                        break;
                    case "German":
                        mTextToSpeech.setLanguage(Locale.GERMAN);
                        break;
                    case "Spanish":
                        mTextToSpeech.setLanguage(new Locale("es", "ES"));
                        break;
                    // Add more languages here
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // If no language is selected, default to US English
                mTextToSpeech.setLanguage(Locale.US);
            }
            ProgressDialog progressDialog = new ProgressDialog(getActivity());

        });

        // Initialize Speech Recognizer
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());

        // Initialize Speech Recognizer Intent
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //setup the microphone icon
        mMicrophoneIcon = view.findViewById(R.id.microphone_icon);
        mMicrophoneIcon.setVisibility(View.INVISIBLE);
        // Setup the button
        Button buttonVoice = view.findViewById(R.id.button_voice);
        buttonVoice.setOnClickListener(v -> {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            Log.i("VoiceFragment", "Listening...");
            Toast.makeText(getContext(), "Listening...", Toast.LENGTH_SHORT).show();
        });


        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.i("VoiceFragment", "Ready for speech");
                getActivity().runOnUiThread(() -> mMicrophoneIcon.setVisibility(View.VISIBLE));
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.i("VoiceFragment", "Beginning of speech");

            }

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                Log.i("VoiceFragment", "End of speech");
                getActivity().runOnUiThread(() -> mMicrophoneIcon.setVisibility(View.INVISIBLE));
            }

            @Override
            public void onError(int error) {
                Log.i("VoiceFragment", "Error: " + error);
            }

            @Override
            public void onResults(Bundle results) {
                // Here's where you handle the results of speech recognition
                ArrayList<String> matches = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {

                    sendToGptApi(matches.get(0));
                }
            }

            private void sendToGptApi(String text) {
                // Show a progress dialog
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();
                //OkHttpClient client = new OkHttpClient();

                // Define the JSON body of the request
                String json = "{"
                        + "\"model\":\"gpt-3.5-turbo\","
                        + "\"messages\":[{\"role\":\"user\",\"content\":\"" + text + "\"}]"
                        + "}";

                RequestBody requestBody = RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        json);

                // Create the request
                String GPT_4_API_URL = "https://api.openai.com/v1/chat/completions";
                String GPT_4_API_KEY  = "YOUR_API_KEY";
                Request request = new Request.Builder()
                        .url(GPT_4_API_URL)
                        .addHeader("Authorization", "Bearer " + GPT_4_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(requestBody)
                        .build();

                // Enqueue the request (async execution)
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Handle failure
                        e.printStackTrace();
                       //log error
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            // Handle failure
                        } else {
                            // On success
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                //String result = jsonObject.getJSONArray("choices").getJSONObject(0).getString("text").trim();
                                String result = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();
                                Log.i("VoiceFragment", "Result: " + result);
                                convertTextToSpeech(result);
                                getActivity().runOnUiThread(() -> {
                                    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    Date date = new Date(System.currentTimeMillis());
                                    messageList.add(new Message("GPT: " + result, formatter.format(date)));
                                    chatAdapter.notifyDataSetChanged();



                                    // Scroll to the bottom of the chat
                                    final ScrollView scrollChat = view.findViewById(R.id.scroll_chat);
                                    scrollChat.post(() -> scrollChat.fullScroll(View.FOCUS_DOWN));
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    private void convertTextToSpeech(String text) {
                        mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                });
            }



            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });



        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }
}