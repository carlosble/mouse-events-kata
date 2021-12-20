package mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Mouse {
    private List<MouseEventListener> listeners = new ArrayList<>();
    public static final long clickTimeWindow = 500;
    private long lastTimePressed;
    private long lastTimeMoved;
    private boolean wasButtonPressed;
    private boolean isWaitingToTriggerSingleClick;
    private boolean isWaitingToTriggerDoubleClick;
    private boolean isWaitingToTriggerTripleClick;
    private static MouseEventType eventToTrigger;

    public void pressLeftButton(long currentTimeInMilliseconds) {
        lastTimePressed = currentTimeInMilliseconds;
        wasButtonPressed = true;
    }

    public void move(MousePointerCoordinates from, MousePointerCoordinates to, long
            currentTimeInMilliseconds) {
        lastTimeMoved = currentTimeInMilliseconds;
        if (wasButtonPressed) {
            eventToTrigger = MouseEventType.Drag;
            notifySubscribers();
        }
    }

    public void releaseLeftButton(long currentTimeInMilliseconds) {
        if (wasButtonPressed) {
            resetButtonPressedState();
            if (wasDragging()) {
                handleDropEvent();
            } else {
                handleClickEvent();
            }
        }
    }

    public void subscribe(MouseEventListener listener) {
        listeners.add(listener);
    }

    private void resetButtonPressedState() {
        wasButtonPressed = false;
    }

    private boolean wasDragging() {
        return lastTimeMoved > lastTimePressed;
    }

    private void handleDropEvent() {
        eventToTrigger = MouseEventType.Drop;
        notifySubscribers();
    }

    private void handleClickEvent() {
        calculateTypeOfClickEvent();
        afterTheClickTimeWindow(() -> {
            boolean isClick = isFinallyAclickEvent();
            // the order of the reset state is important, must be here in the
            // middle to make sure time window is accurate:
            resetClickState();
            // finally notify
            if (isClick) {
                notifySubscribers();
            }
        });
    }

    private boolean isFinallyAclickEvent() {
        return isWaitingToTriggerSingleClick ||
                isWaitingToTriggerDoubleClick ||
                isWaitingToTriggerTripleClick;
    }

    private void afterTheClickTimeWindow(Runnable runnable) {
        CompletableFuture.delayedExecutor(
                clickTimeWindow,
                TimeUnit.MILLISECONDS).execute(
                runnable);
    }

    private void resetClickState() {
        if (isWaitingToTriggerSingleClick) {
            isWaitingToTriggerSingleClick = false;
        }
        if (isWaitingToTriggerDoubleClick) {
            isWaitingToTriggerDoubleClick = false;
        }
        if (isWaitingToTriggerTripleClick) {
            isWaitingToTriggerTripleClick = false;
        }
    }

    private void calculateTypeOfClickEvent() {
        if (!isWaitingToTriggerSingleClick) {
            isWaitingToTriggerSingleClick = true;
            eventToTrigger = MouseEventType.SingleClick;
        } else if (!isWaitingToTriggerDoubleClick) {
            isWaitingToTriggerDoubleClick = true;
            eventToTrigger = MouseEventType.DoubleClick;
        } else {
            isWaitingToTriggerTripleClick = true;
            eventToTrigger = MouseEventType.TripleClick;
        }
    }

    private void notifySubscribers() {
        listeners.forEach(listener -> listener.handleMouseEvent(eventToTrigger));
    }
}
