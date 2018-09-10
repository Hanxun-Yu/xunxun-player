package org.crashxun.player.xunxun.subtitle.api;

import android.view.View;

public class RenderEvent {
        private int id;
        private SubtitleEvent event;
        private View renderView;

        public RenderEvent(SubtitleEvent event, View renderView) {
            this.id = event.getIndex();
            this.event = event;
            this.renderView = renderView;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public SubtitleEvent getEvent() {
            return event;
        }

        public void setEvent(SubtitleEvent event) {
            this.event = event;
        }

        public View getRenderView() {
            return renderView;
        }

        public void setRenderView(View renderView) {
            this.renderView = renderView;
        }
    }