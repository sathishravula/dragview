package com.blahti.example.drag;

import com.blahti.example.drag.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity presents two images and a text view and allows them to be dragged around.
 * Press and hold on a view initiates a drag. 
 * After clicking the Short button, dragging starts with a short touch rather than a long touch.
 *
 * <p> This activity is derviced from the Android Launcher class.
 * 
 */

public class DragActivity extends Activity 
    implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener
{


private DragController mDragController;   // Object that sends out drag-drop events while a view is being moved.
private DragLayer mDragLayer;             // The ViewGroup that supports drag-drop.
private boolean mLongClickStartsDrag = true;    // If true, it takes a long click to start the drag operation.
                                                // Otherwise, any touch event starts a drag.

private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST;

public static final boolean Debugging = false;

/**
 * onCreate - called when the activity is first created.
 * 
 * Creates a drag controller and sets up three views so click and long click on the views are sent to this activity.
 * The onLongClick method starts a drag sequence.
 *
 */

 protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    mDragController = new DragController(this);

    setContentView(R.layout.main);
    setupViews ();
}

/**
 * Build a menu for the activity.
 *
 */    

public boolean onCreateOptionsMenu (Menu menu) 
{
    super.onCreateOptionsMenu(menu);
    
    menu.add (0, CHANGE_TOUCH_MODE_MENU_ID, 0, "Change Touch Mode");
    return true;
}

/**
 * Handle a click on a view. Tell the user to use a long click (press).
 *
 */    

public void onClick(View v) 
{
    if (mLongClickStartsDrag) {
       // Tell the user that it takes a long click to start dragging.
       toast ("Press and hold to drag an image.");
    }
}

/**
 * Handle a click on the Wglxy views at the bottom.
 *
 */    

public void onClickWglxy (View v) {
    Intent viewIntent = new Intent ("android.intent.action.VIEW", 
                                    Uri.parse("http://double-star.appspot.com/blahti/ds-download.html"));
    startActivity(viewIntent);
    
}

/**
 * Handle a long click.
 * If mLongClick only is true, this will be the only way to start a drag operation.
 *
 * @param v View
 * @return boolean - true indicates that the event was handled
 */    

public boolean onLongClick(View v) 
{
    if (mLongClickStartsDrag) {
       
        //trace ("onLongClick in view: " + v + " touchMode: " + v.isInTouchMode ());

        // Make sure the drag was started by a long press as opposed to a long click.
        // (Note: I got this from the Workspace object in the Android Launcher code. 
        //  I think it is here to ensure that the device is still in touch mode as we start the drag operation.)
        if (!v.isInTouchMode()) {
           toast ("isInTouchMode returned false. Try touching the view again.");
           return false;
        }
        return startDrag (v);
    }

    // If we get here, return false to indicate that we have not taken care of the event.
    return false;
}

/**
 * Perform an action in response to a menu item being clicked.
 *
 */

public boolean onOptionsItemSelected (MenuItem item) 
{
    switch (item.getItemId()) {
      case CHANGE_TOUCH_MODE_MENU_ID:
        mLongClickStartsDrag = !mLongClickStartsDrag;
        String message = mLongClickStartsDrag ? "Changed touch mode. Drag now starts on long touch (click)." 
                                              : "Changed touch mode. Drag now starts on touch (click).";
        Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();
        return true;
    }
    return super.onOptionsItemSelected (item);
}

/**
 * Resume the activity.
 */

@Override protected void onResume() {
    super.onResume();

    View v  = findViewById (R.id.wglxy_bar);
    if (v != null) {
       Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.fade_in);
       //anim1.setAnimationListener (new StartActivityAfterAnimation (i));
       v.startAnimation (anim1);
    }
}

/**
 * This is the starting point for a drag operation if mLongClickStartsDrag is false.
 * It looks for the down event that gets generated when a user touches the screen.
 * Only that initiates the drag-drop sequence.
 *
 */    

public boolean onTouch (View v, MotionEvent ev) 
{
    // If we are configured to start only on a long click, we are not going to handle any events here.
    if (mLongClickStartsDrag) return false;

    boolean handledHere = false;

    final int action = ev.getAction();

    // In the situation where a long click is not needed to initiate a drag, simply start on the down event.
    if (action == MotionEvent.ACTION_DOWN) {
       handledHere = startDrag (v);
       if (handledHere) v.performClick ();
    }
    
    return handledHere;
}

/**
 * Start dragging a view.
 *
 */    

public boolean startDrag (View v)
{
    // Let the DragController initiate a drag-drop sequence.
    // I use the dragInfo to pass along the object being dragged.
    // I'm not sure how the Launcher designers do this.
    Object dragInfo = v;
    mDragController.startDrag (v, mDragLayer, dragInfo, DragController.DRAG_ACTION_MOVE);
    return true;
}

/**
 * Finds all the views we need and configure them to send click events to the activity.
 *
 */
private void setupViews() 
{
    DragController dragController = mDragController;

    mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
    mDragLayer.setDragController(dragController);
    dragController.addDropTarget (mDragLayer);

    ImageView i1 = (ImageView) findViewById (R.id.Image1);
    ImageView i2 = (ImageView) findViewById (R.id.Image2);

    i1.setOnClickListener(this);
    i1.setOnLongClickListener(this);
    i1.setOnTouchListener(this);

    i2.setOnClickListener(this);
    i2.setOnLongClickListener(this);
    i2.setOnTouchListener(this);

    TextView tv = (TextView) findViewById (R.id.Text1);
    tv.setOnLongClickListener(this);
    tv.setOnTouchListener(this);

    String message = mLongClickStartsDrag ? "Press and hold to start dragging." 
                                          : "Touch a view to start dragging.";
    Toast.makeText (getApplicationContext(), message, Toast.LENGTH_LONG).show ();

}

/**
 * Show a string on the screen via Toast.
 * 
 * @param msg String
 * @return void
 */

public void toast (String msg)
{
    Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
} // end toast

/**
 * Send a message to the debug log and display it using Toast.
 */

public void trace (String msg) 
{
    if (!Debugging) return;
    Log.d ("DragActivity", msg);
    toast (msg);
}

} // end class
