import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.Gesture.Type;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.MemoryImageSource;

class CustomListener extends Listener {

public Robot robot;
  public void onConnect(Controller c) {
      c.enableGesture(Gesture.Type.TYPE_CIRCLE); //Scrolling pages
      c.enableGesture(Gesture.Type.TYPE_SWIPE); //Launch start
      c.enableGesture(Gesture.Type.TYPE_KEY_TAP);
      c.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
  }

  public void onFrame(Controller c) {
    try { robot = new Robot(); } catch (Exception e) {}
    Frame frame = c.frame();
    InteractionBox box = frame.interactionBox();


    for (Hand hand : frame.hands()) {
    FingerList fingers = hand.fingers().extended();
    System.out.println(fingers.count());
      boolean handType = hand.isRight();
      if (handType) {

        Vector handPos = hand.stabilizedPalmPosition();
        Vector boxHandPos = box.normalizePoint(handPos);
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        robot.mouseMove((int) (screen.width * boxHandPos.getX()), (int) (screen.height - boxHandPos.getY() * screen.height));

      }


    for (Gesture g : frame.gestures()) {
      if (g.type() == Type.TYPE_CIRCLE) {
        CircleGesture circle = new CircleGesture(g);
        if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {
          robot.mouseWheel(1);
          try { Thread.sleep(50); } catch (Exception e) {}
        } else {
          robot.mouseWheel(-1);
          try { Thread.sleep(50); } catch (Exception e) {}
        }

      } else if (g.type() == Type.TYPE_KEY_TAP && fingers.count() <= 1){

        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    	}

       else if (g.type() == Type.TYPE_KEY_TAP && fingers.count() > 1){

        robot.mousePress(InputEvent.BUTTON3_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_MASK);



      } else if (g.type() == Type.TYPE_SCREEN_TAP){

    	  robot.mousePress(InputEvent.BUTTON3_MASK);
    	  robot.mouseRelease(InputEvent.BUTTON3_MASK);

      } else if (g.type() == Type.TYPE_SWIPE && g.state() == State.STATE_START) {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_TAB);
      }}
    }
  }
}


public class LeapMouse {

  public static void main(String[] args){
    CustomListener l = new CustomListener();
    Controller c = new Controller();
    c.addListener(l);
    c.config().setFloat("Gesture.Swipe.MinVelocity", 1500f);
    c.config().save();

    try {
      System.in.read();

    } catch (Exception e) {

    }
    c.removeListener(l);
  }

}
