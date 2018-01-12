package ui.widget.camera.view.utils;


public class LoopTask extends Thread {
    private final String TAG = this.getClass().getSimpleName();
    public boolean isLoop = true;
    public Task task = null;
    public LoopTask(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        super.run();
        task.onRun();
        while (isLoop) {
            task.onLoop();
        }
    }


    public synchronized LoopTask onStart() {
        start();
        isLoop = true;
        return this;
    }


    public synchronized LoopTask onStop() {
        isLoop = false;
        task.onStop();
        return this;
    }

    public static abstract class Task {
        public abstract void onRun();

        public abstract void onLoop();

        public void onStop() {
        }

    }

}
