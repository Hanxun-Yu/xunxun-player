package org.crashxun.player.xunxun.subtitle.ass;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import org.crashxun.player.xunxun.activity.SubtitleAdjustTimeActivity;
import org.crashxun.player.xunxun.subtitle.DateUtil;
import org.crashxun.player.xunxun.subtitle.api.AbstractSubtitleParser;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@formatter:off

//@formatter:on
public class AssSubtitleParser extends AbstractSubtitleParser implements AssSubtitleDefine {
    private static final String ErrorMultipleTag = "multiple tag";
    private static final String ErrorNoFormat = "no format";
    private static final String ErrorFormatCountNotMatchValColCount = "format count not match value column count";


    @Override
    protected List<? extends SubtitleEvent> convertToEvent(String allcontent) {
        Log.d(TAG, "allcontent:" + allcontent);
        if (TextUtils.isEmpty(allcontent)) {
            onParseFailer("subtitle content:null");
            return null;
        }
        //unify various 'enter' character, clear 'utf-16' serial character
        allcontent = allcontent.replaceAll("\r\n", "\n").replaceAll("\ufeff", "");

        List<SubtitleEvent> ret = null;
        //fetch 3 S,V4,E tag content java object
        AssTag assTag = getAssTag(allcontent);
        try {
            if (assTag != null) {
                ret = new ArrayList<>();
                //获取全局宽高
                int subScreenWidth = string2Int(assTag.getS().getPlayResX());
                int subScreenHeight = string2Int(assTag.getS().getPlayResY());

                AssSubtitleEvent subtitleEvent = null;
                AssSubtitleEvent.TextStyle parentStyle = null;
                AssSubtitleEvent.Anim parentAnim = null;
                AssTagEvent.Dialogue dialogue = null;
                AssTagV4Style.Style style = null;


                int index = 0;
                int line = 0;
                for (int i = 0; i < assTag.getE().getDialogues().size(); i++) {
                    dialogue = assTag.getE().getDialogues().get(i);
                    //目前先1个Dialogue = 1个事件
                    subtitleEvent = new AssSubtitleEvent();
                    subtitleEvent.setIndex(i);
                    parentStyle = new AssSubtitleEvent.TextStyle();
                    subtitleEvent.setBaseScreenWidth(subScreenWidth);
                    subtitleEvent.setBaseScreenHeight(subScreenHeight);

                    //字大小,颜色,粗,斜,下划线,删除线,间距,角度
                    style = findStyle(assTag.getV(), dialogue.getStyle());
                    parentStyle.setFontName(style.getFontname());
                    parentStyle.setFontSize(string2Int(style.getFontsize()));
                    parentStyle.setPrimaryColor(getColor(getTextStyleColor(style.getPrimaryColour())));
                    parentStyle.setSecondColor(getColor(getTextStyleColor(style.getSecondaryColour())));
                    parentStyle.setShadowColor(getColor(getTextStyleColor(style.getBackColour())));
                    parentStyle.setBold(isStyleOpen(style.getBold()));
                    parentStyle.setItalic(isStyleOpen(style.getItalic()));
                    parentStyle.setUnderline(isStyleOpen(style.getUnderline()));
                    parentStyle.setStrikeOut(isStyleOpen(style.getStrikeOut()));
                    parentStyle.setSpacing(string2Int(style.getSpacing()));
                    parentStyle.setAngle(string2Float(style.getAngle()));
                    //scaleX,scaleY
                    //...

                    //边框,颜色
                    parentStyle.setBorderColor(getColor(getTextStyleColor(style.getOutlineColour())));
                    parentStyle.setBorderStyle(string2Int(
                            style.getBorderStyle()) == 1 ?
                            AssSubtitleEvent.TextStyle.BorderStyle.ShadowBorder
                            : AssSubtitleEvent.TextStyle.BorderStyle.NormalBorder);
                    parentStyle.setBorderWidth(string2Int(style.getOutline()));
                    parentStyle.setBorderShadowWidth(string2Int(style.getShadow()));
                    subtitleEvent.setAlignment(getAligment(style.getAlignment()));
                    subtitleEvent.setParentTextStyle(parentStyle);


                    //获取字位置
//                subtitleEvent.setPosiX();
//                subtitleEvent.setPosiY();
                    subtitleEvent.setMarginLeft(string2Int(style.getMarginL()));
                    subtitleEvent.setMarginRight(string2Int(style.getMarginR()));
                    subtitleEvent.setMarginVertical(string2Int(style.getMarginV()));

                    //获取时间
                    subtitleEvent.setStartTimeText(dialogue.getStart());
                    subtitleEvent.setEndTimeText(dialogue.getEnd());
                    subtitleEvent.setStartTimeMilliSec(DateUtil.convertTimeNoYMD2ms2(dialogue.getStart() + "0"));
                    subtitleEvent.setEndTimeMilliSec(DateUtil.convertTimeNoYMD2ms2(dialogue.getEnd() + "0"));
                    subtitleEvent.setDuringMilliSec(subtitleEvent.getEndTimeMilliSec() - subtitleEvent.getStartTimeMilliSec());


                    subtitleEvent.setTexts(parseText(subtitleEvent, dialogue.getText()));
                    //获取每行文字
//                    subtitleEvent.setText(dialogue.getText().replaceAll("[{]", "")
//                            .replaceAll("[}]", "")
//                            .replaceAll("\\\\", ""));
                    //                    subtitleEvent.setIndex(index++);

                    //动画
                    //...

                    ret.add(subtitleEvent);
                    int percent = (int) (i * 100f / assTag.getE().getDialogues().size());
                    onParseLoading(path, percent < 0 ? 0 : percent);
                    Log.d(TAG, "convertToEvent i:" + i + " subtitleEventItem:" + subtitleEvent);
                }
                //排序
                //...
                Collections.sort(ret, new Comparator<SubtitleEvent>() {
                    @Override
                    public int compare(SubtitleEvent o1, SubtitleEvent o2) {
                        if (o1.getStartTimeMilliSec() - o2.getStartTimeMilliSec() < 0)
                            return -1;
                        else if (o1.getStartTimeMilliSec() - o2.getStartTimeMilliSec() > 0)
                            return 1;
                        else
                            return 0;
                    }
                });
                onParseLoading(path, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error", e);
            onParseFailer(e.getMessage());
            ret = null;
        }

        return ret;
    }

    private boolean isStyleOpen(String val) {
        boolean ret = false;
        if (val != null) {
            ret = val.equals(VAL_ATTR_VS_ON);
        }
        return ret;
    }


    /**
     * 传入6位颜色 或8位带透明度颜色
     * ass前2位透明度00表示不透明,ff表示透明,与android相反,需要转换
     * @param color
     * @return
     */
    private String getColor(String color) {
        String ret = null;
        if (color != null && !color.equals("")) {
            if (color.length() == 6) {
                ret = "#FF"+color;
            } else if (color.length() == 8) {
                int alpha = Integer.parseInt(color.substring(0,2),16);
                int androidAlpha = 255 - alpha;
                String alphaStr = Integer.toHexString(androidAlpha);
                ret = "#"+alphaStr+color.substring(2);
            }
        }
        return ret;
    }

    private AssSubtitleEvent.Alignment getAligment(String val) {
        AssSubtitleEvent.Alignment ret = null;
        if (val != null) {
            if (val.equals(VAL_ATTR_VS_Alignment_LeftBottom))
                ret = AssSubtitleEvent.Alignment.LeftBottom;
            else if (val.equals(VAL_ATTR_VS_Alignment_Bottom))
                ret = AssSubtitleEvent.Alignment.Bottom;
            else if (val.equals(VAL_ATTR_VS_Alignment_RightBottom))
                ret = AssSubtitleEvent.Alignment.RightBottom;
            else if (val.equals(VAL_ATTR_VS_Alignment_Left))
                ret = AssSubtitleEvent.Alignment.Left;
            else if (val.equals(VAL_ATTR_VS_Alignment_Center))
                ret = AssSubtitleEvent.Alignment.Center;
            else if (val.equals(VAL_ATTR_VS_Alignment_Right))
                ret = AssSubtitleEvent.Alignment.Right;
            else if (val.equals(VAL_ATTR_VS_Alignment_LeftTop))
                ret = AssSubtitleEvent.Alignment.LeftTop;
            else if (val.equals(VAL_ATTR_VS_Alignment_Top))
                ret = AssSubtitleEvent.Alignment.Top;
            else if (val.equals(VAL_ATTR_VS_Alignment_RightTop))
                ret = AssSubtitleEvent.Alignment.RightTop;
        }
        return ret;
    }

    private AssTagV4Style.Style findStyle(AssTagV4Style ats, String styleName) {
        AssTagV4Style.Style ret = null;
        if (ats != null && ats.getStyles() != null && !ats.getStyles().isEmpty()) {
            for (int i = 0; i < ats.getStyles().size(); i++) {
                if (ats.getStyles().get(i).getName().equals(styleName)) {
                    ret = ats.getStyles().get(i);
                    break;
                }
            }
        }
        return ret;
    }

    private AssTag getAssTag(String allcontent) {
        AssTag ret = null;
        AssTagScriptInfo ats = getAssTagScriptInfo(allcontent);
        AssTagV4Style atv = getAssTagV4Style(allcontent);
        AssTagEvent ate = getAssTagEvent(allcontent);
        if (ats != null && atv != null && ate != null) {
            ret = new AssTag();
            ret.setS(ats);
            ret.setV(atv);
            ret.setE(ate);
        }
        return ret;
    }

    private AssTagScriptInfo getAssTagScriptInfo(String allcontent) {
        AssTagScriptInfo ret = null;
        String partStr = getTagContent(allcontent, KEY_PART_Script_Info);
        if (partStr != null) {
            ret = new AssTagScriptInfo();
            String[] lineArr = partStr.split("\n");
            String line = null;
            for (int i = 0; i < lineArr.length; i++) {
                line = lineArr[i];
                if (line.startsWith(KEY_ATTR_SI_Title))
                    ret.setTitle(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Original_Script))
                    ret.setOriginal_Script(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Original_Translation))
                    ret.setOriginal_Translation(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Original_Timing))
                    ret.setOriginal_Timing(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Original_Editing))
                    ret.setOriginal_Editing(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Script_Updated_By))
                    ret.setScript_Updated_By(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Update_Details))
                    ret.setUpdate_Details(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_ScriptType))
                    ret.setScriptType(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Collisions))
                    ret.setCollisions(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_PlayResX))
                    ret.setPlayResX(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_PlayResY))
                    ret.setPlayResY(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Timer))
                    ret.setTimer(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Synch_Point))
                    ret.setSynch_Point(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_WrapStyle))
                    ret.setWrapStyle(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_ScaledBorderAndShadow))
                    ret.setScaledBorderAndShadow(getValue(line));
            }
        }

        return ret;
    }

    private AssTagV4Style getAssTagV4Style(String allcontent) {
        AssTagV4Style ret = null;
        String partStr = getTagContent(allcontent, KEY_PART_v4_Styles);
        if (partStr != null) {
//            System.out.println(partStr);
            String[] lineArr = partStr.split("\n");
            String line = null;
            String[] formatNameArr = null;
            boolean hasFormat = false;
            List<AssTagV4Style.Style> styleList = null;
            for (int i = 0; i < lineArr.length; i++) {
                line = lineArr[i];
                if (line.startsWith(KEY_ATTR_VS_Format)) {
                    String formatsStr = getValue(line);
                    if (formatsStr != null && !formatsStr.equals("")) {
                        formatsStr = formatsStr.replaceAll(" ", "");
                        formatNameArr = formatsStr.split(",");
                        if (styleList == null)
                            styleList = new ArrayList<>();
                        hasFormat = true;
                    } else {
                        throwEx(ErrorNoFormat);
                    }
                }

                if (hasFormat && line.startsWith(KEY_ATTR_VS_Style)) {
                    String[] valArr = getValue(line).split(",");
                    styleList.add(getStyle(formatNameArr, valArr));
                }
            }
            if (styleList != null) {
                ret = new AssTagV4Style();
                ret.setStyles(styleList);
            }
        }
        return ret;
    }

    private AssTagEvent getAssTagEvent(String allcontent) {
        AssTagEvent ret = null;
        String partStr = getTagContent(allcontent, KEY_PART_Events);
        if (partStr != null) {
//            System.out.println(partStr);
            String[] lineArr = partStr.split("\n");
            String line = null;
            String[] formatNameArr = null;
            boolean hasFormat = false;
            List<AssTagEvent.Dialogue> dialogueList = null;
            for (int i = 0; i < lineArr.length; i++) {
                line = lineArr[i];
                if (line.startsWith(KEY_ATTR_E_Format)) {
                    String formatsStr = getValue(line);
                    if (formatsStr != null && !formatsStr.equals("")) {
                        formatsStr = formatsStr.replaceAll(" ", "");
                        formatNameArr = formatsStr.split(",");
                        if (dialogueList == null)
                            dialogueList = new ArrayList<>();
                        hasFormat = true;
                    } else {
                        throwEx(ErrorNoFormat);
                    }
                }

                if (hasFormat && line.startsWith(KEY_ATTR_E_Dialogue)) {
                    String[] valArr = getValue(line).split(",");
                    List<String> valList = new ArrayList<>();
                    boolean found = false;
                    StringBuffer sb = new StringBuffer();
//                    Log.d(TAG,"valArr:"+valArr.length+":"+Arrays.toString(valArr));
                    for (int j = 0; j < valArr.length; j++) {
                        if (valArr[j].contains("{")) {
                            found = true;
                        }
                        if (!found) {
                            valList.add(valArr[j]);
                        } else {
                            sb.append(valArr[j]);
                            if (j != valArr.length - 1) {
                                sb.append(",");
                            }
                        }
                    }
                    if (found) {
                        valList.add(sb.toString());
                    }
                    dialogueList.add(getDialogue(formatNameArr, valList));
                }
            }
            if (dialogueList != null) {
                ret = new AssTagEvent();
                ret.setDialogues(dialogueList);
            }
        }
        return ret;
    }

    private AssTagEvent.Dialogue getDialogue(String[] formatArr, List<String> valArr) {
        AssTagEvent.Dialogue ret = null;

//        System.out.println(Arrays.toString(formatArr));
//        System.out.println(valArr);
        if (formatArr.length != valArr.size()) {
            throwEx(ErrorFormatCountNotMatchValColCount + " format:" + formatArr.length + " valArr:" + valArr.size()
                    + "\n" + " formatArr:" + Arrays.toString(formatArr)
                    + "\n" + " valArr:" + valArr);
        } else {
            ret = new AssTagEvent.Dialogue();
            for (int i = 0; i < formatArr.length; i++) {
                if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Layer))
                    ret.setLayer(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Start))
                    ret.setStart(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_End))
                    ret.setEnd(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Style))
                    ret.setStyle(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Actor))
                    ret.setActor(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_MarginL))
                    ret.setMarginL(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_MarginR))
                    ret.setMarginR(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_MarginV))
                    ret.setMarginV(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Effect))
                    ret.setEffect(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Text))
                    ret.setText(valArr.get(i));
            }
        }
        return ret;
    }

    private AssTagV4Style.Style getStyle(String[] formatArr, String[] valArr) {
        AssTagV4Style.Style ret = null;
        if (formatArr.length != valArr.length) {
            throwEx(ErrorFormatCountNotMatchValColCount);
        } else {
            ret = new AssTagV4Style.Style();
            for (int i = 0; i < formatArr.length; i++) {
                if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Name))
                    ret.setName(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Fontname))
                    ret.setFontname(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Fontsize))
                    ret.setFontsize(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_PrimaryColour))
                    ret.setPrimaryColour(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_SecondaryColour))
                    ret.setSecondaryColour(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_OutlineColour))
                    ret.setOutlineColour(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_BackColour))
                    ret.setBackColour(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Bold))
                    ret.setBold(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Italic))
                    ret.setItalic(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Underline))
                    ret.setUnderline(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_StrikeOut))
                    ret.setStrikeOut(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_ScaleX))
                    ret.setScaleX(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_ScaleY))
                    ret.setScaleY(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Spacing))
                    ret.setSpacing(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Angle))
                    ret.setAngle(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_BorderStyle))
                    ret.setBorderStyle(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Outline))
                    ret.setOutline(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Shadow))
                    ret.setShadow(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Alignment))
                    ret.setAlignment(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_MarginL))
                    ret.setMarginL(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_MarginR))
                    ret.setMarginR(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_MarginV))
                    ret.setMarginV(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Encoding))
                    ret.setEncoding(valArr[i]);
            }
        }
        return ret;
    }

    private String getValue(String line) {
        String ret = null;
        int saperateIndex = line.indexOf(":");
        if (line != null && saperateIndex > 0 && saperateIndex != line.length() - 1) {
            ret = line.substring(line.indexOf(":") + 1);
//            String[] arr = line.split(":");
//            if (arr.length >= 2) {
//                ret = arr[1];
            if (ret.startsWith(" "))
                ret = ret.substring(1);
//            }
        }
        return ret;
    }

    private String getTagContent(String allcontent, String tag) {
        String ret = null;
        StringBuffer sb = null;
        final String tagS = "[";
        final String tagE = "]";
        String targetTag = tagS + tag + tagE;

        if (allcontent == null || !allcontent.contains(targetTag)) {
            return null;
        }

        String[] strArr = allcontent.split("\n");
        boolean start = false;
        for (int i = 0; i < strArr.length; i++) {
            if (start) {
                if (strArr[i].equals(targetTag))
                    throwEx(ErrorMultipleTag + ":" + targetTag);
                else if (strArr[i].startsWith(tagS))
                    break;
            }


            if (start) {
                if (sb == null)
                    sb = new StringBuffer();
                sb.append(strArr[i] + "\n");
            }

            //找到开头
            if (strArr[i].equals(targetTag)) {
                start = true;
            }
        }
        if (sb != null)
            ret = sb.toString();

        return ret;
    }


    /**
     * @param event 字幕文件的一行,为一条事件
     * @param text  一条事件内,有样式不同的内容,即多条内容,每条内容样式不同,每条条目会继承父style和此条之前条目的子Style
     *              <p>
     *              1.数据与对象映射方式
     *              每一条Dialogue数据被解析成如下结构
     *              evet---text1
     *              |---text2
     *              event与text都有自己的style对象,
     *              parentStyle---textStyle1
     *              |---textStyle2
     *              <p>
     *              2.根据每条Dialogue的Style字段产生了父:parentStyle
     *              3.解析Dialogue的Text字段,会产生子:textStyle
     *              简化:一般情况下,认为textStyle中解析出的动画,位置,对齐方式,属于parentStyle(仅限第一条内容之前的style),之后的忽略
     *              <p>
     *              <p>
     *              比如这样一条text,{\fad(500,500)\pos(360,340)\c&HFFFFFF&\fs18\b1}本集关键字：{\c&H5650F3\b0}卓戈   伊林·派恩  提利昂·兰尼斯特 丹妮莉丝·坦格利安
     *              <p>
     *              1.解析出textStyle{\fad(500,500)\pos(360,340)\c&HFFFFFF&\fs18\b1}
     *              2.其中\fad(500,500)\pos(360,340)\这2条动画属性会给parentStyle
     *              3.c&HFFFFFF&\fs18\b1会给textStyle1
     *              4.c&HFFFFFF&\fs18\b1 {\c&H5650F3\b0} 会给textStyle2 (继承parentStyle和textStyle1)
     * @return
     */
    private List<AssSubtitleEvent.Text> parseText(AssSubtitleEvent event, String text) {
        List<AssSubtitleEvent.Text> ret = new ArrayList<>();
        //分离text,分类,排序
        List<TextIndex> textIndices = getTextIndexList(text);

        //遍历,发现style就赋值,发现text,先clone一个事件赋值text,add list
        TextIndex textIndex = null;
        AssTagEventTextStyle tagEventTextStyle = null;

        //文字序号
        int textid = 0;
        for (int i = 0; i < textIndices.size(); i++) {
            textIndex = textIndices.get(i);
            if (textIndex.type == TextIndex.Type.Style) {
                if (tagEventTextStyle == null)
                    tagEventTextStyle = new AssTagEventTextStyle();
                //解析style, 一行只有一个style对象,解析到某个属性则覆盖
                parseTextStyle(tagEventTextStyle, textIndex.text);
            } else if (textIndex.type == TextIndex.Type.Text) {
                AssSubtitleEvent.Text textTmp = new AssSubtitleEvent.Text();
                textTmp.setText(textIndex.text);
                //父样式先赋值
                textTmp.setTextStyle(event.getParentTextStyle().clone());
                //有style则覆盖
                if (tagEventTextStyle != null)
                    overrideStyle(textid++, event, textTmp, tagEventTextStyle);

                ret.add(textTmp);
            }
        }

        Log.d(TAG,"parseText:"+ret);
        return ret;
    }

    /**
     * 有些属性覆盖到event的父style中(比如位置,动画,对齐方式),仅仅只有第一条内容之前出现的textstyle会覆盖进父style
     * <p>
     * 那父style
     *
     * @param textIndex
     * @param event
     * @param text
     * @param style
     */
    private void overrideStyle(int textIndex, AssSubtitleEvent event, AssSubtitleEvent.Text text, AssTagEventTextStyle style) {
        if (text != null && style != null) {
            if (textIndex == 0) {
                if (style.isAlginSetted()) {
                    event.setAlignment(style.getAlgin());
                }
                if (style.isAnimFadeShowTimeSetted()) {
                    if (event.getParentAnim() == null)
                        event.setParentAnim(new AssSubtitleEvent.Anim());
                    event.getParentAnim().setAnimFadeShowTime(style.getAnimFadeShowTime());
                    event.getParentAnim().setAnimFadeHideTime(style.getAnimFadeHideTime());
                }
                if (style.isAnimMoveStartXSetted()) {
                    if (event.getParentAnim() == null)
                        event.setParentAnim(new AssSubtitleEvent.Anim());
                    event.getParentAnim().setAnimMoveStartX(style.getAnimMoveStartX());
                    event.getParentAnim().setAnimMoveStartY(style.getAnimMoveStartY());
                    event.getParentAnim().setAnimMoveEndX(style.getAnimMoveEndX());
                    event.getParentAnim().setAnimMoveEndY(style.getAnimMoveEndY());
                }
                if (style.isPosiXSetted()) {
                    event.setPosiX(style.getPosiX());
                    event.setPosiY(style.getPosiY());
                }
            }
        }


        if (style.isAngleSetted()) {
            text.getTextStyle().setAngle(style.getAngle());
        }


        if (style.isBoldSetted()) {
            text.getTextStyle().setBold(style.isBold());
        }


        if (style.isBorderShadowWidthSetted()) {
            text.getTextStyle().setBorderShadowWidth(style.getBorderShadowWidth());
        }

        if (style.isBorderWidthSetted()) {
            text.getTextStyle().setBorderWidth(style.getBorderWidth());
        }

        if (style.isFontNameSetted()) {
            text.getTextStyle().setFontName(style.getFontName());
        }

        if (style.isFontSizeSetted()) {
            text.getTextStyle().setFontSize(style.getFontSize());
        }
        if (style.isItalicSetted()) {
            text.getTextStyle().setItalic(style.isItalic());
        }

        if (style.isPrimaryColorSetted()) {
            text.getTextStyle().setPrimaryColor(style.getPrimaryColor());
        }
        if (style.isSecondColorSetted()) {
            text.getTextStyle().setSecondColor(style.getSecondColor());
        }
        if (style.isBorderColorSetted()) {
            text.getTextStyle().setBorderColor(style.getBorderColor());
        }

        if (style.isShadowColorSetted()) {
            text.getTextStyle().setShadowColor(style.getShadowColor());
        }
        if (style.isStrikeOutSetted()) {
            text.getTextStyle().setStrikeOut(style.isStrikeOut());
        }
        if (style.isUnderlineSetted()) {
            text.getTextStyle().setUnderline(style.isUnderline());
        }
    }


    private void parseTextStyle(AssTagEventTextStyle tagEventTextStyle, String textStyle) {
        //先把\t干掉
//        Pattern pattern = Pattern.compile("(?:\\\\t)(.+?)(?:\\))");
//        Matcher matcher = pattern.matcher(textStyle);
//        while(matcher.find()) {
//
//        }
        //先把\t干掉
        while (textStyle.contains(OS_Anim_DynamicOS)) {
            int start = textStyle.indexOf(OS_Anim_DynamicOS);
            int foundLeftBracket = 0;
            int foundRightBracket = 0;
            int end = 0;
            for (int i = start; i < textStyle.length(); i++) {
                if (textStyle.charAt(i) == '(')
                    foundLeftBracket++;
                if (textStyle.charAt(i) == ')')
                    foundRightBracket++;
                if (foundLeftBracket != 0 && foundLeftBracket == foundRightBracket) {
                    end = i;
                    break;
                }
            }
            textStyle = textStyle.substring(0, start) + textStyle.substring(end + 1);
        }

        if (textStyle.trim().length() == 0) {
            return;
        }

        //反斜杠分割
        String[] styleArr = textStyle.split("\\\\");
        String styleStr = null;
        for (int i = 0; i < styleArr.length; i++) {
            if (styleArr[i].trim().length() == 0)
                continue;
            styleStr = "\\" + styleArr[i];

            if (styleStr.startsWith(OS_Anim_Fade)) {
                //\fade(0.0,1.0,0.0,500,500,500,500)范围0-255
            } else if (styleStr.startsWith(OS_Anim_FadeSimple)) {
                //\fad(222,222)
                int[] val = getTextStyleBracketVal(styleStr);
                tagEventTextStyle.setAnimFadeShowTime(val[0]);
                tagEventTextStyle.setAnimFadeHideTime(val[1]);
            } else if (styleStr.startsWith(OS_Anim_Move)) {
                //\move(380,235,380,240)
                int[] val = getTextStyleBracketVal(styleStr);
                tagEventTextStyle.setAnimMoveStartX(val[0]);
                tagEventTextStyle.setAnimMoveStartY(val[1]);
                tagEventTextStyle.setAnimMoveEndX(val[2]);
                tagEventTextStyle.setAnimMoveEndY(val[3]);
            }else if (styleStr.startsWith(OS_Position)) {
                //\pos(200,200)
                int[] val = getTextStyleBracketVal(styleStr);
                tagEventTextStyle.setPosiX(val[0]);
                tagEventTextStyle.setPosiY(val[1]);
            }
//            else if (styleStr.startsWith(OS_ShadowDepth)) {
//                \shad[<x,y>]<depth> 阴影深度
//            }
            else if (styleStr.startsWith(OS_FontName)) {
                //\fn微软雅黑
                tagEventTextStyle.setFontName(getTextStyleTagValue(OS_FontName, styleStr));
            } else if (styleStr.startsWith(OS_FontSize)) {
                //\fs56.267
                tagEventTextStyle.setFontSize((int) Double.parseDouble(getTextStyleTagValue(OS_FontSize, styleStr)));
            }else if (styleStr.startsWith(OS_BordWidth)) {
                //\bord[<x,y>]<width> \bord0 \bordx2 \bordy3
                String val = getTextStyleTagValue(OS_BordWidth, styleStr);
                int borderWidth = 0;
                if (val.startsWith("x") || val.startsWith("y")) {
                    borderWidth = string2Int(val.substring(1));
                } else {
                    borderWidth = string2Int(val);
                }
                tagEventTextStyle.setBorderWidth(borderWidth);

            }   else if (styleStr.startsWith(OS_Text_Gradient + "x")) {
                //\fa<x,y><degrees>  \fax-0.5 等同于斜体，一般不要超过±2

                //只支持x轴方向上的倾斜
                tagEventTextStyle.setAngle(string2Float(getTextStyleTagValue(OS_Text_Gradient + "x", styleStr)));
            }else if (styleStr.startsWith(OS_Text_Bold)) {
                //\b<0/1>
                tagEventTextStyle.setBold(getTextStyleOnOff(OS_Text_Bold, styleStr));
            } else if (styleStr.startsWith(OS_Text_Italic)) {
                //\i<0/1>
                tagEventTextStyle.setItalic(getTextStyleOnOff(OS_Text_Italic, styleStr));

            } else if (styleStr.startsWith(OS_Text_Underline)) {
                //这里多打了一个反斜杠\\u <0/1>
                tagEventTextStyle.setUnderline(getTextStyleOnOff(OS_Text_Underline, styleStr));

            } else if (styleStr.startsWith(OS_Text_Deleteline)) {
                //\s <0/1>  删除线（0=关闭，1=开启）
                tagEventTextStyle.setStrikeOut(getTextStyleOnOff(OS_Text_Deleteline, styleStr));

            }
//
            else if (styleStr.startsWith(OS_Color_Primary)) {
                //\c&H<bbggrr>&
                tagEventTextStyle.setPrimaryColor(getColor(getTextStyleColor(getTextStyleTagValue(OS_Color_Primary, styleStr))));

            } else if (styleStr.startsWith(OS_Color_Primary2)) {
                // \1c&H<bbggrr>&  可能末尾没有&
                tagEventTextStyle.setPrimaryColor(getColor(getTextStyleColor(getTextStyleTagValue(OS_Color_Primary2, styleStr))));
            } else if (styleStr.startsWith(OS_Color_Secondary)) {
                //\2c&H<bbggrr>&
                tagEventTextStyle.setSecondColor(getColor(getTextStyleColor(getTextStyleTagValue(OS_Color_Secondary, styleStr))));

            } else if (styleStr.startsWith(OS_Color_Border)) {
                //\3c&H<bbggrr>&
                tagEventTextStyle.setBorderColor(getColor(getTextStyleColor(getTextStyleTagValue(OS_Color_Border, styleStr))));

            } else if (styleStr.startsWith(OS_Color_Shadow)) {
                //\4c&H<bbggrr>&
                tagEventTextStyle.setShadowColor(getColor(getTextStyleColor(getTextStyleTagValue(OS_Color_Shadow, styleStr))));

//            }
//            else if (styleStr.startsWith(OS_Alpha_All)) {
//                \alpha&H<aa>&
//
//            } else if (styleStr.startsWith(OS_Alpha_Primary)) {
//                \1a&H<aa>&
//            } else if (styleStr.startsWith(OS_Alpha_Secondary)) {
//                \2a&H<aa>&
//            } else if (styleStr.startsWith(OS_Alpha_Border)) {
//                \3a&H<aa>&
//            } else if (styleStr.startsWith(OS_Alpha_Shadow)) {
                //\4a&H<aa>&
            } else if (styleStr.startsWith(OS_Align)) {
                //\an<alignment> 类似小键盘
                tagEventTextStyle.setAlgin(string2Int(getTextStyleTagValue(OS_Align, styleStr)));
            } else {
                Log.w(TAG, "TextStyle not support:" + styleStr);
            }
        }

        System.out.println(textStyle);
    }

    /**
     * &H,& 区间内的值
     *
     * @param text
     * @return
     */
    private String getTextStyleAlpha(String text) {
        String ret = null;

        if (text != null && text.length() >= 4) {
            //去除&H
            text = text.substring(2);
            //去除尾部&
            if (text.lastIndexOf("&") == text.length() - 1) {
                text = text.substring(0, text.length() - 1);
            }
            if (text.length() == 2) {
                ret = text;
            }
        }
        return ret;
    }

    /**
     * &H,& 区间内的值
     *
     * @param text
     * @return
     */
    private String getTextStyleColor(String text) {
        String ret = null;

        if (text != null && text.length() >= 8) {
            //去除&H
            text = text.substring(2);
            //去除尾部&
            if (text.lastIndexOf("&") == text.length() - 1) {
                text = text.substring(0, text.length() - 1);
            }
            if (text.length() == 6 || text.length() == 8) {
                ret = text;
            }
        }
        return ret;
    }

    private String getTextStyleTagValue(String tag, String text) {
        String value = null;
        if (tag != null && text != null) {
            Log.d(TAG, "tag:" + tag + " len:" + tag.length() + " text:" + text + " len:" + text.length());
            text = text.trim().replaceAll(" ", "");
            if (tag.length() != text.length()) {
                value = text.substring(tag.length());
            }
        }
        Log.d(TAG, "ret:" + value);
        return value;
    }

    /**
     * @param tag  \b
     * @param text \b1
     * @return 类似这种 \b<0/1>
     */
    private boolean getTextStyleOnOff(String tag, String text) {
        boolean ret = false;
        String strVal = getTextStyleTagValue(tag, text);
        if (strVal == null)
            return ret;

        if (strVal.equals("0")) {
            ret = false;
        } else if (strVal.equals("1")) {
            ret = true;
        }
        return ret;
    }

    private int[] getTextStyleBracketVal(String text) {
        if (text == null)
            return null;
        int[] ret = null;

        int bracketL = text.indexOf("(");
        int bracketR = text.indexOf(")");
        if (bracketL != -1 && bracketR != -1
                && bracketL < bracketR) {
            text = text.substring(bracketL + 1, bracketR);
            text = text.trim();
            String[] strArr = text.split(",");
            if (strArr.length > 0) {
                ret = new int[strArr.length];
            }
            for (int i = 0; i < strArr.length; i++) {
                ret[i] = (int) Double.parseDouble(strArr[i]);
            }

        }
        return ret;
    }

    private List<TextIndex> getTextIndexList(String text) {
        List<TextIndex> textIndices = new ArrayList<>();
        TextIndex textIndex = null;
        //{}内
        Pattern pattern = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            textIndex = new TextIndex();
            textIndex.text = matcher.group();
            textIndex.start = matcher.start();
            textIndex.end = matcher.end();
            textIndex.type = TextIndex.Type.Style;
//            ls.add(matcher.group());
//            System.out.println(matcher.group() + " s:" + matcher.start() + " e:" + matcher.end());
            textIndices.add(textIndex);
        }
        //{}外
        //文字在{}前
        int firstBracketIndex = text.indexOf("{");
        if(firstBracketIndex != -1 && firstBracketIndex != 0) {
            textIndex = new TextIndex();
            textIndex.text =text.substring(0,firstBracketIndex);
            textIndex.start = 0;
            textIndex.end = firstBracketIndex-1;
            textIndex.type = TextIndex.Type.Text;
            textIndices.add(textIndex);
        }

        pattern = Pattern.compile("(?<=\\})([^{]+)(?!=\\{)");
        matcher = pattern.matcher(text);

        while (matcher.find()) {
//            ls.add(matcher.group());
            //过滤空格串
            if (matcher.group().trim().length() == 0)
                continue;
            textIndex = new TextIndex();
            textIndex.text = matcher.group();
            textIndex.start = matcher.start();
            textIndex.end = matcher.end();
            textIndex.type = TextIndex.Type.Text;
            textIndices.add(textIndex);
//            System.out.println(matcher.group() + " s:" + matcher.start() + " e:" + matcher.end());
        }
        Collections.sort(textIndices, new Comparator<TextIndex>() {
            @Override
            public int compare(TextIndex o1, TextIndex o2) {
                return o1.start - o2.start;
            }
        });
        return textIndices;
    }

    /**
     * 存放text中区分样式与文字的子串
     */
    private static class TextIndex {
        enum Type {
            Style,
            Text
        }

        //内容
        String text;
        //在原字符串中的首尾位置
        int start;
        int end;
        //区分样式或文字
        Type type;

        @Override
        public String toString() {
            return "TextIndex{" +
                    "text='" + text + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    ", type=" + type +
                    '}';
        }

    }

    public static void main(String[] args) {
        String haha = "dw,dw,,a,";
//        System.out.println(haha.split(",").length);
//        System.out.println(Arrays.toString(haha.split("dw,dw,,a,")));
        AssSubtitleParser asp = new AssSubtitleParser();

//        String part_script = testGetTagContent(KEY_PART_Script_Info);

//        AssTagScriptInfo ats = asp.getAssTagScriptInfo(getAllContent());
//        System.out.println(ats);
//        AssTagV4Style ats = asp.getAssTagV4Style(getAllContent());
//        System.out.println(ats);
//        AssTagEvent ats = asp.getAssTagEvent(getAllContent());
//        System.out.println(ats);

//        testPattern();
        testGetTextIndex();
    }

    private static void testGetTextIndex() {
        AssSubtitleParser asp = new AssSubtitleParser();
        String test = "dawdwa{\\fad(300,500)\\frx360\\t(20,200,\\frx0(aa,bb))\\pos(320,250)\\fs25\\b1}君临{\\b0\\fs12} King's Landing{\\dw\\t(20,200,\\frx2(aa,bb))\\}dwad{\\dw}";
        List<TextIndex> textIndices = asp.getTextIndexList(test);
        System.out.println(textIndices);
//        for (int i = 0; i < textIndices.size(); i++) {
//            TextIndex textIndex = textIndices.get(i);
//            if (textIndex.type == TextIndex.Type.Style) {
//                //解析style
//                asp.parseTextStyle(null, textIndex.text);
//            }
//        }
    }

    private static void testPattern() {
        //找到非}\\s+\\{
        String test = "{\\fad(300,500)\\frx360\\t(20,200,\\frx0)\\pos(320,250)\\fs25\\b1}君临{\\b0\\fs12} King's Landing";


        List<String> ls = new ArrayList<String>();

        //{}内
        Pattern pattern = Pattern.compile("(?<=\\{)(.+?)(?=\\})");
        Matcher matcher = pattern.matcher(test);

        while (matcher.find()) {
//            ls.add(matcher.group());
            System.out.println(matcher.group() + " s:" + matcher.start() + " e:" + matcher.end());
        }
        //{}外
        pattern = Pattern.compile("(?<=\\})([^{]+)(?!=\\{)");
        matcher = pattern.matcher(test);

        while (matcher.find()) {
//            ls.add(matcher.group());
            System.out.println(matcher.group() + " s:" + matcher.start() + " e:" + matcher.end());
        }
        //找到}  {,}{
//        Pattern pattern = Pattern.compile("(?:\\})(?:\\s+|)(?:\\{)");

//        ("(?:\\})(?:\\s+|)(?:.+)(?:\\s+|)(?:\\{)")

        //找到所有{xx}区间内容,解析成样式
        //找到所有}xx{区间内容,包括}xx,去除纯空项
        //


//        System.out.println(ls);
    }


    private static String getAllContent() {
        return "[Script Info]\n" +
                ";ReEdit by NetAdmin@TLF\n" +
                "\n" +
                "Title: Game.of.Thrones.S01E10.Fire.and.Blood.1080p.bluray.HEVC.AAC.5.1.x265.10Bit.mkv\n" +
                "Original Script: 衣柜军团\n" +
                "Original Translation: “龙堡字幕组”&“衣柜字幕组”\n" +
                "Original Timing: 衣柜军团\n" +
                "Original Editing: 衣柜军团\n" +
                "Script Updated By: 衣柜军团 /  NetAdmin@TLF\n" +
                "Update Details: 合成双语字幕/  剔除假药网Logo，修正格式错误和少量翻译错误\n" +
                "ScriptType: v4.00+\n" +
                "Collisions: Normal\n" +
                "PlayResX: 720\n" +
                "PlayResY: 404\n" +
                "Timer: 100.0000\n" +
                "Synch Point: \n" +
                "WrapStyle: 0\n" +
                "ScaledBorderAndShadow: no\n" +
                "\n" +
                "[V4+ Styles]\n" +
                "Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\n" +
                "Style: Title,Microsoft YaHei,20,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,0,0,0,0,100,100,0,0,1,1,1,2,10,10,10,1\n" +
                "Style: Titles_L,Microsoft YaHei,24,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,-1,0,0,0,100,100,0,0,1,1,1,1,10,10,10,1\n" +
                "Style: Author,Microsoft YaHei,16,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,0,0,0,0,100,100,0,0,1,1,1,2,10,10,10,1\n" +
                "Style: subtitle,Microsoft YaHei,26,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,0,0,0,0,100,100,0,0,1,1,1,2,10,10,10,1\n" +
                "Style: character+,Microsoft YaHei,20,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,0,0,0,0,100,100,0,0,1,1,1,4,10,10,10,1\n" +
                "Style: Character_L,Microsoft YaHei,28,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,-1,0,0,0,100,100,0,0,1,1,1,4,10,10,10,1\n" +
                "Style: character,Microsoft YaHei,20,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,-1,0,0,0,100,100,0,0,1,1,1,2,10,10,10,1\n" +
                "\n" +
                "[Events]\n" +
                "Format: Layer, Start, End, Style, Actor, MarginL, MarginR, MarginV, Effect, Text\n" +
                "Dialogue: 0,0:00:07.80,0:00:11.07,Title,,0,0,0,,{\\fad(500,500)\\pos(360,360)\\b1\\bord0}双语特效字幕\\N{\\b0}“龙堡字幕组”携手“衣柜字幕组”诚挚奉献\\N{\\fs16}专注GOT剧集 华语冰火最专业字幕\n" +
                "Dialogue: 0,0:00:10.99,0:00:12.83,Author,,0,0,0,,{\\fad(500,500)\\pos(479,223)}翻译{\\b1\\fs24}    biscn\n" +
                "Dialogue: 0,0:00:13.20,0:00:15.00,Author,,0,0,0,,{\\fad(500,500)\\pos(185,228)}翻译{\\b1\\fs24}    Arya·Stark\n" +
                "Dialogue: 0,0:00:16.50,0:00:18.50,Title,,0,0,0,,{\\fad(500,500)\\frz82\\move(450,169,517,169)\\fs25\\b1}狭海   {\\fs12\\b0}The Narrow Sea \n" +
                "Dialogue: 0,0:00:18.60,0:00:22.20,Title,,0,0,0,,{\\fad(300,500)\\frx360\\t(20,200,\\frx0)\\pos(320,250)\\fs25\\b1}君临{\\b0\\fs12} King's Landing\n" +
                "Dialogue: 0,0:00:23.30,0:00:25.09,Author,,0,0,0,,{\\fad(500,500)\\pos(153,230)}翻译{\\b1\\fs24}    Violet_Rosa";

    }

    private static String testGetTagContent(String tag) {
        String content = getAllContent();
        AssSubtitleParser asp = new AssSubtitleParser();
        String ret = asp.getTagContent(content, tag);
        return ret;
    }

}
