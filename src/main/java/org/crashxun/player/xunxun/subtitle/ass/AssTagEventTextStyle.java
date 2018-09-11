package org.crashxun.player.xunxun.subtitle.ass;

import android.util.Log;

public class AssTagEventTextStyle {
    final String TAG = getClass().getSimpleName()+"_xunxun";

    //位置
    private int posiX;
    private int posiY;

    private int animFadeShowTime;
    private int animFadeHideTime;
    private int animMoveStartX;
    private int animMoveStartY;
    private int animMoveEndX;
    private int animMoveEndY;

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
//    private int spacing;
    private float angle;

    //边框
//    private AssSubtitleEvent.TextStyle.BorderStyle borderStyle;
    private int borderWidth;
    private int borderShadowWidth;

    private int algin;



    private  boolean posiXSetted;
    private boolean posiYSetted;
    private boolean animFadeShowTimeSetted;
    private boolean animFadeHideTimeSetted;
    private boolean animMoveStartXSetted;
    private boolean animMoveStartYSetted;
    private boolean animMoveEndXSetted;
    private boolean animMoveEndYSetted;
    private boolean isBoldSetted;
    private boolean isItalicSetted;
    private boolean isUnderlineSetted;
    private boolean isStrikeOutSetted;
    private boolean fontNameSetted;
    private boolean fontSizeSetted;
    private boolean primaryColorSetted;
    private boolean secondColorSetted;
    private boolean borderColorSetted;
    private boolean shadowColorSetted;
    private boolean angleSetted;
    private boolean borderWidthSetted;
    private boolean borderShadowWidthSetted;
    private boolean alginSetted;

    public int getPosiX() {
        return posiX;
    }

    public void setPosiX(int posiX) {
        posiXSetted = true;
        this.posiX = posiX;
    }

    public int getPosiY() {
        return posiY;
    }

    public void setPosiY(int posiY) {
        posiYSetted = true;
        this.posiY = posiY;
    }

    public int getAnimFadeShowTime() {
        return animFadeShowTime;
    }

    public void setAnimFadeShowTime(int animFadeShowTime) {
        animFadeShowTimeSetted = true;
        this.animFadeShowTime = animFadeShowTime;
    }

    public int getAnimFadeHideTime() {
        return animFadeHideTime;
    }

    public void setAnimFadeHideTime(int animFadeHideTime) {
        animFadeHideTimeSetted = true;
        this.animFadeHideTime = animFadeHideTime;
    }

    public int getAnimMoveStartX() {
        return animMoveStartX;
    }

    public void setAnimMoveStartX(int animMoveStartX) {
        animMoveStartXSetted = true;
        this.animMoveStartX = animMoveStartX;
    }

    public int getAnimMoveStartY() {
        return animMoveStartY;
    }

    public void setAnimMoveStartY(int animMoveStartY) {
        animMoveStartYSetted = true;
        this.animMoveStartY = animMoveStartY;
    }

    public int getAnimMoveEndX() {
        return animMoveEndX;
    }

    public void setAnimMoveEndX(int animMoveEndX) {
        animMoveEndXSetted = true;
        this.animMoveEndX = animMoveEndX;
    }

    public int getAnimMoveEndY() {
        return animMoveEndY;
    }

    public void setAnimMoveEndY(int animMoveEndY) {
        animMoveEndYSetted = true;
        this.animMoveEndY = animMoveEndY;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBoldSetted = true;
        isBold = bold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalicSetted = true;
        isItalic = italic;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public void setUnderline(boolean underline) {
        isUnderlineSetted = true;
        isUnderline = underline;
    }

    public boolean isStrikeOut() {
        return isStrikeOut;
    }

    public void setStrikeOut(boolean strikeOut) {
        isStrikeOutSetted = true;
        isStrikeOut = strikeOut;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        fontNameSetted = true;
        this.fontName = fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        fontSizeSetted = true;
        this.fontSize = fontSize;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        primaryColorSetted = true;
        this.primaryColor = primaryColor;
        Log.d(TAG,"setPrimaryColor:"+primaryColor);
    }

    public String getSecondColor() {
        return secondColor;
    }

    public void setSecondColor(String secondColor) {
        secondColorSetted = true;
        this.secondColor = secondColor;
        Log.d(TAG,"setSecondColor:"+secondColor);

    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        borderColorSetted = true;
        this.borderColor = borderColor;
    }

    public String getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        shadowColorSetted = true;
        this.shadowColor = shadowColor;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        angleSetted = true;
        this.angle = angle;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        borderWidthSetted = true;
        this.borderWidth = borderWidth;
    }

    public int getBorderShadowWidth() {
        return borderShadowWidth;
    }

    public void setBorderShadowWidth(int borderShadowWidth) {
        borderShadowWidthSetted = true;
        this.borderShadowWidth = borderShadowWidth;
    }

    public int getAlgin() {
        return algin;
    }

    public void setAlgin(int algin) {
        alginSetted = true;
        this.algin = algin;
    }

    public boolean isPosiXSetted() {
        return posiXSetted;
    }

    public boolean isPosiYSetted() {
        return posiYSetted;
    }

    public boolean isAnimFadeShowTimeSetted() {
        return animFadeShowTimeSetted;
    }

    public boolean isAnimFadeHideTimeSetted() {
        return animFadeHideTimeSetted;
    }

    public boolean isAnimMoveStartXSetted() {
        return animMoveStartXSetted;
    }

    public boolean isAnimMoveStartYSetted() {
        return animMoveStartYSetted;
    }

    public boolean isAnimMoveEndXSetted() {
        return animMoveEndXSetted;
    }

    public boolean isAnimMoveEndYSetted() {
        return animMoveEndYSetted;
    }

    public boolean isBoldSetted() {
        return isBoldSetted;
    }

    public boolean isItalicSetted() {
        return isItalicSetted;
    }

    public boolean isUnderlineSetted() {
        return isUnderlineSetted;
    }

    public boolean isStrikeOutSetted() {
        return isStrikeOutSetted;
    }

    public boolean isFontNameSetted() {
        return fontNameSetted;
    }

    public boolean isFontSizeSetted() {
        return fontSizeSetted;
    }

    public boolean isPrimaryColorSetted() {
        return primaryColorSetted;
    }

    public boolean isSecondColorSetted() {
        return secondColorSetted;
    }

    public boolean isBorderColorSetted() {
        return borderColorSetted;
    }

    public boolean isShadowColorSetted() {
        return shadowColorSetted;
    }

    public boolean isAngleSetted() {
        return angleSetted;
    }

    public boolean isBorderWidthSetted() {
        return borderWidthSetted;
    }

    public boolean isBorderShadowWidthSetted() {
        return borderShadowWidthSetted;
    }

    public boolean isAlginSetted() {
        return alginSetted;
    }
}
