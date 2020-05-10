package Main;

public class Clock {
    private static View currentView;

    private static boolean paused = false;
    private static long delay = 1000000000; //1 step per second
    private static long lastStep = System.nanoTime();
    private static long currentTime = System.nanoTime();

    private Clock() {
        //no instance allowed
    }

    /**
     * Static constructor, which creates the clock.
     */
    static {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        currentTime = System.nanoTime();
                        if(currentTime - lastStep > delay) {
                            if(!paused && currentView != null) {
                                currentView.step();
                            }
                            lastStep = lastStep + delay;
                        }

                        Thread.sleep(10);
                    } catch(InterruptedException exception) {
                        Thread.currentThread().interrupt();
                    } catch(Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        };
        thread.setName("Clock");
        thread.start();
    }

    /**
     * Pauses the clock.
     */
    public static void stop() {
        paused = true;
    }

    /**
     * Starts the clock.
     */
    public static void start() {
        paused = false;
    }

    /**
     * Sets the view whose step()-method is called.
     * @param view The view whose step()-method is called.
     */
    public static void setCurrentView(View view) {
        if(view != null) {
            currentView = view;
        }
    }

    /**
     * Sets the speed of the clock.
     * @param stepsPerSecond Defines how many ticks the clock does per second.
     */
    public static void setStepsPerSecond(long stepsPerSecond) {
        if(1 <= stepsPerSecond) {
            long newDelay = 1000000000 / stepsPerSecond;
            if(newDelay < delay) {
                //If the new delay is less than the old delay, the clock will try to catch up,
                //which would result in a brief period in which the animation is too fast.
                //To avoid that the last step is set to have happened "now".
                lastStep = System.nanoTime();
            }
            delay = newDelay;
        }
    }
}
