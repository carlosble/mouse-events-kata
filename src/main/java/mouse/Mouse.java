package mouse;

import java.util.ArrayList;
import java.util.List;

public class Mouse {
    private List<MouseEventListener> listeners = new ArrayList<>();
    private final long timeWindowInMillisecondsForDoubleClick = 500;

    public void pressLeftButton(long currentTimeInMilliseconds) {
        /*... debe notificar a los suscriptores ...*/
        /*... y gestionar el estado ...*/
    }

    public void releaseLeftButton(long currentTimeInMilliseconds) {
        notifySubscribers(MouseEventType.SingleClick);
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
