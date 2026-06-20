package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "AprilTag Camera Test", group = "Vision")
public class AprilTagTesting extends LinearOpMode {

    // This name must match your Robot Configuration webcam name exactly.
    private static final String WEBCAM_NAME = "Webcam 1";

    // Change this to the real black-square size of your printed AprilTag.
    // Example: if the black square is 2 inches wide, keep 2.0.
    private static final double CUSTOM_TAG_SIZE_INCHES = 2.0;

    // Custom AprilTag IDs you want to support.
    private static final int FIRST_CUSTOM_TAG_ID = 0;
    private static final int LAST_CUSTOM_TAG_ID = 50;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {

        /*
         * 1. Create the AprilTag processor.
         * This is the part that detects AprilTags from the camera image.
         */
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setTagLibrary(createTagLibrary())
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .build();

        /*
         * Optional:
         * Decimation changes processing speed vs detection distance.
         * Lower number = better far detection, but slower.
         * Higher number = faster, but worse far detection.
         */
        aprilTag.setDecimation(2);

        /*
         * 2. Create the VisionPortal.
         * VisionPortal opens the camera and sends frames to the AprilTag processor.
         */
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, WEBCAM_NAME))
                .addProcessor(aprilTag)
                .build();

        /*
         * 3. INIT loop.
         * Stay here before pressing START.
         * This helps the Camera Stream button appear in the Driver Hub menu.
         */
        while (opModeInInit()) {
            telemetry.addLine("Camera Ready");
            telemetry.addData("Camera State", visionPortal.getCameraState());
            telemetry.addData("Tag Size", "%.1f inches", CUSTOM_TAG_SIZE_INCHES);
            telemetry.addLine("Open 3-dot menu for Camera Stream before START");
            telemetry.update();

            sleep(50);
        }

        /*
         * 4. Main loop.
         * After pressing START, continuously show AprilTag values.
         */
        while (opModeIsActive()) {
            telemetryAprilTag();
            telemetry.update();

            sleep(50);
        }

        /*
         * 5. Close the camera when the OpMode ends.
         */
        visionPortal.close();
    }

    /*
     * This creates the AprilTag library.
     *
     * A tag library tells FTC:
     * - which tag IDs exist
     * - what each tag is called
     * - how physically large each tag is
     *
     * Without correct tag size, distance/range can be wrong.
     */
    private AprilTagLibrary createTagLibrary() {
        AprilTagLibrary.Builder tagLibraryBuilder = new AprilTagLibrary.Builder()
                .setAllowOverwrite(true);

        /*
         * Add your own custom tags.
         * This adds ID 0 to ID 50.
         */
        for (int id = FIRST_CUSTOM_TAG_ID; id <= LAST_CUSTOM_TAG_ID; id++) {
            tagLibraryBuilder.addTag(
                    id,
                    "Custom Tag " + id,
                    CUSTOM_TAG_SIZE_INCHES,
                    DistanceUnit.INCH
            );
        }

        /*
         * Add official FTC game tags.
         * This lets your robot detect official field AprilTags too.
         */
        tagLibraryBuilder.addTags(AprilTagGameDatabase.getCurrentGameTagLibrary());

        /*
         * Add FTC sample tags.
         * Useful if you are testing with sample AprilTags.
         */
        tagLibraryBuilder.addTags(AprilTagGameDatabase.getSampleTagLibrary());

        return tagLibraryBuilder.build();
    }

    /*
     * This method prints AprilTag values to Driver Hub telemetry.
     */
    private void telemetryAprilTag() {
        List<AprilTagDetection> detections = aprilTag.getDetections();

        telemetry.addLine("Camera Ready");
        telemetry.addLine();

        if (detections.size() == 0) {
            telemetry.addLine("No AprilTag detected");
            telemetry.addLine();
        }

        for (AprilTagDetection detection : detections) {

            /*
             * metadata != null means the tag exists in the library.
             * ftcPose != null means FTC calculated position/rotation data.
             */
            if (detection.metadata != null && detection.ftcPose != null) {

                telemetry.addLine(String.format("==== (ID %d) %s",
                        detection.id,
                        detection.metadata.name));

                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f (inch)",
                        detection.ftcPose.x,
                        detection.ftcPose.y,
                        detection.ftcPose.z));

                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f (deg)",
                        detection.ftcPose.pitch,
                        detection.ftcPose.roll,
                        detection.ftcPose.yaw));

                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f (inch, deg, deg)",
                        detection.ftcPose.range,
                        detection.ftcPose.bearing,
                        detection.ftcPose.elevation));

                telemetry.addLine();

            } else {
                telemetry.addLine(String.format("==== (ID %d) no pose data", detection.id));
                telemetry.addLine("Tag ID not in library or tag size missing");
                telemetry.addLine();
            }
        }

        telemetry.addLine("key:");
        telemetry.addLine("XYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");
    }
}