package unit;

import mouse.Mouse;
import mouse.MouseEventListener;
import mouse.MouseEventType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MouseEventsKataTests {

    class SpyEventListener implements MouseEventListener {
        public MouseEventType receivedEventType;
        public boolean wasEventTriggered;
        public int eventCount;

        @Override
        public void handleMouseEvent(MouseEventType eventType) {
            this.receivedEventType = eventType;
            wasEventTriggered = true;
            eventCount++;
        }
    }

    // TODO:
    //  single click
    //     no click, several clicks after a long time, multiples clicks without release?
    // double click
    //     clicks, click + move + click != double click
    // triple click
    //     click + move + click != double click
    // drag
    //       move without press
    // drop
    //       no move

    @Test
    public void single_click_means_pressing_and_releasing_mouse_button() throws InterruptedException {
        var mouse = new Mouse();
        var listener = new SpyEventListener();
        mouse.subscribe(listener);

        mouse.pressLeftButton(System.currentTimeMillis());
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);

        Thread.sleep(Mouse.timeWindowInMillisecondsForDoubleClick + 100);
        assertThat(listener.receivedEventType).isEqualTo(MouseEventType.SingleClick);
    }

    @Test
    public void single_click_does_not_happen_if_button_is_never_pressed() throws InterruptedException {
        var mouse = new Mouse();
        var listener = new SpyEventListener();
        mouse.subscribe(listener);

        mouse.releaseLeftButton(System.currentTimeMillis() + 10);

        Thread.sleep(Mouse.timeWindowInMillisecondsForDoubleClick + 100);
        assertThat(listener.wasEventTriggered).isFalse();
    }

    @Test
    public void button_can_only_be_released_once() throws InterruptedException {
        var mouse = new Mouse();
        var listener = new SpyEventListener();
        mouse.subscribe(listener);

        mouse.pressLeftButton(System.currentTimeMillis());
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);

        Thread.sleep(Mouse.timeWindowInMillisecondsForDoubleClick + 100);
        assertThat(listener.eventCount).isEqualTo(1);
    }

    @Test
    public void double_click_happens_when_single_click_is_repetead_quickly() throws InterruptedException {
        var mouse = new Mouse();
        var listener = new SpyEventListener();
        mouse.subscribe(listener);

        mouse.pressLeftButton(System.currentTimeMillis());
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);
        mouse.pressLeftButton(System.currentTimeMillis());
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);

        Thread.sleep(Mouse.timeWindowInMillisecondsForDoubleClick + 100);
        assertThat(listener.receivedEventType).isEqualTo(MouseEventType.DoubleClick);
        assertThat(listener.eventCount).isEqualTo(1);
    }
}
