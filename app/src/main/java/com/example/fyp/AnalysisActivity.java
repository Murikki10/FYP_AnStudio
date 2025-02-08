package com.example.fyp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalysisActivity extends Fragment {

    private Spinner actionSpinner; // Dropdown menu for selecting actions
    private TextView captureStatus; // Displays capture status
    private TextView resultText; // Displays workout results
    private Button startCaptureButton; // Button to start capturing
    private Button finishButton; // Button to finish the action
    private PreviewView cameraPreview; // Camera preview display

    private boolean isCapturing = false; // Flag to track capturing state
    private int completedReps = 0; // Simulated completed repetitions count
    private String selectedAction = ""; // User-selected action

    private ExecutorService cameraExecutor; // Executor for camera thread management
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture; // CameraX API instance

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Load the layout file
        View view = inflater.inflate(R.layout.activity_analysis, container, false);

        // Initialize UI elements
        actionSpinner = view.findViewById(R.id.action_spinner);
        captureStatus = view.findViewById(R.id.capture_status);
        resultText = view.findViewById(R.id.result_text);
        startCaptureButton = view.findViewById(R.id.start_capture_button);
        finishButton = view.findViewById(R.id.finish_button);
        cameraPreview = view.findViewById(R.id.camera_preview);

        // Initialize the dropdown menu for selecting actions
        setupActionSpinner();

        // Set button click listeners
        startCaptureButton.setOnClickListener(v -> startCapturing());
        finishButton.setOnClickListener(v -> finishAction());

        // Initialize the camera thread executor
        cameraExecutor = Executors.newSingleThreadExecutor();
        return view;
    }

    // Sets up the dropdown menu, providing action options
    private void setupActionSpinner() {
        List<String> actions = new ArrayList<>();
        actions.add("Select an Action"); // Default option
        actions.add("Squat");
        actions.add("Push-Up");
        actions.add("Jumping Jack");
        actions.add("Plank");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, actions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(adapter);
    }

    // Starts capturing user actions
    private void startCapturing() {
        selectedAction = actionSpinner.getSelectedItem().toString();

        if (selectedAction.equals("Select an Action")) {
            Toast.makeText(getContext(), "Please select an action to begin.", Toast.LENGTH_SHORT).show();
            return;
        }

        isCapturing = true;
        completedReps = 0; // Reset repetitions count
        captureStatus.setText(String.format("Capturing: %s", selectedAction));
        resultText.setText(""); // Clear result display

        // Start CameraX for camera preview
        startCamera();

        Toast.makeText(getContext(), String.format("Started capturing %s...", selectedAction), Toast.LENGTH_SHORT).show();
    }

    // Completes the action and displays the results
    private void finishAction() {
        if (!isCapturing) {
            Toast.makeText(getContext(), "You haven't started capturing yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        isCapturing = false;

        // Simulate analysis results
        String result = String.format(
                "Action: %s\nCompleted Reps: %d\nFeedback: %s",
                selectedAction,
                completedReps,
                provideFeedback(completedReps)
        );

        captureStatus.setText("Capture Complete.");
        resultText.setText(result);

        Toast.makeText(getContext(), "Analysis complete!", Toast.LENGTH_SHORT).show();
    }

    // Simulates feedback generation based on completed repetitions
    private String provideFeedback(int reps) {
        if (reps < 8) {
            return "Try to improve your consistency and aim for more reps.";
        } else if (reps <= 12) {
            return "Good job! Keep maintaining your form for better results.";
        } else {
            return "Excellent work! You're reaching advanced levels!";
        }
    }

    // Starts CameraX and displays the camera preview
    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);

            } catch (Exception e) {
                Log.e("CameraX", "Failed to start camera: " + e.getMessage());
            }
        }, cameraExecutor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}