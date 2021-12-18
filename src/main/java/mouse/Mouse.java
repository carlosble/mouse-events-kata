package mouse;

import jdk.jfr.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Mouse {
    private List<MouseEventListener> listeners = new ArrayList<>();
    public static final long timeWindowInMillisecondsForDoubleClick = 500;
    private long lastTimePressed;
    private boolean buttonIsPressed;
    private boolean isWaitingToTriggerSingleClick;
    private boolean isWaitingToTriggerDoubleClick;
    private boolean isWaitingToTriggerTripleClick;
    private static MouseEventType eventToTrigger;

    public void pressLeftButton(long currentTimeInMilliseconds) {
        lastTimePressed = currentTimeInMilliseconds;
        buttonIsPressed = true;
    }

    public void releaseLeftButton(long currentTimeInMilliseconds) {
        if (lastTimePressed > 0 && buttonIsPressed) {
            buttonIsPressed = false;
            if (!isWaitingToTriggerSingleClick) {
                isWaitingToTriggerSingleClick = true;
                eventToTrigger = MouseEventType.SingleClick;
            }
            else if (!isWaitingToTriggerDoubleClick) {
                isWaitingToTriggerDoubleClick = true;
                eventToTrigger = MouseEventType.DoubleClick;
            }
            else {
                isWaitingToTriggerTripleClick = true;
                eventToTrigger = MouseEventType.TripleClick;
            }
            CompletableFuture.delayedExecutor(
                    timeWindowInMillisecondsForDoubleClick,
                    TimeUnit.MILLISECONDS).execute(
                            () -> {
                                var shouldNotify = isWaitingToTriggerSingleClick ||
                                        isWaitingToTriggerDoubleClick || isWaitingToTriggerTripleClick;
                                if (isWaitingToTriggerSingleClick) {
                                    isWaitingToTriggerSingleClick = false;
                                }
                                if (isWaitingToTriggerDoubleClick) {
                                    isWaitingToTriggerDoubleClick = false;
                                }
                                if (isWaitingToTriggerTripleClick) {
                                    isWaitingToTriggerTripleClick = false;
                                }
                                if (shouldNotify){
                                    notifySubscribers(eventToTrigger);
                                }
                            });
        }
    }

    public void move(MousePointerCoordinates from, MousePointerCoordinates to, long
            currentTimeInMilliseconds) {
        /*... debe notificar a los suscriptores ...*/
        /*... y gestionar el estado ...*/
    }

    public void subscribe(MouseEventListener listener) {
        listeners.add(listener);
    }

    private void notifySubscribers(MouseEventType eventType) {
        listeners.forEach(listener -> listener.handleMouseEvent(eventType));
    }
}
