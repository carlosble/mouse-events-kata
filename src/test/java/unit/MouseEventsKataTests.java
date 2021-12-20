package unit;

import mouse.Mouse;
import mouse.MouseEventListener;
import mouse.MouseEventType;
import mouse.MousePointerCoordinates;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MouseEventsKataTests {

    private Mouse mouse;
    private SpyEventListener listener;

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

    @Before
    public void setUp() {
        mouse = new Mouse();
        listener = new SpyEventListener();
        mouse.subscribe(listener);
    }

    @Test
    public void single_click_means_pressing_and_releasing_mouse_button() throws InterruptedException {
        mouse.pressLeftButton(System.currentTimeMillis());
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);

        delaySimulatingHumanUser();
        assertThat(listener.receivedEventType).isEqualTo(MouseEventType.SingleClick);
    }

    @Test
    public void single_click_does_not_happen_if_button_is_never_pressed() throws InterruptedException {
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);

        delaySimulatingHumanUser();
        assertThat(listener.wasEventTriggered).isFalse();
    }

    @Test
    public void button_can_only_be_released_once() throws InterruptedException {
        mouse.pressLeftButton(System.currentTimeMillis());
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);

        delaySimulatingHumanUser();
        assertThat(listener.eventCount).isEqualTo(1);
    }

    @Test
    public void double_click_happens_when_single_click_is_repetead_quickly() throws InterruptedException {
        mouse.pressLeftButton(System.currentTimeMillis());
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);
        mouse.pressLeftButton(System.currentTimeMillis());
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);

        delaySimulatingHumanUser();
        assertThat(listener.receivedEventType).isEqualTo(MouseEventType.DoubleClick);
        assertThat(listener.eventCount).isEqualTo(1);
    }

    @Test
    public void triple_click_happens_when_single_click_is_repetead_quickly() throws InterruptedException {
        long firstTime = System.currentTimeMillis();
        mouse.pressLeftButton(firstTime);
        mouse.releaseLeftButton(firstTime + 10);
        mouse.pressLeftButton(firstTime + Mouse.clickTimeWindow - 50);
        mouse.releaseLeftButton(firstTime + Mouse.clickTimeWindow - 5);
        mouse.pressLeftButton(firstTime + Mouse.clickTimeWindow + 10);
        mouse.releaseLeftButton(firstTime + Mouse.clickTimeWindow + 20);

        delaySimulatingHumanUser();
        assertThat(listener.receivedEventType).isEqualTo(MouseEventType.TripleClick);
        assertThat(listener.eventCount).isEqualTo(1);
    }

    @Test
    public void dragging_means_clicking_plus_moving() throws InterruptedException {
        long firstTime = System.currentTimeMillis();
        mouse.pressLeftButton(firstTime);
        mouse.move(new MousePointerCoordinates(100, 100),
                   new MousePointerCoordinates(200,200),
                   firstTime + 10);

        assertThat(listener.receivedEventType).isEqualTo(MouseEventType.Drag);
    }

    @Test
    public void dropping_means_clicking_plus_moving_plus_releasing() throws InterruptedException {
        long firstTime = System.currentTimeMillis();
        mouse.pressLeftButton(firstTime);
        mouse.move(new MousePointerCoordinates(100, 100),
                new MousePointerCoordinates(200,200),
                firstTime + 10);
        mouse.releaseLeftButton(firstTime + 20);

        assertThat(listener.receivedEventType).isEqualTo(MouseEventType.Drop);
    }

    @Test
    public void dragging_means_button_is_currently_pressed() throws InterruptedException {
        long firstTime = System.currentTimeMillis();
        mouse.move(new MousePointerCoordinates(100, 100),
                new MousePointerCoordinates(200,200),
                firstTime + 10);

        delaySimulatingHumanUser();
        assertThat(listener.eventCount).isEqualTo(0);
    }

    private void delaySimulatingHumanUser() throws InterruptedException {
        Thread.sleep(Mouse.clickTimeWindow + 100);
    }
}
