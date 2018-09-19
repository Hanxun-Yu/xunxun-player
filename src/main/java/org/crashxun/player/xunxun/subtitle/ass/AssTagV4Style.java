package org.crashxun.player.xunxun.subtitle.ass;

import java.util.List;

/**
 * Created by yuhanxun
 * 2018/9/6
 * description:
 */
public class AssTagV4Style {
    public List<Style> getStyles() {
        return styles;
    }

    public void setStyles(List<Style> styles) {
        this.styles = styles;
    }

    private List<Style> styles;

    public static class Style {
        private String Name;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getFontname() {
            return Fontname;
        }

        public void setFontname(String fontname) {
            Fontname = fontname;
        }

        public String getFontsize() {
            return Fontsize;
        }

        public void setFontsize(String fontsize) {
            Fontsize = fontsize;
        }

        public String getPrimaryColour() {
            return PrimaryColour;
        }

        public void setPrimaryColour(String primaryColour) {
            PrimaryColour = primaryColour;
        }

        public String getSecondaryColour() {
            return SecondaryColour;
        }

        public void setSecondaryColour(String secondaryColour) {
            SecondaryColour = secondaryColour;
        }

        public String getOutlineColour() {
            return OutlineColour;
        }

        public void setOutlineColour(String outlineColour) {
            OutlineColour = outlineColour;
        }

        public String getBackColour() {
            return BackColour;
        }

        public void setBackColour(String backColour) {
            BackColour = backColour;
        }

        public String getBold() {
            return Bold;
        }

        public void setBold(String bold) {
            Bold = bold;
        }

        public String getItalic() {
            return Italic;
        }

        public void setItalic(String italic) {
            Italic = italic;
        }

        public String getUnderline() {
            return Underline;
        }

        public void setUnderline(String underline) {
            Underline = underline;
        }

        public String getStrikeOut() {
            return StrikeOut;
        }

        public void setStrikeOut(String strikeOut) {
            StrikeOut = strikeOut;
        }

        public String getScaleX() {
            return ScaleX;
        }

        public void setScaleX(String scaleX) {
            ScaleX = scaleX;
        }

        public String getScaleY() {
            return ScaleY;
        }

        public void setScaleY(String scaleY) {
            ScaleY = scaleY;
        }

        public String getSpacing() {
            return Spacing;
        }

        public void setSpacing(String spacing) {
            Spacing = spacing;
        }

        public String getAngle() {
            return Angle;
        }

        public void setAngle(String angle) {
            Angle = angle;
        }

        public String getBorderStyle() {
            return BorderStyle;
        }

        public void setBorderStyle(String borderStyle) {
            BorderStyle = borderStyle;
        }

        public String getOutline() {
            return Outline;
        }

        public void setOutline(String outline) {
            Outline = outline;
        }

        public String getShadow() {
            return Shadow;
        }

        public void setShadow(String shadow) {
            Shadow = shadow;
        }

        public String getAlignment() {
            return Alignment;
        }

        public void setAlignment(String alignment) {
            Alignment = alignment;
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

        public String getEncoding() {
            return Encoding;
        }

        public void setEncoding(String encoding) {
            Encoding = encoding;
        }

        private String Fontname;
        private String Fontsize;
        private String PrimaryColour;
        private String SecondaryColour;
        private String OutlineColour;
        private String BackColour;
        private String Bold;
        private String Italic;
        private String Underline;
        private String StrikeOut;
        private String ScaleX;
        private String ScaleY;
        private String Spacing;
        private String Angle;
        private String BorderStyle;
        private String Outline;
        private String Shadow;
        private String Alignment;
        private String MarginL;
        private String MarginR;
        private String MarginV;
        private String Encoding;

        @Override
        public String toString() {
            return "Style{" +
                    "Name='" + Name + '\'' +
                    ", Fontname='" + Fontname + '\'' +
                    ", Fontsize='" + Fontsize + '\'' +
                    ", PrimaryColour='" + PrimaryColour + '\'' +
                    ", SecondaryColour='" + SecondaryColour + '\'' +
                    ", OutlineColour='" + OutlineColour + '\'' +
                    ", BackColour='" + BackColour + '\'' +
                    ", Bold='" + Bold + '\'' +
                    ", Italic='" + Italic + '\'' +
                    ", Underline='" + Underline + '\'' +
                    ", StrikeOut='" + StrikeOut + '\'' +
                    ", ScaleX='" + ScaleX + '\'' +
                    ", ScaleY='" + ScaleY + '\'' +
                    ", Spacing='" + Spacing + '\'' +
                    ", Angle='" + Angle + '\'' +
                    ", BorderStyle='" + BorderStyle + '\'' +
                    ", Outline='" + Outline + '\'' +
                    ", Shadow='" + Shadow + '\'' +
                    ", Alignment='" + Alignment + '\'' +
                    ", MarginL='" + MarginL + '\'' +
                    ", MarginR='" + MarginR + '\'' +
                    ", MarginV='" + MarginV + '\'' +
                    ", Encoding='" + Encoding + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AssTagV4Style{" +
                "styles=" + styles +
                '}';
    }
}
