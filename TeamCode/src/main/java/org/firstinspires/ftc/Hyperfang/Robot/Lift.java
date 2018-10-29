package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.Hyperfang.Sensors.MGL;

public class Lift {
    //Lift variables which need to have accessibility.
    public static DcMotor liftMotor;
    public static DcMotor ratchetMotor;
    public MGL mgl;

    public enum LEVEL {
        GROUND,
        LATCH,
        TOP,
    }

    private LEVEL pos;

    private Servo ratchetServo;
    private Servo hook; //Possibly change to continuous to ease Tele-Op.

    private OpMode mOpMode;

    //Initializes the lift objects.
    public Lift(OpMode opMode){
        mOpMode = opMode;
        liftMotor = mOpMode.hardwareMap.get(DcMotor.class, "vLift");
        ratchetMotor = mOpMode.hardwareMap.get(DcMotor.class, "ratchet");
        ratchetServo = mOpMode.hardwareMap.get(Servo.class, "rServo");
        hook = mOpMode.hardwareMap.get(Servo.class, "hook");

        mgl = new MGL(opMode);
        pos = LEVEL.GROUND;
    }

    //Moves to a specified position of the lift depending on the current position.
    public void moveTo(LEVEL lvl, double power, DcMotor motor) {
        switch (lvl) {
            case GROUND:
                if(!pos.equals(lvl)) move(power, motor);

                if (pos.equals(lvl)) {
                    motor.setPower(0);
                    setPosition(LEVEL.GROUND);
                    break;
                }
                break;
            case LATCH:
                if(!pos.equals(lvl)) move(power, motor);

                if (pos.equals(lvl)) {
                    motor.setPower(0);
                    setPosition(LEVEL.LATCH);
                    break;
                }
                break;
            case TOP:
                if(!pos.equals(lvl)) move(power, motor);

                if (pos.equals(lvl)) {
                    motor.setPower(0);
                    setPosition(LEVEL.TOP);
                    break;
                }
                break;
        }
    }

    //Moves our lift/ratchet up or down depending on the given power.
    public void move(double power, DcMotor motor) {
        mOpMode.telemetry.addData("Motor: ", motor.getDeviceName());
        switch (pos) {
            default:
                motor.setPower(power);
                break;

            case GROUND:
                mOpMode.telemetry.addData("Moving ", power); //For testing
                //Don't move down if we are at the lowest level.
                if (power > 0) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }

                if (power == 0) motor.setPower(0);

                //If we wish to move down from mid-level, make sure we aren't at the base.
                if (power < 0 && !mgl.isTouched()) {
                    motor.setPower(power);
                }
                break;

            case LATCH:
                mOpMode.telemetry.addData("Moving ", power); //For testing
                if (power < 0 || 0 < power) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) {
                        if (power < 0) { pos = LEVEL.GROUND; }
                        if (power > 0) { pos = LEVEL.TOP; }
                    }
                }

                if (power == 0) motor.setPower(0);

                break;

            case TOP:
                mOpMode.telemetry.addData("Moving ", power); //For testing
                //Don't move up if we are at the highest level.
                if (power < 0) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }

                if (power == 0) motor.setPower(0);

                //If we wish to move up from mid-level, make sure we aren't at the base.
                if (power > 0 && !mgl.isTouched()) {
                    motor.setPower(power);
                }
                break;
        }
    }

    //Stops the lift/ratchet.
    public void stop() {
        liftMotor.setPower(0);
        ratchetMotor.setPower(0);
        lockRatchet();
    }

    //Unlocks the ratchet sets it in a state that is ready to be moved. "stop" disallows movement.
    public void unlockRatchet() {
        ratchetServo.setPosition(.7);
    }

    //Locks the ratchet to prevent ratchet movement.
    public void lockRatchet() {
        ratchetServo.setPosition(1);
    }

    //Sets the position of our lift.
    public void setPosition(LEVEL position) {
        pos = position;
    }

    //Returns the position of our lift.
    public String getPosition() {
        return pos.name();
    }

    //Moves the hook to a position which we can hook.
    public void hook() { hook.setPosition(.45); }

    //Moves the hook to a position which we can unhook.
    public void unhook() { hook.setPosition(0.05); }

    public void setLift(double power) {
        mOpMode.telemetry.addData("work ", liftMotor.getPower());
        liftMotor.setPower(power);}
}
