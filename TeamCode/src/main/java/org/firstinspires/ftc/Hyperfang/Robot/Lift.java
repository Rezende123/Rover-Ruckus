package org.firstinspires.ftc.Hyperfang.Robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.Hyperfang.Sensors.MGL;

public class Lift {
    public MGL mgl;

    public enum LEVEL {
        GROUND,
        LATCH,
        TOP,
    }

    private DcMotor liftMotor;
    private DcMotor ratchetMotor;
    private Servo hook; //Possibly change to continuous to ease Tele-Op.

    private LEVEL pos;

    private OpMode mOpMode;

    //Initializes the lift objects.
    public Lift(OpMode opMode){
        mOpMode = opMode;
        liftMotor = mOpMode.hardwareMap.get(DcMotor.class, "lift");
        ratchetMotor = mOpMode.hardwareMap.get(DcMotor.class, "ratchet");
        ratchetMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hook = mOpMode.hardwareMap.get(Servo.class, "hook");

        mgl = new MGL(opMode, "mgl");
        pos = LEVEL.GROUND;
    }

    //TODO create loop to set appropriate power to motor then stop once at desired position
    //May need to edit.
    public void moveTo(LEVEL lvl, double power, DcMotor motor) {
        switch (lvl) {
            case GROUND:
                if(pos == lvl || power != Math.abs(power)){break;}
                move(power, motor);
                pos = LEVEL.GROUND;
                break;
            case LATCH:
                if(pos == lvl){break;}
                move(power, motor);
                pos = LEVEL.LATCH;
                break;
            case TOP:
                if(pos == lvl || power == Math.abs(power)){break;}
                move(power, motor);
                pos = LEVEL.TOP;
                break;
        }
        stop();
    }

    //Moves our lift/ratchet up or down depending on the given power.
    public void move(double power, DcMotor motor) {
        switch (pos) {
            case GROUND:

                //Don't move down if we are at the lowest level.
                if (power > 0) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }

                //If we wish to move down from mid-level, make sure we aren't at the base.
                if (power < 0 && !mgl.isTouched()) {
                    motor.setPower(power);
                }
                break;

            case LATCH:
                if (power < 0 || 0 < power) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) {
                        if (power < 0) { pos = LEVEL.GROUND; }
                        if (power > 0) { pos = LEVEL.TOP; }
                    }
                }
                break;

            case TOP:
                //Don't move up if we are at the highest level.
                if (power < 0) {
                    motor.setPower(power);
                    if (mgl.isStateChange()) { pos = LEVEL.LATCH; }
                }

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
    public void hook() { hook.setPosition(1); } //need to test position

    //Moves the hook to a position which we can unhook.
    public void unhook() { hook.setPosition(0); } //need to test position

    //Returns the lift motor for specification outside the class.
    public DcMotor liftMotor() { return liftMotor; }

    //Returns the ratchet motor for specification outside the class.
    public DcMotor ratchetMotor() { return ratchetMotor; }

}
