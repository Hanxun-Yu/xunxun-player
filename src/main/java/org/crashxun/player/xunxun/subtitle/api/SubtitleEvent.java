package org.crashxun.player.xunxun.subtitle.api;

/**
 * Created by yuhanxun
 * 2018/9/4
 * description: 字幕事件
 */
public class SubtitleEvent {

    private int index;
    private String text;


    private String startTimeText;
    private String endTimeText;
    private long startTimeMilliSec;
    private long endTimeMilliSec;
    private long duringMilliSec;

    //所有尺寸基于此参考宽高
    private int baseScreenWidth;
    private int baseScreenHeight;

    //位置
    private int posiX;
    private int posiY;

    //字体,大小
    private String fontName;
    private int fontSize;

    //主颜色,次颜色,边框颜色,阴影颜色
    private String primaryColor;
    private String secondColor;
    private String borderColor;
    private String shadowColor;

    //粗体,斜体,下划线,删除线
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderline;
    private boolean isStrikeOut;

    //间距,角度
    private int spacing;
    private float angle;

    //边框
    private BorderStyle borderStyle;
    private int borderWidth;
    private int borderShadowWidth;

    private Alignment alignment;
    private int marginVertical;
    private int marginRight;
    private int marginLeft;

    private int layer;

    public enum BorderStyle {
        NormalBorder,
        ShadowBorder
    }

    public enum Alignment {
        LeftTop, Top, RightTop,
        Left, Center, Right,
        LeftBottom, Bottom, RightBottom
    }

    private int animFadeShowTime;
    private int animFadeHideTime;
    private int animMoveStartX;
    private int animMoveStartY;
    private int animMoveEndX;
    private int animMoveEndY;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStartTimeText() {
        return startTimeText;
    }

    public void setStartTimeText(String startTimeText) {
        this.startTimeText = startTimeText;
    }

    public String getEndTimeText() {
        return endTimeText;
    }

    public void setEndTimeText(String endTimeText) {
        this.endTimeText = endTimeText;
    }

    public long getStartTimeMilliSec() {
        return startTimeMilliSec;
    }

    public void setStartTimeMilliSec(long startTimeMilliSec) {
        this.startTimeMilliSec = startTimeMilliSec;
    }

    public long getEndTimeMilliSec() {
        return endTimeMilliSec;
    }

    public void setEndTimeMilliSec(long endTimeMilliSec) {
        this.endTimeMilliSec = endTimeMilliSec;
    }

    public long getDuringMilliSec() {
        return duringMilliSec;
    }

    public void setDuringMilliSec(long duringMilliSec) {
        this.duringMilliSec = duringMilliSec;
    }

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

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
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

    public int getMarginVertical() {
        return marginVertical;
    }

    public void setMarginVertical(int marginVertical) {
        this.marginVertical = marginVertical;
    }

    @Override
    public String toString() {
        return "SubtitleEvent{" +
                "index=" + index +
                ", text='" + text + '\'' +
                ", startTimeMilliSec=" + startTimeMilliSec +
                ", endTimeMilliSec=" + endTimeMilliSec +
                ", duringMilliSec=" + duringMilliSec +
                '}';
    }


    public SubtitleEvent clone() {
        SubtitleEvent o = null;
        try {
            o = (SubtitleEvent) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println(e.toString());
        }
        return o;
    }
}
