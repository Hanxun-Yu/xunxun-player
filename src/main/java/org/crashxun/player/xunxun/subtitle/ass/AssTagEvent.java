package org.crashxun.player.xunxun.subtitle.ass;

import java.util.List;

/**
 * Created by yuhanxun
 * 2018/9/6
 * description:
 */
public class AssTagEvent {
    private List<Dialogue> dialogues;

    public static class Dialogue {
        private String Layer;
        private String Start;
        private String End;
        private String Style;
        private String Actor;
        private String MarginL;
        private String MarginR;
        private String MarginV;
        private String Effect;
        private String Text;

        public String getLayer() {
            return Layer;
        }

        public void setLayer(String layer) {
            Layer = layer;
        }

        public String getStart() {
            return Start;
        }

        public void setStart(String start) {
            Start = start;
        }

        public String getEnd() {
            return End;
        }

        public void setEnd(String end) {
            End = end;
        }

        public String getStyle() {
            return Style;
        }

        public void setStyle(String style) {
            Style = style;
        }

        public String getActor() {
            return Actor;
        }

        public void setActor(String actor) {
            Actor = actor;
        }

        public String getMarginL() {
            return MarginL;
        }

        public void setMarginL(String marginL) {
            MarginL = marginL;
        }

        public String getMarginR() {
            return MarginR;
        }

        public void setMarginR(String marginR) {
            MarginR = marginR;
        }

        public String getMarginV() {
            return MarginV;
        }

        public void setMarginV(String marginV) {
            MarginV = marginV;
        }

        public String getEffect() {
            return Effect;
        }

        public void setEffect(String effect) {
            Effect = effect;
        }

        public String getText() {
            return Text;
        }

        public void setText(String text) {
            Text = text;
        }

        @Override
        public String toString() {
            return "Dialogue{" +
                    "Layer='" + Layer + '\'' +
                    ", Start='" + Start + '\'' +
                    ", End='" + End + '\'' +
                    ", Style='" + Style + '\'' +
                    ", Actor='" + Actor + '\'' +
                    ", MarginL='" + MarginL + '\'' +
                    ", MarginR='" + MarginR + '\'' +
                    ", MarginV='" + MarginV + '\'' +
                    ", Effect='" + Effect + '\'' +
                    ", Text='" + Text + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AssTagEvent{" +
                "dialogues=" + dialogues +
                '}';
    }

    public List<Dialogue> getDialogues() {
        return dialogues;
    }

    public void setDialogues(List<Dialogue> dialogues) {
        this.dialogues = dialogues;
    }
}
