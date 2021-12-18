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

        @Override
        public void handleMouseEvent(MouseEventType eventType) {
            this.receivedEventType = eventType;
            wasEventTriggered = true;
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
    public void single_click_means_pressing_and_releasing_mouse_button(){
        var mouse = new Mouse();
        var listener = new SpyEventListener();
        mouse.subscribe(listener);

        mouse.pressLeftButton(System.currentTimeMillis());
        mouse.releaseLeftButton(System.currentTimeMillis() + 10);

        assertThat(listener.receivedEventType).isEqualTo(MouseEventType.SingleClick);
    }
}
