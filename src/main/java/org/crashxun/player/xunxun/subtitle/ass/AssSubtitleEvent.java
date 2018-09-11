package org.crashxun.player.xunxun.subtitle.ass;

import android.util.Log;

import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;

import java.util.List;

public class AssSubtitleEvent extends SubtitleEvent {
    final String TAG = getClass().getSimpleName() +"_xunxun";
    //所有尺寸基于此参考宽高
    private int baseScreenWidth;
    private int baseScreenHeight;

    //位置
    private int posiX;
    private int posiY;

    //对齐方式
    private int marginVertical;
    private int marginRight;
    private int marginLeft;

    private int layer;

    private TextStyle parentTextStyle;
    private Anim parentAnim;

    private List<Text> texts;

    public int getBaseScreenWidth() {
        return baseScreenWidth;
    }

    public void setBaseScreenWidth(int baseScreenWidth) {
        this.baseScreenWidth = baseScreenWidth;
    }

    public int getBaseScreenHeight() {
        return baseScreenHeight;
    }

    public void setBaseScreenHeight(int baseScreenHeight) {
        this.baseScreenHeight = baseScreenHeight;
    }

    public int getPosiX() {
        return posiX;
    }

    public void setPosiX(int posiX) {
        this.posiX = posiX;
    }

    public int getPosiY() {
        return posiY;
    }

    public void setPosiY(int posiY) {
        this.posiY = posiY;
    }



    public int getMarginVertical() {
        return marginVertical;
    }

    public void setMarginVertical(int marginVertical) {
        this.marginVertical = marginVertical;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public TextStyle getParentTextStyle() {
        return parentTextStyle;

    }

    public void setParentTextStyle(TextStyle parentTextStyle) {
//        Log.e(TAG,"setParentTextStyle:"+parentTextStyle);
        this.parentTextStyle = parentTextStyle;
    }

    public Anim getParentAnim() {
        return parentAnim;
    }

    public void setParentAnim(Anim parentAnim) {
        this.parentAnim = parentAnim;
    }

    public List<Text> getTexts() {
        return texts;
    }

    public void setTexts(List<Text> texts) {
        this.texts = texts;
    }

    public static class Text {
        private String text;
        private TextStyle textStyle;

        //动画暂时属于事件,单条文字不支持动画
//        private  Anim anim;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public TextStyle getTextStyle() {
            return textStyle;
        }

        public void setTextStyle(TextStyle textStyle) {
            this.textStyle = textStyle;
        }

//        public Anim getAnim() {
//            return anim;
//        }
////
//        public void setAnim(Anim anim) {
//            this.anim = anim;
//        }

        @Override
        public String toString() {
            return "Text{" +
                    "text='" + text + '\'' +
                    ", textStyle=" + textStyle +
                    '}';
        }
    }



    public static class TextStyle implements Cloneable {
        //粗体,斜体,下划线,删除线
        private boolean isBold;
        private boolean isItalic;
        private boolean isUnderline;
        private boolean isStrikeOut;
        //字体,大小
        private String fontName;
        private int fontSize;

        //主颜色,次颜色,边框颜色,阴影颜色
        private String primaryColor;
        private String secondColor;
        private String borderColor;
        private String shadowColor;

        //间距,角度
        private int spacing;
        private float angle;

        //边框
        private BorderStyle borderStyle;
        private int borderWidth;
        private int borderShadowWidth;

        public enum BorderStyle {
            NormalBorder,
            ShadowBorder
        }

        public boolean isBold() {
            return isBold;
        }

        public void setBold(boolean bold) {
            isBold = bold;
        }

        public boolean isItalic() {
            return isItalic;
        }

        public void setItalic(boolean italic) {
            isItalic = italic;
        }

        public boolean isUnderline() {
            return isUnderline;
        }

        public void setUnderline(boolean underline) {
            isUnderline = underline;
        }

        public boolean isStrikeOut() {
            return isStrikeOut;
        }

        public void setStrikeOut(boolean strikeOut) {
            isStrikeOut = strikeOut;
        }

        public String getFontName() {
            return fontName;
        }

