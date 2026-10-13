
package com.civifix.civifix_backend.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import com.civifix.civifix_backend.entity.Priority;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

@Service
public class IssueDetectionService {

    @Value("${model.path}")
    private String modelPath;

    private OrtEnvironment env;
    private OrtSession.SessionOptions options;
    private OrtSession session;

    private static final Map<String, Priority> PRIORITY_MAP = Map.of(
            "DamagedElectricalPoles", Priority.HIGH,
            "FallenTrees", Priority.HIGH,
            "Damaged concrete structures", Priority.HIGH,
            "Potholes and RoadCracks", Priority.HIGH,
            "DamagedRoadSigns", Priority.MEDIUM,
            "DeadAnimalsPollution", Priority.MEDIUM,
            "Garbage", Priority.LOW,
            "Graffitti", Priority.LOW,
            "IllegalParking", Priority.LOW
    );

    @PostConstruct
    public void loadModel() {

        try {
            // ONNX Runtime environment
            env = OrtEnvironment.getEnvironment();

            // Session options
            options = new OrtSession.SessionOptions();

            // Load the ONNX model
            session = env.createSession(modelPath, options);

            System.out.println("YOLO model loaded successfully!");

        } catch (OrtException e) {
            System.err.println("Failed to load YOLO model!");
            e.printStackTrace();
        }
    }
    public Priority getPriority(String issue) {
        return PRIORITY_MAP.getOrDefault(issue, Priority.MEDIUM);
    }

    @PreDestroy
    public void releaseResources(){
        try{
            session.close();
        }catch (Exception e){
            System.err.println("Failed to Release session");
            e.printStackTrace();

        }
    }
    private static final String[] NAMES = {
            "Damaged concrete structures",
            "DamagedElectricalPoles",
            "DamagedRoadSigns",
            "DeadAnimalsPollution",
            "FallenTrees",
            "Garbage",
            "Graffitti",
            "IllegalParking",
            "Potholes and RoadCracks"
    };

    public String detect(Path imagePath) {

        if (session == null) {
            return null;
        }

        try {
            // read photo
            BufferedImage image = ImageIO.read(imagePath.toFile());
            if (image == null) {
                return null;
            }

            // 2. 512x512 resize
            BufferedImage resized = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();
            g.drawImage(image, 0, 0, 512, 512, null);
            g.dispose();


            //read normalized rgb value
            float[] data = new float[3 * 512 * 512];
            for (int y = 0; y < 512; y++) {
                for (int x = 0; x < 512; x++) {
                    int rgb = resized.getRGB(x, y);
                    int i = y * 512 + x;

                    data[i] = ((rgb >> 16) & 255) / 255f;
                    data[512 * 512 + i] = ((rgb >> 8) & 255) / 255f;
                    data[2 * 512 * 512 + i] = (rgb & 255) / 255f;
                }
            }


            String inputName = session.getInputNames().iterator().next();

            try (OnnxTensor tensor = OnnxTensor.createTensor(
                    env, FloatBuffer.wrap(data), new long[]{1, 3, 512, 512});
                 OrtSession.Result result = session.run(
                         Collections.singletonMap(inputName, tensor))) {

                float[][][] output = (float[][][]) result.get(0).getValue();

                float bestScore = 0;
                int bestClass = -1;

                for (int i = 0; i < output[0][0].length; i++) {
                    for (int c = 4; c < output[0].length; c++) {
                        if (output[0][c][i] > bestScore) {
                            bestScore = output[0][c][i];
                            bestClass = c - 4;
                        }
                    }
                }

                if (bestClass >= 0 && bestClass < NAMES.length && bestScore >= 0.25f) {
                    return NAMES[bestClass];
                }
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