        public void setFontName(String fontName) {
            this.fontName = fontName;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        public String getPrimaryColor() {
            return primaryColor;
        }

        public void setPrimaryColor(String primaryColor) {
            this.primaryColor = primaryColor;
        }

        public String getSecondColor() {
            return secondColor;
        }

        public void setSecondColor(String secondColor) {
            this.secondColor = secondColor;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(String borderColor) {
            this.borderColor = borderColor;
        }

        public String getShadowColor() {
            return shadowColor;
        }

        public void setShadowColor(String shadowColor) {
            this.shadowColor = shadowColor;
        }

        public int getSpacing() {
            return spacing;
        }

        public void setSpacing(int spacing) {
            this.spacing = spacing;
        }

        public float getAngle() {
            return angle;
        }

        public void setAngle(float angle) {
            this.angle = angle;
        }

        public BorderStyle getBorderStyle() {
            return borderStyle;
        }

        public void setBorderStyle(BorderStyle borderStyle) {
            this.borderStyle = borderStyle;
        }

        public int getBorderWidth() {
            return borderWidth;
        }

        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
        }

        public int getBorderShadowWidth() {
            return borderShadowWidth;
        }

        public void setBorderShadowWidth(int borderShadowWidth) {
            this.borderShadowWidth = borderShadowWidth;
        }

        @Override
        public String toString() {
            return "TextStyle{" +
                    "isBold=" + isBold +
                    ", isItalic=" + isItalic +
                    ", isUnderline=" + isUnderline +
                    ", isStrikeOut=" + isStrikeOut +
                    ", fontName='" + fontName + '\'' +
                    ", fontSize=" + fontSize +
                    ", primaryColor='" + primaryColor + '\'' +
                    ", secondColor='" + secondColor + '\'' +
                    ", borderColor='" + borderColor + '\'' +
                    ", shadowColor='" + shadowColor + '\'' +
                    ", spacing=" + spacing +
                    ", angle=" + angle +
                    ", borderStyle=" + borderStyle +
                    ", borderWidth=" + borderWidth +
                    ", borderShadowWidth=" + borderShadowWidth +
                    '}';
        }

        public TextStyle clone() {
            TextStyle o = null;
            try {
                o = (TextStyle) super.clone();
            } catch (CloneNotSupportedException e) {
                System.out.println(e.toString());
            }
            return o;
        }
    }

    public static class Anim {
        private int animFadeShowTime;
        private int animFadeHideTime;
        private int animMoveStartX;
        private int animMoveStartY;
        private int animMoveEndX;
        private int animMoveEndY;

        public int getAnimFadeShowTime() {
            return animFadeShowTime;
        }

        public void setAnimFadeShowTime(int animFadeShowTime) {
            this.animFadeShowTime = animFadeShowTime;
        }

        public int getAnimFadeHideTime() {
            return animFadeHideTime;
        }

        public void setAnimFadeHideTime(int animFadeHideTime) {
            this.animFadeHideTime = animFadeHideTime;
        }

        public int getAnimMoveStartX() {
            return animMoveStartX;
        }

        public void setAnimMoveStartX(int animMoveStartX) {
            this.animMoveStartX = animMoveStartX;
        }

        public int getAnimMoveStartY() {
            return animMoveStartY;
        }

        public void setAnimMoveStartY(int animMoveStartY) {
            this.animMoveStartY = animMoveStartY;
        }

        public int getAnimMoveEndX() {
            return animMoveEndX;
        }

        public void setAnimMoveEndX(int animMoveEndX) {
            this.animMoveEndX = animMoveEndX;
        }

        public int getAnimMoveEndY() {
            return animMoveEndY;
        }

        public void setAnimMoveEndY(int animMoveEndY) {
            this.animMoveEndY = animMoveEndY;
        }

        @Override
        public String toString() {
            return "Anim{" +
                    "animFadeShowTime=" + animFadeShowTime +
                    ", animFadeHideTime=" + animFadeHideTime +
                    ", animMoveStartX=" + animMoveStartX +
                    ", animMoveStartY=" + animMoveStartY +
                    ", animMoveEndX=" + animMoveEndX +
                    ", animMoveEndY=" + animMoveEndY +
                    '}';
        }
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public void setAlignment(int alignment) {
        switch (alignment) {
            case 1:
                setAlignment(AssSubtitleEvent.Alignment.LeftBottom);
                break;
            case 2:
                setAlignment(AssSubtitleEvent.Alignment.Bottom);

                break;
            case 3:
                setAlignment(AssSubtitleEvent.Alignment.RightBottom);

                break;
            case 4:
                setAlignment(AssSubtitleEvent.Alignment.Left);

                break;
            case 5:
                setAlignment(AssSubtitleEvent.Alignment.Center);

                break;
            case 6:
                setAlignment(AssSubtitleEvent.Alignment.Right);

                break;
            case 7:
                setAlignment(AssSubtitleEvent.Alignment.LeftTop);

                break;
            case 8:
                setAlignment(AssSubtitleEvent.Alignment.Top);

                break;
            case 9:
                setAlignment(AssSubtitleEvent.Alignment.RightTop);
                break;
        }
    }
    public enum Alignment {
        LeftTop, Top, RightTop,
        Left, Center, Right,
        LeftBottom, Bottom, RightBottom
    }
    private Alignment alignment;

    @Override
    public String toString() {
        return super.toString()+"\n"+"AssSubtitleEvent{" +
                "baseScreenWidth=" + baseScreenWidth +
                ", baseScreenHeight=" + baseScreenHeight +
                ", posiX=" + posiX +
                ", posiY=" + posiY +
                ", marginVertical=" + marginVertical +
                ", marginRight=" + marginRight +
                ", marginLeft=" + marginLeft +
                ", layer=" + layer +
                ", parentTextStyle=" + parentTextStyle +
                ", parentAnim=" + parentAnim +
                ", texts=" + texts +
                ", alignment=" + alignment +
                '}';
    }
}
